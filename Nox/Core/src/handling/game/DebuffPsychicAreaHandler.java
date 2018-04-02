/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.MapleClient;
import java.awt.Point;
import net.InPacket;
import netty.ProcessPacket;

/**
 *
 * @author Mazen
 */
public class DebuffPsychicAreaHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        int nSkillID = iPacket.DecodeInteger();
        int nSLV = iPacket.DecodeShort();
        int nPsychicAreaKey = iPacket.DecodeInteger(); // 04 00 00 00
        byte bData = iPacket.DecodeByte(); // 01
        Point posStart = new Point(iPacket.DecodeInteger(), iPacket.DecodeInteger());
        int unk = iPacket.DecodeInteger(); // 00 00 00 00
        // todo this isnt finished
    }

}
