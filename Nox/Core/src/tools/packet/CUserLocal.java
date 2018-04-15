package tools.packet;

import java.util.List;
import service.SendPacketOpcode;
import net.OutPacket;

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
    public static OutPacket onSetDead(boolean animate) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SetDead.getValue());
        oPacket.EncodeBool(animate);

        return oPacket;
    }

    /**
     * Opens the UI on player's death void __thiscall CUserLocal::OnOpenUIOnDead(CUserLocal *this, CInPacket *iPacket)
     *
     * @param stats
     * @return
     */
    public static OutPacket openUIOnDead(List<DeadUIStats> stats) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.OpenUIOnDead.getValue());

        int mask = 0;
        for (DeadUIStats stat : stats) {
            mask |= 1 << stat.getNumShift();
        }
        final boolean isStarPlanet = false;
        final int reviveType = 0;

        oPacket.EncodeInt(mask); //  CInPacket::Decode4(iPacket);
        oPacket.EncodeBool(isStarPlanet); // v3 = (unsigned __int8)CInPacket::Decode1(iPacket);
        oPacket.EncodeInt(reviveType); //   nUIReviveType = CInPacket::Decode4(iPacket);

        /* int maskTest = 5 << DeadUIStats.OnDeadProtectForBuff.getNumShift();
        System.out.println(maskTest + " " + Integer.toBinaryString(maskTest));
        int maskTest2 = 4 << DeadUIStats.OnDeadProtectForBuff.getNumShift();
        System.out.println(maskTest2 + " " + Integer.toBinaryString(maskTest2));
        maskTest2 |= maskTest;
        System.out.println(maskTest2 + " " + Integer.toBinaryString(maskTest2));*/
        if (stats.contains(DeadUIStats.bOnDeadProtectExpMaplePoint)) {
            oPacket.EncodeInt(123); // hmm
        } else {
            // client will send a packet here.... 
            // COutPacket::COutPacket((COutPacket *)&bFail, 0x2B4);
            // int 11
        }

        return oPacket;
    }

    public static OutPacket updateDeathCount(int deathCount) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.DeathCountInfo.getValue());
        oPacket.EncodeInt(deathCount);

        return oPacket;
    }

    /**
     * This packet sends all the character information to the client if the for a player who has 'tagged' as a zero.
     *
     * @param chr
     * @return
     */
    public static OutPacket zeroTag(User chr) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ZERO_TAG.getValue());
        oPacket.EncodeInt(chr.getId());
        PacketHelper.addCharLook(oPacket, chr, false, chr.isZeroBetaState());

        return oPacket;
    }

    /**
     * Creates the Kinesis psychic energy shield effect
     *
     * @param skillLevel
     * @return
     */
    public static OutPacket kinesisPsychicEnergyShieldEffect(int skillLevel) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.KINESIS_PSYCHIC_ENERGY_SHIELD_EFFECT.getValue());
        oPacket.EncodeByte(skillLevel);

        return oPacket;
    }

    /**
     * Creates the character animation when acquiring rune stone
     *
     * @param runeSkillId
     * @return
     */
    public static OutPacket runeStoneAction(int runeSkillId) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.RuneStoneUseAck.getValue());

        boolean start = true;
        oPacket.EncodeBool(start);
        if (!start) {
            oPacket.EncodeInt(runeSkillId);//1932016); // itemid?
        }
        return oPacket;
    }
}
