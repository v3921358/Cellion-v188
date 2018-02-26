package handling.world;

import client.MapleClient;
import client.MapleClient.MapleClientLoginState;
import service.ChannelServer;
import service.FarmServer;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import server.maps.objects.MapleCharacter;
import net.InPacket;
import tools.packet.CField;
import tools.packet.CWvsContext;

public class InterServerHandler {

    public static void enterFarm(final MapleClient c, final MapleCharacter chr) {
        if (chr.hasBlockedInventory() || chr.getMap() == null || chr.getEventInstance() != null || c.getChannelServer() == null
                || FieldLimitType.UnableToMigrate.check(chr.getMap())) {
            c.write(CWvsContext.enableActions());
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
        c.updateLoginState(MapleClientLoginState.CHANGE_CHANNEL, c.getSessionIPAddress());

        chr.saveToDB(false, false);
        chr.getMap().removePlayer(chr);
        c.write(CField.getChannelChange(c, Integer.parseInt(FarmServer.getIP().split(":")[1])));
        c.setPlayer(null);
        c.setReceiving(false);
    }

    public static final void changeChannel(final InPacket iPacket, final MapleClient c, final MapleCharacter chr, final boolean room) {
        if (chr == null || chr.hasBlockedInventory() || chr.getEventInstance() != null || chr.getMap() == null || chr.isInBlockedMap()
                || FieldLimitType.UnableToMigrate.check(chr.getMap())) {
            c.write(CWvsContext.enableActions());
            return;
        }

        final int targetChannel = iPacket.DecodeByte() + 1;
        int mapid = 0;
        if (room) {
            mapid = iPacket.DecodeInteger();

            if (mapid < 910000001 || mapid > 910000022) {
                chr.dropMessage(1, "Request denied due to an unknown error.");
                c.write(CWvsContext.enableActions());
                return;
            }
        }
        chr.updateTick(iPacket.DecodeInteger());

        // Check if target channel is available
        if (!World.isChannelAvailable(targetChannel, chr.getWorld())) {
            chr.dropMessage(1, "Request denied due to an unknown error.");
            c.write(CWvsContext.enableActions());
            return;
        }
        if (room) {
            if (chr.getMapId() == mapid) {
                if (c.getChannel() == targetChannel) {
                    chr.dropMessage(1, "You are already in " + chr.getMap().getMapName());
                    c.write(CWvsContext.enableActions());
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
                    c.write(CWvsContext.enableActions());
                }
            }
        } else {
            chr.changeChannel(targetChannel);
        }
    }
}
