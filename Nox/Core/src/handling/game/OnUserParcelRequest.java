/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package handling.game;

import client.MapleCharacterUtil;
import client.MapleClient;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import static handling.world.PackageHandler.addItemToDB;
import static handling.world.PackageHandler.addMesoToDB;
import static handling.world.PackageHandler.loadItems;
import static handling.world.PackageHandler.loadSingleItem;
import static handling.world.PackageHandler.removeItemFromDB;
import net.InPacket;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePackageActions;
import server.maps.objects.User;
import service.ChannelServer;
import tools.packet.CField;
import tools.packet.CWvsContext;
import net.ProcessPacket;
import tools.LogHelper;

public final class OnUserParcelRequest implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final byte operation = iPacket.DecodeByte();

        switch (operation) {
            case 1: { // Start Donald, 13 digit AS
                final String AS13Digit = iPacket.DecodeString();
                //int unk = iPacket.DecodeInt(); // Theres an int here, value = 1
                //9 = error
                final User.MapleCharacterConversationType conv = c.getPlayer().getConversation();
                if (conv == User.MapleCharacterConversationType.Donald) { // Donald
                    c.SendPacket(CField.sendPackageMSG((byte) 10, loadItems(c.getPlayer())));
                }
                break;
            }
            case 3: { // Send Item
                if (c.getPlayer().getConversation() != User.MapleCharacterConversationType.Donald) {
                    return;
                }
                final byte inventId = iPacket.DecodeByte();
                final short itemPos = iPacket.DecodeShort();
                final short amount = iPacket.DecodeShort();
                final int mesos = iPacket.DecodeInt();
                final String recipient = iPacket.DecodeString();
                boolean quickdelivery = iPacket.DecodeByte() > 0;

                final int finalcost = mesos + GameConstants.getTaxAmount(mesos) + (quickdelivery ? 0 : 5000);

                if (mesos >= 0 && mesos <= 100000000 && c.getPlayer().getMeso() >= finalcost) {
                    final int accid = MapleCharacterUtil.getIdByName(recipient);
                    if (accid != -1) {
                        if (accid != c.getAccID()) {
                            boolean recipientOn = false;
                            MapleClient rClient = null;
                            try {
                                //int channel = c.getChannelServer().getWorldInterface().find(recipient);
                                int channel = c.getChannelServer().getPlayerStorage().getCharacterByName(recipient).getClient().getChannel();
                                if (channel > -1) {
                                    recipientOn = true;
                                    ChannelServer rcserv = ChannelServer.getInstance(channel);
                                    rClient = rcserv.getPlayerStorage().getCharacterByName(recipient).getClient();
                                }
                            } catch (Exception e) {
                                //c.getChannelServer().reconnectWorld();
                            }

                            if (inventId > 0) {
                                final MapleInventoryType inv = MapleInventoryType.getByType(inventId);
                                final Item item = c.getPlayer().getInventory(inv).getItem((byte) itemPos);
                                if (item == null) {
                                    c.SendPacket(CField.sendPackageMSG((byte) 17, null)); // Unsuccessfull
                                    return;
                                }
                                final short flag = item.getFlag();
                                if (ItemFlag.UNTRADABLE.check(flag) || ItemFlag.LOCK.check(flag)) {
                                    c.SendPacket(CWvsContext.enableActions());
                                    return;
                                }
                                if (c.getPlayer().getItemQuantity(item.getItemId(), false) >= amount) {
                                    final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                                    if (!ii.isDropRestricted(item.getItemId()) && !ii.isAccountShared(item.getItemId())) {
                                        if (addItemToDB(item, amount, mesos, c.getPlayer().getName(), accid, recipientOn)) {
                                            if (GameConstants.isThrowingStar(item.getItemId()) || GameConstants.isBullet(item.getItemId())) {
                                                MapleInventoryManipulator.removeFromSlot(c, inv, (byte) itemPos, item.getQuantity(), true);
                                            } else {
                                                MapleInventoryManipulator.removeFromSlot(c, inv, (byte) itemPos, amount, true, false);
                                            }
                                            c.getPlayer().gainMeso(-finalcost, false);
                                            c.SendPacket(CField.sendPackageMSG((byte) 19, null)); // Successfull
                                        } else {
                                            c.SendPacket(CField.sendPackageMSG((byte) 17, null)); // Unsuccessful
                                        }
                                    } else {
                                        c.SendPacket(CField.sendPackageMSG((byte) 17, null)); // Unsuccessfull
                                    }
                                } else {
                                    c.SendPacket(CField.sendPackageMSG((byte) 17, null)); // Unsuccessfull
                                }
                            } else if (addMesoToDB(mesos, c.getPlayer().getName(), accid, recipientOn)) {
                                c.getPlayer().gainMeso(-finalcost, false);

                                c.SendPacket(CField.sendPackageMSG((byte) 19, null)); // Successfull
                            } else {
                                c.SendPacket(CField.sendPackageMSG((byte) 17, null)); // Unsuccessfull
                            }
                            //if (recipientOn && rClient != null) {
                            //    rClient.write(CField.sendPackageMSG(Actions.PACKAGE_MSG.getCode()));
                            //}
                        } else {
                            c.SendPacket(CField.sendPackageMSG((byte) 15, null)); // Same acc error
                        }
                    } else {
                        c.SendPacket(CField.sendPackageMSG((byte) 14, null)); // Name does not exist
                    }
                } else {
                    c.SendPacket(CField.sendPackageMSG((byte) 12, null)); // Not enough mesos
                }
                break;
            }
            case 5: { // Recieve Package
                if (c.getPlayer().getConversation() != User.MapleCharacterConversationType.Donald) {
                    return;
                }
                final int packageid = iPacket.DecodeInt();
                final MaplePackageActions dp = loadSingleItem(packageid, c.getPlayer().getId());
                if (dp == null) {
                    return;
                }
                if (dp.getItem() != null && !MapleInventoryManipulator.checkSpace(c, dp.getItem().getItemId(), dp.getItem().getQuantity(), dp.getItem().getOwner())) {
                    c.SendPacket(CField.sendPackageMSG((byte) 16, null)); // Not enough Space
                    return;
                } else if (dp.getMesos() < 0 || (dp.getMesos() + c.getPlayer().getMeso()) < 0) {
                    c.SendPacket(CField.sendPackageMSG((byte) 17, null)); // Unsuccessfull
                    return;
                }
                removeItemFromDB(packageid, c.getPlayer().getId()); // Remove first
                if (dp.getItem() != null) {
                    MapleInventoryManipulator.addFromDrop(c, dp.getItem(), false);
                }
                if (dp.getMesos() != 0) {
                    c.getPlayer().gainMeso(dp.getMesos(), false);
                }
                c.SendPacket(CField.removeFromPackageList(false, packageid));
                break;
            }
            case 6: { // Remove package
                if (c.getPlayer().getConversation() != User.MapleCharacterConversationType.Donald) {
                    return;
                }
                final int packageid = iPacket.DecodeInt();
                removeItemFromDB(packageid, c.getPlayer().getId());
                c.SendPacket(CField.removeFromPackageList(true, packageid));
                break;
            }
            case 8: { // Close Package Deliverer
                if (c.getPlayer().getConversation() == User.MapleCharacterConversationType.Donald) {
                    c.getPlayer().setConversation(User.MapleCharacterConversationType.None);
                }
                break;
            }
            default: {
                LogHelper.GENERAL_EXCEPTION.get().info("[OnExpeditionRequest] Unknow action type: " + operation);
                break;
            }
        }
    }
}
