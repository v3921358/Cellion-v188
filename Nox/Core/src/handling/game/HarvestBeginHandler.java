package handling.game;

import client.Client;
import client.QuestStatus;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import server.maps.objects.User;
import server.maps.objects.MapleReactor;
import server.quest.Quest;
import net.InPacket;
import tools.packet.CField;
import net.ProcessPacket;

public final class HarvestBeginHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        final User chr = c.getPlayer();
        //its ok if a hacker bypasses this as we do everything in the reactor anyway
        final MapleReactor reactor = c.getPlayer().getMap().getReactorByOid(iPacket.DecodeInt());
        if (reactor == null || !reactor.isAlive() || reactor.getReactorId() > 200011 || chr.getStat().harvestingTool <= 0 || reactor.getTruePosition().distanceSq(chr.getTruePosition()) > 10000 || c.getPlayer().getFatigue() >= 200) {
            return;
        }
        Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short) c.getPlayer().getStat().harvestingTool);
        if (item == null || ((Equip) item).getDurability() == 0) {
            c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
            return;
        }
        QuestStatus marr = c.getPlayer().getQuestNAdd(Quest.getInstance(GameConstants.HARVEST_TIME));
        if (marr.getCustomData() == null) {
            marr.setCustomData("0");
        } else {
            marr.setCustomData(String.valueOf(System.currentTimeMillis()));
            c.SendPacket(CField.harvestMessage(reactor.getObjectId(), 13)); //ok to harvest, gogo
            c.getPlayer().getMap().broadcastMessage(chr, CField.showHarvesting(chr.getId(), item.getItemId()), false);
        }
    }

}
