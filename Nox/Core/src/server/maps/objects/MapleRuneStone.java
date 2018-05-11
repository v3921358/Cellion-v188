package server.maps.objects;

import client.ClientSocket;
import constants.skills.Global;
import java.awt.Point;
import server.Randomizer;
import server.maps.AnimatedMapleMapObject;
import server.maps.MapleFootholdTree;
import server.maps.MapleMap;
import server.maps.MapleMapObjectType;
import tools.packet.CField;

/**
 * Rune that spawns in map, gives 2x EXP to players for a minute.
 *
 * One instance of this object per map, and it is never re-created. If a rune have not been used it will simply set its state as
 * 'nextRuneSpawnTime' = 0.
 *
 * @author
 */
public class MapleRuneStone extends AnimatedMapleMapObject {

    public static enum MapleRuneStoneType {
        RuneofSwiftness(0, Global.LIBERATE_THE_SWIFT_RUNE, Global.LIBERATE_THE_RUNE_OF_THUNDER),
        RuneofRecovery(1, Global.LIBERATE_THE_RECOVERY_RUNE, Global.LIBERATE_THE_RUNE_OF_MIGHT),
        RuneofDecay(2, Global.LIBERATE_THE_COLLAPSING_RUNE, Global.LIBERATE_THE_RUNE_OF_DARKNESS),
        RuneofDestruction(3, Global.LIBERATE_THE_COLLAPSING_RUNE, Global.LIBERATE_THE_RUNE_OF_RICHES),
        RuneofThunder(4, Global.LIBERATE_THE_DESTRUCTIVE_RUNE, Global.LIBERATE_THE_RUNE_OF_THUNDER_1), // TODO: attack buff which strikes monster
        RuneofMight(5, Global.LIBERATE_THE_DESTRUCTIVE_RUNE, Global.LIBERATE_THE_RUNE_OF_MIGHT_1), // makes character gigantic
        RuneofDarkness(6, Global.RUNE_POWER, Global.LIBERATE_THE_RUNE_OF_MIGHT_2),
        RuneOfRiches(7, Global.RUNE_POWER_1, Global.LIBERATE_THE_RUNE_OF_THUNDER_2),;  // if there's anymore, look towards Etc.wz for this.. 

        private final int type;
        private final int primarySkill, secondarySkill;

        private MapleRuneStoneType(int type, int primarySkill, int secondarySkill) {
            this.type = type;
            this.primarySkill = primarySkill;
            this.secondarySkill = secondarySkill;
        }

        public int getPrimarySkill() {
            return primarySkill;
        }

        public int getSecondarySkill() {
            return secondarySkill;
        }

        public int getType() {
            return type;
        }

        public static MapleRuneStoneType getFromInt(int type) {
            for (MapleRuneStoneType t : values()) {
                if (t.getType() == type) {
                    return t;
                }
            }
            return null;
        }
    }

    private MapleRuneStoneType runeType;
    private final int fieldMonsterMinLevel, fieldMonsterMaxLevel;

    private long nextRuneSpawnTime; // time in millis for the rune to respawn itself, 0 means the rune is still active

    public MapleRuneStone(int fieldMonsterMinLevel, int fieldMonsterMaxLevel) {
        this.nextRuneSpawnTime = System.currentTimeMillis() + (1000L * 60L * (long) (Randomizer.nextInt(10))); // randomize first rune spawn time, if the map was recently loaded
        this.fieldMonsterMinLevel = fieldMonsterMinLevel;
        this.fieldMonsterMaxLevel = fieldMonsterMaxLevel;
        this.runeType = MapleRuneStoneType.RuneofSwiftness; // default

        this.setFacingLeft(true);
    }

    /**
     * Gets the max level of monster currently spanwed in the map [Used for checking, player must be within 10 level to be able to use the
     * rune]
     *
     * @return
     */
    public int getFieldMonsterMaxLevel() {
        return fieldMonsterMaxLevel;
    }

    /**
     * Gets the min level of monster currently spawned in the map. [Used for checking, player must be within 10 level to be able to use the
     * rune]
     *
     * @return
     */
    public int getFieldMonsterMinLevel() {
        return fieldMonsterMinLevel;
    }

    /**
     * Gets the type of rune that's currently spawned
     *
     * @return
     */
    public MapleRuneStoneType getRuneType() {
        return runeType;
    }

    /**
     * When the player completes the rune, attempt to hide the object.
     *
     * @param map
     * @return true if successful
     */
    public boolean completeRune(MapleMap map) {
        if (nextRuneSpawnTime == 0) {
            map.broadcastMessage(CField.RunePacket.finishRune());

            // Set next rune spawn time
            nextRuneSpawnTime = System.currentTimeMillis() + (1000L * 60L * (long) (Randomizer.nextInt(10) + 10));
            return true;
        }
        return false;
    }

    public boolean getAbleToRespawn() {
        return nextRuneSpawnTime != 0 && System.currentTimeMillis() > nextRuneSpawnTime;
    }

    public void respawnRuneInMap(MapleMap map, boolean isMapCreation) {
        MapleFootholdTree ft = map.getSharedMapResources().footholds;
        Point p = ft.getAllRelevants().get(Randomizer.nextInt(ft.getAllRelevants().size())).getPoint1();
        setPosition(p);

        this.runeType = MapleRuneStoneType.values()[Randomizer.nextInt(MapleRuneStoneType.values().length)]; // new random rune type     
        this.nextRuneSpawnTime = 0; // set as spawned.

        if (!isMapCreation) {
            map.broadcastMessage(CField.RunePacket.spawnRune(this), p);
        }
    }

    @Override
    public MapleMapObjectType getType() {
        return MapleMapObjectType.RUNE;
    }

    @Override
    public void sendSpawnData(ClientSocket client) {
        if (nextRuneSpawnTime == 0) {
            client.SendPacket(CField.RunePacket.spawnRune(this));
        }
    }

    @Override
    public void sendDestroyData(ClientSocket client) {
        if (nextRuneSpawnTime == 0) {
            client.SendPacket(CField.RunePacket.removeAllRune());
            //client.write(CField.RunePacket.removeRune());
        }
    }
}
