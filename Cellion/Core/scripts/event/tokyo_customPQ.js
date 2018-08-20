function init() {
}

function setup(eim, leaderid) {
    var eim = em.newInstance("tokyo_customPQ" + leaderid);

    eim.setPropertyEx("counter", 0);
    eim.setPropertyEx("diecounter", 0);
    eim.setProperty("allow_warpback", "false");
    eim.setPropertyEx("kc_second", 0);

    var map = eim.createInstanceMapS(802000711,910000000,null);
    map.toggleDrops(true);
    map.killAllMonsters(false);

    eim.schedule("beginQuest", 10000);
    return eim;
}

function beginQuest(eim) { // Custom function
    eim.broadcastPlayerMsg(6, "Dangers waits here in 2102! Defeat all Dunas and Imperial Guard!!!");

    var map = eim.getMapInstance(0);
    // First Job
    var mob = em.getMonster(9400288); // Imperial Guard
    var modified = em.newMonsterStats(9400288);

    modified.setOExp(mob.getMobExp() * 0.3);
    modified.setOHp(mob.getMobMaxHp() * 0.7);
    mob.setOverrideStats(modified);

    eim.registerMonster(mob);
    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(-581,-487));

    // Second mob
    var mob = em.getMonster(9400288); // Imperial Guard
    mob.setOverrideStats(modified);
    eim.registerMonster(mob);
    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(-887,335));

    // Second mob
    var mob = em.getMonster(9400288); // Imperial Guard
    mob.setOverrideStats(modified);
    eim.registerMonster(mob);
    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(-58,335));

    // Third mob
    var mob = em.getMonster(9400270); // Dunas
    var modified = em.newMonsterStats(9400270);

    modified.setOExp(mob.getMobExp() * 0.25);
    modified.setOHp(mob.getMobMaxHp() * 0.4);
    mob.setOverrideStats(modified);

    eim.registerMonster(mob);
    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(-784,43));

    eim.startEventTimer(1200000); // 20 min
    eim.showMapEffect("Gstar/start");
}

function playerEntry(eim, player) {
    var map = eim.getMapInstance(0);
    player.changeMap(map, map.getPortal(0));
    player.dropMessage(5, "As you hear the sound of engines running, you are now travelling through space and time, rushing forward at the speed of light~! You have approx. 20 minutes before the engine runs out of fuel.");
}

function playerRevive(eim, player) {
    return false;
}

function scheduledTimeout(eim) {
    eim.broadcastPlayerMsg(6, "Party quest failed, you will be brought back to where you've came from. Travelling back in time ...");
    eim.disposeIfPlayerBelow(100, 910000000);
}

function changedMap(eim, player, mapid) {
    switch (mapid) {
	case 802000311:
	case 802000711:
	case 802000111:
	case 610030000:
	    return;
    }
    eim.unregisterPlayer(player);
    eim.RemoveWarpbackList(player);

    eim.disposeIfPlayerBelow(100, 910000000);
}

function playerDisconnected(eim, player) {
    return 0;
}

function monsterValue(eim, mobId, chr) {
    if (eim.getPropertyEx("counter") == 1) {
	switch (mobId) {
	    case 9400258: // Maverick Y
	    case 9400257: // Maverick B
	    case 9400256: // Maverick A
		break;
	    default:
		return 0;
	}
	var kc = eim.getPropertyEx("kc_second") + 1;
	var killc = eim.getPropertyEx("MobKillC");
	eim.broadcastPlayerMsg(5, "Defeated "+kc+"/"+killc+"  monsters.");

	if (kc == killc) { // 25
	    var counter = eim.getPropertyEx("counter");

	    eim.saveBossQuest(200, 150001, false);
	    eim.broadcastPlayerMsg(5, "Gained 200 boss PQ points.");
	    eim.broadcastMapEffect(eim.getMapInstance(1), 5120009, "Victory! Proceeding with 20 minutes worth of fuel.");

	    eim.schedule("warpThird", 10000);

	    eim.setPropertyEx("counter", counter+1);
	}
	eim.setPropertyEx("kc_second", kc);
    }
    return 1;
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);

    eim.disposeIfPlayerBelow(0, 0);
}

function end(eim) {
    eim.disposeIfPlayerBelow(100, 910000000);
}

function clearPQ(eim) {
    end(eim);
}

function allMonstersDead(eim) {
    var counter = eim.getPropertyEx("counter");
    
    if (counter == 2) {
	eim.saveBossQuest(300, 150001, true);
	eim.broadcastPlayerMsg(5, "Gained 300 boss PQ points.");
	eim.broadcastMapEffect(eim.getMapInstance(2), 5120034, "Victory! Proceeding with 5 minutes worth of fuel.");

	eim.schedule("warpFourth", 10000);
    } else if (counter == 3) {
	eim.saveBossQuest(4500, 150001, true);
	eim.broadcastPlayerMsg(5, "Gained 4500 boss PQ points.");
	eim.saveBossQuest(2, 150008, true);
	eim.broadcastPlayerMsg(5, "Gained 2 special Boss PQ point!!");
	eim.BossQuestAchievement(5);
	eim.broadcastMapEffect(eim.getMapInstance(3), 5120027, "Victory! Mission accomplish!");

	eim.schedule("warpOut", 10000);
    } else if (counter == 0) {
	eim.saveBossQuest(100, 150001, true);
	eim.broadcastPlayerMsg(5, "Gained 100 boss PQ points.");
	eim.broadcastMapEffect(eim.getMapInstance(0), 5120034, "Victory! Proceeding with 5 minutes worth of fuel.");

	eim.schedule("warpSecond", 10000);
    }
    eim.setPropertyEx("counter", counter+1);
}

function warpOut(eim) { // Custom function
    eim.disposeIfPlayerBelow(100, 910000000);
}

function warpSecond(eim) { // Custom function
    var map = eim.createInstanceMapS(802000311,910000000,null);
    map.toggleDrops(true);
    
    var defeat = Math.round(25 + Math.random() * 10);
    eim.setPropertyEx("MobKillC", defeat);
    eim.warpAllPlayer_CustomMap(1);
    eim.broadcastPlayerMsg(6, "Dangers waits here in year 2095! Defeat "+defeat+" monsters!");
    eim.restartEventTimer(300000); // 5 min
}

function warpThird(eim) { // Custom function
    var map = eim.createInstanceMapS(610030000,910000000,null);
    map.toggleDrops(true);

    eim.warpAllPlayer_CustomMap(2);
    eim.broadcastPlayerMsg(6, "Dangers waits here!");
    eim.restartEventTimer(300000); // 5 min
    
    for (var i = 0; i < 20; i++) {
	var mob = em.getMonster(9400594); // Imperial Guard
	eim.registerMonster(mob);
	map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(500 + (Math.random() * 100),171));
    }
}

function warpFourth(eim) {
    var map = eim.createInstanceMapS(802000111,910000000,null);
    map.toggleDrops(true);
    
    eim.warpAllPlayer_CustomMap(3);
    eim.broadcastPlayerMsg(6, "Dangers waits here in year 2102!");
    eim.restartEventTimer(600000); // 20 min

    var mob = em.getMonster(9400266);
    var modified = em.newMonsterStats(9400266);

    modified.setOExp(mob.getMobExp() * 0.4);
    modified.setOHp(mob.getMobMaxHp() * 1.2);
    mob.setOverrideStats(modified);

    eim.registerMonster(mob);
    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(411,45));
    
    mob = em.getMonster(9400288); // imperial guard
    var modified = em.newMonsterStats(9400288);

    modified.setOExp(mob.getMobExp() * 0.4);
    modified.setOHp(mob.getMobMaxHp() * 0.2);
    mob.setOverrideStats(modified);

    eim.registerMonster(mob);
    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(395,45));
}

function leftParty (eim, player) {
    eim.disposeIfPlayerBelow(100, 910000000);
}

function disbandParty (eim) {
    eim.disposeIfPlayerBelow(100, 910000000);
}

function playerDead(eim, player) {
    var counter = eim.getPropertyEx("diecounter");
    if (counter <= 1) {
	eim.broadcastPlayerMsg(5, player.getName() + " has died, another causality will result in the mission failing.");
	eim.setPropertyEx("diecounter", counter+1);
    } else {
	eim.broadcastPlayerMsg(5, "Mission has failed as a result of players' death.");
	eim.disposeIfPlayerBelow(100, 910000000);
    }
}

function cancelSchedule() {}