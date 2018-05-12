/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package server.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import client.ClientSocket;
import constants.ServerConstants;
import constants.ServerConstants.CommandType;
import constants.ServerConstants.PlayerGMRank;
import database.Database;
import java.sql.Connection;
import scripting.provider.NPCChatByType;
import scripting.provider.NPCChatType;
import service.ChannelServer;
import server.maps.objects.User;
import tools.LogHelper;
import tools.packet.CField;

public class CommandProcessor {

    private final static HashMap<String, MapleCommand> commands = new HashMap<>();
    private final static HashMap<Integer, ArrayList<String>> commandList = new HashMap<>();

    static {

        Class<?>[] CommandFiles = {
            
            PlayerCommand.class,
            DonatorCommand.class,
            
            InternCommand.class,
            GMCommand.class,
            AdminCommand.class,
            
            DeveloperCommands.class,
            ThreadTesterCommand.class,
            
            CommandVault.class
                
        };

        for (Class<?> clasz : CommandFiles) {
            try {
                PlayerGMRank rankNeeded = (PlayerGMRank) clasz.getMethod("getPlayerLevelRequired", new Class<?>[]{}).invoke(null, (Object[]) null);
                Class<?>[] a = clasz.getDeclaredClasses();
                ArrayList<String> cL = new ArrayList<>();
                for (Class<?> c : a) {
                    try {
                        if (!Modifier.isAbstract(c.getModifiers()) && !c.isSynthetic()) {
                            Object o = c.newInstance();
                            boolean enabled;
                            try {
                                enabled = c.getDeclaredField("enabled").getBoolean(c.getDeclaredField("enabled"));
                            } catch (NoSuchFieldException ex) {
                                enabled = true; //Enable all coded commands by default.
                            }
                            if (o instanceof CommandExecute && enabled) {
                                cL.add(rankNeeded.getCommandPrefix() + c.getSimpleName().toLowerCase());
                                commands.put(rankNeeded.getCommandPrefix() + c.getSimpleName().toLowerCase(), new MapleCommand((CommandExecute) o, rankNeeded.getLevel()));
                                if (!rankNeeded.getCommandPrefix().equals(PlayerGMRank.GM.getCommandPrefix()) && !rankNeeded.getCommandPrefix().equals(PlayerGMRank.NORMAL.getCommandPrefix())) { //add it again for GM
                                    commands.put("!" + c.getSimpleName().toLowerCase(), new MapleCommand((CommandExecute) o, PlayerGMRank.GM.getLevel()));
                                }
                            }
                        }
                    } catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException ex) {
                        LogHelper.COMMAND.get().info("Command:\n{}", ex);
                    }
                }
                Collections.sort(cL);
                commandList.put(rankNeeded.getLevel(), cL);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                LogHelper.COMMAND.get().info("Command:\n{}", ex);
            }
        }
    }

    private static void sendDisplayMessage(ClientSocket c, String msg, CommandType type) {
        if (c.getPlayer() == null) {
            return;
        }
        switch (type) {
            case NORMAL:
                c.getPlayer().dropMessage(6, msg);
                break;
            case TRADE:
                c.getPlayer().dropMessage(-2, "Error : " + msg);
                break;
        }
    }

    /**
     * Auto Command List
     * @author Mazen Massoud
     * 
     * @param c ClientSocket
     */
    public static void dropHelp(ClientSocket c) {
        String sAccessRank = "Unknown";
        if (c.getPlayer().isDeveloper()) {
            sAccessRank = "Developer";
        } else if (c.getPlayer().isAdmin()) {
            sAccessRank = "Administrator";
        } else if (c.getPlayer().isGM()) {
            sAccessRank = "Game Master";
        } else if (c.getPlayer().isIntern()) {
            sAccessRank = "Intern";
        }

        final StringBuilder sMessage = new StringBuilder("#d" + ServerConstants.SERVER_NAME + " Administrative Command List\r\n"
                + "Access Level (#b" + sAccessRank + "#k)\r\n\r\n#r");

        for (int i = 2; i <= c.getPlayer().getGMLevel(); i++) {
            if (commandList.containsKey(i)) {
                for (String sCommand : commandList.get(i)) {
                    sMessage.append("\t" + sCommand + "\r\n");
                }
            }
        }

        c.SendPacket(CField.NPCPacket.getNPCTalk(9010000, NPCChatType.OK, sMessage.toString(), NPCChatByType.NPC_Cancellable));
    }

    public static boolean processCommand(ClientSocket c, String line, CommandType type) {
        for (PlayerGMRank prefix : PlayerGMRank.values()) {
            if (line.startsWith(prefix.getCommandPrefix() + prefix.getCommandPrefix())) {
                return false;
            }
        }
        if (String.valueOf(line.charAt(0)).equals(PlayerGMRank.NORMAL.getCommandPrefix())) {
            String[] splitted = line.split(" ");
            splitted[0] = splitted[0].toLowerCase();

            MapleCommand co = commands.get(splitted[0]);
            if (co == null || co.getType() != type) {
                sendDisplayMessage(c, "The specified player command does not exist.", type);
                return true;
            }
            try {
                int ret = co.execute(c, splitted); //Don't really care about the return value. ;D
            } catch (Exception e) {
                sendDisplayMessage(c, "There was an error.", type);
                if (c.getPlayer().isGM()) {
                    sendDisplayMessage(c, "Error: " + e, type);
                    LogHelper.COMMAND.get().info("Command " + line + "Player: " + c.getPlayer().getName() + ":\n{}", e);
                }
            }
            return true;
        }

        if (c.getPlayer().getGMLevel() > PlayerGMRank.NORMAL.getLevel()) {
            if (line.charAt(0) == '`' && c.getPlayer().getGMLevel() > 2) {
                for (final ChannelServer cserv : ChannelServer.getAllInstances()) {
                    //cserv.broadcastGMMessage(tools.packet.CField.multiChat("[GM Chat] " + c.getPlayer().getName(), line.substring(1), 6));
                }
                return true;
            }
            if (line.split(" ")[0].equals("cmd") || String.valueOf(line.charAt(0)).equals(PlayerGMRank.INTERN.getCommandPrefix()) || String.valueOf(line.charAt(0)).equals(PlayerGMRank.GM.getCommandPrefix()) || String.valueOf(line.charAt(0)).equals(PlayerGMRank.DONOR.getCommandPrefix())) { //Redundant for now, but in case we change symbols later. This will become extensible.
                String[] splitted = line.split(" ");
                splitted[0] = splitted[0].toLowerCase();

                MapleCommand co = commands.get(splitted[0]);
                if (co == null) {
                    if (splitted[0].equals(line.charAt(0) + "commands")) {
                        dropHelp(c);
                        return true;
                    }
                    sendDisplayMessage(c, "That command does not exist.", type);
                    return true;
                }
                if (c.getPlayer().getGMLevel() >= co.getReqGMLevel()) {
                    int ret = 0;
                    try {
                        ret = co.execute(c, splitted);
                    } catch (ArrayIndexOutOfBoundsException x) {
                        sendDisplayMessage(c, "The command was not used properly: " + x, type);
                    } catch (Exception e) {
                        LogHelper.COMMAND.get().info("Command " + line + "Player: " + c.getPlayer().getName() + ":\n{}", e);
                    }
                    if (ret > 0 && c.getPlayer() != null) { //incase d/c after command or something
                        if (c.getPlayer().isGM()) {
                            logCommandToDB(c.getPlayer(), line, "gmlog");
                        } else {
                            logCommandToDB(c.getPlayer(), line, "internlog");
                        }
                    }
                } else {
                    sendDisplayMessage(c, "You do not have the privileges to use that command.", type);
                }
                return true;
            }
        }
        return false;
    }

    public static void logCommandToDB(User player, String command, String table) {
        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("INSERT INTO " + table + " (cid, command, mapid) VALUES (?, ?, ?)")) {
                ps.setInt(1, player.getId());
                ps.setString(2, command);
                ps.setInt(3, player.getMap().getId());
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            LogHelper.SQL.get().info("Logging Error:\n{}", ex);
        }

    }

    public static String getCommandsForLevel(int level) {
        String commandlist = "";
        for (int i = 0; i < commandList.get(level).size(); i++) {
            commandlist += commandList.get(level).get(i);
            if (i + 1 < commandList.get(level).size()) {
                commandlist += ", ";
            }
        }
        return commandlist;
    }
}
