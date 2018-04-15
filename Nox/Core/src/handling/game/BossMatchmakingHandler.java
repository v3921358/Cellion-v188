/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.MapleClient;
import handling.world.MaplePartyCharacter;
import net.InPacket;
import net.ProcessPacket;
import scripting.AbstractPlayerInteraction;
import scripting.provider.NPCScriptManager;
import server.maps.MapleMap;
import server.maps.objects.User;
import tools.packet.CWvsContext;

/**
 *
 * @author Mazen Massoud
 */
public class BossMatchmakingHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    private static enum BossOperation {
        BALROG(1), // Requires Quest: 2241
        ZAKUM_EASY(2),
        ZAKUM(3),
        ZAKUM_CHAOS(4),
        URSUS(999), // Requires Quest: 33565 // Not handled here apparently.

        MAGNUS_EASY(21),
        MAGNUS(22), // Requires Quest: 31833
        MAGNUS_HARD(23),
        HILLA(7),
        HILLA_HARD(8),
        PIERRE(9), // Requires Quest: 30007
        PIERRE_CHAOS(10),
        VONBON(11),
        VONBON_CHAOS(12),
        CRIMSONQUEEN(13),
        CRIMSONQUEEN_CHAOS(14),
        VELLUM(15),
        VELLUM_CHAOS(16),
        VONLEON_EASY(999), // Requires Quest: 3157
        VONLEON(999),
        HORNTAIL_EASY(27), // Requires Quest : 7313
        HORNTAIL(5),
        HORNTAIL_CHAOS(6),
        ARKARIUM_EASY(19), // Requires Quest : 31179
        ARKARIUM(20),
        PINKBEAN(24), // Requires Quest: 3521
        PINKBEAN_CHAOS(25),
        CYGNUS_EASY(999),// Not handled here apparently.

        CYGNUS(26), // Requires Quest: 31152
        LOTUS(29), // Requires Quest: 33294
        LOTUS_HARD(28),
        DAMIEN(32), // Requires Quest: 34015
        DAMIEN_HARD(33),
        GOLLUX(103), // Requires Quest: 17523
        RANMARU(104),
        RANMARU_HARD(109),
        PRINCESSNO(105), // Requires Quest: 58955
        LUCID(34), // Requires Quest: 34330

        NOT_FOUND(-2);

        private final int nValue;

        private BossOperation(int nValue) {
            this.nValue = nValue;
        }

        public int getValue() {
            return nValue;
        }

        public static BossOperation getFromValue(int nValue) {
            for (BossOperation nIndex : values()) {
                if (nIndex.getValue() == nValue) {
                    return nIndex;
                }
            }
            return NOT_FOUND;
        }
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final User pPlayer = c.getPlayer();
        pPlayer.updateTick(iPacket.DecodeInt());
        int nBossType = iPacket.DecodeInt();
        BossOperation nType = BossOperation.getFromValue(nBossType);
        int nDestination;
        iPacket.DecodeInt(); // Unknown
        iPacket.DecodeInt(); // Unknown

        switch (nType) {
            case BALROG:
                nDestination = 0;
                pPlayer.dropMessage(5, "Sorry, this boss is currently unavailable.");
                break;
            case ZAKUM_EASY:
            case ZAKUM:
            case ZAKUM_CHAOS:
                nDestination = 211042300;
                break;
            case MAGNUS_EASY:
            case MAGNUS:
            case MAGNUS_HARD:
                nDestination = 401060000;
                break;
            case HILLA:
            case HILLA_HARD:
                nDestination = 0;
                pPlayer.dropMessage(5, "Sorry, this boss is currently unavailable.");
                break;
            case PIERRE:
            case PIERRE_CHAOS:
            case VONBON:
            case VONBON_CHAOS:
            case CRIMSONQUEEN:
            case CRIMSONQUEEN_CHAOS:
            case VELLUM:
            case VELLUM_CHAOS:
                nDestination = 105200000;
                break;
            case HORNTAIL_EASY:
            case HORNTAIL:
            case HORNTAIL_CHAOS:
                nDestination = 240050400;
                break;
            case ARKARIUM_EASY:
            case ARKARIUM:
                nDestination = 0;
                pPlayer.dropMessage(5, "Sorry, this boss is currently unavailable.");
                break;
            case PINKBEAN:
            case PINKBEAN_CHAOS:
                nDestination = 0;
                pPlayer.dropMessage(5, "Sorry, this boss is currently unavailable.");
                break;
            case CYGNUS:
                nDestination = 0;
                pPlayer.dropMessage(5, "Sorry, this boss is currently unavailable.");
                break;
            case LOTUS:
            case LOTUS_HARD:
                nDestination = 350060000;
                break;
            case DAMIEN:
            case DAMIEN_HARD:
                nDestination = 0;
                pPlayer.dropMessage(5, "Sorry, this boss is currently unavailable.");
                break;
            case GOLLUX:
                nDestination = 0;
                pPlayer.dropMessage(5, "Sorry, this boss is currently unavailable.");
                break;
            case RANMARU:
            /*nDestination = 807300100;
                break;*/
            case RANMARU_HARD:
                nDestination = 807300200;
                break;
            case PRINCESSNO:
                nDestination = 0;
                pPlayer.dropMessage(5, "Sorry, this boss is currently unavailable.");
                break;
            case LUCID:
                nDestination = 0;
                pPlayer.dropMessage(5, "Sorry, this boss is currently unavailable.");
                break;
            default:
                nDestination = 0;
                System.out.println("[Debug] Boss Matchmaking Operation Found (" + nBossType + ")");
                break;
        }

        if (nDestination != 0) {
            pPlayer.changeMap(nDestination, 0);
            c.SendPacket(CWvsContext.enableActions());
        } else {
            c.SendPacket(CWvsContext.enableActions());
        }

        //Incase we want to handle it with parties, but it's a better quality of life without it.
        /*if (pPlayer.getParty() == null || pPlayer.getParty().getMembers().size() == 1) {
            pPlayer.changeMap(nDestination, 0);
            c.write(CWvsContext.enableActions());
            return;
        }
        
        if (pPlayer.getParty().getLeader().getId() != pPlayer.getId()) {
            pPlayer.dropMessage(5, "Only the party leader may perform this boss.");
            c.write(CWvsContext.enableActions());
            return;
        }
        
        final MapleMap target = pPlayer.getClient().getChannelServer().getMapFactory().getMap(nDestination);
        final int pMap = pPlayer.getMapId();
        for (final MaplePartyCharacter chr : pPlayer.getParty().getMembers()) {
            final MapleCharacter curChar = pPlayer.getClient().getChannelServer().getPlayerStorage().getCharacterById(chr.getId());
            if (curChar != null && (curChar.getMapId() == pMap || curChar.getEventInstance() == pPlayer.getEventInstance())) {
                curChar.changeMap(target, target.getPortal(0));
            }
        }
        c.write(CWvsContext.enableActions());*/
    }

}
