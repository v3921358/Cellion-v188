package handling.world;

import client.ClientSocket;
import client.ClientSocket.MapleClientLoginState;
import service.ChannelServer;
import service.FarmServer;
import enums.FieldLimitType;
import server.maps.MapleMap;
import server.maps.objects.User;
import net.InPacket;
import service.AuctionServer;
import tools.packet.CField;
import tools.packet.WvsContext;

public class InterServerHandler {

    public static void enterFarm(final ClientSocket c, final User chr) {
        if (chr.hasBlockedInventory() || chr.getMap() == null || chr.getEventInstance() != null || c.getChannelServer() == null
                || FieldLimitType.UnableToMigrate.check(chr.getMap())) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }

        ChannelServer ch = ChannelServer.getInstance(c.getChannel());
        if (chr.getMessenger() != null) {
            MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(chr);
            World.Messenger.leaveMessenger(chr.getMessenger().getId(), messengerplayer);
        }
        PlayerBuffStorage.addBuffsToStorage(chr.getId(), chr.getAllBuffs());
        PlayerBuffStorage.addCooldownsToStorage(chr.getId(), chr.getCooldowns());
        PlayerBuffStorage.addDiseaseToStorage(chr.getId(), chr.getAllDiseases());

        World.changeChannelData(new CharacterTransfer(chr), chr.getId(), -30);

        ch.removePlayer(chr);
        c.updateLoginState(MapleClientLoginState.ChangeChannel, c.getSessionIPAddress());

        chr.saveToDB(false, false);
        chr.getMap().removePlayer(chr);
        c.SendPacket(CField.getChannelChange(c, Integer.parseInt(FarmServer.getIP().split(":")[1])));
        c.setPlayer(null);
        c.setReceiving(false);
    }
    

    public static void enterAuctionHouse(final ClientSocket c, final User chr) {
        if (chr.hasBlockedInventory() || chr.getMap() == null || chr.getEventInstance() != null || c.getChannelServer() == null
                || FieldLimitType.UnableToMigrate.check(chr.getMap())) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }

        ChannelServer ch = ChannelServer.getInstance(c.getChannel());
        if (chr.getMessenger() != null) {
            MapleMessengerCharacter messengerplayer = new MapleMessengerCharacter(chr);
            World.Messenger.leaveMessenger(chr.getMessenger().getId(), messengerplayer);
        }
        PlayerBuffStorage.addBuffsToStorage(chr.getId(), chr.getAllBuffs());
        PlayerBuffStorage.addCooldownsToStorage(chr.getId(), chr.getCooldowns());
        PlayerBuffStorage.addDiseaseToStorage(chr.getId(), chr.getAllDiseases());

        World.changeChannelData(new CharacterTransfer(chr), chr.getId(), -60);

        ch.removePlayer(chr);
        c.updateLoginState(MapleClientLoginState.ChangeChannel, c.getSessionIPAddress());

        chr.saveToDB(false, false);
        chr.getMap().removePlayer(chr);
        c.SendPacket(CField.getChannelChange(c, Integer.parseInt(AuctionServer.getIP().split(":")[1])));
        c.setPlayer(null);
        c.setReceiving(false);
    }

    public static final void changeChannel(final InPacket iPacket, final ClientSocket c, final User chr, final boolean room) {
        if (chr == null || chr.hasBlockedInventory() || chr.getEventInstance() != null || chr.getMap() == null || chr.isInBlockedMap()
                || FieldLimitType.UnableToMigrate.check(chr.getMap())) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }

        final int targetChannel = iPacket.DecodeByte() + 1;
        int mapid = 0;
        if (room) {
            mapid = iPacket.DecodeInt();

            if (mapid < 910000001 || mapid > 910000022) {
                chr.dropMessage(1, "Request denied due to an unknown error.");
                c.SendPacket(WvsContext.enableActions());
                return;
            }
        }
        chr.updateTick(iPacket.DecodeInt());

        // Check if target channel is available
        if (!World.isChannelAvailable(targetChannel, chr.getWorld())) {
            chr.dropMessage(1, "Request denied due to an unknown error.");
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        if (room) {
            if (chr.getMapId() == mapid) {
                if (c.getChannel() == targetChannel) {
                    chr.dropMessage(1, "You are already in " + chr.getMap().getMapName());
                    c.SendPacket(WvsContext.enableActions());
                } else { // diff channel
                    chr.changeChannel(targetChannel);
                }
            } else { // diff map
                if (c.getChannel() != targetChannel) {
                    chr.changeChannel(targetChannel);
                }
                final MapleMap warpz = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(mapid);
                if (warpz != null) {
                    chr.changeMap(warpz, warpz.getPortal("out00"));
                } else {
                    chr.dropMessage(1, "Request denied due to an unknown error.");
                    c.SendPacket(WvsContext.enableActions());
                }
            }
        } else {
            chr.changeChannel(targetChannel);
        }
    }
}
