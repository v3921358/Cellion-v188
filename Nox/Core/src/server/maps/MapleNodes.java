package server.maps;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import tools.Pair;

public class MapleNodes {

    private final List<Rectangle> areas;
    private final List<MonsterPoint> monsterPoints;
    private final List<Integer> skillIds;
    private final List<Pair<Integer, Integer>> mobsToSpawn;

    private int mapid;

    public MapleNodes(final int mapid) {
        areas = new ArrayList<>();
        skillIds = new ArrayList<>();

        monsterPoints = new ArrayList<>();
        mobsToSpawn = new ArrayList<>();
        this.mapid = mapid;
    }

    public final void addMapleArea(final Rectangle rec) {
        areas.add(rec);
    }

    public final List<Rectangle> getAreas() {
        return new ArrayList<>(areas);
    }

    public final Rectangle getArea(final int index) {
        return getAreas().get(index);
    }

    public static class MonsterPoint {

        public int x, y, fh, cy, team;

        public MonsterPoint(int x, int y, int fh, int cy, int team) {
            this.x = x;
            this.y = y;
            this.fh = fh;
            this.cy = cy;
            this.team = team;
        }
    }

    public final List<MonsterPoint> getMonsterPoints() {
        return monsterPoints;
    }

    public final void addMonsterPoint(int x, int y, int fh, int cy, int team) {
        this.monsterPoints.add(new MonsterPoint(x, y, fh, cy, team));
    }

    public final void addMobSpawn(int mobId, int spendCP) {
        this.mobsToSpawn.add(new Pair<>(mobId, spendCP));
    }

    public final List<Pair<Integer, Integer>> getMobsToSpawn() {
        return mobsToSpawn;
    }

    public final List<Integer> getSkillIds() {
        return skillIds;
    }

    public final void addSkillId(int z) {
        this.skillIds.add(z);
    }
}
