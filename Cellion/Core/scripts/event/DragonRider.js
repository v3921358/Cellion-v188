function init() {
    em.setProperty("state", "0");
}

function setup(eim, leaderid) {
    em.setProperty("state", "1");

    var eim = em.newInstance("DragonRider" + leaderid);
    
    eim.setPropertyEx("StageSet", 1);
    eim.setPropertyEx("Stage1_MobLeft", 99999);
    eim.setPropertyEx("Stage2_MobLeft", 99999);
    eim.setProperty("LastBossSummoned", "false");
    eim.setPropertyEx("defeated", 0);
    eim.setProperty("allow_warpback", "false");

    var fact = em.getMapFactory();
    fact.getMap(240080040).resetMap(); // Revive site
    fact.getMap(240080041).resetMap();
    // respawn
    for (var i = 240080100; i <= 240080500; i+=100) {
	fact.getMap(i).resetMap();
    }
    eim.schedule("beginQuest", 3000);
    eim.startEventTimer(3600000); // 1 hrs

    return eim;
}

function beginQuest(eim) { // Custom function
    eim.setPropertyEx("Spawned", 1); // prevent map rush
    
    var fact = em.getMapFactory();
    for (var i = 240080100; i <= 240080500; i+=100) {
	eim.setFieldAverageLevel(i);
	
	var map = fact.getMap(i);
	map.respawn(true);
	switch (i) {
	    case 240080100:
		eim.setPropertyEx("Stage1_MobLeft", em.getMonsterSize(i));
		break;
	    case 240080200:
		eim.setPropertyEx("Stage2_MobLeft", em.getMonsterSize(i));
		break;
	    case 240080300:
		map.spawnMonsterOnGroundBelow(em.getMonster(8300006), new java.awt.Point(916, -96));
		break;
	}
    }
}

function playerEntry(eim, player) {
    if (em.PartyQuestDailyEntrance(player, 150045, 5)) {
	var map = em.getMapFactory().getMap(240080100);
	player.changeMap(map, map.getPortal(0));
    } else {
	playerExit(eim,player);
    }
}

function playerRevive(eim, player) {
    return false;
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, 240080000);
}

function changedMap(eim, player, mapid) {
    switch (mapid) {
	case 240080040:
	case 240080041:
	case 240080100:
	case 240080200:
	case 240080300:
	case 240080400:
	case 240080500:
	case 240080600:
	case 240080700:
	case 240080800:
	    return;
    }
    eim.unregisterPlayer(player);
    eim.RemoveWarpbackList(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	em.setProperty("state", "0");
    }
}

function playerDisconnected(eim, player) {
    return 0;
}

function scheduledTimeout(eim) {
    end(eim);
    em.setProperty("state", "0");
}

function monsterValue(eim, mobId) {
    switch (eim.getPropertyEx("StageSet")) {
	case 1:
	    var mob = eim.getPropertyEx("Stage1_MobLeft");
	    mob--;
	    if (mob > 0) {
		eim.broadcastTitle("There are "+mob+" monsters left.");
	    } else { // Completed
		eim.broadcastshowEffect("quest/party/clear");
		eim.broadcastplaySound("Party1/Clear");
		eim.setPropertyEx("StageSet", 2);
	    }
	    eim.setPropertyEx("Stage1_MobLeft", mob);
	    break;
	case 2:
	    var mob = eim.getPropertyEx("Stage2_MobLeft");
	    mob--;
	    if (mob > 0) {
		eim.broadcastTitle("There are "+mob+" monsters left.");
	    } else { // Completed
		eim.broadcastshowEffect("quest/party/clear");
		eim.broadcastplaySound("Party1/Clear");
		eim.setPropertyEx("StageSet", 3);
	    }
	    eim.setPropertyEx("Stage2_MobLeft", mob);
	    break;
	case 3:
	    if (mobId == 8300006) {
		eim.broadcastshowEffect("quest/party/clear");
		eim.broadcastplaySound("Party1/Clear");
		eim.setPropertyEx("StageSet", 4);
	    }
	    break;
	case 4:
	    if (eim.getPropertyEx("defeated") == 0 && mobId == 8300007) {
		eim.schedule("bossDefeated", 5000);
		eim.setPropertyEx("defeated", 1);
	    }
	    break;
    }
    return 1;
}

function bossDefeated(eim) { // custom
    var map = em.getMapFactory().getMap(240080500);
    map.setReactorState(); // special map :D
    map.spawnNpc(2085003, new java.awt.Point(-325, -10), true);
    
    eim.broadcastshowEffect("quest/party/clear");
    eim.broadcastplaySound("Party1/Clear");
    eim.givePartyReward(0,0,4,7000); // itemid, count, exp %, NX
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	em.setProperty("state", "0");
    }
}

function end(eim) {
    eim.disposeIfPlayerBelow(100, 240080000);

    em.setProperty("state", "0");
}

function clearPQ(eim) {
    end(eim);
}

function allMonstersDead(eim) {}
function removePlayer(eim, player) {}
function leftParty (eim, player) {}
function disbandParty (eim) {
    end(eim);
}
function playerDead(eim, player) {}
function cancelSchedule() {}