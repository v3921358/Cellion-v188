package server.messages;

import client.Client;
import net.OutPacket;

/**
 * @author Steven
 *
 */
public class MesoMessage implements MessageInterface {

    private final int income;
    private final int action;
    private final String accountName;

    public MesoMessage(int income, int action, Client c) {
        this.income = income;
        this.action = action;
        accountName = c.getAccountName();
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.EncodeByte(_MessageOpcodesType.Meso.getType());
        oPacket.EncodeInt(income);
        oPacket.EncodeInt(action);
        if (action != -1 && action != 24) {
            oPacket.EncodeString(accountName);
        }

    }

}
