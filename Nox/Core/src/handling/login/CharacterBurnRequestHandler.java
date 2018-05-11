package handling.login;

import client.Client;
import constants.ServerConstants;
import java.util.List;
import net.InPacket;
import server.maps.objects.User;
import tools.packet.CWvsContext;
import tools.packet.CLogin;
import net.ProcessPacket;

/*
 * @Author Novak
 *
 */
public final class CharacterBurnRequestHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        String errormsg = "The Character Burning Event is currently unavailable. Check our website to see when your characters can burn again.";
        if (!ServerConstants.BURNING_CHARACTER_EVENT || iPacket.DecodeByte() != 0) {
            c.SendPacket(CWvsContext.broadcastMsg(errormsg));
            return;
        }
        int accountId = iPacket.DecodeInt();
        if (c == null || accountId != c.getAccID()) {
            return;
        }
        if (!canBurn(c)) {
            return;
        }
        int characterId = iPacket.DecodeInt();
        User character = c.loadCharacterById(characterId);
        if (character.getAccountID() != c.getAccID()) {
            return;
        }
        if (!character.updateBurning(characterId, true)) {
            c.SendPacket(CWvsContext.broadcastMsg(errormsg));
        }
        c.SendPacket(CLogin.burningEventEffect((byte) 1, characterId));
    }

    private boolean canBurn(Client c) {
        final List<User> chars = c.loadCharacters(c.getWorld());
        User[] characters = chars.toArray(new User[chars.size()]);
        for (int i = 0; i < characters.length; i++) {
            if (characters[i].isBurning()) {
                return false;
            }
        }
        return true;
    }
}
