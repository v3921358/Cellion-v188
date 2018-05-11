package server.life;

import java.awt.Point;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import client.MonsterStatus;
import client.MonsterStatusEffect;
import client.SkillFactory;
import server.MapleCarnivalFactory;
import server.MapleCarnivalFactory.MCSkill;
import server.MapleStatEffect;
import server.Randomizer;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.objects.Reactor;
import server.maps.objects.Summon;
import tools.packet.WvsContext;

public class SpawnPoint extends Spawns {

    private final MonsterStats monster;
    private final Point pos;
    private long nextPossibleSpawn;
    private final int mobTime, fh, f, id;
    private int carnival = -1, level = -1;
    private final AtomicInteger spawnedMonsters = new AtomicInteger(0);
    private final String msg;
    private final byte carnivalTeam;

    public SpawnPoint(final Mob monster, final Point pos, final int mobTime, final byte carnivalTeam, final String msg) {
        this.monster = monster.getStats();
        this.pos = pos;
        this.id = monster.getId();
        this.fh = monster.getFh();
        this.f = monster.getF();
        this.carnivalTeam = carnivalTeam;
        this.msg = msg;

        this.nextPossibleSpawn = System.currentTimeMillis();

        this.mobTime = (mobTime < 0 ? -1 : (mobTime * 1000));
        if (this.mobTime > 10_000) { // if > 10 seconds
            // boss monster should not spawn instantly, randomize it a bit after server restart to avoid one person killing all channels
            this.nextPossibleSpawn += this.mobTime * (0.5f + Randomizer.nextFloat() * 0.7f);
        }
    }

    public final void setCarnival(int c) {
        this.carnival = c;
    }

    public final void setLevel(int c) {
        this.level = c;
    }

    @Override
    public Point getSpawnPoint() {
        return pos;
    }

    @Override
    public boolean isBossSpawnPoint() {
        return false;
    }

    @Override
    public final int getF() {
        return f;
    }

    @Override
    public final int getFh() {
        return fh;
    }

    @Override
    public final Point getPosition() {
        return pos;
    }

    @Override
    public final MonsterStats getMonster() {
        return monster;
    }

    @Override
    public final byte getCarnivalTeam() {
        return carnivalTeam;
    }

    @Override
    public final int getCarnivalId() {
        return carnival;
    }

    @Override
    public final boolean shouldSpawn(long time, float monsterRate) {
        if (mobTime < 0) {
            return false;
        }
        if (mobTime < 0 || ((mobTime != 0 || !monster.getMobile()) && spawnedMonsters.get() > 0) || spawnedMonsters.get() > 2) {//lol
            return false;
        }

        return nextPossibleSpawn <= time;
    }

    @Override
    public final Mob spawnMonster(final MapleMap map) {
        final Mob mob = new Mob(id, monster);
        mob.setPosition(new Point(pos));
        mob.setCy(pos.y);
        mob.setRx0(pos.x - 50);
        mob.setRx1(pos.x + 50); //these dont matter for mobs
        mob.setFh(fh);
        mob.setF(f);
        mob.setCarnivalTeam(carnivalTeam);
        if (level > -1) {
            mob.changeLevel(level);
        }
        spawnedMonsters.incrementAndGet();
        mob.addListener(() -> {
            nextPossibleSpawn = System.currentTimeMillis();

            if (mobTime > 0) {
                nextPossibleSpawn += mobTime;
            }
            spawnedMonsters.decrementAndGet();
        });

        map.spawnMonster(mob, -2);
        if (carnivalTeam > -1) {
            for (MapleMapObject o : map.getAllMapObjects(MapleMapObjectType.REACTOR)) { //parsing through everytime a monster is spawned? not good idea
                final Reactor r = (Reactor) o;

                if (r.getName().startsWith(String.valueOf(carnivalTeam)) && r.getReactorId() == (9980000 + carnivalTeam) && r.getState() < 5) {
                    final int num = Integer.parseInt(r.getName().substring(1, 2)); //00, 01, etc
                    final MCSkill skil = MapleCarnivalFactory.getInstance().getGuardian(num);
                    if (skil != null) {
                        skil.getSkill().applyEffect(null, mob, false);
                    }
                }
            }
        }
        for (Summon s : map.getAllSummons()) {
            if (s.getSkill() == 35111005) {
                final MapleStatEffect effect = SkillFactory.getSkill(s.getSkill()).getEffect(s.getSkillLevel());
                for (Map.Entry<MonsterStatus, Integer> stat : effect.getMonsterStati().entrySet()) {
                    mob.applyStatus(s.getOwner(), new MonsterStatusEffect(stat.getKey(), stat.getValue(), s.getSkill(), null, false), false, effect.getDuration(), true, effect);
                }
                break;
            }
        }
        if (msg != null) {
            map.broadcastMessage(WvsContext.broadcastMsg(6, msg));
        }
        return mob;
    }

    @Override
    public final int getMobTime() {
        return mobTime;
    }
}
