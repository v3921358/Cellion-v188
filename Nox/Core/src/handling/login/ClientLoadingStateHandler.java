package handling.login;

import client.MapleClient;
import net.InPacket;
import netty.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class ClientLoadingStateHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        // does nothing for now
        int count = iPacket.DecodeInteger();
        if (iPacket.Available() == 4) {
            int code = iPacket.DecodeInteger();
        }

    }

}
