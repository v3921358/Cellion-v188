package handling.game;

import java.util.ArrayList;
import java.util.List;

import client.MapleClient;
import server.MapleItemInformationProvider;
import server.life.MapleMonsterInformationProvider;
import server.life.MonsterDropEntry;
import server.life.MonsterGlobalDropEntry;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

public final class MonsterBookDropsRequest implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final User chr = c.getPlayer();
        if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
            return;
        }
        chr.updateTick(iPacket.DecodeInteger()); // tick
        final int cardid = iPacket.DecodeInteger();
        final int mobid = MapleItemInformationProvider.getInstance().getCardMobId(cardid);
        if (mobid <= 0 || !chr.getMonsterBook().hasCard(cardid)) {
            c.write(CWvsContext.getCardDrops(cardid, null));
            return;
        }
        final MapleMonsterInformationProvider ii = MapleMonsterInformationProvider.getInstance();
        final List<Integer> newDrops = new ArrayList<>();
        for (final MonsterDropEntry de : ii.retrieveDrop(mobid)) {
            if (de.itemId > 0 && de.questid <= 0 && !newDrops.contains(de.itemId)) {
                newDrops.add(de.itemId);
            }
        }
        for (final MonsterGlobalDropEntry de : ii.getGlobalDrop()) {
            if (de.itemId > 0 && de.questid <= 0 && !newDrops.contains(de.itemId)) {
                newDrops.add(de.itemId);
            }
        }
        c.write(CWvsContext.getCardDrops(cardid, newDrops));
    }

}
