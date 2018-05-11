package handling.game;

import client.ClientSocket;
import server.life.Mob;
import server.maps.objects.User;
import net.InPacket;
import server.maps.SharedMapResources.MapleNodeInfo;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class MobNodeHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();
        Mob mob_from = chr.getMap().getMonsterByOid(iPacket.DecodeInt());
        int newNode = iPacket.DecodeInt();
        int nodeSize = chr.getMap().getSharedMapResources().getNodes().size();

        if (mob_from != null && nodeSize > 0) {
            MapleNodeInfo mni = chr.getMap().getSharedMapResources().getNode(newNode);
            if (mni == null) {
                return;
            }
            if (mni.attr == 2) {
                switch (chr.getMapId() / 100) {
                    case 9211200:
                    case 9211201:
                    case 9211202:
                    case 9211203:
                    case 9211204:
                        chr.getMap().talkMonster("Please escort me carefully.", 5120035, mob_from.getObjectId());
                        break;
                    case 9320001:
                    case 9320002:
                    case 9320003:
                        chr.getMap().talkMonster("Please escort me carefully.", 5120051, mob_from.getObjectId());
                }
            }

            mob_from.setLastNode(newNode);
            if (chr.getMap().getSharedMapResources().isLastNode(newNode)) {
                switch (chr.getMapId() / 100) {
                    case 9211200:
                    case 9211201:
                    case 9211202:
                    case 9211203:
                    case 9211204:
                    case 9320001:
                    case 9320002:
                    case 9320003:
                        chr.getMap().broadcastMessage(WvsContext.broadcastMsg(5, "Proceed to the next stage."));
                        chr.getMap().removeMonster(mob_from);
                }
            }
        }
    }

}
