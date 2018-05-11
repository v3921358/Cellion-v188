package server.life;

import constants.GameConstants;

public class ForcedMonsterStats {

    private int exp, watk, matk, acc, eva, PDRate, MDRate, pushed, speed, level, nUserCount;
    private long hp, mp;

    public ForcedMonsterStats(MonsterStats stats) {
        hp = stats.getHp();
        exp = stats.getExp();
        mp = stats.getMp();
        watk = stats.getPADamage();
        matk = stats.getMADamage();
        acc = stats.getAcc();
        eva = stats.getEva();
        PDRate = stats.getPDRate();
        MDRate = stats.getMDRate();
        pushed = stats.getPushed();
        speed = stats.getSpeed();
        level = stats.getLevel();
    }

    public ForcedMonsterStats(MonsterStats stats, int newLevel, boolean pqMob) { // here we go i think
        final double mod = (double) newLevel / (double) stats.getLevel();
        final double hpRatio = (double) stats.getHp() / (double) stats.getExp();
        final double pqMod = (pqMob ? 1.5 : 1.0); // god damn
        hp = (long) Math.round((!stats.isBoss() ? GameConstants.getMonsterHP(newLevel) : (stats.getHp() * mod)) * pqMod); // right here lol
        exp = (int) Math.round((!stats.isBoss() ? (GameConstants.getMonsterHP(newLevel) / hpRatio) : (stats.getExp())));
        mp = (int) Math.round(stats.getMp() * mod * pqMod);
        watk = (int) Math.round(stats.getPADamage() * mod);
        matk = (int) Math.round(stats.getMADamage() * mod);
        acc = (int) Math.round(stats.getAcc() + Math.max(0, newLevel - stats.getLevel()) * 2);
        eva = (int) Math.round(stats.getEva() + Math.max(0, newLevel - stats.getLevel()));
        PDRate = Math.min(stats.isBoss() ? 30 : 20, (int) Math.round(stats.getPDRate() * mod));
        MDRate = Math.min(stats.isBoss() ? 30 : 20, (int) Math.round(stats.getMDRate() * mod));
        pushed = (int) Math.round(stats.getPushed() * mod);
        speed = (int) Math.round(stats.getSpeed() * mod);
        level = 150;
        //  exp = exp * level;
    }

    public long getMp() {
        return mp;
    }

    public void setMp(int mp) {
        this.mp = mp;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public long getHp() {
        return hp;
    }

    public void setHp(long hp) {
        this.hp = hp;
    }

    public int getWatk() {
        return watk;
    }

    public void setWatk(int watk) {
        this.watk = watk;
    }

    public int getMatk() {
        return matk;
    }

    public void setMatk(int matk) {
        this.matk = matk;
    }

    public int getAcc() {
        return acc;
    }

    public void setAcc(int acc) {
        this.acc = acc;
    }

    public int getEva() {
        return eva;
    }

    public void setEva(int eva) {
        this.eva = eva;
    }

    public int getPDRate() {
        return PDRate;
    }

    public void setPDRate(int pDRate) {
        PDRate = pDRate;
    }

    public int getMDRate() {
        return MDRate;
    }

    public void setMDRate(int mDRate) {
        MDRate = mDRate;
    }

    public int getPushed() {
        return pushed;
    }

    public void setPushed(int pushed) {
        this.pushed = pushed;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getnUserCount() {
        return nUserCount;
    }

    public void setnUserCount(int nUserCount) {
        this.nUserCount = nUserCount;
    }
}
