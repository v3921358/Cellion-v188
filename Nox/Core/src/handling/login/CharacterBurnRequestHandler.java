package handling.login;

import client.MapleClient;
import constants.ServerConstants;
import java.util.List;
import net.InPacket;
import server.maps.objects.MapleCharacter;
import tools.packet.CWvsContext;
import tools.packet.CLogin;
import netty.ProcessPacket;

/*
 * @Author Novak
 *
 */
public final class CharacterBurnRequestHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        String errormsg = "The Character Burning Event is currently unavailable. Check our website to see when your characters can burn again.";
        if (!ServerConstants.BURNING_CHARACTER_EVENT || iPacket.DecodeByte() != 0) {
            c.write(CWvsContext.broadcastMsg(errormsg));
            return;
        }
        int accountId = iPacket.DecodeInteger();
        if (c == null || accountId != c.getAccID()) {
            return;
        }
        if (!canBurn(c)) {
            return;
        }
        int characterId = iPacket.DecodeInteger();
        MapleCharacter character = c.loadCharacterById(characterId);
        if (character.getAccountID() != c.getAccID()) {
            return;
        }
        if (!character.updateBurning(characterId, true)) {
            c.write(CWvsContext.broadcastMsg(errormsg));
        }
        c.write(CLogin.burningEventEffect((byte) 1, characterId));
    }

    private boolean canBurn(MapleClient c) {
        final List<MapleCharacter> chars = c.loadCharacters(c.getWorld());
        MapleCharacter[] characters = chars.toArray(new MapleCharacter[chars.size()]);
        for (int i = 0; i < characters.length; i++) {
            if (characters[i].isBurning()) {
                return false;
            }
        }
        return true;
    }
}
