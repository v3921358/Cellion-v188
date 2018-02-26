package tools.packet;

import java.util.List;

import service.SendPacketOpcode;
import net.OutPacket;
import net.Packet;
import server.MapleCarnivalParty;
import server.maps.objects.MapleCharacter;

public class MonsterCarnivalPacket {

    public static Packet startMonsterCarnival(final MapleCharacter chr, final int enemyavailable, final int enemytotal) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MCarnivalEnter.getValue());
        final MapleCarnivalParty friendly = chr.getCarnivalParty();
        oPacket.Encode(friendly.getTeam());
        oPacket.EncodeInteger(chr.getAvailableCP());
        oPacket.EncodeInteger(chr.getTotalCP());
        oPacket.EncodeInteger(friendly.getAvailableCP()); // ??
        oPacket.EncodeInteger(friendly.getTotalCP()); // ??
        oPacket.Encode(0); // ??

        return oPacket.ToPacket();
    }

    public static Packet playerDiedMessage(String name, int lostCP, int team) { //CPQ
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MONSTER_CARNIVAL_DIED.getValue());
        oPacket.Encode(team); //team
        oPacket.EncodeString(name);
        oPacket.Encode(lostCP);

        return oPacket.ToPacket();
    }

    public static Packet playerLeaveMessage(boolean leader, String name, int team) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MONSTER_CARNIVAL_LEAVE.getValue());
        oPacket.Encode(leader ? 7 : 0);
        oPacket.Encode(team); // 0: red, 1: blue
        oPacket.EncodeString(name);

        return oPacket.ToPacket();
    }

    public static Packet CPUpdate(boolean party, int curCP, int totalCP, int team) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MONSTER_CARNIVAL_OBTAINED_CP.getValue());
        oPacket.EncodeInteger(curCP);
        oPacket.EncodeInteger(totalCP);

        return oPacket.ToPacket();
    }

    public static Packet showMCStats(int left, int right) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MONSTER_CARNIVAL_STATS.getValue());
        oPacket.EncodeInteger(left);
        oPacket.EncodeInteger(right);

        return oPacket.ToPacket();
    }

    public static Packet playerSummoned(String name, int tab, int number) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MONSTER_CARNIVAL_SUMMON.getValue());
        oPacket.Encode(tab);
        oPacket.Encode(number);
        oPacket.EncodeString(name);

        return oPacket.ToPacket();
    }

    public static Packet showMCResult(int mode) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MONSTER_CARNIVAL_RESULT.getValue());
        oPacket.Encode(mode);

        return oPacket.ToPacket();
    }

    public static Packet showMCRanking(List<MapleCharacter> players) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MONSTER_CARNIVAL_RANKING.getValue());
        oPacket.EncodeShort(players.size());
        for (MapleCharacter i : players) {
            oPacket.EncodeInteger(i.getId());
            oPacket.EncodeString(i.getName());
            oPacket.EncodeInteger(10); // points
            oPacket.Encode(0); // team
        }

        return oPacket.ToPacket();
    }

    public static Packet startCPQ(byte team, int usedcp, int totalcp) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.MCarnivalEnter.getValue());
        oPacket.Encode(0); //team
        oPacket.EncodeShort(0); //Obtained CP - Used CP
        oPacket.EncodeShort(0); //Total Obtained CP
        oPacket.EncodeShort(0); //Obtained CP - Used CP of the team
        oPacket.EncodeShort(0); //Total Obtained CP of the team
        oPacket.EncodeShort(0); //Obtained CP - Used CP of the team
        oPacket.EncodeShort(0); //Total Obtained CP of the team
        oPacket.EncodeShort(0); //Probably useless nexon shit
        oPacket.EncodeLong(0); //Probably useless nexon shit
        return oPacket.ToPacket();
    }

    public static Packet obtainCP() {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.MONSTER_CARNIVAL_OBTAINED_CP.getValue());
        oPacket.EncodeShort(0); //Obtained CP - Used CP
        oPacket.EncodeShort(0); //Total Obtained CP
        return oPacket.ToPacket();
    }

    public static Packet obtainPartyCP() {
        OutPacket oPacket = new OutPacket(80);
        //oPacket.encodeShort(SendPacketOpcode.MONSTER_CARNIVAL_PARTY_CP.getValue());
        oPacket.Encode(0); //Team where the points are given to.
        oPacket.EncodeShort(0); //Obtained CP - Used CP
        oPacket.EncodeShort(0); //Total Obtained CP
        return oPacket.ToPacket();
    }

    public static Packet CPQSummon() {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.MONSTER_CARNIVAL_SUMMON.getValue());
        oPacket.Encode(0); //Tab
        oPacket.Encode(0); //Number of summon inside the tab
        oPacket.EncodeString(""); //Name of the player that summons
        return oPacket.ToPacket();
    }

    public static Packet CPQDied() {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.MONSTER_CARNIVAL_SUMMON.getValue());
        oPacket.Encode(0); //Team
        oPacket.EncodeString(""); //Name of the player that died
        oPacket.Encode(0); //Lost CP
        return oPacket.ToPacket();
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
    public static Packet CPQMessage(byte message) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.MONSTER_CARNIVAL_MESSAGE.getValue());
        oPacket.Encode(message); //Message
        return oPacket.ToPacket();
    }

    public static Packet leaveCPQ() {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.MONSTER_CARNIVAL_LEAVE.getValue());
        oPacket.Encode(0); //Something?
        oPacket.Encode(0); //Team
        oPacket.EncodeString(""); //Player name
        return oPacket.ToPacket();
    }
}
