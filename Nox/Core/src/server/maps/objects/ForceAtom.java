/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.maps.objects;

import java.awt.Point;
import net.OutPacket;

/**
 *
 * @author 
 */
public class ForceAtom {
    private int key;
    private int inc;
    private int firstImpact;
    private int secondImpact;
    private int angle;
    private int startDelay;
    private int createTime;
    private int maxHitCount;
    private int effectIdx;
    private Point startPosition;

    public ForceAtom() {
    }

    public ForceAtom(int key, int inc, int firstImpact, int secondImpact, int angle, int startDelay, int createTime, int maxHitCount, int effectIdx, Point startPosition) {
        this.key = key;
        this.inc = inc;
        this.firstImpact = firstImpact;
        this.secondImpact = secondImpact;
        this.angle = angle;
        this.startDelay = startDelay;
        this.createTime = createTime;
        this.maxHitCount = maxHitCount;
        this.effectIdx = effectIdx;
        this.startPosition = startPosition;
    }

    public void encode(OutPacket oPacket) {
        oPacket.EncodeInteger(getKey());
        oPacket.EncodeInteger(getInc());
        oPacket.EncodeInteger(getFirstImpact());
        oPacket.EncodeInteger(getSecondImpact());
        oPacket.EncodeInteger(getAngle());
        oPacket.EncodeInteger(getStartDelay());
        oPacket.EncodeInteger((int) getStartPosition().getX());
        oPacket.EncodeInteger((int) getStartPosition().getY());
        oPacket.EncodeInteger(getCreateTime());
        oPacket.EncodeInteger(getMaxHitCount());
        oPacket.EncodeInteger(getEffectIdx());
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public int getInc() {
        return inc;
    }

    public void setInc(int inc) {
        this.inc = inc;
    }

    public int getFirstImpact() {
        return firstImpact;
    }

    public void setFirstImpact(int firstImpact) {
        this.firstImpact = firstImpact;
    }

    public int getSecondImpact() {
        return secondImpact;
    }

    public void setSecondImpact(int secondImpact) {
        this.secondImpact = secondImpact;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getStartDelay() {
        return startDelay;
    }

    public void setStartDelay(int startDelay) {
        this.startDelay = startDelay;
    }

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public int getMaxHitCount() {
        return maxHitCount;
    }

    public void setMaxHitCount(int maxHitCount) {
        this.maxHitCount = maxHitCount;
    }

    public int getEffectIdx() {
        return effectIdx;
    }

    public void setEffectIdx(int effectIdx) {
        this.effectIdx = effectIdx;
    }

    public Point getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(Point startPosition) {
        this.startPosition = startPosition;
    }
}
