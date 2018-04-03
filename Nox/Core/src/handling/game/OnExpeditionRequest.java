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

import client.MapleClient;
import constants.GameConstants;
import handling.world.ExpeditionType;
import handling.world.MapleExpedition;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.World;
import java.util.Iterator;
import net.InPacket;
import server.maps.Event_DojoAgent;
import server.maps.objects.User;
import service.ChannelServer;
import tools.packet.CWvsContext;
import netty.ProcessPacket;
import tools.LogHelper;

public final class OnExpeditionRequest implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        if ((c.getPlayer() == null) || (c.getPlayer().getMap() == null)) {
            return;
        }
        int mode = iPacket.DecodeByte();
        String name;
        MapleParty part;
        MapleExpedition exped;
        int cid;
        Iterator i$;

        switch (mode) {
            case 76://64
            case 134:
                ExpeditionType et = ExpeditionType.getById(iPacket.DecodeInteger());
                if ((et != null) && (c.getPlayer().getParty() == null) && (c.getPlayer().getLevel() <= et.maxLevel) && (c.getPlayer().getLevel() >= et.minLevel)) {
                    MapleParty party = World.Party.createParty(new MaplePartyCharacter(c.getPlayer()), et.exped);
                    c.getPlayer().setParty(party);
                    c.write(CWvsContext.PartyPacket.partyCreated(party));
                    c.write(CWvsContext.ExpeditionPacket.expeditionStatus(World.Party.getExped(party.getExpeditionId()), true, false));
                } else {
                    c.write(CWvsContext.ExpeditionPacket.expeditionError(0, ""));
                }
                break;
            case 77://65
            case 135:
                name = iPacket.DecodeString();
                int theCh = World.Find.findChannel(name);
                if (theCh > 0) {
                    User invited = ChannelServer.getInstance(theCh).getPlayerStorage().getCharacterByName(name);
                    MapleParty party = c.getPlayer().getParty();
                    if ((invited != null) && (invited.getParty() == null) && (party != null) && (party.getExpeditionId() > 0)) {
                        MapleExpedition me = World.Party.getExped(party.getExpeditionId());
                        if ((me != null) && (me.getAllMembers() < me.getType().maxMembers) && (invited.getLevel() <= me.getType().maxLevel) && (invited.getLevel() >= me.getType().minLevel)) {
                            c.write(CWvsContext.ExpeditionPacket.expeditionError(7, invited.getName()));
                            invited.getClient().write(CWvsContext.ExpeditionPacket.expeditionInvite(c.getPlayer(), me.getType().exped));
                        } else {
                            c.write(CWvsContext.ExpeditionPacket.expeditionError(3, invited.getName()));
                        }
                    } else {
                        c.write(CWvsContext.ExpeditionPacket.expeditionError(2, name));
                    }
                } else {
                    c.write(CWvsContext.ExpeditionPacket.expeditionError(0, name));
                }
                break;
            case 78://66
            case 136:
                name = iPacket.DecodeString();
                int action = iPacket.DecodeInteger();
                int theChh = World.Find.findChannel(name);
                if (theChh <= 0) {
                    break;
                }
                User cfrom = ChannelServer.getInstance(theChh).getPlayerStorage().getCharacterByName(name);
                if ((cfrom != null) && (cfrom.getParty() != null) && (cfrom.getParty().getExpeditionId() > 0)) {
                    MapleParty party = cfrom.getParty();
                    exped = World.Party.getExped(party.getExpeditionId());
                    if ((exped != null) && (action == 8)) {
                        if ((c.getPlayer().getLevel() <= exped.getType().maxLevel) && (c.getPlayer().getLevel() >= exped.getType().minLevel) && (exped.getAllMembers() < exped.getType().maxMembers)) {
                            int partyId = exped.getFreeParty();
                            if (partyId < 0) {
                                c.write(CWvsContext.PartyPacket.partyStatusMessage(21, null));
                            } else if (partyId == 0) {
                                party = World.Party.createPartyAndAdd(new MaplePartyCharacter(c.getPlayer()), exped.getId());
                                c.getPlayer().setParty(party);
                                c.write(CWvsContext.PartyPacket.partyCreated(party));
                                c.write(CWvsContext.ExpeditionPacket.expeditionStatus(exped, true, false));
                                World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionJoined(c.getPlayer().getName()), null);
                                World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionUpdate(exped.getIndex(party.getId()), party), null);
                            } else {
                                c.getPlayer().setParty(World.Party.getParty(partyId));
                                World.Party.updateParty(partyId, PartyOperation.JOIN, new MaplePartyCharacter(c.getPlayer()));
                                c.getPlayer().receivePartyMemberHP();
                                c.getPlayer().updatePartyMemberHP();
                                c.write(CWvsContext.ExpeditionPacket.expeditionStatus(exped, false, false));
                                World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionJoined(c.getPlayer().getName()), null);
                            }
                        } else {
                            c.write(CWvsContext.ExpeditionPacket.expeditionError(3, cfrom.getName()));
                        }
                    } else if (action == 9) {
                        cfrom.getClient().write(CWvsContext.PartyPacket.partyStatusMessage(23, c.getPlayer().getName()));
                    }
                }
                break;
            case 79://67
            case 137:
                part = c.getPlayer().getParty();
                if ((part == null) || (part.getExpeditionId() <= 0)) {
                    break;
                }
                exped = World.Party.getExped(part.getExpeditionId());
                if (exped != null) {
                    if (GameConstants.isDojo(c.getPlayer().getMapId())) {
                        Event_DojoAgent.failed(c.getPlayer());
                    }
                    if (exped.getLeader() == c.getPlayer().getId()) {
                        World.Party.disbandExped(exped.getId());
                        if (c.getPlayer().getEventInstance() != null) {
                            c.getPlayer().getEventInstance().disbandParty();
                        }
                    } else if (part.getLeader().getId() == c.getPlayer().getId()) {
                        World.Party.updateParty(part.getId(), PartyOperation.DISBAND, new MaplePartyCharacter(c.getPlayer()));
                        if (c.getPlayer().getEventInstance() != null) {
                            c.getPlayer().getEventInstance().disbandParty();
                        }
                        World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionLeft(c.getPlayer().getName()), null);
                    } else {
                        World.Party.updateParty(part.getId(), PartyOperation.LEAVE, new MaplePartyCharacter(c.getPlayer()));
                        if (c.getPlayer().getEventInstance() != null) {
                            c.getPlayer().getEventInstance().leftParty(c.getPlayer());
                        }
                        World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionLeft(c.getPlayer().getName()), null);
                    }
                    if (c.getPlayer().getPyramidSubway() != null) {
                        c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                    }
                    c.getPlayer().setParty(null);
                }
                break;
            case 80://68
            case 138:
                part = c.getPlayer().getParty();
                if ((part == null) || (part.getExpeditionId() <= 0)) {
                    break;
                }
                exped = World.Party.getExped(part.getExpeditionId());
                if ((exped != null) && (exped.getLeader() == c.getPlayer().getId())) {
                    cid = iPacket.DecodeInteger();
                    for (i$ = exped.getParties().iterator(); i$.hasNext();) {
                        int i = ((Integer) i$.next()).intValue();
                        MapleParty par = World.Party.getParty(i);
                        if (par != null) {
                            MaplePartyCharacter expelled = par.getMemberById(cid);
                            if (expelled != null) {
                                if ((expelled.isOnline()) && (GameConstants.isDojo(c.getPlayer().getMapId()))) {
                                    Event_DojoAgent.failed(c.getPlayer());
                                }
                                World.Party.updateParty(i, PartyOperation.EXPEL, expelled);
                                if ((c.getPlayer().getEventInstance() != null)
                                        && (expelled.isOnline())) {
                                    c.getPlayer().getEventInstance().disbandParty();
                                }

                                if ((c.getPlayer().getPyramidSubway() != null) && (expelled.isOnline())) {
                                    c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                                }
                                World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionLeft(expelled.getName()), null);
                                break;
                            }
                        }
                    }
                }
                break;
            case 81://69
            case 139:
                part = c.getPlayer().getParty();
                if ((part == null) || (part.getExpeditionId() <= 0)) {
                    break;
                }
                exped = World.Party.getExped(part.getExpeditionId());
                if ((exped != null) && (exped.getLeader() == c.getPlayer().getId())) {
                    MaplePartyCharacter newleader = part.getMemberById(iPacket.DecodeInteger());
                    if (newleader != null) {
                        World.Party.updateParty(part.getId(), PartyOperation.CHANGE_LEADER, newleader);
                        exped.setLeader(newleader.getId());
                        World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionLeaderChanged(0), null);
                    }
                }
                break;
            case 82://70
            case 140:
                part = c.getPlayer().getParty();
                if ((part == null) || (part.getExpeditionId() <= 0)) {
                    break;
                }
                exped = World.Party.getExped(part.getExpeditionId());
                if ((exped != null) && (exped.getLeader() == c.getPlayer().getId())) {
                    cid = iPacket.DecodeInteger();
                    for (i$ = exped.getParties().iterator(); i$.hasNext();) {
                        int i = ((Integer) i$.next()).intValue();
                        MapleParty par = World.Party.getParty(i);
                        if (par != null) {
                            MaplePartyCharacter newleader = par.getMemberById(cid);
                            if ((newleader != null) && (par.getId() != part.getId())) {
                                World.Party.updateParty(par.getId(), PartyOperation.CHANGE_LEADER, newleader);
                            }
                        }
                    }
                }
                break;
            case 83://71
            case 141:
                part = c.getPlayer().getParty();
                if ((part == null) || (part.getExpeditionId() <= 0)) {
                    break;
                }
                exped = World.Party.getExped(part.getExpeditionId());
                if ((exped != null) && (exped.getLeader() == c.getPlayer().getId())) {
                    int partyIndexTo = iPacket.DecodeInteger();
                    if ((partyIndexTo < exped.getType().maxParty) && (partyIndexTo <= exped.getParties().size())) {
                        cid = iPacket.DecodeInteger();
                        for (i$ = exped.getParties().iterator(); i$.hasNext();) {
                            int i = ((Integer) i$.next()).intValue();
                            MapleParty par = World.Party.getParty(i);
                            if (par != null) {
                                MaplePartyCharacter expelled = par.getMemberById(cid);
                                if ((expelled != null) && (expelled.isOnline())) {
                                    User chr = World.getStorage(expelled.getChannel()).getCharacterById(expelled.getId());
                                    if (chr == null) {
                                        break;
                                    }
                                    if (partyIndexTo < exped.getParties().size()) {
                                        MapleParty party = World.Party.getParty((exped.getParties().get(partyIndexTo)).intValue());
                                        if ((party == null) || (party.getMembers().size() >= 8)) {
                                            c.getPlayer().dropMessage(5, "Invalid party.");
                                            break;
                                        }
                                    }
                                    if (GameConstants.isDojo(c.getPlayer().getMapId())) {
                                        Event_DojoAgent.failed(c.getPlayer());
                                    }
                                    World.Party.updateParty(i, PartyOperation.EXPEL, expelled);
                                    if (partyIndexTo < exped.getParties().size()) {
                                        MapleParty party = World.Party.getParty((exped.getParties().get(partyIndexTo)).intValue());
                                        if ((party != null) && (party.getMembers().size() < 8)) {
                                            World.Party.updateParty(party.getId(), PartyOperation.JOIN, expelled);
                                            chr.receivePartyMemberHP();
                                            chr.updatePartyMemberHP();
                                            chr.getClient().write(CWvsContext.ExpeditionPacket.expeditionStatus(exped, true, false));
                                        }
                                    } else {
                                        MapleParty party = World.Party.createPartyAndAdd(expelled, exped.getId());
                                        chr.setParty(party);
                                        chr.getClient().write(CWvsContext.PartyPacket.partyCreated(party));
                                        chr.getClient().write(CWvsContext.ExpeditionPacket.expeditionStatus(exped, true, false));
                                        World.Party.expedPacket(exped.getId(), CWvsContext.ExpeditionPacket.expeditionUpdate(exped.getIndex(party.getId()), party), null);
                                    }
                                    if ((c.getPlayer().getEventInstance() != null)
                                            && (expelled.isOnline())) {
                                        c.getPlayer().getEventInstance().disbandParty();
                                    }

                                    if (c.getPlayer().getPyramidSubway() == null) {
                                        break;
                                    }
                                    c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                                    break;
                                }
                            }
                        }
                    }

                }

                break;
            default:
                if (!c.getPlayer().isGM()) {
                    break;
                }
                LogHelper.GENERAL_EXCEPTION.get().info("[OnExpeditionRequest] Unknow action type: " + mode);
        }
    }
}
