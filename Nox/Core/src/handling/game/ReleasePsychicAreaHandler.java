/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.MapleClient;
import net.InPacket;
import netty.ProcessPacket;
import tools.packet.JobPacket;

/**
 *
 * @author Mazen
 */
public class ReleasePsychicAreaHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        int nPsychicAreaKey = iPacket.DecodeInteger();

        c.write(JobPacket.Kinesis.OnReleasePsychicArea(c.getPlayer().getId(), nPsychicAreaKey));
        c.getPlayer().getMap().broadcastMessage(c.getPlayer(), JobPacket.Kinesis.OnReleasePsychicArea(c.getPlayer().getId(), nPsychicAreaKey), false);
    }

}
