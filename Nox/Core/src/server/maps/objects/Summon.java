package server.maps.objects;

import java.awt.Point;

import client.MapleClient;
import client.SkillFactory;
import client.anticheat.CheatingOffense;
import constants.GameConstants;
import constants.skills.BeastTamer;
import server.MapleStatEffect;
import server.maps.AnimatedMapleMapObject;
import server.maps.MapleMap;
import server.maps.MapleMapObjectType;
import server.maps.SummonMovementType;
import tools.packet.CField.SummonPacket;

public class Summon extends AnimatedMapleMapObject {

    private final int ownerid, skillLevel, ownerLevel, skill;
    private MapleMap map; //required for instanceMaps
    private short hp;
    private boolean changedMap = false;
    private SummonMovementType movementType;
    private int summonDuration;

    // Since player can have more than 1 summon [Pirate] 
    // Let's put it here instead of cheat tracker
    private int lastSummonTickCount;
    private byte summonTickCountReset;
    private long summonTickDifference;
    private long lastAttackTime;

    public Summon(User owner, MapleStatEffect skill, Point pos, SummonMovementType movementType, int summonDuration) {
        this(owner, skill.getSourceId(), skill.getLevel(), pos, movementType, summonDuration);
    }

    public Summon(User owner, int sourceid, int level, Point pos, SummonMovementType movementType, int summonDuration) {
        super();
        this.ownerid = owner.getId();
        this.ownerLevel = owner.getLevel();
        this.skill = sourceid;
        this.map = owner.getMap();
        this.skillLevel = level;
        this.movementType = movementType;
        this.summonDuration = summonDuration; // in millis
        setPosition(pos);

        if (!isPuppet()) { // Safe up 12 bytes of data, since puppet doesn't attack.
            lastSummonTickCount = 0;
            summonTickCountReset = 0;
            summonTickDifference = 0;
            lastAttackTime = 0;
        }
    }

    @Override
    public final void sendSpawnData(final MapleClient client) {
    }

    @Override
    public final void sendDestroyData(final MapleClient client) {
        client.getPlayer().getMap().broadcastMessage(SummonPacket.removeSummon(this, false));
        client.SendPacket(SummonPacket.removeSummon(this, false));
    }

    public final void updateMap(final MapleMap map) {
        this.map = map;
    }

    public final User getOwner() {
        return map.getCharacterById(ownerid);
    }

    /**
     * The duration in millis that this summon is active for
     *
     * @return
     */
    public int getSummonDuration() {
        return summonDuration;
    }

    public final int getOwnerId() {
        return ownerid;
    }

    public final int getOwnerLevel() {
        return ownerLevel;
    }

    public final int getSkill() {
        return skill;
    }

    public final short getHP() {
        return hp;
    }

    public final void addHP(final short delta) {
        this.hp += delta;
    }

    public final SummonMovementType getMovementType() {
        return movementType;
    }

    public final boolean isPuppet() {
        switch (skill) {
            case 3111002:
            case 3211002:
            case 3120012:
            case 3220012:
            case 13111004:
            case 4341006:
            case 33111003:
                return true;
        }
        return isAngel();
    }

    public final boolean isAngel() {
        return GameConstants.isAngel(skill);
    }

    public final boolean isMultiAttack() {
        return skill == BeastTamer.LIL_FORT || skill == 61111002 || skill == 35111002 || skill == 36121002 || skill == 36121013 || skill == 35121003 || (!isGaviota() && skill != 33101008 && skill < 35000000) || skill == 35111009 || skill == 35111010 || skill == 35111001 || skill == 42111003;
    }

    public final boolean isGaviota() {
        return skill == 5211002;
    }

    public final boolean isBeholder() {
        return skill == 1301013; // Evil Eye;
    }

    public final boolean isMultiSummon() {
        return skill == 5211002 || skill == 5211001 || skill == 5220002 || skill == 32111006 || skill == 33101008;
    }

    public final boolean isSummon() {
        switch (skill) {
            case 33001007: // Jaguar Summon
            case 33001011: // Jaguar Summon
            case 12111004:
            case 1321007: //beholder
            case 1301013:
            case 14000027: // Shadow Bat
            case 2321003:
            case 2121005:
            case 3221014: // Arrow Illusion
            case 5711001: // turret
            case 2221005:
            case 5211001: // Pirate octopus summon
            case 5211002:
            case 5220002: // wrath of the octopi
            case 13111004:
            case 11001004:
            case 12001004:
            case 13001004:
            case 14001005:
            case 15001004:
            case 33111005:
            case 35111001:
            case 35111010:
            case 35111009:
            case 35111002: //pre-bb = 35111002, 35111004(amp?), 35111005(accel)
            case 35111005:
            case 35111011:
            case 35121009:
            case 35121010:
            case 35121011:
            case 4111007:
            case 4211007: //dark flare
            case 14111010: //dark flare
            case 32111006:
            case 33101008:
            case 35121003:
            case 3101007:
            case 3201007:
            case 3111005:
            case 3211005:
            case 5321003:
            case 5321004:
            case 23111008:
            case 23111009:
            case 23111010:
            case 42101001: // Shikigami Charm
            case 42111003: // Kishin Shoukan
            case 36121002:
            case 36121013:
            case 36121014:
            case 42101021: // Foxfire
            case 42121021: // Foxfire
                //   case 3121013:
                return true;
        }
        return isAngel();
    }

    public final int getSkillLevel() {
        return skillLevel;
    }

    public final int getSummonType() {
        if (isAngel()) {
            return 2;
        } else if ((skill != 33111003 && skill != 3120012 && skill != 3220012 && isPuppet()) || skill == 33101008 || skill == 35111002) {
            return 0;
        }
        switch (skill) {
            case 1321007:
            case 1301013:
            case 36121014:
                return 2; //buffs and stuff
            case 35111001: //satellite.
            case 35111009:
            case 35111010:
            case 42111003: // Kishin Shoukan
            case 36121013:
                return 3; //attacks what you attack
            case 35121009: //bots n. tots
                return 5; //sub summons
            case 35121003:
                return 6; //charge
            case 4111007: // test
            case 4211007: //dark flare
            case 14111010: //dark flare
                return 7; //attacks what you get hit by
            case 42101001: // Shikigami Charm
                return 8;
            case 14000027:
                return 0;
        }
        return 1;
    }

    @Override
    public final MapleMapObjectType getType() {
        return MapleMapObjectType.SUMMON;
    }

    public final void checkSummonAttackFrequency(final User chr, final int tickcount) {
        final int tickdifference = (tickcount - lastSummonTickCount);
        if (tickdifference < SkillFactory.getSummonData(skill).delay) {
            chr.getCheatTracker().registerOffense(CheatingOffense.FAST_SUMMON_ATTACK);
        }
        final long summonTimeCode = System.currentTimeMillis() - tickcount;
        final long difference = summonTickDifference - summonTimeCode;
        if (difference > 500) {
            chr.getCheatTracker().registerOffense(CheatingOffense.FAST_SUMMON_ATTACK);
        }
        summonTickCountReset++;
        if (summonTickCountReset > 4) {
            summonTickCountReset = 0;
            summonTickDifference = summonTimeCode;
        }
        lastSummonTickCount = tickcount;
    }

    public final void checkPVPSummonAttackFrequency(final User chr) {
        final long tickdifference = (System.currentTimeMillis() - lastAttackTime);
        if (tickdifference < SkillFactory.getSummonData(skill).delay) {
            chr.getCheatTracker().registerOffense(CheatingOffense.FAST_SUMMON_ATTACK);
        }
        lastAttackTime = System.currentTimeMillis();
    }

    public final boolean isChangedMap() {
        return changedMap;
    }

    public final void setChangedMap(boolean cm) {
        this.changedMap = cm;
    }
}
