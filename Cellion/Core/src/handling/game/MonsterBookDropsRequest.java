package handling.game;

import java.util.ArrayList;
import java.util.List;

import client.ClientSocket;
import server.MapleItemInformationProvider;
import server.life.MonsterInformationProvider;
import server.life.MonsterDropEntry;
import server.life.MonsterGlobalDropEntry;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.WvsContext;
import net.ProcessPacket;

public final class MonsterBookDropsRequest implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final User chr = c.getPlayer();
        if (c.getPlayer() == null || c.getPlayer().getMap() == null) {
            return;
        }
        chr.updateTick(iPacket.DecodeInt()); // tick
        final int cardid = iPacket.DecodeInt();
        final int mobid = MapleItemInformationProvider.getInstance().getCardMobId(cardid);
        if (mobid <= 0 || !chr.getMonsterBook().hasCard(cardid)) {
            c.SendPacket(WvsContext.getCardDrops(cardid, null));
            return;
        }
        final MonsterInformationProvider ii = MonsterInformationProvider.getInstance();
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
        c.SendPacket(WvsContext.getCardDrops(cardid, newDrops));
    }

}
