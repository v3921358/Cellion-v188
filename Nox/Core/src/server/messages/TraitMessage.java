package server.messages;

import client.Stat;
import net.OutPacket;

/**
 * @author Steven
 *
 */
public class TraitMessage implements MessageInterface {

    private long flag = 0;
    private final int amount;

    public TraitMessage(long flag, int amount) {
        this.flag = flag;
        this.amount = amount;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.EncodeByte(_MessageOpcodesType.Trait.getType());//MessageType
        oPacket.EncodeLong(flag);
        if ((flag & Stat.CharismaEXP.getValue()) != 0) {
            oPacket.EncodeInt(amount);
        }
        if ((flag & Stat.InsightEXP.getValue()) != 0) {
            oPacket.EncodeInt(amount);
        }
        if ((flag & Stat.WillEXP.getValue()) != 0) {
            oPacket.EncodeInt(amount);
        }
        if ((flag & Stat.CraftEXP.getValue()) != 0) {
            oPacket.EncodeInt(amount);
        }
        if ((flag & Stat.SenseEXP.getValue()) != 0) {
            oPacket.EncodeInt(amount);
        }
        if ((flag & Stat.CharmEXP.getValue()) != 0) {
            oPacket.EncodeInt(amount);
        }
    }

}
