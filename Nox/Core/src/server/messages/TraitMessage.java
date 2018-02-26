package server.messages;

import client.MapleStat;
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
        oPacket.Encode(_MessageOpcodesType.Trait.getType());//MessageType
        oPacket.EncodeLong(flag);
        if ((flag & MapleStat.CHARISMA.getValue()) != 0) {
            oPacket.EncodeInteger(amount);
        }
        if ((flag & MapleStat.INSIGHT.getValue()) != 0) {
            oPacket.EncodeInteger(amount);
        }
        if ((flag & MapleStat.WILL.getValue()) != 0) {
            oPacket.EncodeInteger(amount);
        }
        if ((flag & MapleStat.CRAFT.getValue()) != 0) {
            oPacket.EncodeInteger(amount);
        }
        if ((flag & MapleStat.SENSE.getValue()) != 0) {
            oPacket.EncodeInteger(amount);
        }
        if ((flag & MapleStat.CHARM.getValue()) != 0) {
            oPacket.EncodeInteger(amount);
        }
    }

}
