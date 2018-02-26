
var exitMap = 105200000;
var startMap = 105200200;
var bossMap = 105200210;
var bossId = 8900100;//8900101 and 8900102 as well I think //TODO
var eventName = "Root1";
var deathCount = 5;
var time = 900000; //15 mins

function init() {
    cakes = 11;
    em.setProperty("state", "0");
    em.setProperty("leader", "true");
}

function setup(level, leaderid) {
    em.setProperty("state", "1");
    em.setProperty("leader", "true");
    var eim = em.newInstance(eventName + leaderid);
    em.setProperty("stage", "0");
    var map = eim.setInstanceMap(startMap);
    map.resetFully(true);
    map.setSpawns(true);
    eim.deathCount(deathCount);
    //map.resetSpawnLevel(level);
    eim.startEventTimer(time);
    return eim;
}

function playerEntry(eim, player) {
    var map = eim.getMapInstance(0);
    player.changeMap(map, map.getPortal(1));
    player.setDeathCount(deathCount);
    player.updateDeathCount();
    eim.deathCount(deathCount);
}

function playerRevive(eim, player) {
    if(player.getDeathCount() <= 0) {
        end(eim);
    } else {
        player.updateDeathCount();
    }
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, exitMap);
    em.setProperty("state", "0");
    em.setProperty("leader", "true");
    //end(eim);
}

function changedMap(eim, player, mapid) {
    if (mapid != startMap && mapid != bossMap) {
        eim.unregisterPlayer(player);

        if (eim.disposeIfPlayerBelow(0, 0)) {
            em.setProperty("state", "0");
            em.setProperty("leader", "true");
        }
    }
    player.updateDeathCount();
}

function playerDisconnected(eim, player) {
    return 0;
}

function monsterValue(eim, mobId) {
    if (mobId == bossId) {//TODO
        eim.broadcastPlayerMsg(5, "You have killed the boss!");
//        end(eim);
    }
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
    eim.disposeIfPlayerBelow(100, exitMap);
    em.setProperty("state", "0");
    em.setProperty("leader", "true");
}

function clearPQ(eim) {
    end(eim);
}

function allMonstersDead(eim) {
}

function leftParty(eim, player) {
    end(eim);
}
function disbandParty(eim) {
    end(eim);
}
function playerDead(eim, player) {
}
function cancelSchedule() {
}