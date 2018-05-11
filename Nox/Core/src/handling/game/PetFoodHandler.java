package handling.game;

import client.Client;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import net.InPacket;
import server.MapleInventoryManipulator;
import server.Randomizer;
import server.maps.objects.User;
import server.maps.objects.Pet;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.PetPacket;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class PetFoodHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        int previousFullness = 100;
        Pet pet = null;
        //  MapleCharacter chr = chr;
        // int previousFullness = 100;
        // MaplePet pet = null;
        if (c.getPlayer() == null) {
            return;
        }
        for (final Pet pets : c.getPlayer().getPets()) {
            if (pets.getSummoned()) {
                if (pets.getFullness() < previousFullness) {
                    previousFullness = pets.getFullness();
                    pet = pets;
                }
            }
        }
        if (pet == null) {
            c.getPlayer().dropMessage(5, "Your pet is not hungry.");
            c.SendPacket(CWvsContext.enableActions());
            return;
        }

        c.getPlayer().updateTick(iPacket.DecodeInt());
        short slot = iPacket.DecodeShort();
        final int itemId = iPacket.DecodeInt();
        Item petFood = c.getPlayer().getInventory(MapleInventoryType.USE).getItem(slot);
        if (petFood == null || petFood.getItemId() != itemId || petFood.getQuantity() <= 0 || itemId / 10000 != 212) {
            c.getPlayer().dropMessage(-1, "Wrong item" + itemId);
            c.SendPacket(CWvsContext.enableActions());
            return;
        }
        boolean gainCloseness = false;

        if (Randomizer.nextInt(99) <= 50) {
            gainCloseness = true;
        }
        if (pet.getFullness() < 100) {
            int newFullness = pet.getFullness() + 30;
            if (newFullness > 100) {
                newFullness = 100;
            }
            pet.setFullness(newFullness);
            final byte index = c.getPlayer().getPetIndex(pet);

            if (gainCloseness && pet.getCloseness() < 30000) {
                int newCloseness = pet.getCloseness() + 1;
                if (newCloseness > 30000) {
                    newCloseness = 30000;
                }
                pet.setCloseness(newCloseness);
                if (newCloseness >= GameConstants.getClosenessNeededForLevel(pet.getLevel() + 1)) {
                    pet.setLevel(pet.getLevel() + 1);
                    c.SendPacket(CField.EffectPacket.showOwnPetLevelUp(null, index));
                    c.getPlayer().dropMessage(6, "Your pet has leveled up! " + pet.getName() + " is now level " + pet.getLevel() + "." + " Pet Closeness: " + pet.getCloseness());
                    c.getPlayer().getMap().broadcastMessage(CField.EffectPacket.showOwnPetLevelUp(c.getPlayer(), index));
                }
            }
            c.SendPacket(PetPacket.updatePet(pet, c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) pet.getItem().getPosition()), false));
            c.getPlayer().getMap().broadcastMessage(c.getPlayer(), PetPacket.commandResponse(c.getPlayer().getId(), (byte) 1, index, true, true), true);
        } else {
            if (gainCloseness) {
                int newCloseness = pet.getCloseness() - 1;
                if (newCloseness < 0) {
                    newCloseness = 0;
                }
                pet.setCloseness(newCloseness);
                if (newCloseness < GameConstants.getClosenessNeededForLevel(pet.getLevel())) {
                    pet.setLevel(pet.getLevel() - 1);
                }
            }
            c.SendPacket(PetPacket.updatePet(pet, c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) pet.getItem().getPosition()), false));
            c.getPlayer().getMap().broadcastMessage(c.getPlayer(), PetPacket.commandResponse(c.getPlayer().getId(), (byte) 1, c.getPlayer().getPetIndex(pet), false, true), true);
        }
        MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.USE, slot, (short) 1, true, false);
        c.SendPacket(CWvsContext.enableActions());
    }

}
