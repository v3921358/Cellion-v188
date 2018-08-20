function init() {
    em.setProperty("state", "0");
}

function setup(eim, leaderid) {
    em.setProperty("state", "1");
    
    var eim = em.newInstance("CygnusEmpress" + leaderid);
    
    eim.setPropertyEx("RecordPlayerTimeSnapshot", 1);
    
    var mf = em.getMapFactory();
    
    var map = mf.getMap(271040100);
    map.resetMap();
    var mob = em.getMonster(8850012);
    eim.registerMonster(mob);
    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(-470,115));
    
    map = mf.getMap(271040110); // Resting spot
    map.resetMap();

    eim.startEventTimer(3600000); // 1 hr

    return eim;
}

function playerEntry(eim, player) {
    var map = em.getMapFactory().getMap(271040100);
    player.changeMap(map, map.getPortal(0));
    
    player.dropMessage(5, "Potions have a 30 seconds cooldown.");
}

function playerRevive(eim, player) {
    return false;
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, 271030000);
}

function changedMap(eim, player, mapid) {
    if (mapid != 271040100 && mapid != 271040110) {
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
    if (mobId == 8850011) {
	eim.RecordPlayerTimeSnapshot("Cygnus_Empress");
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
    eim.disposeIfPlayerBelow(100, 271030000);

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