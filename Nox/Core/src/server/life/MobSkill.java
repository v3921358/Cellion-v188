package server.life;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import client.MapleDisease;
import client.MonsterStatus;
import constants.GameConstants;
import service.ChannelServer;
import server.Randomizer;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.objects.User;
import server.maps.objects.Mist;
import tools.packet.CField;
import tools.packet.MobPacket;

public class MobSkill {

    private int skillId, skillLevel, mpCon, spawnEffect, hp, x, y;
    private long duration, cooltime;
    private float prop;
    private short effectDelay;
    private short limit;
    private List<Integer> toSummon = new ArrayList<>();
    private Point lt, rb;
    private boolean summonOnce;
    private int delay;

    public MobSkill(int skillId, int level) {
        this.skillId = skillId;
        skillLevel = level;
    }

    public void setOnce(boolean o) {
        this.summonOnce = o;
    }

    public boolean onlyOnce() {
        return summonOnce;
    }

    public void setMpCon(int mpCon) {
        this.mpCon = mpCon;
    }

    public void addSummons(List<Integer> toSummon) {
        this.toSummon = toSummon;
    }

    public void setSpawnEffect(int spawnEffect) {
        this.spawnEffect = spawnEffect;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setCoolTime(long cooltime) {
        this.cooltime = cooltime;
    }

    public void setProp(float prop) {
        this.prop = prop;
    }

    public void setLtRb(Point lt, Point rb) {
        this.lt = lt;
        this.rb = rb;
    }

    public void setLimit(short limit) {
        this.limit = limit;
    }

    public boolean checkCurrentBuff(User player, Mob monster) {
        boolean stop = false;
        switch (skillId) {
            case 100:
            case 110:
            case 150:
                stop = monster.isBuffed(MonsterStatus.WEAPON_ATTACK_UP);
                break;
            case 101:
            case 111:
            case 151:
                stop = monster.isBuffed(MonsterStatus.MAGIC_ATTACK_UP);
                break;
            case 102:
            case 112:
            case 152:
                stop = monster.isBuffed(MonsterStatus.WEAPON_DEFENSE_UP);
                break;
            case 103:
            case 113:
            case 153:
                stop = monster.isBuffed(MonsterStatus.MAGIC_DEFENSE_UP);
                break;
            //154-157, don't stop it
            case 140:
            case 141:
            case 142:
            case 143:
            case 144:
            case 145:
                stop = monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY) || monster.isBuffed(MonsterStatus.MAGIC_IMMUNITY) || monster.isBuffed(MonsterStatus.WEAPON_IMMUNITY);
                break;
            case 170:
                if (monster != null && monster.getMap() != null) {
                    final Point oldPos = monster.getPosition();
                    for (int i = 0; i < 4; i++) {
                        final Point point = monster.getMap().calcPointBelow(new Point(oldPos.x + (i % 2 == 0 ? x : -x), oldPos.y + (i / 2 == 0 ? y : -y)));
                        if (point != null) {
                            monster.setPosition(point);
                            break;
                        }
                    }
                    monster.getMap().broadcastPacket(MobPacket.spawnMonster(monster, -1, 0, false), oldPos);
                    monster.getMap().broadcastPacket(MobPacket.spawnMonster(monster, -1, 0, false), monster.getTruePosition());
                    monster.getMap().broadcastPacket(MobPacket.getMonsterTeleport(monster.getObjectId(), x, y));
                    monster.getMap().moveMonster(monster, monster.getTruePosition());
                }
                break;
            case 200:
                stop = player.getMap().getAllMapObjectSize(MapleMapObjectType.MONSTER) >= limit;
                break;
        }
        stop |= monster.isBuffed(MonsterStatus.MAGIC_CRASH);
        return stop;
    }

    public void applyEffect(User player, Mob monster, boolean skill) {
        applyEffect(player, monster, skill, 0);
    }

    public void applyEffect(User player, Mob monster, boolean skill, int delay) {
        MapleDisease disease = MapleDisease.getBySkill(skillId);
        Map<MonsterStatus, Integer> stats = new EnumMap<>(MonsterStatus.class);
        List<Integer> reflection = new LinkedList<>();

        switch (skillId) {
            case 100:
            case 110:
            case 150:
                stats.put(MonsterStatus.WEAPON_ATTACK_UP, x);
                break;
            case 101:
            case 111:
            case 151:
                stats.put(MonsterStatus.MAGIC_ATTACK_UP, x);
                break;
            case 102:
            case 112:
            case 152:
                stats.put(MonsterStatus.WEAPON_DEFENSE_UP, x);
                break;
            case 103:
            case 113:
            case 153:
                stats.put(MonsterStatus.MAGIC_DEFENSE_UP, x);
                break;
            case 154:
                stats.put(MonsterStatus.ACC, x);
                break;
            case 155:
                stats.put(MonsterStatus.EVA, x);
                break;
            case 115:
            case 156:
                stats.put(MonsterStatus.SPEED, x);
                break;
            case 157:
                stats.put(MonsterStatus.SEAL, x); //o.o
                break;
            case 114:
                if (lt != null && rb != null && skill && monster != null) {
                    List<MapleMapObject> objects = getObjectsInRange(monster, MapleMapObjectType.MONSTER);
                    final int hp = (getX() / 1000) * (int) (950 + 1050 * Math.random());
                    for (MapleMapObject mons : objects) {
                        ((Mob) mons).heal(hp, getY(), true);
                    }
                } else if (monster != null) {
                    monster.heal(getX(), getY(), true);
                }
                break;
            case 105: //consume..
                if (lt != null && rb != null && skill && monster != null) {
                    List<MapleMapObject> objects = getObjectsInRange(monster, MapleMapObjectType.MONSTER);
                    for (MapleMapObject mons : objects) {
                        if (mons.getObjectId() != monster.getObjectId()) {
                            player.getMap().killMonster((Mob) mons, player, true, false, (byte) 1, 0);
                            monster.heal(getX(), getY(), true);
                            break;
                        }
                    }
                } else if (monster != null) {
                    monster.heal(getX(), getY(), true);
                }
                break;
            case 127:
                if (lt != null && rb != null && skill && monster != null && player != null) {
                    for (User character : getPlayersInRange(monster, player)) {
                        character.dispel();
                    }
                } else if (player != null) {
                    player.dispel();
                }
                break;

            case 129: // Banish
                if (monster != null && monster.getMap().getSquadByMap() == null) { //not pb/vonleon map
                    if (monster.getEventInstance() != null && monster.getEventInstance().getName().indexOf("BossQuest") != -1) {
                        break;
                    }
                    final BanishInfo info = monster.getStats().getBanishInfo();
                    if (info != null) {
                        final MapleMap tomap = ChannelServer.getInstance(monster.getMap().getChannel()).getMapFactory().getMap(info.getMap());

                        if (lt != null && rb != null && skill && player != null) {
                            for (User chr : getPlayersInRange(monster, player)) {
                                if (!chr.hasBlockedInventory()) {
                                    chr.dropMessage(5, info.getMsg());

                                    chr.changeMap(tomap, tomap.getPortal(info.getPortal()));
                                }
                            }
                        } else if (player != null && !player.hasBlockedInventory()) {
                            player.dropMessage(5, info.getMsg());

                            player.changeMap(tomap, tomap.getPortal(info.getPortal()));
                        }
                    }
                }
                break;
            case 131: // Mist
                if (monster != null) {
                    monster.getMap().spawnMist(new Mist(calculateBoundingBox(monster.getTruePosition(), true), monster, this), x * 10, false);
                }
                break;
            case 140:
                stats.put(MonsterStatus.WEAPON_IMMUNITY, x);
                break;
            case 141:
                stats.put(MonsterStatus.MAGIC_IMMUNITY, x);
                break;
            case 142: // Weapon / Magic Immunity
                stats.put(MonsterStatus.DAMAGE_IMMUNITY, x);
                break;
            case 143: // Weapon Reflect
                stats.put(MonsterStatus.WEAPON_DAMAGE_REFLECT, x);
                stats.put(MonsterStatus.WEAPON_IMMUNITY, x);
                reflection.add(x);
                break;
            case 144: // Magic Reflect
                stats.put(MonsterStatus.MAGIC_DAMAGE_REFLECT, x);
                stats.put(MonsterStatus.MAGIC_IMMUNITY, x);
                reflection.add(x);
                break;
            case 145: // Weapon / Magic reflect
                stats.put(MonsterStatus.WEAPON_DAMAGE_REFLECT, x);
                stats.put(MonsterStatus.WEAPON_IMMUNITY, x);
                stats.put(MonsterStatus.MAGIC_DAMAGE_REFLECT, x);
                stats.put(MonsterStatus.MAGIC_IMMUNITY, x);
                reflection.add(x);
                reflection.add(x);
                break;
            case 173:
                disease = MapleDisease.TORNADO;
                break;
            case 184: //ë°˜ë°˜ ê°•ì œìœ„ì¹˜ì�´ë�™ - ìž�ì¿°
                disease = MapleDisease.TELEPORT;
                this.setX(player.getPosition().x);
                this.setY(player.getPosition().y);
                break;
            case 191: //ë°˜ë°˜ íƒ€ìž„ì›Œí”„ - ìž�ì¿°
                Point posFrom = monster.getPosition();
                Point mylt = new Point(lt.x + posFrom.x, lt.y + posFrom.y);
                Point myrb = new Point(rb.x + posFrom.x, rb.y + posFrom.y);
                Rectangle box = new Rectangle(posFrom.x, posFrom.y, myrb.x - mylt.x, myrb.y - mylt.y);
                //System.out.println(box.toString());
                Mist clock = new Mist(box, monster, this);
                clock.setClockType(Randomizer.rand(1, 2));
                monster.getMap().spawnClockMist(clock);
                //player.send(MainPacketCreator.spawnClockMist(clock));
                monster.getMap().broadcastPacket(CField.getGameMessage("Sacrifice.", (short) 7));
                break;
            case 200:
                if (monster == null) {
                    return;
                }
                for (Integer mobId : getSummons()) {
                    Mob toSpawn = null;
                    try {
                        toSpawn = LifeFactory.getMonster(GameConstants.getCustomSpawnID(monster.getId(), mobId));
                    } catch (RuntimeException e) { //monster doesn't exist
                        continue;
                    }
                    if (toSpawn == null) {
                        continue;
                    }
                    toSpawn.setPosition(monster.getTruePosition());
                    int ypos = (int) monster.getTruePosition().getY(), xpos = (int) monster.getTruePosition().getX();

                    switch (mobId) {
                        case 8500003: // Pap bomb high
                            toSpawn.setFh((int) Math.ceil(Math.random() * 19.0));
                            ypos = -590;
                            break;
                        case 8500004: // Pap bomb
                            //Spawn between -500 and 500 from the monsters X position
                            xpos = (int) (monster.getTruePosition().getX() + Math.ceil(Math.random() * 1000.0) - 500);
                            ypos = (int) monster.getTruePosition().getY();
                            break;
                        case 8510100: //Pianus bomb
                            if (Math.ceil(Math.random() * 5) == 1) {
                                ypos = 78;
                                xpos = (int) (0 + Math.ceil(Math.random() * 5)) + ((Math.ceil(Math.random() * 2) == 1) ? 180 : 0);
                            } else {
                                xpos = (int) (monster.getTruePosition().getX() + Math.ceil(Math.random() * 1000.0) - 500);
                            }
                            break;
                        case 8820007: //mini bean
                            continue;
                    }
                    // Get spawn coordinates (This fixes monster lock)
                    // TODO get map left and right wall.
                    switch (monster.getMap().getId()) {
                        case 220080001: //Pap map
                            if (xpos < -890) {
                                xpos = (int) (-890 + Math.ceil(Math.random() * 150));
                            } else if (xpos > 230) {
                                xpos = (int) (230 - Math.ceil(Math.random() * 150));
                            }
                            break;
                        case 230040420: // Pianus map
                            if (xpos < -239) {
                                xpos = (int) (-239 + Math.ceil(Math.random() * 150));
                            } else if (xpos > 371) {
                                xpos = (int) (371 - Math.ceil(Math.random() * 150));
                            }
                            break;
                    }
                    monster.getMap().spawnMonsterWithEffect(toSpawn, getSpawnEffect(), monster.getMap().calcPointBelow(new Point(xpos, ypos - 1)));
                }
                break;
        }

        if (stats.size() > 0 && monster != null) {
            if (!checkCurrentBuff(player, monster)) {
                if (lt != null && rb != null && skill) {
                    for (MapleMapObject mons : getObjectsInRange(monster, MapleMapObjectType.MONSTER)) {
                        ((Mob) mons).applyMonsterBuff(stats, getSkillId(), getDuration(), this, reflection);
                    }
                } else {
                    monster.applyMonsterBuff(stats, getSkillId(), getDuration(), this, reflection);
                }
            }
        }
        if (disease != null && player != null) {
            if (lt != null && rb != null && skill && monster != null) {
                for (User chr : getPlayersInRange(monster, player)) {
                    chr.giveDebuff(disease, this);
                }
            } else {
                player.giveDebuff(disease, this);
            }
        }
        if (monster != null) {
            monster.setMp(monster.getMp() - getMpCon());
        }
    }

    public int getSkillId() {
        return skillId;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public int getMpCon() {
        return mpCon;
    }

    public List<Integer> getSummons() {
        return Collections.unmodifiableList(toSummon);
    }

    public int getSpawnEffect() {
        return spawnEffect;
    }

    public int getHP() {
        return hp;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public long getDuration() {
        return duration;
    }

    public long getCoolTime() {
        return cooltime;
    }

    public Point getLt() {
        return lt;
    }

    public Point getRb() {
        return rb;
    }

    public int getLimit() {
        return limit;
    }

    public boolean makeChanceResult() {
        return prop >= 1.0 || Math.random() < prop;
    }

    private Rectangle calculateBoundingBox(Point posFrom, boolean facingLeft) {
        Point mylt, myrb;
        if (facingLeft) {
            mylt = new Point(lt.x + posFrom.x, lt.y + posFrom.y);
            myrb = new Point(rb.x + posFrom.x, rb.y + posFrom.y);
        } else {
            myrb = new Point(lt.x * -1 + posFrom.x, rb.y + posFrom.y);
            mylt = new Point(rb.x * -1 + posFrom.x, lt.y + posFrom.y);
        }
        final Rectangle bounds = new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
        return bounds;
    }

    private List<User> getPlayersInRange(Mob monster, User player) {
        final Rectangle bounds = calculateBoundingBox(monster.getTruePosition(), monster.isFacingLeft());
        List<User> players = new ArrayList<>();
        players.add(player);
        return monster.getMap().getPlayersInRectAndInList(bounds, players);
    }

    private List<MapleMapObject> getObjectsInRange(Mob monster, MapleMapObjectType objectType) {
        final Rectangle bounds = calculateBoundingBox(monster.getTruePosition(), monster.isFacingLeft());
        List<MapleMapObjectType> objectTypes = new ArrayList<>();
        objectTypes.add(objectType);
        return monster.getMap().getMapObjectsInRect(bounds, objectTypes);
    }

    /**
     * @return the effectDelay
     */
    public short getEffectDelay() {
        return effectDelay;
    }

    /**
     * @param effectDelay the effectDelay to set
     */
    public void setEffectDelay(short effectDelay) {
        this.effectDelay = effectDelay;
    }

    /**
     * @return the delay on the skill
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Sets the delay on a skill
     *
     * @param chr
     * @param monster
     * @param nextSkill
     * @param option
     */
    public void setDelay(User chr, Mob monster, int nextSkill, short option) {

    }

}
