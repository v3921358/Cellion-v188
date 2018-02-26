package tools.packet;

import client.inventory.Item;
import constants.ServerConstants;
import java.util.List;

import service.SendPacketOpcode;
import net.OutPacket;
import net.Packet;
import server.maps.objects.MapleCharacter;
import server.maps.objects.MaplePet;
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
    public static final Packet OnActivated(int dwCharacterID, int nIdx, boolean bOnInitialize, MaplePet pPet, int nRemoveReason) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.PetActivated.getValue());
        oPacket.EncodeInteger(dwCharacterID);
        oPacket.EncodeInteger(nIdx);
        if (nIdx >= 0 && nIdx < 3) {
            oPacket.EncodeBool(pPet != null);
            if (pPet != null) {
                oPacket.EncodeBool(bOnInitialize);
                oPacket.EncodeInteger(pPet.getItem().getItemId());
                oPacket.EncodeString(pPet.getName());
                oPacket.EncodeLong(pPet.getItem().getUniqueId());
                oPacket.EncodePosition(pPet.getPos());
                oPacket.Encode(pPet.getStance());
                oPacket.EncodeShort(pPet.getFh());
                oPacket.EncodeInteger(pPet.getColor());
                oPacket.EncodeShort(pPet.getGiant());
                oPacket.Encode(pPet.getTransform());
                oPacket.Encode(pPet.getReinforced());

                if (ServerConstants.DEVELOPER_PACKET_DEBUG_MODE) {
                    System.err.println(String.format("[Debug] Pet Template [%d], Name [%s], SN [%d], Pos [%d:%d]", pPet.getItem().getItemId(), pPet.getName(), pPet.getItem().getUniqueId(), pPet.getPos().x, pPet.getPos().y));
                }
            } else {
                oPacket.EncodeByte((byte) nRemoveReason);
            }
        }
        return oPacket.ToPacket();
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
    public static final Packet OnDeactivated(int dwCharacterID, int nIdx, MaplePet pPet) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.PetActivated.getValue());
        oPacket.EncodeInteger(dwCharacterID);
        oPacket.EncodeInteger(nIdx);
        oPacket.Encode(0); //Remove = 0, Don't Remove = 1
        oPacket.Encode(0); //Hunger Boolean

        return oPacket.ToPacket();
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
    public static final Packet updatePet(MaplePet pet, Item item, boolean active) { // what a fucking meme.. this is a hardcode invoperation
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.InventoryOperation.getValue());
        oPacket.Encode(0);
        oPacket.Encode(2);
        oPacket.Encode(0);
        oPacket.Encode(3);
        oPacket.Encode(5);
        oPacket.EncodeShort(pet.getItem().getPosition());
        oPacket.Encode(0);
        oPacket.Encode(0);
        oPacket.Encode(5);
        oPacket.EncodeShort(pet.getItem().getPosition());
        oPacket.Encode(3);
        oPacket.EncodeInteger(pet.getItem().getItemId());
        oPacket.Encode(1);
        oPacket.EncodeLong(pet.getItem().getUniqueId());
        PacketHelper.addPetItemInfo(oPacket, item, pet, active);
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static final Packet addPetData(MapleCharacter chr, MaplePet pet) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeInteger(pet.getItem().getItemId());
        oPacket.EncodeString(pet.getName());
        oPacket.EncodeLong(pet.getItem().getUniqueId());
        oPacket.EncodeShort(pet.getPos().x);
        oPacket.EncodeShort(pet.getPos().y - 20);
        oPacket.Encode(pet.getStance());
        oPacket.EncodeShort(pet.getFh());
        oPacket.EncodeInteger(pet.getColor());
        oPacket.EncodeShort(pet.getGiant());
        oPacket.Encode(pet.getTransform());
        oPacket.Encode(pet.getReinforced());

        return oPacket.ToPacket();
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
    public static Packet movePet(int cid, int index, MaplePet pet, List<LifeMovementFragment> moves) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.PetMove.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.EncodeInteger(index);

        PacketHelper.serializeMovementList(oPacket, pet, moves, 0);

        return oPacket.ToPacket();
    }

    public static Packet petChat(int cid, int un, String text, byte slot) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.PET_CHAT.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.Encode(slot);
        oPacket.Encode(un);
        oPacket.Encode(0);
        oPacket.EncodeString(text);
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static Packet petColor(int cid, byte slot, int color) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.PetHueChanged.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.Encode(slot);
        oPacket.EncodeInteger(color);

        return oPacket.ToPacket();
    }

    public static final Packet commandResponse(int cid, byte command, byte slot, boolean success, boolean food) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.PetActionCommand.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.EncodeInteger(slot);
        oPacket.Encode(command == 1 ? 2 : 1);
        if (command != 1) {
            oPacket.Encode(command);
            oPacket.Encode(success ? 1 : 0);
            oPacket.Encode(0);
        } else {
            oPacket.Encode(1);
            oPacket.EncodeInteger(0);
        }

        return oPacket.ToPacket();
    }

    public static final Packet showPetLevelUp(MapleCharacter chr, byte index) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserEffectRemote.getValue());
        oPacket.EncodeInteger(chr.getId());
        oPacket.Encode(6);
        oPacket.Encode(0);
        oPacket.EncodeInteger(index);

        return oPacket.ToPacket();
    }

    public static final Packet petSize(int cid, byte slot, short size) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.PET_SIZE.getValue());
        oPacket.EncodeInteger(cid);
        oPacket.Encode(slot);
        oPacket.EncodeShort(size);

        return oPacket.ToPacket();
    }

    public static final Packet showPetUpdate(MapleCharacter chr, int uniqueId, byte index) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.PetLoadExceptionList.getValue());
        oPacket.EncodeInteger(chr.getId());

        oPacket.EncodeInteger(index);
        oPacket.EncodeLong(uniqueId);
        oPacket.Encode(0); // aExceptionList size for loop this size and print integer itemids

        return oPacket.ToPacket();
    }
}
