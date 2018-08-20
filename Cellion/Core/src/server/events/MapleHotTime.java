/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.events;

import constants.GameConstants;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import enums.NPCInterfaceType;
import enums.NPCChatType;
import server.maps.objects.User;
import service.ChannelServer;
import tools.packet.CField;

/**
 *
 * @author Song
 */
public class MapleHotTime {

    public static void Schedule() {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.of("America/Los_Angeles");
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        ZonedDateTime zonedNext = zonedNow.withHour(12).withMinute(00).withSecond(0);
        
        if (zonedNow.compareTo(zonedNext) > 0) {
            zonedNext = zonedNext.plusDays(1);
        }

        Duration duration = Duration.between(zonedNow, zonedNext);
        long initalDelay = duration.getSeconds();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("[Info] Running HotTime.");
            RunHotTime();
        }, initalDelay, 12 * 60 * 60, TimeUnit.SECONDS);
    }

    private static void RunHotTime() {
        int item = GameConstants.getHotTimeItem();
        for (ChannelServer cserv : ChannelServer.getAllInstances()) {
            for (User mch : cserv.getPlayerStorage().getAllCharacters()) {
                if (mch.getClient().canClickNPC()) {
                    mch.gainItem(item, 1);
                    mch.getClient().SendPacket(CField.NPCPacket.getNPCTalk(9010010, NPCChatType.OK, "You got the #t" + item + "#, right? Click it to see what's inside. Go ahead and check your item inventory now, if you're curious.", NPCInterfaceType.NPC_UnCancellable, 9010010));
                    mch.getClient().SendPacket(CField.EffectPacket.showRewardItemAnimation(item, ""));
                }
            }
        }
    }
}
