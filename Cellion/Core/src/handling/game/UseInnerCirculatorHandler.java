package handling.game;

import client.InnerAbillity;
import client.InnerSkillValueHolder;
import client.ClientSocket;
import client.inventory.Item;
import enums.InventoryType;
import java.util.LinkedList;
import java.util.List;
import server.MapleInventoryManipulator;
import net.InPacket;
import tools.packet.CField;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseInnerCirculatorHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        int itemid = iPacket.DecodeInt();
        short slot = (short) iPacket.DecodeInt();
        Item item = c.getPlayer().getInventory(InventoryType.USE).getItem(slot);
        if (item.getItemId() == itemid) {
            List<InnerSkillValueHolder> newValues = new LinkedList<>();
            int i = 0;
            for (InnerSkillValueHolder isvh : c.getPlayer().getInnerSkills()) {
                if (!isvh.isLocked()) {
                    if (i == 0 && c.getPlayer().getInnerSkills().size() > 1 && itemid == 2702000) { //Ultimate Circulator
                        newValues.add(InnerAbillity.getInstance().renewSkill(isvh.getRank(), itemid, true, false));
                    } else {
                        newValues.add(InnerAbillity.getInstance().renewSkill(isvh.getRank(), itemid, false, false));
                    }
                    //c.getPlayer().changeSkillLevel(SkillFactory.getSkill(isvh.getSkillId()), (byte) 0, (byte) 0);
                } else {
                    newValues.add(isvh);
                }
                i++;
            }
            c.getPlayer().getInnerSkills().clear();
            byte ability = 1;
            for (InnerSkillValueHolder isvh : newValues) {
                c.getPlayer().getInnerSkills().add(isvh);
                c.SendPacket(CField.updateInnerPotential(ability, isvh.getSkillId(), isvh.getSkillLevel(), isvh.getRank()));
                ability++;
                //c.getPlayer().changeSkillLevel(SkillFactory.getSkill(isvh.getSkillId()), isvh.getSkillLevel(), isvh.getSkillLevel());
            }
            MapleInventoryManipulator.removeFromSlot(c, InventoryType.USE, slot, (short) 1, false);

            //c.write(CField.gameMsg("Inner Potential has been reconfigured.")); //not sure if it's working
            c.getPlayer().dropMessage(5, "Inner Potential has been reconfigured.");
        }
        c.SendPacket(WvsContext.enableActions());
    }
}
