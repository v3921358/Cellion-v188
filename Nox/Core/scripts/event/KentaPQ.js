function init() {
    em.setProperty("state", "0");
}

function monsterValue(eim, mobId) {
    if (mobId == 9300460) { // Kenta
	if (eim.getPropertyEx("Stage2_ProtectDone") == 0) {
	    clearPQ(eim); // Ended
	}
    } else if (mobId == 9300461 || mobId == 9300468) {
	var state = eim.getPropertyEx("Stage4_Pianus");
	state++;
	eim.setPropertyEx("Stage4_Pianus", state);
	if (state == 2) {
	    var map = eim.getMapFactory().getMap(923040400);
	    map.killAllMonsters(true);

	    eim.broadcastshowEffect("quest/party/clear");
	    eim.broadcastplaySound("Party1/Clear");
	    eim.givePartyReward(4001535, 1, 140, 0.25, 0); // itemid, count, exp %, NX
	}
    }
    return 1;
}

function setup() {
    em.setProperty("state", "1");

    var eim = em.newInstance("KentaPQ");
    eim.setProperty("allow_warpback", "false");
    eim.setPropertyEx("AirBubble", 0); // Air bubble for stage 2, needs 20
    eim.setPropertyEx("Stage2_ProtectDone", 0);
    eim.setPropertyEx("Stage4_Pianus", 0);

    for (var i = 923040100; i <= 923040400; i += 100) {
	var map = eim.getMapFactory().getMap(i);
	map.resetMap();
	if (i == 923040300) {
	    map.spawnNpc(9020004, new java.awt.Point(2,626), true);
	} else if (i == 923040400) {
	    map.spawnNpc(9020004, new java.awt.Point(-119,168), true);
	}
    }
    eim.schedule("beginQuest", 3000);
    eim.startEventTimer(1200000);

    return eim;
}

function beginQuest(eim) { // Custom function
    eim.setPropertyEx("Spawned", 1); // prevent map rush
    eim.setFieldAverageLevel(923040100);
    eim.setFieldAverageLevel(923040200);
    eim.setFieldAverageLevel(923040300);
    eim.setFieldAverageLevel(923040400);

    var map = eim.getMapFactory().getMap(923040100);
    map.respawn(true);

    var map = eim.getMapFactory().getMap(923040400);
    map.spawnMonsterOnGroundBelow(em.getMonster(9300461), new java.awt.Point(578,138));

    var mob2 = em.getMonster(9300468);
    map.spawnMonsterOnGroundBelow(mob2, new java.awt.Point(-1061,138));
}

function protect1(eim) {
    eim.schedule("protect2", 60000);
    eim.broadcastMapEffect(em.getMapFactory().getMap(923040300), 5120052, "Just 2 minutes more!");
}

function protect2(eim) {
    eim.schedule("protect3", 40000);
    eim.broadcastMapEffect(em.getMapFactory().getMap(923040300), 5120052, "Just 1 minute more!");
}

function protect3(eim) {
    eim.schedule("protect4", 20000);
    eim.broadcastMapEffect(em.getMapFactory().getMap(923040300), 5120052, "Just 20 seconds more!");
}

function protect4(eim) {
    eim.setPropertyEx("Stage2_ProtectDone", 1);
    var map = eim.getMapFactory().getMap(923040300);
    map.spawnNpc(9020004, new java.awt.Point(-27,625), true);
    map.killAllMonsters(true);

    eim.broadcastshowEffect("quest/party/clear");
    eim.broadcastplaySound("Party1/Clear");
}

function playerEntry(eim, player) {
    if (em.PartyQuestDailyEntrance(player, 150043, 7)) {
	var map = eim.getMapFactory().getMap(923040100);
	player.changeMap(map, map.getPortal(0));
    } else {
	playerExit(eim,player);
    }
}

function playerDead(eim, player) {
}

function changedMap(eim, player, mapid) {
    switch (mapid) {
	case 923040100:
	case 923040200:
	case 923040300:
	case 923040400:
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
    if (eim.disposeIfPlayerBelow(3, 923040000)) {
	em.setProperty("state", "0");
    } else {
	playerExit(eim, player);
    }
}

function disbandParty(eim) {
    // Boot whole party and end
    eim.disposeIfPlayerBelow(100, 923040000);

    em.setProperty("state", "0");
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);

    var exit = eim.getMapFactory().getMap(923040000);
    player.changeMap(exit, exit.getPortal(0));
}

function scheduledTimeout(eim) {
    clearPQ(eim);
}

function clearPQ(eim) {
    // KPQ does nothing special with winners
    eim.disposeIfPlayerBelow(100, 923040000);

    em.setProperty("state", "0");
}

function allMonstersDead(eim) {
}

function cancelSchedule() {
}
