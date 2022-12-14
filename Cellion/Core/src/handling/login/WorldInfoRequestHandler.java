package handling.login;

import java.util.List;

import client.ClientSocket;
import constants.ServerConstants;
import constants.WorldConstants;
import net.InPacket;
import server.maps.objects.User;
import tools.packet.CLogin;
import net.ProcessPacket;

public final class WorldInfoRequestHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        sendServerList(iPacket, c);
    }

    public static void sendServerList(InPacket iPacket, ClientSocket c) {
        //c.write(LoginPacket.changeBackground());

        for (WorldConstants.WorldOption servers : WorldConstants.WorldOption.values()) {
            if (WorldConstants.WorldOption.getById(servers.getWorld()).show()) {
                c.SendPacket(CLogin.OnWorldInformation(servers.getWorld()));
            }
        }
        c.SendPacket(CLogin.OnWorldInformation(-1));

        if (c.isLoggedIn()) {
            boolean hasCharacters = false;
            for (int world = 0; world < WorldConstants.WorldOption.values().length; world++) {
                final List<User> chars = c.loadCharacters(world);
                if (chars != null) {
                    hasCharacters = true;
                    break;
                }
            }
            if (ServerConstants.TESPIA) {
                for (WorldConstants.TespiaWorldOption value : WorldConstants.TespiaWorldOption.values()) {
                    String world = value.getWorld();
                }
            }
            if (!hasCharacters) {
                c.SendPacket(CLogin.enableRecommended(WorldConstants.WorldOption.recommended));
            }
            if (WorldConstants.WorldOption.recommended >= 0) {
                c.SendPacket(CLogin.sendRecommended(WorldConstants.WorldOption.recommended, WorldConstants.WorldOption.recommendedmsg));
            }
        } else { //Token login, world list is requested before account validation.
            c.SendPacket(CLogin.enableRecommended(WorldConstants.WorldOption.recommended));
            if (WorldConstants.WorldOption.recommended >= 0) {
                c.SendPacket(CLogin.sendRecommended(WorldConstants.WorldOption.recommended, WorldConstants.WorldOption.recommendedmsg));
            }
        }
    }

}
