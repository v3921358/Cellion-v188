/*
 * Cellion Development
 */
package handling.game;

import client.ClientSocket;
import server.maps.objects.User;
import net.InPacket;
import net.ProcessPacket;

/**
 * Link Skill Handler
 * @author Mazen Massoud
 */
public final class LinkedSkillRequest implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final User pPlayer = c.getPlayer();
        int nSourceSkillID = iPacket.DecodeInt();
        int nLinkedSkill = 0;
        int dwCharID = iPacket.DecodeInt();
        int dwAccID = c.getPlayer().getAccountID();

        pPlayer.yellowMessage("Tell Mazen the following information please:");
        pPlayer.yellowMessage("nSrc : " + nSourceSkillID);
        pPlayer.yellowMessage("dwChar : " + dwCharID);
        pPlayer.yellowMessage("dwAcc : " + dwAccID);
        
        switch (nSourceSkillID) {
            case 110:
                nLinkedSkill = 80000000;
                break;
            case 20021110:
                nLinkedSkill = 80001040;
                break;
            case 20030204:
                nLinkedSkill = 80000002;
                break;
            case 20040218:
                nLinkedSkill = 80000005;
                break;
            case 30010112:
                nLinkedSkill = 80000001;
                break;
            case 50001214:
                nLinkedSkill = 80001140;
                break;
            case 60000222:
                nLinkedSkill = 80000006;
                break;
            case 60011219:
                nLinkedSkill = 80001155;
                break;
        }
        pPlayer.giveLinkSkill(pPlayer, nLinkedSkill, dwAccID, dwCharID, pPlayer.getSkillLevel(nSourceSkillID), pPlayer.getMasterLevel(nSourceSkillID));
    }

}
