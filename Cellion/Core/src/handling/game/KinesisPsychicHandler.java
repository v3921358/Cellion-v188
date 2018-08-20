/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.ClientSocket;
import client.jobs.Kinesis;
import client.jobs.Kinesis.KinesisHandler;
import net.InPacket;
import net.OutPacket;
import net.ProcessPacket;
import server.life.Mob;
import server.maps.objects.User;
import service.SendPacketOpcode;

/**
 *
 * @author Mazen Massoud
 */
public final class KinesisPsychicHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        //KinesisHandler.handlePsychicPoint(c.getPlayer(), 0);
        /*
        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserCreatePsychicArea.getValue());
        // First AttackInfo Start
        oPacket.EncodeInt(c.getPlayer().getId());
        oPacket.Encode(1);
        final int skillid = iPacket.DecodeInt();
        oPacket.EncodeInt(skillid);
        oPacket.EncodeShort(iPacket.DecodeShort());
        oPacket.EncodeInt(iPacket.DecodeInt());
        oPacket.EncodeInt(iPacket.DecodeInt());
        // First AttackInfo End
        int i = 0;
        int point = 0;
        boolean end = false;
        MapleMonster target = null;
        while (true) {
            end = (iPacket.DecodeByte() <= 0);
            oPacket.Encode(!end ? 1 : 0);
            if (!end) {
                oPacket.Encode(!end ? 1 : 0);
                oPacket.EncodeInt(iPacket.DecodeInt());
            } else {
                break;
            }
            iPacket.Skip(4);
            oPacket.EncodeInt((i) + 1);
            final int monsterid = iPacket.DecodeInt();
            oPacket.EncodeInt(monsterid); //
            oPacket.EncodeShort(iPacket.DecodeShort());
            if (monsterid != 0) {
                target = c.getPlayer().getMap().getMonsterByOid(monsterid);
            }
            iPacket.Skip(2);
            oPacket.EncodeInt(monsterid != 0 ? (int) target.getHp() : 100);
            oPacket.EncodeInt(monsterid != 0 ? (int) target.getHp() : 100);
            oPacket.Encode(iPacket.DecodeByte());
            oPacket.EncodePosition(iPacket.DecodePosition());
            oPacket.EncodePosition(iPacket.DecodePosition());
            oPacket.EncodePosition(iPacket.DecodePosition());
            oPacket.EncodePosition(iPacket.DecodePosition());
            i++;
        }
        // PPoint Check
        c.getPlayer().handlePsychicPoint(skillid);

        c.write(oPacket);*/
    }

}
