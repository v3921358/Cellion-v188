/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package server.maps;

import java.awt.Point;

import client.Client;
import constants.GameConstants;

public abstract class MapleMapObject {

    private final Point position = new Point();
    private int objectId;
    private short vxCS;
    private short vyCS;
    private short yCS;
    private short xCS;
    private int tEncodedGatherDuration;

    public Point getPosition() {
        return new Point(position);
    }

    public Point getTruePosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position.x = position.x;
        this.position.y = position.y;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int id) {
        this.objectId = id;
    }

    public int getRange() {
        return GameConstants.maxViewRangeSq();
    }

    /**
     * @return the vxCS
     */
    public short getVxCS() {
        return vxCS;
    }

    /**
     * @param vxCS the vxCS to set
     */
    public void setvXCS(short vxCS) {
        this.vxCS = vxCS;
    }

    /**
     * @return the vyCS
     */
    public short getVyCS() {
        return vyCS;
    }

    /**
     * @param vyCS the vyCS to set
     */
    public void setvYCS(short vyCS) {
        this.vyCS = vyCS;
    }

    /**
     * @return the yCS
     */
    public short getyCS() {
        return yCS;
    }

    /**
     * @param yCS the yCS to set
     */
    public void setyCS(short yCS) {
        this.yCS = yCS;
    }

    /**
     * @return the xCS
     */
    public short getxCS() {
        return xCS;
    }

    /**
     * @param xCS the xCS to set
     */
    public void setxCS(short xCS) {
        this.xCS = xCS;
    }

    /**
     * @return the tEncodedGatherDuration
     */
    public int gettEncodedGatherDuration() {
        return tEncodedGatherDuration;
    }

    /**
     * @param tEncodedGatherDuration the tEncodedGatherDuration to set
     */
    public void settEncodedGatherDuration(int tEncodedGatherDuration) {
        this.tEncodedGatherDuration = tEncodedGatherDuration;
    }

    public abstract MapleMapObjectType getType();

    public abstract void sendSpawnData(final Client client);

    public abstract void sendDestroyData(final Client client);
}
