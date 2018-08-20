package handling.game;

import client.ClientSocket;
import client.inventory.Item;
import enums.InventoryType;
import constants.GameConstants;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import server.MapleInventoryManipulator;
import server.Randomizer;
import server.potentials.ItemPotentialOption;
import net.InPacket;
import server.potentials.ItemPotentialProvider;
import enums.NebuliteGrade;
import tools.packet.CField;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class UseNebuliteFusionHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        /*   c.getPlayer().updateTick(iPacket.DecodeInt());
        c.getPlayer().setScrolledPosition((short) 0);

        final int nebuliteId1 = iPacket.DecodeInt();
        final Item nebulite1 = c.getPlayer().getInventory(MapleInventoryType.SETUP).getItem((byte) iPacket.decodeShort());
        final int nebuliteId2 = iPacket.DecodeInt();
        final Item nebulite2 = c.getPlayer().getInventory(MapleInventoryType.SETUP).getItem((byte) iPacket.decodeShort());
        final long mesos = iPacket.DecodeInt();
        final int premiumQuantity = iPacket.DecodeInt();

        if (nebulite1 == null || nebulite2 == null || nebuliteId1 != nebulite1.getItemId() || nebuliteId2 != nebulite2.getItemId() || (mesos == 0 && premiumQuantity == 0) || (mesos != 0 && premiumQuantity != 0) || mesos < 0 || premiumQuantity < 0 || c.getPlayer().hasBlockedInventory()) {
            c.getPlayer().dropMessage(1, "Failed to fuse Nebulite.");
            c.write(CWvsContext.inventoryOperation(true, new ArrayList<>()));
            return;
        }

        final NebuliteGrade grade1 = GameConstants.getNebuliteGrade(nebuliteId1);
        final NebuliteGrade grade2 = GameConstants.getNebuliteGrade(nebuliteId2);

        final NebuliteGrade lowerRank = NebuliteGrade.getLowerRank(grade1, grade2);
        final NebuliteGrade higherRank = NebuliteGrade.getHigherRank(grade1, grade2);

        if (grade1 == NebuliteGrade.None || grade2 == NebuliteGrade.None
                || (higherRank == NebuliteGrade.GradeA && premiumQuantity != 2)
                || (higherRank == NebuliteGrade.GradeB && premiumQuantity != 1)
                || (higherRank == NebuliteGrade.GradeC && mesos != 5000)
                || (higherRank == NebuliteGrade.GradeD && mesos != 3000) || (mesos > 0 && c.getPlayer().getMeso() < mesos)
                || (premiumQuantity > 0 && c.getPlayer().getItemQuantity(4420000, false) < premiumQuantity) || grade1 == NebuliteGrade.GradeS || grade2 == NebuliteGrade.GradeS
                || (c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < 1)) { // 4000 + = S, 3000 + = A, 2000 + = B, 1000 + = C, else = D
            c.write(CField.useNebuliteFusion(c.getPlayer().getId(), 0, false));
            return; // Most of them were done in client, so we just send the unsuccessfull packet, as it is only here when they packet edit.
        }

        NebuliteGrade newChosenRank;

        // 50% rate that it may use the lower rank of the two, or higher rank.
        newChosenRank = Randomizer.nextInt(100) < 50 ? lowerRank : higherRank;

        // 4% rate that it may rank up.
        if (Randomizer.nextInt(100) < 4) {
            final NebuliteGrade nextGrade = NebuliteGrade.getNextGrade(newChosenRank); // can the item rank up further?
            if (nextGrade != NebuliteGrade.None) {
                newChosenRank = nextGrade;
            }
        }

        ItemPotentialOption newOption = ItemPotentialProvider.getRandomNebulitePotential(newChosenRank);
        if (newOption != null) {
            final int newNebuliteItemId = newOption.opID;

            c.getPlayer().gainMeso(-mesos, true);
            MapleInventoryManipulator.removeById(c, MapleInventoryType.ETC, 4420000, premiumQuantity, false, false);

            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.SETUP, nebulite1.getPosition(), (short) 1, false);
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.SETUP, nebulite2.getPosition(), (short) 1, false);
            MapleInventoryManipulator.addById(c, newNebuliteItemId, (short) 1, "Fused from " + nebuliteId1 + " and " + nebuliteId2 + " on " + LocalDateTime.now());
            c.write(CField.useNebuliteFusion(c.getPlayer().getId(), newNebuliteItemId, true));
        }*/
    }
}
