package handling.game;

import client.MapleClient;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.InventoryConstants;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleStorage;
import server.maps.objects.MapleCharacter;
import net.InPacket;
import server.maps.objects.MapleCharacter.MapleCharacterConversationType;
import tools.packet.CField;
import tools.packet.CWvsContext;
import netty.ProcessPacket;
import tools.LogHelper;

/**
 *
 * @author
 */
public class StorageHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        byte mode = iPacket.DecodeByte();

        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        MapleStorage storage = chr.getStorage();

        switch (mode) {
            case 4: {
                byte type = iPacket.DecodeByte();
                byte slot = storage.getSlot(MapleInventoryType.getByType(type), iPacket.DecodeByte());
                Item item = storage.takeOut(slot);

                if (item != null) {
                    if (!MapleInventoryManipulator.checkSpace(c, item.getItemId(), item.getQuantity(), item.getOwner())) {
                        storage.store(item);
                        chr.dropMessage(1, "Your inventory is full");
                    } else {
                        MapleInventoryManipulator.addFromDrop(c, item, false);
                        storage.sendTakenOut(c, GameConstants.getInventoryType(item.getItemId()));
                    }
                } else {
                    c.write(CWvsContext.enableActions());
                }
                break;
            }
            case 5: {
                byte slot = (byte) iPacket.DecodeShort();
                int itemId = iPacket.DecodeInteger();
                MapleInventoryType type = GameConstants.getInventoryType(itemId);
                short quantity = iPacket.DecodeShort();
                MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                if (quantity < 1) {
                    return;
                }
                if (storage.isFull()) {
                    c.write(CField.NPCPacket.getStorageFull());
                    return;
                }
                if (chr.getInventory(type).getItem((short) slot) == null) {
                    c.write(CWvsContext.enableActions());
                    return;
                }

                if (chr.getMeso() < 100L) {
                    chr.dropMessage(1, "You don't have enough mesos to store the item");
                } else {
                    Item item = chr.getInventory(type).getItem((short) slot).copy();

                    if (InventoryConstants.isPet(item.getItemId())) {
                        c.write(CWvsContext.enableActions());
                        return;
                    }
                    short flag = item.getFlag();
                    if ((ii.isPickupRestricted(item.getItemId())) && (storage.findById(item.getItemId()) != null)) {
                        c.write(CWvsContext.enableActions());
                        return;
                    }
                    if ((item.getItemId() == itemId) && ((item.getQuantity() >= quantity) || (GameConstants.isThrowingStar(itemId)) || (GameConstants.isBullet(itemId)))) {
                        if (ii.isDropRestricted(item.getItemId())) {
                            /*if (ItemFlag.KARMA_EQ.check(flag)) {
                                item.setFlag((short) (flag - ItemFlag.KARMA_EQ.getValue()));
                            } else if (ItemFlag.KARMA_USE.check(flag)) {
                                item.setFlag((short) (flag - ItemFlag.KARMA_USE.getValue()));
                            } else if (ItemFlag.KARMA_ACC.check(flag)) {
                                item.setFlag((short) (flag - ItemFlag.KARMA_ACC.getValue()));
                            } else if (ItemFlag.KARMA_ACC_USE.check(flag)) {
                                item.setFlag((short) (flag - ItemFlag.KARMA_ACC_USE.getValue()));
                            } else {
                                c.write(CWvsContext.enableActions());
                                return;
                            }*/
                        }
                        if ((GameConstants.isThrowingStar(itemId)) || (GameConstants.isBullet(itemId))) {
                            quantity = item.getQuantity();
                        }
                        chr.gainMeso(-100L, false, false);
                        MapleInventoryManipulator.removeFromSlot(c, type, (short) slot, quantity, false);
                        item.setQuantity(quantity);
                        storage.store(item);
                    } else {
                        return;
                    }
                }
                storage.sendStored(c, GameConstants.getInventoryType(itemId));
                break;
            }
            case 6:
                storage.arrange();
                storage.update(c);
                break;
            case 7: {
                long meso = iPacket.DecodeInteger();
                long storageMesos = storage.getMeso();
                long playerMesos = chr.getMeso();

                if (((meso > 0L) && (storageMesos >= meso)) || ((meso < 0L) && (playerMesos >= -meso))) {
                    if ((meso < 0L) && (storageMesos - meso < 0L)) {
                        meso = -(9999999999L - storageMesos);
                        if (-meso <= playerMesos);
                    } else if ((meso > 0L) && (playerMesos + meso < 0L)) {
                        meso = 9999999999L - playerMesos;
                        if (meso > storageMesos) {
                            return;
                        }
                    }
                    storage.setMeso(storageMesos - meso);
                    chr.gainMeso(meso, false, false);
                } else {
                    return;
                }
                storage.sendMeso(c);
                break;
            }
            case 8:
                storage.close();
                if (chr.getConversation() == MapleCharacterConversationType.Storage) {
                    chr.setConversation(MapleCharacterConversationType.None);
                }
                break;
            default:
                LogHelper.GENERAL_EXCEPTION.get().info("Unhandled Storage mode : " + mode);
        }
    }

}
