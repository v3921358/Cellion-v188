package handling.game;

import client.ClientSocket;
import client.inventory.Equip;
import client.inventory.ModifyInventory;
import client.inventory.ModifyInventoryOperation;
import constants.ItemConstants;
import java.util.ArrayList;
import java.util.List;
import net.InPacket;
import server.maps.objects.User.CharacterTemporaryValues;
import tools.Pair;
import tools.packet.WvsContext;
import tools.packet.CubePacket;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class UseSelectBlackCubeOptionHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        c.getPlayer().updateTick(iPacket.DecodeInt());
        final short option = iPacket.DecodeShort();
        final long temporaryKeyValue = iPacket.DecodeLong(); // or also the equipment's uniqueid which Nexon uses

        final boolean selectedAfterState = option == 6;// after state = 6, before state = 7

        if (selectedAfterState) {
            Object obj = c.getPlayer().getTemporaryValues().getTemporaryObject(temporaryKeyValue);

            if (obj != null && obj.getClass() == Pair.class) {
                Pair<Equip, Equip> pair_eqps = (Pair) obj;

                // replace the old eq with new eq stats
                Equip eq_originalRef = pair_eqps.getLeft();
                Equip eq_newStatRef = pair_eqps.getRight();

                eq_originalRef.setPotential1(eq_newStatRef.getPotential1());
                eq_originalRef.setPotential2(eq_newStatRef.getPotential2());
                eq_originalRef.setPotential3(eq_newStatRef.getPotential3());
                eq_originalRef.setPotentialTier(eq_newStatRef.getPotentialTier());

                // Update inventory equipment 
                List<ModifyInventory> modifications = new ArrayList<>();
                modifications.add(new ModifyInventory(ModifyInventoryOperation.AddItem, eq_originalRef));
                c.SendPacket(WvsContext.inventoryOperation(true, modifications));

                if (temporaryKeyValue == CharacterTemporaryValues.KEY_MEMORIAL_CUBE) {
                    c.SendPacket(CubePacket.memorialCubeModified(false, eq_originalRef.getPosition(), ItemConstants.MEMORY_CUBE, eq_originalRef));
                }
                return;
            }
            c.Close(); // nuuu u h4x0r
        } else {
            // do nothing, since the original equipment state remains.
        }
    }
}
