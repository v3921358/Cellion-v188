/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.ClientSocket;
import java.awt.Point;
import net.InPacket;
import net.ProcessPacket;

/**
 * DebuffPsychicArea
 * @author Mazen Massoud
 */
public class DebuffPsychicAreaHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        int nSkillID = iPacket.DecodeInt();
        int nSLV = iPacket.DecodeShort();
        int nPsychicAreaKey = iPacket.DecodeInt(); // 04 00 00 00
        byte bData = iPacket.DecodeByte(); // 01
        Point posStart = new Point(iPacket.DecodeInt(), iPacket.DecodeInt());
        int unk = iPacket.DecodeInt(); // 00 00 00 00
        // todo this isnt finished
    }

}
