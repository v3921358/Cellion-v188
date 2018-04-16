/*
 * Kerning Square VIP Zone.
 */

var mapid_KerningSquare_7thFloor8thFloorAreaA = 103040400;

function init() {
    for (var i = 0; i < 10; i++) { // 0 -> 9
        em.setPropertyEx("VIPZoneMap_" + i, 0); // set this zone empty or not
    }
}

function setup() {
    var eim = em.newInstance("KerningSquareVIPZone_" + (Math.random() * 100000));
    eim.setPropertyEx("VIPZoneSelected", -1);

    var foundAvailableMap = false;

    for (var i = 0; i < 10; i++) { // 0 -> 9
        if (em.getPropertyEx("VIPZoneMap_" + i) == 0) { // check if this zone is used or not
            em.setPropertyEx("VIPZoneMap_" + i, 1); // set this zone empty or not
            eim.setPropertyEx("VIPZoneSelected", i);

            eim.startEventTimer(1000 * 60 * 50); // 50 minutes

            // Respawn boss
            var map = eim.getMapFactory().getMap(103040460 + i);
            map.resetMap();
             map.respawn(true);

            foundAvailableMap = true;
            break;
        }
    }
    if (!foundAvailableMap) {
        eim.disposeIfPlayerBelow(100, mapid_KerningSquare_7thFloor8thFloorAreaA);
    }
    return eim;
}

function playerEntry(eim, player) {
    var VIPZoneSelected = eim.getPropertyEx("VIPZoneSelected");
    if (VIPZoneSelected != -1) {
        var map = eim.getMapFactory().getMap(103040440 + VIPZoneSelected);
        player.changeMap(map, map.getPortal(1));
    }
}

function playerDead(eim, player) {
}

function playerRevive(eim, player) {
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, mapid_KerningSquare_7thFloor8thFloorAreaA);
    em.setPropertyEx("VIPZoneMap_" + eim.getPropertyEx("VIPZoneSelected"), 0); // set this zone empty or not
}

function changedMap(eim, player, mapid) {
    if (mapid < 103040440 || mapid > 103040469) {
        eim.unregisterPlayer(player);

        if (eim.disposeIfPlayerBelow(0, 0)) {
            // set state
            em.setPropertyEx("VIPZoneMap_" + eim.getPropertyEx("VIPZoneSelected"), 0); // set this zone empty or not
        }
    }
}

function playerDisconnected(eim, player) {
    return 0;
}

function leftParty(eim, player) {
    // If only 2 players are left, uncompletable:
    playerExit(eim, player);
}

function disbandParty(eim) {
    eim.disposeIfPlayerBelow(100, mapid_KerningSquare_7thFloor8thFloorAreaA);
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    var map = eim.getMapFactory().getMap(mapid_KerningSquare_7thFloor8thFloorAreaA);
    player.changeMap(map, map.getPortal(0));

    em.setPropertyEx("VIPZoneMap_" + eim.getPropertyEx("VIPZoneSelected"), 0); // set this zone empty or not
}

function clearPQ(eim) {
    eim.disposeIfPlayerBelow(100, mapid_KerningSquare_7thFloor8thFloorAreaA);

    em.setPropertyEx("VIPZoneMap_" + eim.getPropertyEx("VIPZoneSelected"), 0); // set this zone empty or not
}

function allMonstersDead(eim) {
//has nothing to do with monster killing
}

function monsterValue(eim, mobId) {
    return 1;
}

function cancelSchedule() {
}