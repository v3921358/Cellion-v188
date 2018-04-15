/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.messages;

import net.OutPacket;

/**
 * 11:26:06.972 00661741	OPCODE	0059	(89) 11:26:06.972 01A3BF0F	BYTE	26	(38) 11:26:06.972 01A0B3F2	BYTE	00	(0) 11:26:06.972 01A0B46D	WORD
 * 0000	(0) 11:26:06.972 01A0B481	WORD	0000	(0) 11:26:06.972 01A0B495	BYTE	00	(0) --- 59 00 26 00 00 00 00 00 00 59 00 26 00 0059 26 00 0000
 * 0000 00 0059 26 00 "Potential Scrolls, Shielding Wards, and other special scrolls have no effect with Star Force Enhancement. Do you
 * still want to enhance it?"
 *
 * @author Lloyd Korn
 */
public class SpecialScrollWarningMessage implements MessageInterface {

    public SpecialScrollWarningMessage() {
    }

    /* (non-Javadoc)
	 * @see server.messages.MessageInterface#messagePacket(tools.data.OutPacket)
     */
    @Override
    public void messagePacket(OutPacket oPacket) {
        oPacket.EncodeByte(_MessageOpcodesType.SpecialScrollWarningPopup.getType());
        oPacket.EncodeByte(0); // these may be slotid or something
        oPacket.EncodeShort(0);
        oPacket.EncodeShort(0);
        oPacket.EncodeByte(0);
    }

}
