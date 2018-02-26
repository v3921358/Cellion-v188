package server.messages;

import net.OutPacket;

/**
 * @author Steven
 *
 */
public class GuildPointMessage implements MessageInterface {

    private final int guildPoint;

    public GuildPointMessage(int guildPoint) {
        this.guildPoint = guildPoint;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.Encode(_MessageOpcodesType.GuildPoint.getType());
        oPacket.EncodeInteger(guildPoint);
    }

}
