package handling.game;

import client.MapleClient;
import client.MapleQuestStatus;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import server.maps.objects.User;
import server.maps.objects.MapleReactor;
import server.quest.MapleQuest;
import net.InPacket;
import tools.packet.CField;
import netty.ProcessPacket;

public final class HarvestBeginHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final User chr = c.getPlayer();
        //its ok if a hacker bypasses this as we do everything in the reactor anyway
        final MapleReactor reactor = c.getPlayer().getMap().getReactorByOid(iPacket.DecodeInteger());
        if (reactor == null || !reactor.isAlive() || reactor.getReactorId() > 200011 || chr.getStat().harvestingTool <= 0 || reactor.getTruePosition().distanceSq(chr.getTruePosition()) > 10000 || c.getPlayer().getFatigue() >= 200) {
            return;
        }
        Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((short) c.getPlayer().getStat().harvestingTool);
        if (item == null || ((Equip) item).getDurability() == 0) {
            c.getPlayer().getStat().handleProfessionTool(c.getPlayer());
            return;
        }
        MapleQuestStatus marr = c.getPlayer().getQuestNAdd(MapleQuest.getInstance(GameConstants.HARVEST_TIME));
        if (marr.getCustomData() == null) {
            marr.setCustomData("0");
        } else {
            marr.setCustomData(String.valueOf(System.currentTimeMillis()));
            c.write(CField.harvestMessage(reactor.getObjectId(), 13)); //ok to harvest, gogo
            c.getPlayer().getMap().broadcastMessage(chr, CField.showHarvesting(chr.getId(), item.getItemId()), false);
        }
    }

}
