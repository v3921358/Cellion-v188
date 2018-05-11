/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.ClientSocket;
import constants.GameConstants;
import net.InPacket;
import scripting.provider.NPCScriptManager;
import server.maps.objects.User;
import server.quest.Quest;
import tools.packet.CField;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class ClientEnvironmentHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {

    }
}
