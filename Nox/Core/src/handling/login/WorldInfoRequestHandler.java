package handling.login;

import java.util.List;

import client.Client;
import constants.ServerConstants;
import constants.WorldConstants;
import net.InPacket;
import server.maps.objects.User;
import tools.packet.CLogin;
import net.ProcessPacket;

public final class WorldInfoRequestHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        sendServerList(iPacket, c);
    }

    public static void sendServerList(InPacket iPacket, Client c) {
        //c.write(LoginPacket.changeBackground());
        if (ServerConstants.TESPIA) {
            for (WorldConstants.TespiaWorldOption tespiaservers : WorldConstants.TespiaWorldOption.values()) {
                if (WorldConstants.TespiaWorldOption.getById(tespiaservers.getWorld()).show() && WorldConstants.TespiaWorldOption.getById(tespiaservers.getWorld()) != null) {
                    c.SendPacket(CLogin.getServerList(Integer.parseInt(tespiaservers.getWorld().replace("t", ""))));
                }
            }
        } else {
            for (WorldConstants.WorldOption servers : WorldConstants.WorldOption.values()) {
                if (WorldConstants.WorldOption.getById(servers.getWorld()).show() && servers != null) {
                    c.SendPacket(CLogin.getServerList(servers.getWorld()));
                }
            }
        }
        c.SendPacket(CLogin.getEndOfServerList());
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
    }

}
