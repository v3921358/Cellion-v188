/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.ClientSocket;
import client.jobs.Kinesis.KinesisHandler;
import net.InPacket;
import net.ProcessPacket;
import server.maps.objects.User;
import tools.packet.JobPacket;

/**
 * DoActivePsychicArea
 * @author Mazen Massoud
 */
public class DoActivePsychicAreaHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User pPlayer = c.getPlayer();
        int nKey = iPacket.DecodeInt();

        c.SendPacket(JobPacket.Kinesis.OnDoActivePsychicArea(nKey, 1));
        pPlayer.getMap().broadcastPacket(c.getPlayer(), JobPacket.Kinesis.OnDoActivePsychicArea(nKey, 1), false);

        if (pPlayer.getPrimaryStack() > 0) {
            KinesisHandler.psychicPointResult(pPlayer, pPlayer.getPrimaryStack() - 1);
        }

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
    public void Process(ClientSocket c, InPacket iPacket) {
        MapleCharacter pPlayer = c.getPlayer();
        int nKey = iPacket.DecodeInt();
        c.write(JobPacket.Kinesis.OnDoActivePsychicArea(nKey, 1));
        pPlayer.getMap().broadcastMessage(c.getPlayer(), JobPacket.Kinesis.OnDoActivePsychicArea(nKey, 1), false);
    }*/
}
