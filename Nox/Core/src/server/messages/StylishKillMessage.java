package server.messages;

import provider.data.HexTool;
import net.OutPacket;

/**
 * @author Steven
 *
 */
public class StylishKillMessage implements MessageInterface {

    private final StylishKillMessageType mode;
    private final long primaryValue;
    private final int secondaryValue;

    public StylishKillMessage(StylishKillMessageType mode, long primaryValue, int secondaryValue) {
        this.mode = mode;
        this.primaryValue = primaryValue;
        this.secondaryValue = secondaryValue;
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.Encode(_MessageOpcodesType.StylishKill.getType());
        oPacket.Encode(mode.getType());

        switch (mode) {
            case Combo:
                oPacket.EncodeInteger((int) primaryValue); // count
                oPacket.EncodeInteger(secondaryValue); // mob id
                break;
            case MultiKill:
                oPacket.EncodeLong(primaryValue); //nBonus
                oPacket.EncodeInteger(secondaryValue); //count
                break;
        }
    }

    public enum StylishKillMessageType {
        Combo(1), // How many monsters that were killed non stop [max 20% of the monster's EXP. max combo = 999]
        MultiKill(0), // How many mobs were killed within a single hit
        ;
        private final int type;

        private StylishKillMessageType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }
    }
}
