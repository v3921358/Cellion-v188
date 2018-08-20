package handling.login;

import client.ClientSocket;
import constants.ServerConstants;
import java.util.List;
import net.InPacket;
import server.maps.objects.User;
import tools.packet.WvsContext;
import tools.packet.CLogin;
import net.ProcessPacket;

/*
 * @Author Novak
 *
 */
public final class CharacterBurnRequestHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        String errormsg = "The Character Burning Event is currently unavailable. Check our website to see when your characters can burn again.";
        if (!ServerConstants.BURNING_CHARACTER_EVENT || iPacket.DecodeByte() != 0) {
            c.SendPacket(WvsContext.broadcastMsg(errormsg));
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
            c.SendPacket(WvsContext.broadcastMsg(errormsg));
        }
        c.SendPacket(CLogin.burningEventEffect((byte) 1, characterId));
    }

    private boolean canBurn(ClientSocket c) {
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
