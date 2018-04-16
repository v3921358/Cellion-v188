function init() {
    em.setProperty("state", "0");
}

function setup(eim) {
    em.setProperty("state", "1");
    
    var eim = em.newInstance("AniBattle");
    eim.setPropertyEx("AllowEntrance", 1);
    
    var mf = em.getMapFactory();

    var map = mf.getMap(921133000);
    map.resetMap();
    
    eim.startEventTimer(120000); // 2 min start,

    return eim;
}

function playerEntry(eim, player) {
    var map = em.getMapFactory().getMap(921130000);
    player.changeMap(map, map.getPortal(0));
}

function playerRevive(eim, player) {
    return false;
}

function scheduledTimeout(eim) {
    if (eim.getPropertyEx("AllowEntrance") == 1) {
	eim.startEventTimer(1200000); // 20 min
	eim.warpAllPlayer(921133000);
	
	eim.setPropertyEx("AllowEntrance", 0);
    } else {
	eim.disposeIfPlayerBelow(100, 100000000);
	em.setProperty("state", "0");
    }
}

function changedMap(eim, player, mapid) {
    switch (mapid) {
	case 921130000:
	case 921131000:
	case 921132000:
	case 921133000:
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

function monsterValue(eim, mobId) {
    if (mobId == 8210013) {
	eim.broadcastMapEffect(em.getMapFactory().getMap(921133000), 5120025, "Guard Ani has been successfully defeated. Move by going to NPC Luden.");
    }
    return 1;
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	em.setProperty("state", "0");
    }
}

function end(eim) {
    eim.disposeIfPlayerBelow(100, 100000000);

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