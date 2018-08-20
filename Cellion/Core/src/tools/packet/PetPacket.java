package tools.packet;

import client.inventory.Item;
import constants.ServerConstants;
import java.util.List;

import service.SendPacketOpcode;
import net.OutPacket;

import server.maps.objects.User;
import server.maps.objects.Pet;
import server.movement.LifeMovementFragment;

public class PetPacket {

    /**
     * This is the packet to handle spawning pets.
     *
     * @param int dwCharacterID (Character ID)
     * @param int nIdx (Pet Index)
     * @param boolean bOnInitialize (Is Pet Being Initialized)
     * @param MaplePet pPet
     * @param int nRemoveReason
     *
     * @example PetPacket.OnActivated(MapleCharacter.id, getPetIndex(pet), true, pet, 0)
     *
     */
    public static final OutPacket OnActivated(int dwCharacterID, int nIdx, boolean bOnInitialize, Pet pPet, int nRemoveReason) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.PetActivated.getValue());
        oPacket.EncodeInt(dwCharacterID);
        oPacket.EncodeInt(nIdx);
        if (nIdx >= 0 && nIdx < 3) {
            oPacket.EncodeBool(pPet != null);
            if (pPet != null) {
                oPacket.EncodeBool(bOnInitialize);
                oPacket.EncodeInt(pPet.getItem().getItemId());
                oPacket.EncodeString(pPet.getName());
                oPacket.EncodeLong(pPet.getItem().getUniqueId());
                oPacket.EncodePosition(pPet.getPos());
                oPacket.EncodeByte(pPet.getStance());
                oPacket.EncodeShort(pPet.getFh());
                oPacket.EncodeInt(pPet.getColor());
                oPacket.EncodeShort(pPet.getGiant());
                oPacket.EncodeByte(pPet.getTransform());
                oPacket.EncodeByte(pPet.getReinforced());

                if (ServerConstants.DEVELOPER_DEBUG_MODE) {
                    System.err.println(String.format("[Debug] Pet Template [%d], Name [%s], SN [%d], Pos [%d:%d]", pPet.getItem().getItemId(), pPet.getName(), pPet.getItem().getUniqueId(), pPet.getPos().x, pPet.getPos().y));
                }
            } else {
                oPacket.EncodeByte((byte) nRemoveReason);
            }
        }
        return oPacket;
    }

    /**
     * This is the packet to handle removing pets.
     *
     * @param int dwCharacterID (Character ID)
     * @param int nIdx (Pet Index)
     * @param MaplePet pPet
     *
     * @example PetPacket.OnActivated(MapleCharacter.id, getPetIndex(pet), pet)
     *
     */
    public static final OutPacket OnDeactivated(int dwCharacterID, int nIdx, Pet pPet) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.PetActivated.getValue());
        oPacket.EncodeInt(dwCharacterID);
        oPacket.EncodeInt(nIdx);
        oPacket.EncodeByte(0); //Remove = 0, Don't Remove = 1
        oPacket.EncodeByte(0); //Hunger Boolean

        return oPacket;
    }

    /**
     * This is the packet updates pets in your equipped inventory.
     *
     * @param MaplePet pet
     * @param Item item
     * @param boolean active
     *
     * @return OutPacket oPacket
     *
     */
    public static final OutPacket updatePet(Pet pet, Item item, boolean active) { // what a fucking meme.. this is a hardcode invoperation

        OutPacket oPacket = new OutPacket(SendPacketOpcode.InventoryOperation.getValue());
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(2);
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(3);
        oPacket.EncodeByte(5);
        oPacket.EncodeShort(pet.getItem().getPosition());
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(5);
        oPacket.EncodeShort(pet.getItem().getPosition());
        oPacket.EncodeByte(3);
        oPacket.EncodeInt(pet.getItem().getItemId());
        oPacket.EncodeByte(1);
        oPacket.EncodeLong(pet.getItem().getUniqueId());
        PacketHelper.addPetItemInfo(oPacket, item, pet, active);
        oPacket.EncodeByte(0);

        return oPacket;
    }

    /**
     * This is the packet allows pets to move
     *
     * @param int cid - Player Id
     * @param int petId - Pet Id
     * @param MaplePet pet
     * @param List<LifeMovementFragment> moves - The list of movements the pet will take
     *
     * @return OutPacket oPacket
     *
     */
    public static OutPacket movePet(int cid, int index, Pet pet, List<LifeMovementFragment> moves) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.PetMove.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeInt(index);

        PacketHelper.serializeMovementList(oPacket, pet, moves, 0);

        return oPacket;
    }

    public static OutPacket petChat(int cid, int un, String text, byte slot) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.PET_CHAT.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeByte(slot);
        oPacket.EncodeByte(un);
        oPacket.EncodeByte(0);
        oPacket.EncodeString(text);
        oPacket.EncodeByte(0);

        return oPacket;
    }

    public static OutPacket petColor(int cid, byte slot, int color) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.PetHueChanged.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeByte(slot);
        oPacket.EncodeInt(color);

        return oPacket;
    }

    public static final OutPacket commandResponse(int cid, byte command, byte slot, boolean success, boolean food) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.PetActionCommand.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeInt(slot);
        oPacket.EncodeByte(command == 1 ? 2 : 1);
        if (command != 1) {
            oPacket.EncodeByte(command);
            oPacket.EncodeByte(success ? 1 : 0);
            oPacket.EncodeByte(0);
        } else {
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(0);
        }

        return oPacket;
    }

    public static final OutPacket showPetLevelUp(User chr, byte index) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserEffectRemote.getValue());
        oPacket.EncodeInt(chr.getId());
        oPacket.EncodeByte(6);
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(index);

        return oPacket;
    }

    public static final OutPacket petSize(int cid, byte slot, short size) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.PET_SIZE.getValue());
        oPacket.EncodeInt(cid);
        oPacket.EncodeByte(slot);
        oPacket.EncodeShort(size);

        return oPacket;
    }

    public static final OutPacket showPetUpdate(User chr, int uniqueId, byte index) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.PetLoadExceptionList.getValue());
        oPacket.EncodeInt(chr.getId());

        oPacket.EncodeInt(index);
        oPacket.EncodeLong(uniqueId);
        oPacket.EncodeByte(0); // aExceptionList size for loop this size and print integer itemids

        return oPacket;
    }
}
