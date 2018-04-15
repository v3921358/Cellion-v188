package server.life.mob;

import net.OutPacket;

/**
 * Created on 1/4/2018.
 */
public class BurnedInfo {

    private int characterId, skillId, damage, interval, end, dotAnimation, dotCount, superPos, attackDelay, dotTickIdx, dotTickDamR;
    private int startTime;
    private int lastUpdate;

    public BurnedInfo deepCopy() {
        BurnedInfo copy = new BurnedInfo();
        copy.setCharacterId(getCharacterId());
        copy.setSkillId(getSkillId());
        copy.setDamage(getDamage());
        copy.setInterval(getInterval());
        copy.setEnd(getEnd());
        copy.setDotAnimation(getDotAnimation());
        copy.setDotCount(getDotCount());
        copy.setSuperPos(getSuperPos());
        copy.setAttackDelay(getAttackDelay());
        copy.setDotTickIdx(getDotTickIdx());
        copy.setDotTickDamR(getDotTickDamR());
        copy.setLastUpdate(getLastUpdate());
        copy.setStartTime(getStartTime());
        return copy;
    }

    public int getCharacterId() {
        return characterId;
    }

    public void setCharacterId(int characterId) {
        this.characterId = characterId;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getDotAnimation() {
        return dotAnimation;
    }

    public void setDotAnimation(int dotAnimation) {
        this.dotAnimation = dotAnimation;
    }

    public int getDotCount() {
        return dotCount;
    }

    public void setDotCount(int dotCount) {
        this.dotCount = dotCount;
    }

    public int getSuperPos() {
        return superPos;
    }

    public void setSuperPos(int superPos) {
        this.superPos = superPos;
    }

    public int getAttackDelay() {
        return attackDelay;
    }

    public void setAttackDelay(int attackDelay) {
        this.attackDelay = attackDelay;
    }

    public int getDotTickIdx() {
        return dotTickIdx;
    }

    public void setDotTickIdx(int dotTickIdx) {
        this.dotTickIdx = dotTickIdx;
    }

    public int getDotTickDamR() {
        return dotTickDamR;
    }

    public void setDotTickDamR(int dotTickDamR) {
        this.dotTickDamR = dotTickDamR;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(int lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void encode(OutPacket oPacket) {
        oPacket.EncodeInt(getCharacterId());
        oPacket.EncodeInt(getSkillId());
        oPacket.EncodeInt(getDamage());
        oPacket.EncodeInt(getInterval());
        oPacket.EncodeInt(getEnd());
        oPacket.EncodeInt(getDotAnimation());
        oPacket.EncodeInt(getDotCount());
        oPacket.EncodeInt(getSuperPos());
        oPacket.EncodeInt(getAttackDelay());
        oPacket.EncodeInt(getDotTickIdx());
        oPacket.EncodeInt(getDotTickDamR());
        oPacket.EncodeInt(getLastUpdate());
        oPacket.EncodeInt(getStartTime());

        oPacket.Fill(0, 69);
    }
}
