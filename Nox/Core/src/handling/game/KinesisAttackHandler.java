/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.MapleClient;
import net.InPacket;
import net.OutPacket;
import netty.ProcessPacket;
import server.maps.objects.MapleCharacter;
import service.SendPacketOpcode;

/**
 *
 * @author Mazen
 */
public final class KinesisAttackHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {

        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.UserEnterFieldPsychicInfo.getValue());
        oPacket.EncodeInteger(c.getPlayer().getId());
        oPacket.Encode(1);
        oPacket.EncodeInteger(iPacket.DecodeInteger());
        oPacket.EncodeInteger((int) iPacket.DecodeLong());
        final int mobcount = iPacket.DecodeInteger();
        oPacket.EncodeInteger(mobcount);
        final int skillid = iPacket.DecodeInteger();
        oPacket.EncodeInteger(skillid);
        oPacket.EncodeShort(iPacket.DecodeShort());
        oPacket.EncodeInteger((0xFFFFFFFF - mobcount) + 1);
        final int unknown_i = iPacket.DecodeInteger();
        oPacket.EncodeInteger(unknown_i != 0xFFFFFFFF ? unknown_i + 4000 : unknown_i);
        oPacket.Encode(iPacket.DecodeByte());
        final short unknown_si = iPacket.DecodeShort();
        oPacket.EncodeShort(unknown_si != 0xFFFF ? unknown_si : 0);
        final short unknown_sii = iPacket.DecodeShort();
        oPacket.EncodeShort(unknown_sii != 0xFFFF ? unknown_sii : 0);
        final short unknown_siii = iPacket.DecodeShort();
        oPacket.EncodeShort(unknown_siii != 0xFFFF ? unknown_siii : 0);
        oPacket.EncodePosition(iPacket.DecodePosition());
        oPacket.EncodePosition(iPacket.DecodePosition());

        /* PPoint Check */
        c.getPlayer().handlePsychicPoint(skillid);

        c.write(oPacket.ToPacket());
    }
}
