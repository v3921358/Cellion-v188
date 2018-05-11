package handling.login;

import client.MapleCharacterCreationUtil;
import java.util.List;

import client.Client;
import constants.ServerConstants;
import constants.WorldConstants;
import constants.WorldConstants.WorldOption;
import handling.world.World;
import service.ChannelServer;
import net.InPacket;
import server.maps.objects.User;
import tools.packet.CWvsContext;
import tools.packet.CLogin;
import net.ProcessPacket;

public final class CharListRequestHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        if (!c.isLoggedIn()) {
            c.Close();
            return;
        }
        iPacket.DecodeByte(); //2?
        final int server = iPacket.DecodeByte();
        final int channel = iPacket.DecodeByte() + 1;

        if (!World.isChannelAvailable(channel, server) || !WorldConstants.WorldOption.isExists(server)) {
            c.SendPacket(CLogin.getLoginFailed(10)); //cannot process so many
            return;
        }

        WorldOption world = WorldConstants.WorldOption.getById(server);

        if (!world.isAvailable() && !(c.isGm() && server == WorldConstants.gmserver)) {
            c.SendPacket(CWvsContext.broadcastMsg(1, "We are sorry, but " + WorldConstants.getNameById(server) + " is currently not available. \r\nPlease try another world."));
            c.SendPacket(CLogin.getLoginFailed(1)); //Shows no message, but it is used to unstuck
            return;
        }
        final List<User> chars = c.loadCharacters(server);
        if (chars != null && ChannelServer.getInstance(channel) != null) {
            c.setWorld(server);
            c.setChannel(channel);
            if (c.isGm() || ServerConstants.BURNING_CHARACTER_EVENT) {
                handleBurningEvent(c, chars);
            }
            c.SendPacket(CLogin.getCharList(c, c.getSecondPassword(), chars,
                    MapleCharacterCreationUtil.getCharacterSlots(c.getAccID(), c.getWorld()), world == WorldOption.Reboot));
        } else {
            c.Close();
        }
        c.SendPacket(CLogin.getJobListPacket());
    }

    private void handleBurningEvent(Client c, List<User> chars) {
        User[] characters = chars.toArray(new User[chars.size()]);
        boolean hasBurningCharacter = false;
        int burningCharacterId = 0;
        for (int i = 0; i < characters.length; i++) {
            if (characters[i].isBurning()) {
                hasBurningCharacter = true;
                burningCharacterId = characters[i].getId();
            }
        }
        if (hasBurningCharacter) {
            c.SendPacket(CLogin.burningEventEffect((byte) 1, burningCharacterId));
        }
        if (!hasBurningCharacter) {
            for (int i = 0; i < characters.length; i++) {
                c.SendPacket(CLogin.burningEventEffect((byte) 1, characters[i].getId()));
            }
        }
    }
}
