package handling.game;

import client.Client;
import client.inventory.MapleInventoryType;
import client.inventory.PetCommand;
import client.inventory.PetDataFactory;
import constants.GameConstants;
import net.InPacket;
import server.Randomizer;
import server.maps.objects.User;
import server.maps.objects.Pet;
import tools.packet.CField;
import tools.packet.PetPacket;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class PetCommandHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        User chr = c.getPlayer();
        Pet pet = chr.getPet(c.getPlayer().getPetIndex((int) iPacket.DecodeLong()));
        iPacket.DecodeByte(); //always 0?
        if (pet == null) {
            return;
        }
        PetCommand petCommand = PetDataFactory.getPetCommand(pet.getItem().getItemId(), iPacket.DecodeByte());
        if (petCommand == null) {
            return;
        }

        byte petIndex = (byte) chr.getPetIndex(pet);
        boolean success = false;
        if (Randomizer.nextInt(99) <= petCommand.getProbability()) {
            success = true;
            if (pet.getCloseness() < 30000) {
                double newCloseness = pet.getCloseness() + (petCommand.getIncrease() * c.getChannelServer().getTraitRate());
                if (newCloseness > 30000) {
                    newCloseness = 30000;
                }
                pet.setCloseness((int) newCloseness);
                if (newCloseness >= GameConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                    pet.setLevel(pet.getLevel() + 1);
                    c.SendPacket(CField.EffectPacket.showOwnPetLevelUp(null, petIndex));
                    chr.getMap().broadcastMessage(PetPacket.showPetLevelUp(chr, petIndex));
                }
                //  chr.forceUpdateItem(pet.getItem());
                c.SendPacket(PetPacket.updatePet(pet, chr.getInventory(MapleInventoryType.CASH).getItem((byte) pet.getItem().getPosition()), false));
            }
        }
        chr.getMap().broadcastMessage(PetPacket.commandResponse(chr.getId(), (byte) petCommand.getSkillId(), petIndex, success, false));
    }
}
