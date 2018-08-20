/**
	Biscuit House - Witch Night Out
**/

function init() {
}

function setup(eim, leaderid) {
    var map = em.findAvailableMapFrom(910031000, 910031029);
    if (map != null) {
	var eim = em.newInstance("witchnightout" + leaderid);

	eim.startEventTimer(300000);
	eim.setPropertyEx("pt", 0);
	eim.setPropertyEx("id", map.getId());
    } else {
	eim.setPropertyEx("id", -1);
	eim.disposeIfPlayerBelow(100, 91003000);
    }
    return eim;
}

function playerEntry(eim, player) {
    var id = eim.getPropertyEx("id");
    if (id != -1) {
	var map = eim.getMapFactory().getMap(id);
	player.changeMap(map, map.getPortal(0));
    }
}

function playerExit(eim, player) {
    //    eim.unregisterPlayer(player);
    var returnmap = em.getMapFactory().getMap(91003000);
    player.changeMap(returnmap, returnmap.getPortal(0));

    eim.dispose();
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, 91003000);
}

function changedMap(eim, player, mapid) {
    if (mapid >= 910031000 && mapid <= 910031029) {
	return;
    }
    eim.unregisterPlayer(player);
    eim.dispose();
}

function playerDisconnected(eim, player) {
    return 0;
}

function clear(eim) {
    eim.dispose();
}

function cancelSchedule() {
}

function playerDead() {
}

function monsterValue(eim, mobId) {
    var point = eim.getPropertyEx("pt") + 5;

    eim.incEventPoint(point,10227);
    eim.incEventPoint(point,10226);

    eim.setPropertyEx("pt", point);
    return 1;
}

function allMonstersDead(eim) {
}

function disbandParty(eim) {
    eim.disposeIfPlayerBelow(100, 91003000);
}