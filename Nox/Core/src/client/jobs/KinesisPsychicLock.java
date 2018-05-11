/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.jobs;

import java.awt.Point;

/**
 *
 * @author Mazen Massoud
 */
public class KinesisPsychicLock {

    private final int nLocalPsychicLockKey, nParentPsychicAreaKey, dwMobID, nStuffID, nUsableCount, posRelPosFirst;
    private final long nMobMaxHP, nMobCurHP;
    private final Point posStart, posRelPosSecond;

    public KinesisPsychicLock(int nLocalPsychicLockKey, int nParentPsychicAreaKey, int dwMobID, int nStuffID, long nMobMaxHP, long nMobCurHP, int nUsableCount, int posRelPosFirst, Point posStart, Point posRelPosSecond) {
        this.nLocalPsychicLockKey = nLocalPsychicLockKey;
        this.nParentPsychicAreaKey = nParentPsychicAreaKey;
        this.dwMobID = dwMobID;
        this.nStuffID = nStuffID;
        this.nUsableCount = nUsableCount;
        this.nMobMaxHP = nMobMaxHP;
        this.nMobCurHP = nMobCurHP;
        this.posRelPosFirst = posRelPosFirst;
        this.posStart = posStart;
        this.posRelPosSecond = posRelPosSecond;
    }

    public int getLocalPsychicLockKey() {
        return nLocalPsychicLockKey;
    }

    public int getParentPsychicAreaKey() {
        return nParentPsychicAreaKey;
    }

    public int getMobID() {
        return dwMobID;
    }

    public int getStuffID() {
        return nStuffID;
    }

    public long getMobMaxHP() {
        return nMobMaxHP;
    }

    public long getMobCurHP() {
        return nMobCurHP;
    }

    public int getUsableCount() {
        return nUsableCount;
    }

    public int getRelPosFirst() {
        return posRelPosFirst;
    }

    public Point getStart() {
        return posStart;
    }

    public Point getRelPosSecond() {
        return posRelPosSecond;
    }
}
