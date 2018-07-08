/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.ClientSocket;
import net.InPacket;
import net.ProcessPacket;
import tools.packet.CField;

/**
 *
 * @author Mazen
 */
public final class SkillEffectCancelHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        int nSkillID = iPacket.DecodeInt();
        c.getPlayer().getMap().broadcastPacket(c.getPlayer(), CField.skillCancel(c.getPlayer(), nSkillID), false);
    }
}
