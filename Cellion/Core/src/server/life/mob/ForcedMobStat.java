/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.life.mob;

import net.OutPacket;

/**
 *
 * @author Mazen Massoud
 */
public class ForcedMobStat {

    private long maxHP, maxMP, exp;
    private int pad, mad, pdr, mdr, acc, eva, pushed, speed, level, userCount;

    public long getMaxHP() {
        return maxHP;
    }

    public void setMaxHP(long maxHP) {
        this.maxHP = maxHP;
    }

    public long getMaxMP() {
        return maxMP;
    }

    public void setMaxMP(long maxMP) {
        this.maxMP = maxMP;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public int getPad() {
        return pad;
    }

    public void setPad(int pad) {
        this.pad = pad;
    }

    public int getMad() {
        return mad;
    }

    public void setMad(int mad) {
        this.mad = mad;
    }

    public int getPdr() {
        return pdr;
    }

    public void setPdr(int pdr) {
        this.pdr = pdr;
    }

    public int getMdr() {
        return mdr;
    }

    public void setMdr(int mdr) {
        this.mdr = mdr;
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

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public void encode(OutPacket oPaclet) {
        oPaclet.EncodeInt((int) getMaxHP());
        oPaclet.EncodeInt((int) getMaxMP());
        oPaclet.EncodeInt((int) getExp());
        oPaclet.EncodeInt(getPad());
        oPaclet.EncodeInt(getMad());
        oPaclet.EncodeInt(getPdr());
        oPaclet.EncodeInt(getMdr());
        oPaclet.EncodeInt(getAcc());
        oPaclet.EncodeInt(getEva());
        oPaclet.EncodeInt(getPushed());
        oPaclet.EncodeInt(getSpeed());
        oPaclet.EncodeInt(getLevel());
        oPaclet.EncodeInt(getUserCount());
    }

    public ForcedMobStat deepCopy() {
        ForcedMobStat copy = new ForcedMobStat();
        copy.setMaxHP((int) getMaxHP());
        copy.setMaxMP((int) getMaxMP());
        copy.setExp((int) getExp());
        copy.setPad(getPad());
        copy.setMad(getMad());
        copy.setPdr(getPdr());
        copy.setMdr(getMdr());
        copy.setAcc(getAcc());
        copy.setEva(getEva());
        copy.setPushed(getPushed());
        copy.setSpeed(getSpeed());
        copy.setLevel(getLevel());
        copy.setUserCount(getUserCount());
        return copy;
    }
}
