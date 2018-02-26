/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.MapleClient;
import client.SkillMacro;
import net.InPacket;
import net.OutPacket;
import netty.ProcessPacket;
import service.SendPacketOpcode;

/**
 *
 * @author Mazen
 */
public class SaveDamageSkinRequest implements ProcessPacket<MapleClient> {// like this?

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override // we need to decode the damage skin from the inpacket, to receive the ID
    public void Process(MapleClient c, InPacket iPacket) { // I know, there is probably something similar we can look at, yeh tbh only thing left is
        OutPacket oPacket = new OutPacket(80);
        int num = iPacket.DecodeByte();

        for (int i = 0; i < num; i++) {
            //  String name = iPacket.DecodeString();
            int dmgskin = iPacket.DecodeInteger();
            // int skill1 = iPacket.DecodeInteger();
            // int skill2 = iPacket.DecodeInteger();
            // int skill3 = iPacket.DecodeInteger();

            //SkillMacro macro = new SkillMacro(skill1, skill2, skill3, name, shout, i);
            //c.getPlayer().updateMacros(i, macro);
        }
    }
}
