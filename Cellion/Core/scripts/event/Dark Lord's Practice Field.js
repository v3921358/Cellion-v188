/**
	Dark Lord's Practice Field, 4th job Quest
**/

//var map;
//var eim;

function init() {
    em.setProperty("noEntry","false");
}

function playerEntry(eim, player) {
    var map = eim.getMapFactory().getMap(910300000);
    player.changeMap(map, map.getPortal(0));
    eim.startEventTimer(player, 600000); // 10 min
    em.setProperty("noEntry","true");
}

function scheduledTimeout(eim, player) {
    var returnmap = eim.getMapFactory().getMap(103000000);
    player.changeMap(returnmap, returnmap.getPortal(0));
}

function changedMap(eim, player, mapid) {
    if (mapid == 980040000) {
	//    eim.unregisterPlayer(player);
	em.cancel();
	em.disposeInstance(player.getName());
	eim.stopEventTimer();
    }
}

function playerExit(eim, player) {
    em.setProperty("noEntry","false");
    em.cancel();
    em.disposeInstance(player.getName());
    eim.dispose();
}

function playerDisconnected(eim, player) {
    em.setProperty("noEntry","false");
    em.cancel();
    em.disposeInstance(player.getName());
    eim.dispose();
}

function clear(eim) {
    em.setProperty("noEntry","false");
    var player = eim.getPlayers().get(0);
    player.changeMap(returnMap, returnMap.getPortal(4));
    eim.unregisterPlayer(player);
    em.cancel();
    em.disposeInstance("DollHouse");
    eim.dispose();
}

function cancelSchedule() {
}