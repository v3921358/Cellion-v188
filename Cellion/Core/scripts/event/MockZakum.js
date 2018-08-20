var retmap = 221000000;

function init() {
    em.setProperty("state", "0");
}

function setup(eim, leaderid) {
    em.setProperty("state", "1");

    var eim = em.newInstance("MockZakum" + leaderid);
    
    var map = eim.createInstanceMapS(280030001, retmap, false, false, false, null);
    map.toggleDrops(true);
    
    eim.setPropertyEx("SuperZakumDead",0);
    eim.setPropertyEx("RecordPlayerTimeSnapshot", 1);

    eim.startEventTimer(2400000); // 30 min
    eim.schedule("beginTease", 10000);

    return eim;
}

function beginTease(eim) { // Custom function
    var map = eim.getMapInstance(0);
    
 //   eim.setFieldAverageLevel(map);
    map.spawnNpc(2050012, new java.awt.Point(-383,-422), true);
    eim.schedule("beginSpawn", 10000);
    
    eim.broadcastMapEffect(map, 5120025, "Don't panic, Adventurers! You'll be much stronger after being trained.");
}

function beginSpawn(eim) { // Custom function
    var map = eim.getMapInstance(0);
    
    eim.broadcastChangeMusic(map, "Bgm23/CrimsonTower");
    
    var mobs = [8800100, 8800103,8800104,8800105,8800106,8800107,8800108,8800109,8800110];
    for (var i = 0; i < mobs.length; i++) {
	var mob = em.getMonster(mobs[i]);
	
	var override = em.newMonsterStats(mobs[i]);
	
	override.setOExp(0);
	override.setOHp(1100000000);
	mob.setHp(1100000000);
	override.setOPdRate(80);
	override.setOMdRate(80);
	override.setOAcc(50000);
	override.setOPADamage(1);
	override.setOMADamage(1);
	
	mob.setOverrideStats(override);
	if (i == 0) {
	    map.spawnFakeMonsterOnGroundBelow(mob, new java.awt.Point(-10, -215));
	} else {
	    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(-10, -215));
	}
    }
}

function playerEntry(eim, player) {
    var map = eim.getMapInstance(0);
    
    player.changeMap(map, map.getPortal(0));
}

function playerRevive(eim, player) {
    return false;
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, retmap);
}

function changedMap(eim, player, mapid) {
    if (mapid != 280030001) {
	eim.unregisterPlayer(player);
	eim.RemoveWarpbackList(player);

	if (eim.disposeIfPlayerBelow(0, 0)) {
	    em.setProperty("state", "0");
	}
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
    if (mobId == 8800102) { // Main third body	
	
	if (eim.getPropertyEx("SuperZakumDead") == 0) {
	    eim.setPropertyEx("SuperZakumDead",1);
	    eim.setProperty("allow_warpback", "false");
	    
	    eim.schedule("superZakumDead", 10000);

	    eim.RecordPlayerTimeSnapshot("Super_Zakum");
	}
    }
    return 1;
}

function superZakumDead(eim) {
    var map = eim.getMapInstance(0);

    em.dropItem(map, 1003361, 0, 1, 1, -90, -210, 0);
 /*   if (Math.random() * 100 < 30) {
	em.dropItem(map, 3010313, 0, 1, 1, 90, -210, 1);
    }*/
    map.spawnMonsterOnGroundBelow(em.getMonster(8890000), new java.awt.Point(-90,-210));
    map.spawnMonsterOnGroundBelow(em.getMonster(8890002), new java.awt.Point(90,-210));
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	em.setProperty("state", "0");
    }
}

function end(eim) {
    eim.disposeIfPlayerBelow(100, retmap);

    em.setProperty("state", "0");
}

function clearPQ(eim) {
    end(eim);
}

function allMonstersDead(eim) {}
function removePlayer(eim, player) {}
function leftParty (eim, player) {}
function disbandParty (eim) {}
function playerDead(eim, player) {}
function cancelSchedule() {}