package handling.game;

import client.ClientSocket;
import constants.GameConstants;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.World;
import handling.world.World.Find;
import static handling.world.World.getStorage;
import service.ChannelServer;
import server.maps.Event_DojoAgent;
import server.maps.objects.User;
import server.quest.Quest;
import net.InPacket;
import tools.packet.WvsContext;
import tools.packet.WvsContext.PartyPacket;
import net.ProcessPacket;
import tools.LogHelper;

/**
 *
 * @author
 */
public class PartyOperationHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    public static class PartyOperations {

        public static final int LoadParty = 0,
                CreateNewParty = 1,
                WithdrawParty = 2,
                JoinParty = 3,
                InviteParty = 4,
                InviteIntrusion = 5,
                KickParty = 6,
                ChangePartyBoss = 7,
                ApplyParty = 8,
                SetAppliable = 9,
                ClearIntrusion = 10,
                CreateNewParty_Group = 11,
                JoinParty_Group = 12,
                PartySetting = 13,
                LoadStarPlanetPoint = 14,
                InvitePartyAccepted = 42;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        int operation = iPacket.DecodeByte();
        MapleParty party = c.getPlayer().getParty();
        MaplePartyCharacter partyplayer = new MaplePartyCharacter(c.getPlayer());

        switch (operation) {
            case PartyOperations.CreateNewParty:
                boolean isPrivate = iPacket.DecodeByte() != 0;
                String partyName = iPacket.DecodeString();
                if (party == null) {
                    party = World.Party.createParty(partyplayer, partyName, isPrivate);
                    c.getPlayer().setParty(party);
                    c.SendPacket(WvsContext.PartyPacket.partyCreated(party));
                } else {
                    if (party.getExpeditionId() > 0) {
                        c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                        return;
                    }
                    if ((partyplayer.equals(party.getLeader())) && (party.getMembers().size() == 1)) {
                        c.SendPacket(WvsContext.PartyPacket.partyCreated(party));
                    } else {
                        c.getPlayer().dropMessage(5, "You can't create a party as you are already in one");
                    }
                }
                break;
            case PartyOperations.WithdrawParty://dispand and leave?
                if (party == null) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    return;
                }
                if (partyplayer.equals(party.getLeader())) {
                    if (GameConstants.isDojo(c.getPlayer().getMapId())) {
                        Event_DojoAgent.failed(c.getPlayer());
                    }
                    if (c.getPlayer().getPyramidSubway() != null) {
                        c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                    }
                    World.Party.updateParty(party.getId(), PartyOperation.DISBAND, partyplayer);
                    if (c.getPlayer().getEventInstance() != null) {
                        c.getPlayer().getEventInstance().disbandParty();
                    }
                } else {
                    if (GameConstants.isDojo(c.getPlayer().getMapId())) {
                        Event_DojoAgent.failed(c.getPlayer());
                    }
                    if (c.getPlayer().getPyramidSubway() != null) {
                        c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                    }
                    World.Party.updateParty(party.getId(), PartyOperation.LEAVE, partyplayer);
                    if (c.getPlayer().getEventInstance() != null) {
                        c.getPlayer().getEventInstance().leftParty(c.getPlayer());
                    }
                }
                c.getPlayer().setParty(null);
                break;
            case PartyOperations.PartySetting:
                MapleParty toChange = c.getPlayer().getParty();
                if (toChange == null) {
                    break;
                }
                isPrivate = iPacket.DecodeByte() != 0;
                String newName = iPacket.DecodeString();
                party.setPrivacy(isPrivate);
                party.setName(newName);
                for (MaplePartyCharacter partyMember : party.getMembers()) {
                    if (partyMember == null) {
                        continue;
                    }
                    int ch = Find.findChannel(partyMember.getName());
                    if (ch > 0) {
                        User chr = getStorage(ch).getCharacterByName(partyMember.getName());
                        if (chr != null) {
                            chr.getClient().SendPacket(PartyPacket.changePartySettings(newName, isPrivate));
                        }
                    }
                }
            case PartyOperations.JoinParty:
                int partyid = iPacket.DecodeInt();
                if (party == null) {
                    party = World.Party.getParty(partyid);
                    if (party != null) {
                        if (party.getExpeditionId() > 0) {
                            c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                            return;
                        }
                        if ((party.getMembers().size() < 6) && (c.getPlayer().getQuestNoAdd(Quest.getInstance(122901)) == null)) {
                            c.getPlayer().setParty(party);
                            World.Party.updateParty(party.getId(), PartyOperation.JOIN, partyplayer);
                            c.getPlayer().receivePartyMemberHP();
                            c.getPlayer().updatePartyMemberHP();
                        } else {
                            c.SendPacket(WvsContext.PartyPacket.partyStatusMessage(31, null));
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "The party you are trying to join does not exist");
                    }
                } else {
                    c.getPlayer().dropMessage(5, "You can't join the party as you are already in one");
                }
                break;
            case PartyOperations.InviteParty:
                String theName = iPacket.DecodeString();
                if (party == null) {
                    party = World.Party.createParty(partyplayer, theName, false);
                    c.getPlayer().setParty(party);
                    c.SendPacket(WvsContext.PartyPacket.partyCreated(party));
                }
                int theCh = World.Find.findChannel(theName);
                if (theCh > 0) {
                    User invited = ChannelServer.getInstance(theCh).getPlayerStorage().getCharacterByName(theName);
                    if ((invited != null) && (invited.getParty() == null) && (invited.getQuestNoAdd(Quest.getInstance(122901)) == null)) {
                        if (party.getExpeditionId() > 0) {
                            c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                            return;
                        }
                        if (party.getMembers().size() < 6) {
                            c.SendPacket(WvsContext.PartyPacket.partyStatusMessage(37, invited.getName()));
                            invited.getClient().SendPacket(WvsContext.PartyPacket.partyInvite(c.getPlayer()));
                        } else {
                            c.SendPacket(WvsContext.PartyPacket.partyStatusMessage(31, null));
                        }
                    } else {
                        c.getPlayer().dropMessage(6, "The person you are trying to invite is already in a party.");
                    }
                } else { //To do: To this properly
                       c.SendPacket(WvsContext.PartyPacket.partyStatusMessage(34, null));
                }
                break;
            case PartyOperations.KickParty:
                if ((party == null) || (partyplayer == null) || (!partyplayer.equals(party.getLeader()))) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    return;
                }
                MaplePartyCharacter expelled = party.getMemberById(iPacket.DecodeInt());
                if (expelled != null) {
                    if ((GameConstants.isDojo(c.getPlayer().getMapId())) && (expelled.isOnline())) {
                        Event_DojoAgent.failed(c.getPlayer());
                    }
                    if ((c.getPlayer().getPyramidSubway() != null) && (expelled.isOnline())) {
                        c.getPlayer().getPyramidSubway().fail(c.getPlayer());
                    }
                    World.Party.updateParty(party.getId(), PartyOperation.EXPEL, expelled);
                    if (c.getPlayer().getEventInstance() != null) {
                        if (expelled.isOnline()) {
                            c.getPlayer().getEventInstance().disbandParty();
                        }
                    }
                }
                break;
            case PartyOperations.ChangePartyBoss:
                if (party == null) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    return;
                }
                MaplePartyCharacter newleader = party.getMemberById(iPacket.DecodeInt());
                if ((newleader != null) && (partyplayer.equals(party.getLeader()))) {
                    World.Party.updateParty(party.getId(), PartyOperation.CHANGE_LEADER, newleader);
                }
                break;
            case 66://was 7
                if (party != null) {
                    if ((c.getPlayer().getEventInstance() != null) || (c.getPlayer().getPyramidSubway() != null) || (party.getExpeditionId() > 0) || (GameConstants.isDojo(c.getPlayer().getMapId()))) {
                        c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                        return;
                    }
                    if (partyplayer.equals(party.getLeader())) {
                        World.Party.updateParty(party.getId(), PartyOperation.DISBAND, partyplayer);
                    } else {
                        World.Party.updateParty(party.getId(), PartyOperation.LEAVE, partyplayer);
                    }
                    c.getPlayer().setParty(null);
                }
                int partyid_ = iPacket.DecodeInt();
                party = World.Party.getParty(partyid_);
                if ((party == null) || (party.getMembers().size() >= 8)) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    return;
                }
                User cfrom = c.getPlayer().getMap().getCharacterById(party.getLeader().getId());
                if ((cfrom != null) && (cfrom.getQuestNoAdd(Quest.getInstance(122900)) == null)) {
                    c.SendPacket(WvsContext.PartyPacket.partyStatusMessage(50, c.getPlayer().getName()));
                    cfrom.getClient().SendPacket(WvsContext.PartyPacket.partyRequestInvite(c.getPlayer()));
                } else {
                    c.getPlayer().dropMessage(5, "Player was not found or player is not accepting party requests.");
                }
                break;
            case PartyOperations.ApplyParty:
                if (iPacket.DecodeByte() > 0) {
                    c.getPlayer().getQuestRemove(Quest.getInstance(122900));
                } else {
                    c.getPlayer().getQuestNAdd(Quest.getInstance(122900));
                }
                break;
            default:
                LogHelper.GENERAL_EXCEPTION.get().info("Unhandled Party function: " + operation);
        }
    }

}
