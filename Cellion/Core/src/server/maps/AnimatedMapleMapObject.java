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

public abstract class AnimatedMapleMapObject extends MapleMapObject {

    private int stance;

    public int getStance() {
        return stance;
    }

    /**
     * Set the stance of the animated map object
     *
     * @param stance
     */
    public void setStance(int stance) {
        this.stance = stance;
    }

    /**
     * A convenient way to set the stance via boolean
     *
     * @param left
     */
    public void setFacingLeft(boolean left) {
        this.stance = left ? 0 : 1;
    }

    public boolean isFacingLeft() {
        return getStance() % 2 != 0;
    }

    public int getFacingDirection() {
        return Math.abs(getStance() % 2);
    }

}
