package handling.common;

import client.MapleClient;
import handling.PacketThrottleLimits;
import net.InPacket;
import netty.ProcessPacket;

@PacketThrottleLimits(
        FlagCount = 5,
        ResetTimeMillis = 1000 * 60 * 60, // 1 hour 
        MinTimeMillisBetweenPackets = 3000,
        FunctionName = "KeepAliveHandler",
        BanType = PacketThrottleLimits.PacketThrottleBanType.Disconnect)
public final class KeepAliveHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        int GetTickCount = iPacket.DecodeInteger(); // Win32 API for the time in millis since the computer started

        c.pongReceived();
    }

}
