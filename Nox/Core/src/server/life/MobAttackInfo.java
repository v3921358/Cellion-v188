package server.life;

public class MobAttackInfo {

    private final byte type, action, attackId;
    private final int bulletSpeed;
    private final boolean isDeadlyAttack, magic, knockback, isElement, fixDamRType, notMissAttack;
    private final int mpBurn, mpCon, cooltime, damageSharingTime;
    private final int diseaseSkill, diseaseLevel;
    private final short fixDamR, attackRatio;

    public MobAttackInfo(byte attackId, byte action, boolean magic, boolean isDeadlyAttack, boolean knockback, int bulletSpeed, int mpBurn, int diseaseSkill, int diseaseLevel, int mpCon, boolean isElement,
            byte type, short fixDamR, boolean fixDamRType, short attackRatio, int cooltime, int damageSharingTime, boolean notMissAttack) {
        this.attackId = attackId;
        this.action = action;
        this.magic = magic;
        this.isDeadlyAttack = isDeadlyAttack;
        this.knockback = knockback;
        this.bulletSpeed = bulletSpeed;
        this.mpBurn = mpBurn;
        this.diseaseSkill = diseaseSkill;
        this.diseaseLevel = diseaseLevel;
        this.mpCon = mpCon;
        this.isElement = isElement;

        this.type = type;
        this.fixDamR = fixDamR;
        this.fixDamRType = fixDamRType;
        this.attackRatio = attackRatio;

        this.cooltime = cooltime;
        this.damageSharingTime = damageSharingTime;
        this.notMissAttack = notMissAttack;
    }

    public byte getType() {
        return type;
    }

    public int getCooltime() {
        return cooltime;
    }

    public int getDamageSharingTime() {
        return damageSharingTime;
    }

    public boolean isNotMissAttack() {
        return notMissAttack;
    }

    public short getFixDamR() {
        return fixDamR;
    }

    public boolean isFixDamRType() {
        return fixDamRType;
    }

    public short getAttackRatio() {
        return attackRatio;
    }

    public boolean isElement() {
        return isElement;
    }

    public int getBulletSpeed() {
        return bulletSpeed;
    }

    public byte getAttackId() {
        return attackId;
    }

    public byte getAction() {
        return action;
    }

    public boolean isKnockBack() {
        return knockback;
    }

    public boolean isDeadlyAttack() {
        return isDeadlyAttack;
    }

    public boolean isMagic() {
        return magic;
    }

    public int getMpBurn() {
        return mpBurn;
    }

    public int getDiseaseSkill() {
        return diseaseSkill;
    }

    public int getDiseaseLevel() {
        return diseaseLevel;
    }

    public int getMpCon() {
        return mpCon;
    }
}
