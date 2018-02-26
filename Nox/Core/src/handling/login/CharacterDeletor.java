package handling.login;

import java.util.List;

import client.MapleClient;
import handling.PacketThrottleLimits;
import net.InPacket;
import server.maps.objects.MapleCharacter;
import tools.packet.CLogin;
import netty.ProcessPacket;

@PacketThrottleLimits(
        FlagCount = 3,
        ResetTimeMillis = 1000 * 60 * 60, // 1 hour
        MinTimeMillisBetweenPackets = 5000,
        FunctionName = "CharacterDeletor",
        BanType = PacketThrottleLimits.PacketThrottleBanType.PermanentBan)
public final class CharacterDeletor implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    private static boolean loginFailCount(final MapleClient c) {
        c.loginAttempt++;
        return c.loginAttempt > 3;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        String secondPW = iPacket.DecodeString();
        if (secondPW == null) {
            if (iPacket.DecodeByte() > 0) { // Specific if user have second password or not
                secondPW = iPacket.DecodeString();
            }
            iPacket.DecodeString();
        }

        final int charId = iPacket.DecodeInteger();

        if (!c.login_Auth(charId) || !c.isLoggedIn() || loginFailCount(c)) {
            c.close();
            return; // Attempting to delete other character
        }
        byte state = 0;

        if (c.getSecondPassword() != null) { // On the server, there's a second password
            if (secondPW == null) { // Client's hacking
                c.close();
                return;
            } else if (!c.checkSecondPassword(secondPW, false)) { // Wrong Password
                state = 20;
            }
        }
        if (state == 0) {
            List<MapleCharacter> chars = c.loadCharacters(c.getWorld());
            for (int i = 0; i < chars.size(); i++) {
                if (chars.get(i).getCharListPosition() > c.loadCharacterById(charId).getCharListPosition()) {
                    chars.get(i).setCharListPosition(chars.get(i).getCharListPosition() - 1);
                    chars.get(i).updateCharlistPosition(chars.get(i).getCharListPosition());
                    i = 0;
                }
            }
            //This is somewhat useless, but it's here becuase characters aren't actually deleted. (#ProLogging hehe) 
            //So we set the burning effect to false to make sure that players can make new ones
            MapleCharacter deletedchar = c.loadCharacterById(charId);
            if (!deletedchar.updateBurning(charId, false)) {
                state = 20;
            } else {
                state = (byte) c.deleteCharacter(charId);
            }
        }
        c.write(CLogin.deleteCharResponse(charId, state));
    }

}
