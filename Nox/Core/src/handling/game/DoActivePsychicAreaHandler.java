/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.MapleClient;
import handling.jobs.Kinesis.KinesisHandler;
import net.InPacket;
import netty.ProcessPacket;
import server.maps.objects.MapleCharacter;
import tools.packet.JobPacket;

/**
 *
 * @author Mazen
 */
public class DoActivePsychicAreaHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        MapleCharacter pPlayer = c.getPlayer();
        int nKey = iPacket.DecodeInteger();
        
        c.write(JobPacket.Kinesis.OnDoActivePsychicArea(nKey, 1));
        pPlayer.getMap().broadcastMessage(c.getPlayer(), JobPacket.Kinesis.OnDoActivePsychicArea(nKey, 1), false);

        if (pPlayer.getPrimaryStack() > 0) KinesisHandler.psychicPointResult(pPlayer, pPlayer.getPrimaryStack() - 1);
        
        /*if (pPlayer.getPrimaryStack() > 0) {
            c.write(JobPacket.Kinesis.OnDoActivePsychicArea(nKey, 1));
            pPlayer.getMap().broadcastMessage(c.getPlayer(), JobPacket.Kinesis.OnDoActivePsychicArea(nKey, 1), false);
            
            KinesisHandler.psychicPointResult(pPlayer, pPlayer.getPrimaryStack() - 1);
        } else {
            c.write(JobPacket.Kinesis.OnReleasePsychicArea(c.getPlayer().getId(), nKey));
            c.getPlayer().getMap().broadcastMessage(c.getPlayer(), JobPacket.Kinesis.OnReleasePsychicArea(c.getPlayer().getId(), nKey), false);
        }*/
    }
    
    /*@Override
    public void Process(MapleClient c, InPacket iPacket) {
        MapleCharacter pPlayer = c.getPlayer();
        int nKey = iPacket.DecodeInteger();
        c.write(JobPacket.Kinesis.OnDoActivePsychicArea(nKey, 1));
        pPlayer.getMap().broadcastMessage(c.getPlayer(), JobPacket.Kinesis.OnDoActivePsychicArea(nKey, 1), false);
    }*/
}
