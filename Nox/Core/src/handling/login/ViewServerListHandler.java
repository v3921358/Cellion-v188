/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.login;

import client.MapleClient;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author kaz_v
 */
public class ViewServerListHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        if (iPacket.DecodeByte() == 0) {
            WorldInfoRequestHandler.sendServerList(iPacket, c);
        }
    }
}
