/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.Client;
import net.InPacket;
import net.OutPacket;
import net.ProcessPacket;
import server.maps.objects.User;
import service.SendPacketOpcode;

/**
 *
 * @author Mazen Massoud
 */
public final class KinesisAttackHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserEnterFieldPsychicInfo.getValue());
        oPacket.EncodeInt(c.getPlayer().getId());
        oPacket.EncodeByte(1);
        oPacket.EncodeInt(iPacket.DecodeInt());
        oPacket.EncodeInt((int) iPacket.DecodeLong());
        final int mobcount = iPacket.DecodeInt();
        oPacket.EncodeInt(mobcount);
        final int skillid = iPacket.DecodeInt();
        oPacket.EncodeInt(skillid);
        oPacket.EncodeShort(iPacket.DecodeShort());
        oPacket.EncodeInt((0xFFFFFFFF - mobcount) + 1);
        final int unknown_i = iPacket.DecodeInt();
        oPacket.EncodeInt(unknown_i != 0xFFFFFFFF ? unknown_i + 4000 : unknown_i);
        oPacket.EncodeByte(iPacket.DecodeByte());
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

        c.SendPacket(oPacket);
    }
}
