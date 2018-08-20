/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
/**
 * @author TheGM
 * @purpose Spawns monsters escape pq (9218000 or 9300453)
 * @map aderal prison
 */
/*function act() {
	//rm.mapMessage(6, "One of the pieces has been placed.");
         rm.gainItem(4001528, -1);
	var em = rm.getEventManager("Prison");
	if (em != null) {
            if(rm.isAllReactorState(9218000, 0) == true) {
                em.setProperty("glpq5", parseInt(em.getProperty("glpq5")) + 1);
		eim.setProperty("kentaSaving", parseInt(em.getProperty("kentaSaving")) + 1);
                rm.showMapEffect("quest/party/clear");
                rm.playSound(true, "Party1/Clear");
		//var r = rm.getMap().getReactorByName("minerva");
		//r.forceHitReactor(r.getState() + 1);
            }
	}
}*/

function act() {
	var em = rm.getEventManager("Prison");
	if (em != null) {
		rm.gainItem(4001528, -1);
		em.setProperty("kentaSaving", parseInt(em.getProperty("kentaSaving")) + 1);
		if (em.getProperty("kentaSaving").equals("4")) { //all 5 done
			rm.showMapEffect("quest/party/clear");
                        rm.playSound(true, "Party1/Clear");
		}
	}
}