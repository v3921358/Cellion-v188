function init() {
    em.setProperty("state", "0");
}

function setup(eim, leaderid) {
    em.setProperty("state", "1");
    
    var eim = em.newInstance("VonLeon" + leaderid);
    
    eim.setPropertyEx("RecordPlayerTimeSnapshot", 1);
    
    var mf = em.getMapFactory();
    
    var map = mf.getMap(211070100);
    map.resetMap();
     map.spawnNpc(2161000, new java.awt.Point(-53,-181), true);
    
    // spawnnpc

    var prison = mf.getMap(211070101);
    prison.resetMap();

    eim.startEventTimer(3600000); // 1 hr

    return eim;
}

function playerEntry(eim, player) {
    var map = em.getMapFactory().getMap(211070100);
    player.changeMap(map, map.getPortal(0));
}

function playerRevive(eim, player) {
    return false;
}

function scheduledTimeout(eim) {
    end(eim);
    em.setProperty("state", "0");
}

function changedMap(eim, player, mapid) {
    if (mapid != 211070101 && mapid != 211070100 && mapid != 211070110) {
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

function monsterValue(eim, mobId) {
    if (mobId == 8840000) {
	eim.RecordPlayerTimeSnapshot("Von_Leon");
	eim.broadcastMapEffect(em.getMapFactory().getMap(211070100), 5120025, "You have defeated Von Leon King. Please use the main entrance of the Audience Room to get out.");
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
    eim.disposeIfPlayerBelow(100, 211060801);

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