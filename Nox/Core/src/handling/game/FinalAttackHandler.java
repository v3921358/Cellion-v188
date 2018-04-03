/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.MapleClient;
import net.InPacket;
import netty.ProcessPacket;
import server.maps.objects.User;
import tools.packet.CField;

/**
 *
 * @author Mazen
 */
public class FinalAttackHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final User pPlayer = c.getPlayer();
        int nSkill = iPacket.DecodeInteger();
        int pSkill = iPacket.DecodeInteger(); // Might actually be nDamage.
        int nTarget = iPacket.DecodeInteger();
        int tRequest = iPacket.DecodeInteger();
        c.write(CField.finalAttackRequest(pPlayer, nSkill, pPlayer.getFinalAttackSkill(), 0, nTarget, tRequest));
    }
}
