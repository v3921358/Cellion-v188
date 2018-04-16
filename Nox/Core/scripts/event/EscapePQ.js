function init() {
    em.setProperty("state", "0");
}

function monsterValue(eim, mobId) {
    if (mobId == 9300454 && eim.getPropertyEx("Stage7Clear") == 0) {
	eim.setPropertyEx("Stage7Clear", 1);

	var map = eim.getMapFactory().getMap(921160700);
	map.killAllMonsters(true);
	eim.broadcastshowEffect("quest/party/clear");
	eim.broadcastplaySound("Party1/Clear");
	eim.givePartyReward(4001534, 1, 135, 0.25, 0); // itemid, count, NX
	eim.broadcastMapEffect(map, 5120053, "You have finally defeated Guard Ani!");
    }
    return 1;
}

function setup() {
    em.setProperty("state", "1");

    var eim = em.newInstance("EscapePQ");
    eim.setProperty("allow_warpback", "false");
    eim.setPropertyEx("Stage1Clear", 0);
    eim.setPropertyEx("Stage2Clear", 0);
    eim.setPropertyEx("Stage3Clear", 0);
    eim.setPropertyEx("Stage4Clear", 0);
    eim.setPropertyEx("Stage5Clear", 0);
    eim.setPropertyEx("Stage6Clear", 0);
    eim.setPropertyEx("Stage7Clear", 0);

    for (var i = 921160100; i <= 921160700; i += 100) {
	var map = eim.getMapFactory().getMap(i);
	map.resetMap();
    }
    for (var i = 921160300; i <= 921160350; i += 10) {
	var map = eim.getMapFactory().getMap(i);
	map.resetMap();
    }
    eim.schedule("beginQuest", 3000);
    eim.startEventTimer(1200000);

    return eim;
}

function beginQuest(eim) { // Custom function
    eim.setPropertyEx("Spawned", 1); // prevent map rush
    for (var i = 921160100; i <= 921160700; i += 100) {
	eim.setFieldAverageLevel(i);
    }
    for (var i = 921160100; i <= 921160700; i += 100) {
	var map = eim.getMapFactory().getMap(i);
	switch (i) {
	    case 921160200:
	    case 921160400:
		map.respawn(true);
		break;
	}
    }
    for (var i = 921160300; i <= 921160350; i += 10) {
	var map = eim.getMapFactory().getMap(i);
	map.respawn(true);
    }
    var map = eim.getMapFactory().getMap(921160700);
    map.spawnMonsterOnGroundBelow(em.getMonster(9300454), new java.awt.Point(-708,-181));

}
function playerEntry(eim, player) {
    if (em.PartyQuestDailyEntrance(player, 150044, 7)) {
	var map = eim.getMapFactory().getMap(921160100);
	player.changeMap(map, map.getPortal(0));
    } else {
	playerExit(eim,player);
    }
}

function playerDead(eim, player) {
}

function changedMap(eim, player, mapid) {
    if (mapid >= 921160100 && mapid <= 921160700) {
	return;
    }
    eim.unregisterPlayer(player);
    eim.RemoveWarpbackList(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	em.setProperty("state", "0");
    }
}

function playerRevive(eim, player) {
}

function playerDisconnected(eim, player) {
    return -2;
}

function leftParty(eim, player) {
    // If only 2 players are left, uncompletable
    if (eim.disposeIfPlayerBelow(3, 921160000)) {
	em.setProperty("state", "0");
    } else {
	playerExit(eim, player);
    }
}

function disbandParty(eim) {
    // Boot whole party and end
    eim.disposeIfPlayerBelow(100, 921160000);

    em.setProperty("state", "0");
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);

    var exit = eim.getMapFactory().getMap(921160000);
    player.changeMap(exit, exit.getPortal(0));
}

function scheduledTimeout(eim) {
    clearPQ(eim);
}

function clearPQ(eim) {
    // KPQ does nothing special with winners
    eim.disposeIfPlayerBelow(100, 921160000);

    em.setProperty("state", "0");
}

function allMonstersDead(eim) {
}

function cancelSchedule() {
}
