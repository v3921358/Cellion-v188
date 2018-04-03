/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.MapleClient;
import client.jobs.Kinesis;
import client.jobs.Kinesis.KinesisHandler;
import net.InPacket;
import net.OutPacket;
import netty.ProcessPacket;
import server.life.MapleMonster;
import server.maps.objects.User;
import service.SendPacketOpcode;

/**
 *
 * @author Mazen
 */
public final class KinesisPsychicHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        //KinesisHandler.handlePsychicPoint(c.getPlayer(), 0);
        /*OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.UserCreatePsychicArea.getValue());
        // First AttackInfo Start
        oPacket.EncodeInteger(c.getPlayer().getId());
        oPacket.Encode(1);
        final int skillid = iPacket.DecodeInteger();
        oPacket.EncodeInteger(skillid);
        oPacket.EncodeShort(iPacket.DecodeShort());
        oPacket.EncodeInteger(iPacket.DecodeInteger());
        oPacket.EncodeInteger(iPacket.DecodeInteger());
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
                oPacket.EncodeInteger(iPacket.DecodeInteger());
            } else {
                break;
            }
            iPacket.Skip(4);
            oPacket.EncodeInteger((i) + 1);
            final int monsterid = iPacket.DecodeInteger();
            oPacket.EncodeInteger(monsterid); //
            oPacket.EncodeShort(iPacket.DecodeShort());
            if (monsterid != 0) {
                target = c.getPlayer().getMap().getMonsterByOid(monsterid);
            }
            iPacket.Skip(2);
            oPacket.EncodeInteger(monsterid != 0 ? (int) target.getHp() : 100);
            oPacket.EncodeInteger(monsterid != 0 ? (int) target.getHp() : 100);
            oPacket.Encode(iPacket.DecodeByte());
            oPacket.EncodePosition(iPacket.DecodePosition());
            oPacket.EncodePosition(iPacket.DecodePosition());
            oPacket.EncodePosition(iPacket.DecodePosition());
            oPacket.EncodePosition(iPacket.DecodePosition());
            i++;
        }
        // PPoint Check
        c.getPlayer().handlePsychicPoint(skillid);

        c.write(oPacket.ToPacket());*/
    }

}
