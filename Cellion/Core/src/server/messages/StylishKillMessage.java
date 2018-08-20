package server.messages;

import enums.MessageOpcodesType;
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
    private final StylishKillMessageStyle style;

    public StylishKillMessage(StylishKillMessageType mode, long primaryValue, int secondaryValue, StylishKillMessageStyle style) {
        this.mode = mode;
        this.primaryValue = primaryValue;
        this.secondaryValue = secondaryValue;
        this.style = style;
    }
    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.EncodeByte(MessageOpcodesType.StylishKill.getType());
        oPacket.EncodeByte(mode.getType());
        switch (mode) {
            case Combo:
                oPacket.EncodeInt((int) primaryValue); // count
                oPacket.EncodeInt(secondaryValue); // mob id
                oPacket.EncodeInt(style.getStyle());
                oPacket.EncodeInt(0);
                break;
            case MultiKill:
                oPacket.EncodeLong(primaryValue); //nBonus (bonus EXP)
                oPacket.EncodeInt(0); 
                oPacket.EncodeInt(secondaryValue); // count
                oPacket.EncodeInt(style.getStyle()); // theme
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

    public enum StylishKillMessageStyle {
        Normal(0),
        Halloween(1), // Used for Halloween events.
        Party(2), // Used for KMS' 5000th day-versary.
        Magpie(3), // Used for Magpie New Year event.
        SweetHoney(4), // Used for Sugar Rush Honey Flow event.
        ;
        private final int style;

        private StylishKillMessageStyle(int style) {
            this.style = style;
        }

        public int getStyle() {
            return style;
        }
    }
}
