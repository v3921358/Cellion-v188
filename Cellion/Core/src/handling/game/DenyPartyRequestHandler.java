package handling.game;

import client.ClientSocket;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.World;
import server.maps.objects.User;
import server.quest.Quest;
import net.InPacket;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class DenyPartyRequestHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        int action = iPacket.DecodeByte();
        System.out.println("[Debug] Party Result Operation: " + action);
        if ((action == 50)) {
            User chr = c.getPlayer().getMap().getCharacterById(iPacket.DecodeInt());
            if ((chr != null) && (chr.getParty() == null) && (c.getPlayer().getParty() != null) && (c.getPlayer().getParty().getLeader().getId() == c.getPlayer().getId()) && (c.getPlayer().getParty().getMembers().size() < 6) && (c.getPlayer().getParty().getExpeditionId() <= 0) && (chr.getQuestNoAdd(Quest.getInstance(122901)) == null) && (c.getPlayer().getQuestNoAdd(Quest.getInstance(122900)) == null)) {
                chr.setParty(c.getPlayer().getParty());
                World.Party.updateParty(c.getPlayer().getParty().getId(), PartyOperation.JOIN, new MaplePartyCharacter(chr));
                chr.receivePartyMemberHP();
                chr.updatePartyMemberHP();
            }
            return;
        }
        int partyid = iPacket.DecodeInt();
        if ((c.getPlayer().getParty() == null) && (c.getPlayer().getQuestNoAdd(Quest.getInstance(122901)) == null)) {
            MapleParty party = World.Party.getParty(partyid);
            if (party != null) {
                if (party.getExpeditionId() > 0) {
                    c.getPlayer().dropMessage(5, "You may not do party operations while in a raid.");
                    return;
                }
                switch (action) {
                    case 42://case 38:
                        if (party.getMembers().size() < 6) {
                            c.getPlayer().setParty(party);
                            World.Party.updateParty(partyid, PartyOperation.JOIN, new MaplePartyCharacter(c.getPlayer()));
                            c.getPlayer().receivePartyMemberHP();
                            c.getPlayer().updatePartyMemberHP();
                        } else {
                            c.SendPacket(WvsContext.PartyPacket.partyStatusMessage(31, null));
                        }
                        break;
                    case 33:
                        User cfrom = c.getChannelServer().getPlayerStorage().getCharacterById(party.getLeader().getId());
                        if (cfrom != null) { //Currently giving "Your request for a party didn't work due to an unexpected error"
                            cfrom.getClient().SendPacket(WvsContext.PartyPacket.partyStatusMessage(34, c.getPlayer().getName()));
                        }
                        break;
                    case 41: //Decline invite
                        //To Do properly and better.. this is ugly
                        User cfrom2 = c.getChannelServer().getPlayerStorage().getCharacterById(party.getLeader().getId());
                        cfrom2.dropMessage(5, c.getPlayer().getName() + " has declined your party invite");
                        break;
                    default:
                        break;
                }
            } else {
                c.getPlayer().dropMessage(5, "The party you are trying to join does not exist.");
            }
        } else {
            c.getPlayer().dropMessage(5, "You can't join the party as you are already in one.");
        }
    }

}
