/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.Client;
import java.awt.Point;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author Mazen Massoud
 */
public class DebuffPsychicAreaHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        int nSkillID = iPacket.DecodeInt();
        int nSLV = iPacket.DecodeShort();
        int nPsychicAreaKey = iPacket.DecodeInt(); // 04 00 00 00
        byte bData = iPacket.DecodeByte(); // 01
        Point posStart = new Point(iPacket.DecodeInt(), iPacket.DecodeInt());
        int unk = iPacket.DecodeInt(); // 00 00 00 00
        // todo this isnt finished
    }

}
