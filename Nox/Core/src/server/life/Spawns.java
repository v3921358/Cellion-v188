package server.life;

import java.awt.Point;

import server.maps.MapleMap;

public abstract class Spawns {

    public abstract MonsterStats getMonster();

    public abstract byte getCarnivalTeam();

    public abstract boolean shouldSpawn(long time, float spawnRate);

    public abstract int getCarnivalId();

    public abstract Mob spawnMonster(MapleMap map);

    public abstract int getMobTime();

    public abstract Point getPosition();

    public abstract int getF();

    public abstract int getFh();

    /**
     * Whether this spawn point is an instance of SpawnPointAreaBoss
     *
     * @return
     */
    public abstract boolean isBossSpawnPoint();

    /*
     * The position where the spawn point will spawn monster
     * @return Point
     */
    public abstract Point getSpawnPoint();
}
