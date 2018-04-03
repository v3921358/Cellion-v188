package tools.packet;

import java.util.List;
import service.SendPacketOpcode;
import net.OutPacket;
import net.Packet;
import server.maps.objects.User;

/**
 *
 * @author
 */
public class CUserLocal {

    public static enum DeadUIStats {
        bOnDeadRevive(0),
        OnDeadProtectForBuff(1),
        bOnDeadOpenExpProtector(2), // custom naming
        bOnDeadProtectBuffMaplePoint(3), // custom naming
        bOnDeadProtectExpMaplePoint(4),;

        private final int shift;

        private DeadUIStats(int shift) {
            this.shift = shift;
        }

        /**
         * Number of shifts to the right the client uses to check for this bit.
         *
         * @return int shifts
         */
        public int getNumShift() {
            return shift;
        }
    }

    /**
     * 1 header packet before OPEN_UI_ONDEAD. Creates some tremble/glass scatter animation if activated zero intro? Arkarium? beta get
     * freed?
     *
     * @param animate
     * @return
     */
    public static Packet onSetDead(boolean animate) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.SetDead.getValue());
        oPacket.Encode(animate);

        return oPacket.ToPacket();
    }

    /**
     * Opens the UI on player's death void __thiscall CUserLocal::OnOpenUIOnDead(CUserLocal *this, CInPacket *iPacket)
     *
     * @param stats
     * @return
     */
    public static Packet openUIOnDead(List<DeadUIStats> stats) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.OpenUIOnDead.getValue());

        int mask = 0;
        for (DeadUIStats stat : stats) {
            mask |= 1 << stat.getNumShift();
        }
        final boolean isStarPlanet = false;
        final int reviveType = 0;

        oPacket.EncodeInteger(mask); //  CInPacket::Decode4(iPacket);
        oPacket.Encode(isStarPlanet); // v3 = (unsigned __int8)CInPacket::Decode1(iPacket);
        oPacket.EncodeInteger(reviveType); //   nUIReviveType = CInPacket::Decode4(iPacket);

        /* int maskTest = 5 << DeadUIStats.OnDeadProtectForBuff.getNumShift();
        System.out.println(maskTest + " " + Integer.toBinaryString(maskTest));
        int maskTest2 = 4 << DeadUIStats.OnDeadProtectForBuff.getNumShift();
        System.out.println(maskTest2 + " " + Integer.toBinaryString(maskTest2));
        maskTest2 |= maskTest;
        System.out.println(maskTest2 + " " + Integer.toBinaryString(maskTest2));*/
        if (stats.contains(DeadUIStats.bOnDeadProtectExpMaplePoint)) {
            oPacket.EncodeInteger(123); // hmm
        } else {
            // client will send a packet here.... 
            // COutPacket::COutPacket((COutPacket *)&bFail, 0x2B4);
            // int 11
        }

        return oPacket.ToPacket();
    }

    public static Packet updateDeathCount(int deathCount) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.DeathCountInfo.getValue());
        oPacket.EncodeInteger(deathCount);

        return oPacket.ToPacket();
    }

    /**
     * This packet sends all the character information to the client if the for a player who has 'tagged' as a zero.
     *
     * @param chr
     * @return
     */
    public static Packet zeroTag(User chr) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ZERO_TAG.getValue());
        oPacket.EncodeInteger(chr.getId());
        PacketHelper.addCharLook(oPacket, chr, false, chr.isZeroBetaState());

        return oPacket.ToPacket();
    }

    /**
     * Creates the Kinesis psychic energy shield effect
     *
     * @param skillLevel
     * @return
     */
    public static Packet kinesisPsychicEnergyShieldEffect(int skillLevel) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.KINESIS_PSYCHIC_ENERGY_SHIELD_EFFECT.getValue());
        oPacket.Encode(skillLevel);

        return oPacket.ToPacket();
    }

    /**
     * Creates the character animation when acquiring rune stone
     *
     * @param runeSkillId
     * @return
     */
    public static Packet runeStoneAction(int runeSkillId) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.RuneStoneUseAck.getValue());

        boolean start = true;
        oPacket.Encode(start);
        if (!start) {
            oPacket.EncodeInteger(runeSkillId);//1932016); // itemid?
        }
        return oPacket.ToPacket();
    }
}
