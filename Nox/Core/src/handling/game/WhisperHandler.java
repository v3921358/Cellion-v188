package handling.game;

import client.MapleCharacterUtil;
import client.MapleClient;
import constants.ServerConstants;
import handling.PacketThrottleLimits;
import handling.world.World;
import net.InPacket;
import server.commands.CommandProcessor;
import server.maps.objects.MapleCharacter;
import tools.LogHelper;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

/**
 *
 * @author Mazen
 */
public class WhisperHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        MapleCharacter chr = c.getPlayer();

        chr.dropMessage(6, "Please use @w <name> <message> to send your whisper, or @f <name> to find a player.");
    }
}
