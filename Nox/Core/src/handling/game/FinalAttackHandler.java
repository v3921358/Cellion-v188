/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.Client;
import net.InPacket;
import net.ProcessPacket;
import server.maps.objects.User;
import tools.packet.CField;

/**
 *
 * @author Mazen Massoud
 */
public class FinalAttackHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        final User pPlayer = c.getPlayer();
        int nSkill = iPacket.DecodeInt();
        int pSkill = iPacket.DecodeInt(); // Might actually be nDamage.
        int nTarget = iPacket.DecodeInt();
        int tRequest = iPacket.DecodeInt();
        c.SendPacket(CField.finalAttackRequest(pPlayer, nSkill, pPlayer.getFinalAttackSkill(), 0, nTarget, tRequest));
    }
}
