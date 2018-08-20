/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.maps.objects;

import java.util.ArrayList;
import java.util.List;
import net.OutPacket;

/**
 *
 * @author Mazen Massoud
 */
public class StopForceAtom {
    private int idx;
    private int count;
    private int weaponId;
    private List<Integer> angleInfo = new ArrayList<>();

    public void encode(OutPacket oPacket) {
        oPacket.EncodeInt(getIdx());
        oPacket.EncodeInt(getCount());
        oPacket.EncodeInt(getWeaponId());
        oPacket.EncodeInt(getAngleInfo().size());
        for(int i : getAngleInfo()) {
            oPacket.EncodeInt(i);
        }
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int nIdx) {
        this.idx = nIdx;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int nCount) {
        this.count = nCount;
    }

    public int getWeaponId() {
        return weaponId;
    }

    public void setWeaponId(int nWeaponID) {
        this.weaponId = nWeaponID;
    }

    public List<Integer> getAngleInfo() {
        return angleInfo;
    }

    public void setAngleInfo(List<Integer> aAngleInfo) {
        this.angleInfo = aAngleInfo;
    }
}
