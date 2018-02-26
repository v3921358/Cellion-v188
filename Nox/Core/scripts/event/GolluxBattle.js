var minPlayers = 2;

function init() {
em.setProperty("state", "0");
	em.setProperty("leader", "true");
}

function setup(level, leaderid) {
em.setProperty("state", "1");
	em.setProperty("leader", "true");
    var eim = em.newInstance("2095_tokyo" + leaderid);
        var map =  eim.setInstanceMap(863010300);
        var map2 = eim.setInstanceMap(863010200);
        var map3 = eim.setInstanceMap(863010500);
        var map4 = eim.setInstanceMap(863010600);
	map.resetFully();
  map2.resetFully();
  map3.resetFully();
  map4.resetFully();
    return eim;
}

function playerEntry(eim, player) {
var map = eim.getMapFactory().getMap(863010300);
    player.changeMap(map, map.getPortal(0));
}

function playerRevive(eim, player) {
}

function scheduledTimeout(eim) {
    end(eim);
}

function changedMap(eim, player, mapid) {
    if (mapid < 863010200 || mapid > 863010600) {
	eim.unregisterPlayer(player);

	if (eim.disposeIfPlayerBelow(0, 0)) {
		em.setProperty("state", "0");
		em.setProperty("leader", "true");
	}
    }
}

function playerDisconnected(eim, player) {
    return 0;
}

function monsterValue(eim, mobId) {
    return 1;
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	em.setProperty("state", "0");
		em.setProperty("leader", "true");
	}
}

function end(eim) {
    eim.disposeIfPlayerBelow(100, 610040810);
	em.setProperty("state", "0");
		em.setProperty("leader", "true");
}

function clearPQ(eim) {
    end(eim);
}

function allMonstersDead(eim) {
}

function leftParty (eim, player) {
    // If only 2 players are left, uncompletable:
	end(eim);
}
function disbandParty (eim) {
	end(eim);
}
function playerDead(eim, player) {}
function cancelSchedule() {}