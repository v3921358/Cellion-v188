/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.farm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import client.MapleClient;
import client.MapleClient.MapleClientLoginState;
import constants.WorldConstants.WorldOption;
import handling.world.CharacterTransfer;
import handling.world.World;
import service.ChannelServer;
import service.FarmServer;
import service.LoginServer;
import server.farm.MapleFarm;
import server.maps.objects.MapleCharacter;
import tools.Pair;
import net.InPacket;
import tools.packet.CField;
import tools.packet.FarmPacket;

/**
 *
 * @author Itzik
 */
public class FarmOperation {

    public static void EnterFarm(final CharacterTransfer transfer, final MapleClient c) {
        if (transfer == null) {
            c.close();
            return;
        }
        MapleCharacter chr = MapleCharacter.reconstructCharacter(transfer, c, false);

        c.setPlayer(chr);
        c.setAccID(chr.getAccountID());

        if (!c.CheckIPAddress()) { // Remote hack
            c.close();
            return;
        }

        final MapleClientLoginState state = c.getLoginState();
        boolean allowLogin = false;

        switch (state) {
            case LOGIN_SERVER_TRANSITION:
            case CHANGE_CHANNEL:
                if (!World.isCharacterListConnected(c.loadCharacterNames(c.getWorld()))) {
                    allowLogin = true;
                }
                break;
        }

        if (!allowLogin) {
            c.setPlayer(null);
            c.close();
            return;
        }
        c.updateLoginState(MapleClientLoginState.LOGIN_LOGGEDIN, c.getSessionIPAddress());

        FarmServer.getPlayerStorage().registerPlayer(chr);

        c.write(FarmPacket.updateMonster(new LinkedList<>()));
        c.write(FarmPacket.enterFarm(c));
        c.write(FarmPacket.farmQuestData(new LinkedList<>(), new LinkedList<>()));
        c.write(FarmPacket.updateMonsterInfo(new LinkedList<>()));
        c.write(FarmPacket.updateAesthetic(c.getFarm().getAestheticPoints()));
        c.write(FarmPacket.spawnFarmMonster1());
        c.write(FarmPacket.farmPacket1());
        c.write(FarmPacket.updateFarmFriends(new LinkedList<>()));
        c.write(FarmPacket.updateFarmInfo(c));
        //c.write(CField.createPacketFromHexString("19 72 1E 02 00 00 00 00 00 00 00 00 00 00 00 00 0B 00 43 72 65 61 74 69 6E 67 2E 2E 2E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 00 00 00 00 00 00 00 00 01 00 00 00 00 0B 00 43 72 65 61 74 69 6E 67 2E 2E 2E 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 02 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 00 FF FF FF FF 00"));
        c.write(FarmPacket.updateQuestInfo(21002, (byte) 1, ""));
        SimpleDateFormat sdfGMT = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
        sdfGMT.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        String timeStr = sdfGMT.format(Calendar.getInstance().getTime()).replaceAll("-", "");
        c.write(FarmPacket.updateQuestInfo(21001, (byte) 1, timeStr));
        c.write(FarmPacket.updateQuestInfo(21003, (byte) 1, "30"));
        c.write(FarmPacket.updateUserFarmInfo(chr, false));
        List<Pair<MapleFarm, Integer>> ranking = new LinkedList<>();
        ranking.add(new Pair<>(MapleFarm.getDefault(1, c, "Mazen"), 1));
        ranking.add(new Pair<>(MapleFarm.getDefault(1, c, "Novak"), 1));
        ranking.add(new Pair<>(MapleFarm.getDefault(1, c, "MrPie"), 1));
        c.write(FarmPacket.sendFarmRanking(chr, ranking));
        c.write(FarmPacket.updateAvatar(new Pair<>(WorldOption.Scania, chr), null, false));
        if (c.getFarm().getName().equals("Creating...")) { //todo put it on farm update handler
            c.write(FarmPacket.updateQuestInfo(1111, (byte) 0, "A1/"));
            c.write(FarmPacket.updateQuestInfo(2001, (byte) 0, "A1/"));
        }
    }

    public static void LeaveFarm(final InPacket iPacket, final MapleClient c, final MapleCharacter chr) {
        FarmServer.getPlayerStorage().deregisterPlayer(chr);

        c.updateLoginState(MapleClientLoginState.LOGIN_SERVER_TRANSITION, c.getSessionIPAddress());

        try {
            World.changeChannelData(new CharacterTransfer(chr), chr.getId(), c.getChannel());
            c.write(CField.getChannelChange(c, Integer.parseInt(ChannelServer.getInstance(c.getChannel()).getIP().split(":")[1])));
        } finally {
            final String s = c.getSessionIPAddress();
            LoginServer.addIPAuth(s.substring(s.indexOf('/') + 1, s.length()));
            chr.saveToDB(false, true);
            c.setPlayer(null);
            c.setReceiving(false);
            c.close();
        }
    }
}
