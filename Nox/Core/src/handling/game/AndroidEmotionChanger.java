/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import server.maps.objects.User;
import tools.packet.CField;

/**
 *
 * @author Novak
 */
public class AndroidEmotionChanger {

    public static void changeEmotion(User chr, int emote) {
        if ((emote > 0) && (chr != null) && (chr.getMap() != null) && (!chr.isHidden()) && (emote <= 17) && (chr.getAndroid() != null)) {
            chr.getMap().broadcastMessage(CField.showAndroidEmotion(chr.getId(), (byte) emote));
        }
    }
}
