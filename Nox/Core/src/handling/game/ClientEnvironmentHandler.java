/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.MapleClient;
import constants.GameConstants;
import net.InPacket;
import scripting.provider.NPCScriptManager;
import server.maps.objects.MapleCharacter;
import server.quest.MapleQuest;
import tools.packet.CField;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

/**
 *
 * @author
 */
public class ClientEnvironmentHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {

    }
}
