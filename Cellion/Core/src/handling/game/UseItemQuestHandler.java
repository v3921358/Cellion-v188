package handling.game;

import client.ClientSocket;
import client.QuestStatus;
import client.QuestStatus.QuestState;
import client.inventory.Item;
import enums.InventoryType;
import java.util.List;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.quest.Quest;
import tools.Pair;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseItemQuestHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final short slot = iPacket.DecodeShort();
        final int itemId = iPacket.DecodeInt();
        final Item item = c.getPlayer().getInventory(InventoryType.ETC).getItem(slot);
        final int qid = iPacket.DecodeInt();
        final Quest quest = Quest.getInstance(qid);
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        Pair<Integer, List<Integer>> questItemInfo = null;
        boolean found = false;
        for (Item i : c.getPlayer().getInventory(InventoryType.ETC)) {
            if (i.getItemId() / 10000 == 422) {
                questItemInfo = ii.questItemInfo(i.getItemId());
                if (questItemInfo != null && questItemInfo.getLeft() == qid && questItemInfo.getRight() != null && questItemInfo.getRight().contains(itemId)) {
                    found = true;
                    break; //i believe it's any order
                }
            }
        }
        if (quest != null && found && item != null && item.getQuantity() > 0 && item.getItemId() == itemId) {
            final int newData = iPacket.DecodeInt();
            final QuestStatus stats = c.getPlayer().getQuestNoAdd(quest);
            if (stats != null && stats.getStatus() == QuestState.Started) {
                stats.setCustomData(String.valueOf(newData));
                c.getPlayer().updateQuest(stats, true);
                MapleInventoryManipulator.removeFromSlot(c, InventoryType.ETC, slot, (short) 1, false);
            }
        }
    }

}
