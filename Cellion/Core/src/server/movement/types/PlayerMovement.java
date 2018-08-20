package server.movement.types;

import client.CharacterTemporaryStat;
import java.awt.Point;
import java.util.List;

import client.ClientSocket;
import constants.ServerConstants;
import constants.skills.BattleMage;
import handling.world.MovementParse;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;
import net.InPacket;
import server.maps.MapleMap;
import server.maps.objects.User;
import server.movement.LifeMovementFragment;
import tools.packet.CField;
import net.ProcessPacket;
import server.MapleInventoryManipulator;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.objects.Pet;
import tools.Utility;

/**
 * @author Steven
 *
 */
public class PlayerMovement implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return c.isLoggedIn();
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User pPlayer = c.getPlayer();
        if (pPlayer == null) {
            return;
        }
        MapleMap pMap = c.getPlayer().getMap();
        Point pPOS = pPlayer.getPosition();

        iPacket.DecodeByte(); //fieldKey
        iPacket.DecodeInt(); //fieldCrc?
        iPacket.DecodeInt(); //update_time
        iPacket.DecodeByte(); //unkown
        pPlayer.settEncodedGatherDuration(iPacket.DecodeInt()); //tEncodedGatherDuration
        pPlayer.setxCS(iPacket.DecodeShort()); //x_CS
        pPlayer.setyCS(iPacket.DecodeShort()); //y_CS
        pPlayer.setvXCS(iPacket.DecodeShort());//vx_CS
        pPlayer.setvYCS(iPacket.DecodeShort()); //vy_CS

        List<LifeMovementFragment> res = MovementParse.parseMovement(iPacket);
        if (res != null) {
            iPacket.DecodeByte();//aKeyPadState
            if (pPlayer.isHidden()) {
                c.getPlayer().getMap().broadcastGMMessage(pPlayer, CField.movePlayer(pPlayer, res), false);
            } else {
                c.getPlayer().getMap().broadcastPacket(c.getPlayer(), CField.movePlayer(pPlayer, res), false);
            }
            MovementParse.updatePosition(res, pPlayer);
            Point pPOS_ = pPlayer.getTruePosition();
            pMap.movePlayer(pPlayer, pPOS_);

            if (pPlayer.getFollowId() > 0 && pPlayer.isFollowOn() && pPlayer.isFollowInitiator()) {
                User follower = pMap.getCharacterById(pPlayer.getFollowId());
                if (follower != null) {
                    Point originalPosition = follower.getPosition();
                    follower.getClient().SendPacket(CField.moveFollow(pPOS, originalPosition, pPOS_, res));
                    MovementParse.updatePosition(res, follower);
                    pMap.movePlayer(follower, pPOS_);
                    pMap.broadcastPacket(follower, CField.movePlayer(follower, res), false);
                } else {
                    pPlayer.checkFollow();
                }
            }

            // Checks
            int count = c.getPlayer().getFallCounter();
            boolean samepos = (pPOS_.y > c.getPlayer().getOldPosition().y) && (Math.abs(pPOS_.x - c.getPlayer().getOldPosition().x) < 5);
            if (samepos && ((pPOS_.y > pMap.getSharedMapResources().bottom + 250) || (pMap.getSharedMapResources().footholds.findBelow(pPOS_) == null))) {
                if (count > 5) {
                    c.getPlayer().changeMap(pMap, pMap.getPortal(0));
                    c.getPlayer().setFallCounter(0);
                } else {
                    count++;
                    c.getPlayer().setFallCounter(count);
                }
            } else if (count > 0) {
                c.getPlayer().setFallCounter(0);
            }
            c.getPlayer().setOldPosition(pPOS_);

            // Battle Mage Aura Handling
            if (!samepos && (c.getPlayer().getBuffSource(CharacterTemporaryStat.BMageAura) == BattleMage.DARK_AURA)) {
                c.getPlayer().getStatForBuff(CharacterTemporaryStat.BMageAura).applyMonsterBuff(c.getPlayer());
            } else if (!samepos && (c.getPlayer().getBuffSource(CharacterTemporaryStat.BMageAura) == BattleMage.WEAKENING_AURA)) {
                c.getPlayer().getStatForBuff(CharacterTemporaryStat.BMageAura).applyMonsterBuff(c.getPlayer());
            }

            // Pet Loot Handling
            if (pPlayer.hasPetVacuum()) {
                Utility.petVacuumRequest(pPlayer);
            } else if (ServerConstants.AUTO_PET_LOOT) {
                Utility.petLootRequest(pPlayer);
            }

            // for vac hack
            if (pPlayer.isGM()) {
                pPlayer.setLastGMMovement(res); // Etc for GM vac hack
            }
        }
    }
}
