/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
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
package server;

import handling.world.World;
import server.maps.objects.User;
import tools.packet.WvsContext;

/**
 *
 * @author KyleShum
 */
public class MapleAchievement {

    private String name;
    private int reward;
    private final boolean notice;

    public MapleAchievement(String name, int reward) {
        this.name = name;
        this.reward = reward;
        this.notice = true;
    }

    public MapleAchievement(String name, int reward, boolean notice) {
        this.name = name;
        this.reward = reward;
        this.notice = notice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public boolean getNotice() {
        return notice;
    }

    public void finishAchievement(User chr) {
        chr.modifyCSPoints(1, reward, false);
        chr.setAchievementFinished(MapleAchievements.getInstance().getByMapleAchievement(this));
        if (notice && !chr.isGM()) {
            World.Broadcast.broadcastMessage(WvsContext.broadcastMsg(6, "[Achievement] Congratulations to " + chr.getName() + " on " + name + " and rewarded with " + (reward / 2) + " cash!"));
        } else {
            chr.getClient().SendPacket(WvsContext.broadcastMsg(5, "[Achievement] You've gained " + (reward / 2) + " Cash as you " + name + "."));
        }
    }
}
