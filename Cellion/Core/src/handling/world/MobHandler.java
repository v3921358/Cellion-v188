package handling.world;

import enums.InventoryType;
import server.MapleInventoryManipulator;
import server.life.Mob;
import server.maps.MapleMap;
import server.maps.objects.User;
import tools.packet.WvsContext;

public class MobHandler {

    public static final void checkShammos(User chr, Mob mobto, MapleMap map) {
        MapleMap mapp;
        if ((!mobto.isAlive()) && (mobto.getStats().isEscort())) {
            for (User chrz : map.getCharacters()) {
                if ((chrz.getParty() != null) && (chrz.getParty().getLeader().getId() == chrz.getId())) {
                    if (!chrz.haveItem(2022698)) {
                        break;
                    }
                    MapleInventoryManipulator.removeById(chrz.getClient(), InventoryType.USE, 2022698, 1, false, true);
                    mobto.heal((int) mobto.getMobMaxHp(), mobto.getMobMaxMp(), true);
                    return;
                }

            }

            map.broadcastPacket(WvsContext.broadcastMsg(6, "Your party has failed to protect the monster."));
            mapp = chr.getMap().getForcedReturnMap();
            for (User chrz : map.getCharacters()) {
                chrz.changeMap(mapp, mapp.getPortal(0));
            }
        } else if ((mobto.getStats().isEscort()) && (mobto.getEventInstance() != null)) {
            mobto.getEventInstance().setProperty("HP", String.valueOf(mobto.getHp()));
        }
    }
}
