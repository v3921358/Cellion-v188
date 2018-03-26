package server.movement.types;

import client.CharacterTemporaryStat;
import java.awt.Point;
import java.util.List;

import client.MapleClient;
import constants.ServerConstants;
import constants.skills.BattleMage;
import handling.world.MovementParse;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;
import net.InPacket;
import server.maps.MapleMap;
import server.maps.objects.MapleCharacter;
import server.movement.LifeMovementFragment;
import tools.packet.CField;
import netty.ProcessPacket;
import server.MapleInventoryManipulator;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.objects.MaplePet;

/**
 * @author Steven
 *
 */
public class PlayerMovement implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return c.isLoggedIn();
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        MapleMap map = c.getPlayer().getMap();
        Point position = chr.getPosition();

        iPacket.DecodeByte(); //fieldKey
        iPacket.DecodeInteger(); //fieldCrc?
        iPacket.DecodeInteger(); //update_time
        iPacket.DecodeByte(); //unkown
        chr.settEncodedGatherDuration(iPacket.DecodeInteger()); //tEncodedGatherDuration
        chr.setxCS(iPacket.DecodeShort()); //x_CS
        chr.setyCS(iPacket.DecodeShort()); //y_CS
        chr.setvXCS(iPacket.DecodeShort());//vx_CS
        chr.setvYCS(iPacket.DecodeShort()); //vy_CS

        List<LifeMovementFragment> res = MovementParse.parseMovement(iPacket);
        if (res != null) {
            iPacket.DecodeByte();//aKeyPadState
            if (chr.isHidden()) {
                c.getPlayer().getMap().broadcastGMMessage(chr, CField.movePlayer(chr, res), false);
            } else {
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), CField.movePlayer(chr, res), false);
            }
            MovementParse.updatePosition(res, chr);
            Point pos = chr.getTruePosition();
            map.movePlayer(chr, pos);

            if (chr.getFollowId() > 0 && chr.isFollowOn() && chr.isFollowInitiator()) {
                MapleCharacter follower = map.getCharacterById(chr.getFollowId());
                if (follower != null) {
                    Point originalPosition = follower.getPosition();
                    follower.getClient().write(CField.moveFollow(position, originalPosition, pos, res));
                    MovementParse.updatePosition(res, follower);
                    map.movePlayer(follower, pos);
                    map.broadcastMessage(follower, CField.movePlayer(follower, res), false);
                } else {
                    chr.checkFollow();
                }
            }

            // checks
            int count = c.getPlayer().getFallCounter();
            boolean samepos = (pos.y > c.getPlayer().getOldPosition().y) && (Math.abs(pos.x - c.getPlayer().getOldPosition().x) < 5);
            if (samepos && ((pos.y > map.getSharedMapResources().bottom + 250) || (map.getSharedMapResources().footholds.findBelow(pos) == null))) {
                if (count > 5) {
                    c.getPlayer().changeMap(map, map.getPortal(0));
                    c.getPlayer().setFallCounter(0);
                } else {
                    count++;
                    c.getPlayer().setFallCounter(count);
                }
            } else if (count > 0) {
                c.getPlayer().setFallCounter(0);
            }
            c.getPlayer().setOldPosition(pos);

            // Battle Mage Aura Handling
            if (!samepos && (c.getPlayer().getBuffSource(CharacterTemporaryStat.BMageAura) == BattleMage.DARK_AURA)) {
                c.getPlayer().getStatForBuff(CharacterTemporaryStat.BMageAura).applyMonsterBuff(c.getPlayer());
            } else if (!samepos && (c.getPlayer().getBuffSource(CharacterTemporaryStat.BMageAura) == BattleMage.WEAKENING_AURA)) {
                c.getPlayer().getStatForBuff(CharacterTemporaryStat.BMageAura).applyMonsterBuff(c.getPlayer());
            }

            // Pet Loot Handling
            ReentrantLock petSafety = new ReentrantLock();
            if (ServerConstants.AUTO_PET_LOOT) {
                petSafety.lock();
                final List<MapleMapObject> items = chr.getMap().getMapObjectsInRange(chr.getPosition(), 2500, Arrays.asList(MapleMapObjectType.ITEM));
                MapleMapItem mapLoot;
                boolean bHasPet = false;
                for (int i = 0; i <= 3; i++) {
                    MaplePet pet = chr.getPet(i);
                    if (pet != null) {
                        bHasPet = true; // Checks all pet slots to confirm if player has a pet active.
                    }
                }
                if (bHasPet) {
                    for (MapleMapObject item : items) {
                        mapLoot = (MapleMapItem) item;
                        if (mapLoot.getMeso() > 0) {
                            chr.gainMeso(mapLoot.getMeso(), true);
                        } else if (mapLoot.getItem() == null || !MapleInventoryManipulator.addFromDrop(chr.getClient(), mapLoot.getItem(), true)) {
                            continue;
                        }
                        /*if (ServerConstants.STRICT_PET_LOOT) {
                            // Strict pet loot ignores equipment.
                            if (mapLoot.getItem().getType() == ItemType.Equipment) {
                                continue;
                            }
                        }*/

                        mapLoot.setPickedUp(true);
                        chr.getMap().broadcastMessage(CField.removeItemFromMap(mapLoot.getObjectId(), 5, chr.getId()), mapLoot.getPosition());
                        chr.getMap().removeMapObject(item);
                    }
                    try {
                        //System.out.println("[Debug] Pet Loot Size: " + items.size());
                    } finally {
                        petSafety.unlock();
                    }
                }
            }

            // for vac hack
            if (chr.isGM()) {
                chr.setLastGMMovement(res); // Etc for GM vac hack
            }
        }
    }
}
