package handling.game;

import client.MapleClient;
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
import server.quest.MapleQuest;
import net.InPacket;
import tools.packet.CWvsContext;
import tools.packet.CWvsContext.PartyPacket;
import netty.ProcessPacket;
import tools.LogHelper;

/**
 *
 * @author
 */
public class PartyOperationHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    public static class PartyOperations {

        public static final int createParty = 0x01;
        public static final int leaveParty = 0x02;
        public static final int changePartySettings = 0x0D;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        int operation = iPacket.DecodeByte();
        MapleParty party = c.getPlayer().getParty();
        MaplePartyCharacter partyplayer = new MaplePartyCharacter(c.getPlayer());

        switch (operation) {
            case PartyOperations.createParty:
                boolean isPrivate = iPacket.DecodeByte() != 0;
                String partyName = iPacket.DecodeString();
                if (party == null) {
                    party = World.Party.createParty(partyplayer, partyName, isPrivate);
                    c.getPlayer().setParty(party);
                    c.write(CWvsContext.PartyPacket.partyCreated(party));
                } else {
                    if (party.getExpeditionId() > 0) {
                        c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                        return;
                    }
                    if ((partyplayer.equals(party.getLeader())) && (party.getMembers().size() == 1)) {
                        c.write(CWvsContext.PartyPacket.partyCreated(party));
                    } else {
                        c.getPlayer().dropMessage(5, "You can't create a party as you are already in one");
                    }
                }
                break;
            case PartyOperations.leaveParty://dispand and leave?
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
            case PartyOperations.changePartySettings:
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
                            chr.getClient().write(PartyPacket.changePartySettings(newName, isPrivate));
                        }
                    }
                }
            case 3:
                int partyid = iPacket.DecodeInteger();
                if (party == null) {
                    party = World.Party.getParty(partyid);
                    if (party != null) {
                        if (party.getExpeditionId() > 0) {
                            c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                            return;
                        }
                        if ((party.getMembers().size() < 8) && (c.getPlayer().getQuestNoAdd(MapleQuest.getInstance(122901)) == null)) {
                            c.getPlayer().setParty(party);
                            World.Party.updateParty(party.getId(), PartyOperation.JOIN, partyplayer);
                            c.getPlayer().receivePartyMemberHP();
                            c.getPlayer().updatePartyMemberHP();
                        } else {
                            c.write(CWvsContext.PartyPacket.partyStatusMessage(22, null));
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "The party you are trying to join does not exist");
                    }
                } else {
                    c.getPlayer().dropMessage(5, "You can't join the party as you are already in one");
                }
                break;
            case 4:
                String theName = iPacket.DecodeString();
                if (party == null) {
                    party = World.Party.createParty(partyplayer, theName, false);
                    c.getPlayer().setParty(party);
                    c.write(CWvsContext.PartyPacket.partyCreated(party));
                }
                int theCh = World.Find.findChannel(theName);
                if (theCh > 0) {
                    User invited = ChannelServer.getInstance(theCh).getPlayerStorage().getCharacterByName(theName);
                    if ((invited != null) && (invited.getParty() == null) && (invited.getQuestNoAdd(MapleQuest.getInstance(122901)) == null)) {
                        if (party.getExpeditionId() > 0) {
                            c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                            return;
                        }
                        if (party.getMembers().size() < 6) {
                            c.write(CWvsContext.PartyPacket.partyStatusMessage(34, invited.getName()));
                            invited.getClient().write(CWvsContext.PartyPacket.partyInvite(c.getPlayer()));
                        } else {
                            c.write(CWvsContext.PartyPacket.partyStatusMessage(26, null));
                        }
                    } else {
                        c.getPlayer().dropMessage(6, "The person you are trying to invite is already in a party.");
                    }
                } else { //To do: To this properly
                    c.getPlayer().dropMessage(5, theName + " could not be found.");
                    //   c.write(CWvsContext.PartyPacket.partyStatusMessage(28, null));
                }
                break;
            case 6://was5
                if ((party == null) || (partyplayer == null) || (!partyplayer.equals(party.getLeader()))) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    return;
                }
                MaplePartyCharacter expelled = party.getMemberById(iPacket.DecodeInteger());
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
            case 7://was 6
                if (party == null) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    return;
                }
                MaplePartyCharacter newleader = party.getMemberById(iPacket.DecodeInteger());
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
                int partyid_ = iPacket.DecodeInteger();
                party = World.Party.getParty(partyid_);
                if ((party == null) || (party.getMembers().size() >= 8)) {
                    break;
                }
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    return;
                }
                User cfrom = c.getPlayer().getMap().getCharacterById(party.getLeader().getId());
                if ((cfrom != null) && (cfrom.getQuestNoAdd(MapleQuest.getInstance(122900)) == null)) {
                    c.write(CWvsContext.PartyPacket.partyStatusMessage(50, c.getPlayer().getName()));
                    cfrom.getClient().write(CWvsContext.PartyPacket.partyRequestInvite(c.getPlayer()));
                } else {
                    c.getPlayer().dropMessage(5, "Player was not found or player is not accepting party requests.");
                }
                break;
            case 8:
                if (iPacket.DecodeByte() > 0) {
                    c.getPlayer().getQuestRemove(MapleQuest.getInstance(122900));
                } else {
                    c.getPlayer().getQuestNAdd(MapleQuest.getInstance(122900));
                }
                break;
            default:
                LogHelper.GENERAL_EXCEPTION.get().info("Unhandled Party function: " + operation);
        }
    }

}
