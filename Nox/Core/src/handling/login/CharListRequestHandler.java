package handling.login;

import client.MapleCharacterCreationUtil;
import java.util.List;

import client.ClientSocket;
import constants.ServerConstants;
import constants.WorldConstants;
import constants.WorldConstants.WorldOption;
import handling.world.World;
import java.util.Calendar;
import service.ChannelServer;
import net.InPacket;
import server.maps.objects.User;
import tools.packet.WvsContext;
import tools.packet.CLogin;
import net.ProcessPacket;
import tools.packet.PacketHelper;

public final class CharListRequestHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        iPacket.DecodeByte();
        iPacket.DecodeByte();
        String sPassport = iPacket.DecodeString(); // u nid 2 add passport 2 sql n get acct from this instead of user/pw in checkloginhandler
        iPacket.Decode(16); // MachineID
        iPacket.DecodeInt();
        iPacket.DecodeByte();
        final int server = iPacket.DecodeByte();
        final int channel = iPacket.DecodeByte() + 1;
        int dwIP = iPacket.DecodeInt();

        if (!World.isChannelAvailable(channel, server) || !WorldConstants.WorldOption.isExists(server)) {
            c.SendPacket(CLogin.getLoginFailed(10)); //cannot process so many
            return;
        }

        WorldOption world = WorldConstants.WorldOption.getById(server);

        if (!world.isAvailable() && !(c.isGm() && server == WorldConstants.gmserver)) {
            c.SendPacket(WvsContext.broadcastMsg(1, "We are sorry, but " + WorldConstants.getNameById(server) + " is currently not available. \r\nPlease try another world."));
            c.SendPacket(CLogin.getLoginFailed(1)); //Shows no message, but it is used to unstuck
            return;
        }

        final boolean ipBan = c.hasBannedIP();
        final boolean macBan = c.hasBannedMac();

        int loginok = c.LoginPassword(sPassport);

        final Calendar tempbannedTill = c.getTempBanCalendar();

        if (loginok == 0 && (ipBan || macBan) && !c.isGm()) {
            loginok = 3;
            if (macBan) {
                // this is only an ipban o.O" - maybe we should refactor this a bit so it's more readable
                User.ban(c.GetIP().split(":")[0], "Enforcing account ban, account " + c.getAccountName(), false, 4, false);
                return;
            }
        }
        if (loginok != 0) {
            if (loginok == 3) {
                c.SendPacket(WvsContext.broadcastMsg(1, c.showBanReason(c.getAccountName(), true)));
                c.SendPacket(CLogin.getLoginFailed(1)); //Shows no message, used for unstuck the login button
                return;
            } else {
                c.SendPacket(CLogin.getLoginFailed(loginok));
                return;
            }
        } else if (tempbannedTill.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {
            c.clearInformation();
            c.SendPacket(CLogin.getTempBan(PacketHelper.getTime(tempbannedTill.getTimeInMillis()), c.getBanReason()));
            return;
        } else {
            c.loginAttempt = 0;
            LoginWorker.registerClient(c);
        }

        //if (!c.isLoggedIn()) {
        //    c.Close();
        //    return;
        //}
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

    private void handleBurningEvent(ClientSocket c, List<User> chars) {
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
