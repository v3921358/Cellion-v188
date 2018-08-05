package server.commands;

import client.CharacterTemporaryStat;
import client.ClientSocket;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.MapleInventory;
import client.jobs.Sengoku;
import client.jobs.Sengoku.HayatoHandler;
import enums.InventoryType;
import constants.GameConstants;
import constants.ItemConstants;
import constants.ServerConstants;
import constants.ServerConstants.PlayerGMRank;
import constants.skills.Hayato;
import constants.skills.Kanna;
import constants.skills.Shade;
import database.Database;
import enums.ItemPotentialTierType;
import handling.world.World;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.life.LifeFactory;
import server.life.Mob;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import enums.SavedLocationType;
import enums.SummonMovementType;
import enums.WeatherEffectNotice;
import java.util.EnumMap;
import server.maps.objects.Summon;
import server.maps.objects.User;
import server.messages.StylishKillMessage;
import tools.LogHelper;
import tools.StringUtil;
import tools.packet.BuffPacket;
import tools.packet.CField;
import tools.packet.MobPacket;
import tools.packet.WvsContext;

/**
 * Player Commands
 * @author Mazen Massoud
 */
public class PlayerCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.NORMAL;
    }

    /**
     * Personal command I modify while debugging to do different operations.
     */
    public static class Debug extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            if (!ServerConstants.DEVELOPER_DEBUG_MODE) return 0;
            User pPlayer = c.getPlayer();
            int nType = Integer.parseInt(splitted[1]);
            int sValue = Integer.parseInt(splitted[2]);
            switch (nType) {
                case 1: 
                    Mob pMob = LifeFactory.getMonster(100100);
                    pPlayer.getMap().spawnMonsterOnGroundBelow(pMob, c.getPlayer().getPosition());
                    pPlayer.getMap().spawnMonsterOnGroundBelow(pMob, c.getPlayer().getPosition());
                    break;
                case 2:
                    pPlayer.OnLevelUp();
                    break;
                case 3:
                    pPlayer.setGM((byte) 5);
                    break;
                case 4:
                    pPlayer.tCrimsonQueen = 0;
                    pPlayer.tVonBon = 0;
                    pPlayer.tPierre = 0;
                    pPlayer.tVellum = 0;
                    pPlayer.tMagnus = 0;
                    pPlayer.tHorntail = 0;
                    break;
                case 5: 
                    Mob pElite = LifeFactory.getMonster(100100);
                    
                    pElite.changeLevel(200);
                    pElite.setHp(25000);
                    pElite.getStats().setFinalMaxHp(25000);
                    
                    c.getPlayer().getMap().spawnMonsterOnGroundBelow(pElite, c.getPlayer().getPosition(), 200);
                    break;
                case 6:
                    Equip pEquip = (Equip) (Equip) MapleItemInformationProvider.getInstance().getEquipById(1002140);
                    pEquip.setStr((short) 32767);
                    pEquip.setDex((short) 32767);
                    pEquip.setInt((short) 32767);
                    pEquip.setLuk((short) 32767);
                    pEquip.setPotential1(42051);
                    pEquip.setPotential2(42051);
                    pEquip.setPotential3(42051);
                    pEquip.setBonusPotential1(42051);
                    pEquip.setBonusPotential2(42051);
                    pEquip.setBonusPotential3(42051);
                    MapleInventoryManipulator.addbyItem(c, pEquip);
                    break;
                case 7:
                    Equip pEquip2 = (Equip) (Equip) MapleItemInformationProvider.getInstance().getEquipById(sValue);
                    pEquip2.setStr((short) 32767);
                    pEquip2.setDex((short) 32767);
                    pEquip2.setInt((short) 32767);
                    pEquip2.setLuk((short) 32767);
                    pEquip2.setWatk((short) 1999);
                    pEquip2.setMatk((short) 1999);
                    pEquip2.setBossDamage((byte) 250);
                    pEquip2.setTotalDamage((byte) 250);
                    pEquip2.setAllStat((byte) 999);
                    pEquip2.setPotentialTier(ItemPotentialTierType.Legendary);
                    pEquip2.setPotentialBonusTier(ItemPotentialTierType.Legendary);
                    pEquip2.setStarFlag((byte) 10);
                    pEquip2.setPotential1(42051);
                    pEquip2.setPotential2(42051);
                    pEquip2.setPotential3(42051);
                    pEquip2.setBonusPotential1(42051);
                    pEquip2.setBonusPotential2(42051);
                    pEquip2.setBonusPotential3(42051);
                    MapleInventoryManipulator.addbyItem(c, pEquip2);
                    break;
                case 8:
                    pPlayer.yellowMessage("primary: "+sValue+" / secondary: "+Integer.parseInt(splitted[3])+" / tertiary: "+Integer.parseInt(splitted[4])+" / quaterary: "+Integer.parseInt(splitted[5]));
                    //c.getPlayer().getClient().SendPacket(WvsContext.messagePacket(new StylishKillMessage(StylishKillMessage.StylishKillMessageType.MultiKill, sValue, Integer.parseInt(splitted[3]), Integer.parseInt(splitted[4]), Integer.parseInt(splitted[5]))));
                    //c.getPlayer().getClient().SendPacket(WvsContext.messagePacket(new StylishKillMessage(StylishKillMessage.StylishKillMessageType.Combo, sValue, Integer.parseInt(splitted[3]), Integer.parseInt(splitted[4]), Integer.parseInt(splitted[5]))));
                    break;
                case 9:
                    String sEliteMessage = "+20 NX";
                    WeatherEffectNotice pType = WeatherEffectNotice.RewardPoints;
                    pPlayer.getMap().broadcastPacket(WvsContext.OnWeatherEffectNotice(sEliteMessage, pType, 1000));
                    break;
                case 10:
                    pPlayer.dropMessage(5, "Aight yo, looks like you got like " + pPlayer.getNodeCount() + " nodes in total.");
                    break;
                case 11:
                    pPlayer.OnUserVMatrix();
                    break;
                case 12:
                    pPlayer.setPrimaryStack(500);
                    HayatoHandler.updateBladeStanceRequest(pPlayer, pPlayer.getPrimaryStack());
                    break;  
            }
            return 1;
        }
    }
    
    public static class Help extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().yellowMessage("----------------- PLAYER COMMANDS -----------------");
            c.getPlayer().yellowMessage("@sell <from slot> <to slot> : Sell specific item slots instantly.");
            c.getPlayer().yellowMessage("@support <message> : Send a message to availible staff members.");
            c.getPlayer().yellowMessage("@check : Displays account currency and character information.");
            c.getPlayer().yellowMessage("@dispose : Enables your character's actions when stuck.");
            c.getPlayer().yellowMessage("@event : Quick travel to the current event, if available.");
            c.getPlayer().yellowMessage("@job : Job advance, applicable if you have missed an advancement.");
            c.getPlayer().yellowMessage("@fm : Quick travel to the Free Market.");
            c.getPlayer().completeDispose();
            return 1;
        }
    }
    
    public static class Check extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().yellowMessage("----------------- CHARACTER & WALLET INFO -----------------");
            c.getPlayer().yellowMessage("NX Cash : " + c.getPlayer().getNX());
            c.getPlayer().yellowMessage("Donor Points : " + c.getPlayer().getDPoints());
            c.getPlayer().yellowMessage("Vote Points : " + c.getPlayer().getVPoints());
            c.getPlayer().yellowMessage("Event Points : " + c.getPlayer().getEPoints());
            c.getPlayer().yellowMessage("Current Prestige : " + c.getPlayer().getPrestige());
            c.getPlayer().yellowMessage("Prestige Memory : " + c.getPlayer().getPrestigeMemory());
            c.getPlayer().completeDispose();
            return 1;
        }
    }
    
    public static class FM extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            User pPlayer = c.getPlayer();
            for (int i : GameConstants.blockedMaps) {
                if (pPlayer.getMapId() == i) {
                    pPlayer.dropMessage(5, "You may not use this command here.");
                    return 0;
                }
            }
            if (pPlayer.getMapId() == ServerConstants.UNIVERSAL_START_MAP || pPlayer.getMapId() == ServerConstants.JAIL_MAP || pPlayer.getMapId() == 910000000) {
                pPlayer.dropMessage(5, "You may not use this command here.");
                return 0;
            }

            pPlayer.saveLocation(SavedLocationType.FREE_MARKET, pPlayer.getMap().getReturnMap().getId());
            MapleMap map = c.getChannelServer().getMapFactory().getMap(910000000);
            pPlayer.changeMap(map, map.getPortal(0));
            return 1;
        }
    }

    public static class Support extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            World.Broadcast.broadcastGMMessage(WvsContext.broadcastMsg(c.getPlayer().isGM() ? 6 : 5, "[Player Support] " + c.getPlayer().getName() + " : " + StringUtil.joinStringFrom(splitted, 1)));
            c.getPlayer().dropMessage(5, "Your message has been sent successfully.");
            return 1;
        }
    }
    
    public static class Event extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            if (c.getPlayer().getClient().getChannelServer().eventOn) {
                c.getPlayer().changeMap(c.getPlayer().getClient().getChannelServer().eventMap, 0);
                c.getPlayer().dropMessage(5, "Welcome to the " + ServerConstants.SERVER_NAME + " event, have fun!");
            } else {
                c.getPlayer().dropMessage(5, "Sorry, there is currently no event being hosted.");
            }
            return 1;
        }
    }

    public static class Sell extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            User player = c.getPlayer();
            if (splitted.length < 3 || player.hasBlockedInventory()) {
                c.getPlayer().dropMessage(6, "Syntax: @sell <eq/use/setup/etc> <start slot> <end slot>");
                return 0;
            } else {
                InventoryType type;
                if (splitted[1].equalsIgnoreCase("eq")) {
                    type = InventoryType.EQUIP;
                } else if (splitted[1].equalsIgnoreCase("use")) {
                    type = InventoryType.USE;
                } else if (splitted[1].equalsIgnoreCase("setup")) {
                    type = InventoryType.SETUP;
                } else if (splitted[1].equalsIgnoreCase("etc")) {
                    type = InventoryType.ETC;
                } else {
                    c.getPlayer().dropMessage(5, "Invalid Syntax. @sell <eq/use/setup/etc>");
                    return 0;
                }
                MapleInventory inv = c.getPlayer().getInventory(type);
                byte start = Byte.parseByte(splitted[2]);
                byte end = Byte.parseByte(splitted[3]);
                int totalMesosGained = 0;
                for (byte i = start; i <= end; i++) {
                    if (inv.getItem(i) != null) {
                        MapleItemInformationProvider iii = MapleItemInformationProvider.getInstance();
                        int itemPrice = (int) iii.getPrice(inv.getItem(i).getItemId());
                        totalMesosGained += itemPrice;
                        player.gainMeso(itemPrice < 0 ? 0 : itemPrice, true);
                        MapleInventoryManipulator.removeFromSlot(c, type, i, inv.getItem(i).getQuantity(), true);
                    }
                }
                c.getPlayer().dropMessage(5, "You sold items in slots " + start + " to " + end + ", and gained " + totalMesosGained + " mesos.");
            }
            return 1;
        }
    }

    public static class Save extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().dropMessage(-1, "Saved Successfully");
            c.getPlayer().saveToDB(false, false);
            c.getPlayer().completeDispose();
            return 1;
        }
    }
    
    public static class Monster extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            User pPlayer = c.getPlayer();
            pPlayer.yellowMessage("----------- NEARBY MONSTER DATA -----------");
            final List<MapleMapObject> aMobsToAggro = pPlayer.getMap().getAllMapObjects(MapleMapObjectType.MONSTER);
            for (MapleMapObject pObject : aMobsToAggro) {
                Mob pMob = (Mob) pObject;
                if (pMob.isAlive()) {
                    pPlayer.yellowMessage("Level : " + pMob.getStats().getLevel());
                    pPlayer.yellowMessage("Name : " + pMob.getName());
                    if (pPlayer.isIntern()) pPlayer.yellowMessage("Monster ID : " + pMob.getStats().getId());
                    pPlayer.yellowMessage("Maximum HP :" + pMob.getStats().getHp());
                    break;
                }
            }
            return 1;
        }
    }
    
    public static class EA extends Dispose {
    }
    
    public static class Dispose extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().dropMessage(5, "Your characters actions have been enabled.");
            c.getPlayer().completeDispose();
            return 1;
        }
    }
    
    /*
    *   Point Claim Method from Database
    *   @purpose Allow players to claim points from the database without needing to log out.
    *
    *   @author Mazen
    *   @author Poppy
     */
    public static class Vote extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            if (ServerConstants.USE_API) {
                // Voting will then be handled straight through the API.
                return 0;
            }
            
            int nAmount = 0;
            boolean bSuccess = false;

            try (Connection con = Database.GetConnection()) {
                
                try (PreparedStatement ps = con.prepareStatement("SELECT * FROM cms_votes WHERE accountid = " + c.getPlayer().getAccountID() + " AND collected = 0")) {

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            nAmount += rs.getInt(5);
                            PreparedStatement ps_2 = (PreparedStatement) con.prepareStatement("UPDATE cms_votes SET collected = 1 WHERE id = " + rs.getInt(1));
                            ps_2.executeUpdate();
                            ps_2.close();

                            bSuccess = true;
                        }
                        c.getPlayer().setVPoints(c.getPlayer().getVPoints() + nAmount);
                    }

                    if (bSuccess) {
                        c.getPlayer().dropMessage(6, "You have claimed " + nAmount + " vote points, and now have a total of " + c.getPlayer().getVPoints() + ".");
                    } else {
                        c.getPlayer().dropMessage(5, "Sorry, looks like you don't have any unclaimed vote points.");
                    }
                } catch (SQLException e) {
                    LogHelper.SQL.get().info("[Vote Claim] Error retrieving last character creation time\n", e);
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(PlayerCommand.class.getName()).log(Level.SEVERE, null, ex);
            }
            return 1;
        }
    }
}
