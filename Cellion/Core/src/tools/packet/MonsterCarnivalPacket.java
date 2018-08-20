package tools.packet;

import java.util.List;

import service.SendPacketOpcode;
import net.OutPacket;

import server.MapleCarnivalParty;
import server.maps.objects.User;

public class MonsterCarnivalPacket {

    public static OutPacket startMonsterCarnival(final User chr, final int enemyavailable, final int enemytotal) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MCarnivalEnter.getValue());
        final MapleCarnivalParty friendly = chr.getCarnivalParty();
        oPacket.EncodeByte(friendly.getTeam());
        oPacket.EncodeInt(chr.getAvailableCP());
        oPacket.EncodeInt(chr.getTotalCP());
        oPacket.EncodeInt(friendly.getAvailableCP()); // ??
        oPacket.EncodeInt(friendly.getTotalCP()); // ??
        oPacket.EncodeByte(0); // ??

        return oPacket;
    }

    public static OutPacket playerDiedMessage(String name, int lostCP, int team) { //CPQ

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MONSTER_CARNIVAL_DIED.getValue());
        oPacket.EncodeByte(team); //team
        oPacket.EncodeString(name);
        oPacket.EncodeByte(lostCP);

        return oPacket;
    }

    public static OutPacket playerLeaveMessage(boolean leader, String name, int team) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MONSTER_CARNIVAL_LEAVE.getValue());
        oPacket.EncodeByte(leader ? 7 : 0);
        oPacket.EncodeByte(team); // 0: red, 1: blue
        oPacket.EncodeString(name);

        return oPacket;
    }

    public static OutPacket CPUpdate(boolean party, int curCP, int totalCP, int team) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MONSTER_CARNIVAL_OBTAINED_CP.getValue());
        oPacket.EncodeInt(curCP);
        oPacket.EncodeInt(totalCP);

        return oPacket;
    }

    public static OutPacket showMCStats(int left, int right) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MONSTER_CARNIVAL_STATS.getValue());
        oPacket.EncodeInt(left);
        oPacket.EncodeInt(right);

        return oPacket;
    }

    public static OutPacket playerSummoned(String name, int tab, int number) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MONSTER_CARNIVAL_SUMMON.getValue());
        oPacket.EncodeByte(tab);
        oPacket.EncodeByte(number);
        oPacket.EncodeString(name);

        return oPacket;
    }

    public static OutPacket showMCResult(int mode) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MONSTER_CARNIVAL_RESULT.getValue());
        oPacket.EncodeByte(mode);

        return oPacket;
    }

    public static OutPacket showMCRanking(List<User> players) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MONSTER_CARNIVAL_RANKING.getValue());
        oPacket.EncodeShort(players.size());
        for (User i : players) {
            oPacket.EncodeInt(i.getId());
            oPacket.EncodeString(i.getName());
            oPacket.EncodeInt(10); // points
            oPacket.EncodeByte(0); // team
        }

        return oPacket;
    }

    public static OutPacket startCPQ(byte team, int usedcp, int totalcp) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MCarnivalEnter.getValue());
        oPacket.EncodeByte(0); //team
        oPacket.EncodeShort(0); //Obtained CP - Used CP
        oPacket.EncodeShort(0); //Total Obtained CP
        oPacket.EncodeShort(0); //Obtained CP - Used CP of the team
        oPacket.EncodeShort(0); //Total Obtained CP of the team
        oPacket.EncodeShort(0); //Obtained CP - Used CP of the team
        oPacket.EncodeShort(0); //Total Obtained CP of the team
        oPacket.EncodeShort(0); //Probably useless nexon shit
        oPacket.EncodeLong(0); //Probably useless nexon shit
        return oPacket;
    }

    public static OutPacket obtainCP() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MONSTER_CARNIVAL_OBTAINED_CP.getValue());
        oPacket.EncodeShort(0); //Obtained CP - Used CP
        oPacket.EncodeShort(0); //Total Obtained CP
        return oPacket;
    }

    /*
    public static OutPacket obtainPartyCP() {
        
        OutPacket oPacket = new OutPacket(SendPacketOpcode.MONSTER_CARNIVAL_PARTY_CP.getValue());
        oPacket.Encode(0); //Team where the points are given to.
        oPacket.EncodeShort(0); //Obtained CP - Used CP
        oPacket.EncodeShort(0); //Total Obtained CP
        return oPacket;
    }
     */
    public static OutPacket CPQSummon() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MONSTER_CARNIVAL_SUMMON.getValue());
        oPacket.EncodeByte(0); //Tab
        oPacket.EncodeByte(0); //Number of summon inside the tab
        oPacket.EncodeString(""); //Name of the player that summons
        return oPacket;
    }

    public static OutPacket CPQDied() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MONSTER_CARNIVAL_SUMMON.getValue());
        oPacket.EncodeByte(0); //Team
        oPacket.EncodeString(""); //Name of the player that died
        oPacket.EncodeByte(0); //Lost CP
        return oPacket;
    }

    /**
     * Sends a CPQ Message
     *
     * Possible values for <code>message</code>:<br>
     * 1: You don't have enough CP to continue. 2: You can no longer summon the Monster. 3: You can no longer summon the being. 4: This
     * being is already summoned. 5: This request has failed due to an unknown error.
     *
     * @param message Displays a message inside Carnival PQ
     * @return
     *
     */
    public static OutPacket CPQMessage(byte message) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MONSTER_CARNIVAL_MESSAGE.getValue());
        oPacket.EncodeByte(message); //Message
        return oPacket;
    }

    public static OutPacket leaveCPQ() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MONSTER_CARNIVAL_LEAVE.getValue());
        oPacket.EncodeByte(0); //Something?
        oPacket.EncodeByte(0); //Team
        oPacket.EncodeString(""); //Player name
        return oPacket;
    }
}
