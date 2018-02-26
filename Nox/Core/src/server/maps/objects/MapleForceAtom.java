package server.maps.objects;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Steven
 */
public class MapleForceAtom {

    private MapleForceAtomTypes type;
    private int speed = 0, spawndelay = 0, distance = 0, angle = 0;
    private Point position = new Point();
    private int charId = 0;
    private boolean byMob = false;
    private int targetOid = 0;
    private boolean toMob = false;
    private List<Integer> objects = new ArrayList<>();
    private int attackCount = 0;
    private int skillId = 0;
    private int firstImpact = 0, secondImpact = 0;

    public MapleForceAtom(MapleForceAtomTypes type) {
        this.type = type;
    }

    public Point getPosition() {
        return new Point(position);
    }

    public MapleForceAtomTypes getType() {
        return type;
    }

    public int getMaxSpeed() {
        return speed;
    }

    public int getSpawnDelay() {
        return spawndelay;
    }

    public int getDistance() {
        return distance;
    }

    public int getAngle() {
        return angle;
    }

    /**
     * This returns the character id
     *
     * @return int
     */
    public int getCharId() {
        return charId;
    }

    /**
     * This sets the character id
     *
     * @param int
     */
    public void setCharId(int charId) {
        this.charId = charId;
    }

    /**
     * Returns if force is from mob
     *
     * @return boolean
     */
    public boolean isByMob() {
        return byMob;
    }

    /**
     * This sets if the force is coming from a mob
     *
     * @param boolean
     */
    public void setByMob(boolean byMob) {
        this.byMob = byMob;
    }

    /**
     * This returns the object id of the target
     *
     * @return int
     */
    public int getTargetOid() {
        return targetOid;
    }

    /**
     * This sets the target object id
     *
     * @param int
     */
    public void setTargetOid(int targetOid) {
        this.targetOid = targetOid;
    }

    /**
     * This method returns if the atom is going to a mob
     *
     * @return boolean
     */
    public boolean isToMob() {
        return toMob;
    }

    /**
     * This sets if the atom should go to a mob
     *
     * @param boolean
     */
    public void setToMob(boolean toMob) {
        this.toMob = toMob;
    }

    /**
     * This method returns a list of object id
     *
     * @return List<Integer>
     */
    public List<Integer> getObjects() {
        return objects;
    }

    /**
     * Returns the attackCount
     *
     * @return int
     */
    public int getAttackCount() {
        return attackCount;
    }

    /**
     * This will set the attackCount to the object
     *
     * @param int attackCount
     */
    public void setAttackCount(int attackCount) {
        this.attackCount = attackCount;
    }

    /**
     * Returns the skillId
     *
     * @return int
     */
    public int getSkillId() {
        return skillId;
    }

    /**
     * Gives a skillId to the object
     *
     * @param int skillId
     */
    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public int getSecondImpact() {
        return secondImpact;
    }

    public void setSecondImpact(int secondImpact) {
        this.secondImpact = secondImpact;
    }

    public int getFirstImpact() {
        return firstImpact;
    }

    public void setFirstImpact(int firstImpact) {
        this.firstImpact = firstImpact;
    }

}
