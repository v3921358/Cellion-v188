package handling.world;

import java.awt.Point;
import java.util.List;
import server.life.Mob;
import tools.Pair;

public class AttackMonster {

    private final Mob monster;
    private final int objectid;
    private final int mobid, mobCrc;
    private final Point pointNow, pointPrev;
    private final List<Pair<Long, Boolean>> attack;

    public AttackMonster(Mob monster, int objectid, Point pointNow, List<Pair<Long, Boolean>> attack) {
        this.monster = monster;
        this.objectid = objectid;
        this.pointNow = pointNow;
        this.pointPrev = pointNow;
        this.mobid = -1;
        this.mobCrc = -1;
        this.attack = attack;
    }

    public AttackMonster(Mob monster, int objectid, int mobid, int mobCrc, Point pointNow, Point pointPrev, List<Pair<Long, Boolean>> attack) {
        this.monster = monster;
        this.objectid = objectid;
        this.pointNow = pointNow;
        this.pointPrev = pointPrev;
        this.mobid = mobid; // default = -1 if none
        this.mobCrc = mobCrc;
        this.attack = attack;
    }

    public List<Pair<Long, Boolean>> getAttacks() {
        return attack;
    }

    public Point getPosition() {
        return pointNow;
    }

    public Point getPositionPrev() {
        return pointPrev;
    }

    public int getMonsterId() {
        return mobid;
    }

    public int getMonsterCRC() {
        return mobCrc;
    }

    public int getObjectId() {
        return objectid;
    }

    public Mob getMonster() {
        return monster;
    }
}
