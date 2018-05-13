package handling.game;

import client.ClientSocket;
import client.QuestStatus;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import server.maps.objects.User;
import server.maps.objects.Reactor;
import server.quest.Quest;
import net.InPacket;
import tools.packet.CField;
import net.ProcessPacket;

public final class HarvestBeginHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final User chr = c.getPlayer();
        //its ok if a hacker bypasses this as we do everything in the reactor anyway
        final Reactor reactor = c.getPlayer().getMap().getReactorByOid(iPacket.DecodeInt());
        if (reactor == null || !reactor.isAlive() || reactor.getReactorId() > 200011 || chr.getStat().harvestingTool <= 0 || reactor.getTruePosition().distanceSq(chr.getTruePosition()) > 10000 || c.getPlayer().getFatigue() >= 200) {
            return;
        }
        Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short) c.getPlayer().getStat().harvestingTool);
        if (item == null || ((Equip) item).getDurability() == 0) {
            c.getPlayer().getStat().OnProfessionToolRequest(c.getPlayer());
            return;
        }
        QuestStatus marr = c.getPlayer().getQuestNAdd(Quest.getInstance(GameConstants.HARVEST_TIME));
        if (marr.getCustomData() == null) {
            marr.setCustomData("0");
        } else {
            marr.setCustomData(String.valueOf(System.currentTimeMillis()));
            c.SendPacket(CField.harvestMessage(reactor.getObjectId(), 13)); //ok to harvest, gogo
            c.getPlayer().getMap().broadcastPacket(chr, CField.showHarvesting(chr.getId(), item.getItemId()), false);
        }
    }

}
