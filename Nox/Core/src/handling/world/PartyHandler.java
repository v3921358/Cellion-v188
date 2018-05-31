package handling.world;

import java.util.ArrayList;
import java.util.List;

import client.ClientSocket;
import enums.FieldLimitType;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.WvsContext;

public class PartyHandler {

    public static void MemberSearch(InPacket iPacket, ClientSocket c) {
        if ((c.getPlayer().isInBlockedMap()) || (FieldLimitType.VipRock.checkFlag(c.getPlayer().getMap()))) {
            c.getPlayer().dropMessage(5, "You may not do party search here.");
            return;
        }
        c.SendPacket(WvsContext.PartyPacket.showMemberSearch(c.getPlayer().getMap().getCharacters()));
    }

    public static final void PartySearch(InPacket iPacket, ClientSocket c) {
        if ((c.getPlayer().isInBlockedMap()) || (FieldLimitType.VipRock.checkFlag(c.getPlayer().getMap()))) {
            c.getPlayer().dropMessage(5, "You may not do party search here.");
            return;
        }
        List<MapleParty> parties = new ArrayList<>();
        for (User chr : c.getPlayer().getMap().getCharacters()) {
            if ((chr.getParty() != null)
                    && (chr.getParty().getId() != c.getPlayer().getParty().getId()) && (!parties.contains(chr.getParty()))) {
                parties.add(chr.getParty());
            }
        }

        c.SendPacket(WvsContext.PartyPacket.showPartySearch(parties));
    }
}
