
function init() {
    cakes = 11;
    em.setProperty("state", "0");
    em.setProperty("leader", "true");
}

function setup(level, leaderid) {
    em.setProperty("state", "1");
    em.setProperty("leader", "true");
    var eim = em.newInstance("RootPierre" + leaderid);
    em.setProperty("stage", "0");
    var map = eim.setInstanceMap(105200200);
    var map1 = eim.setInstanceMap(105200210);
    map.resetFully(true);
    map.setSpawns(true);
    eim.deathCount(5);
    //map.resetSpawnLevel(level);
    eim.startEventTimer(900000); //15 mins
    return eim;
}

function playerEntry(eim, player) {
    var map = eim.getMapInstance(0);
    player.changeMap(map, map.getPortal(1));
    player.setDeathCount(5);
    player.updateDeathCount();
    eim.deathCount(5);
}

function playerRevive(eim, player) {
    if(player.getDeathCount() <= 0) {
        end(eim);
    } else {
        player.updateDeathCount();
    }
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, 105200000);
    em.setProperty("state", "0");
    em.setProperty("leader", "true");
    //end(eim);
}

function changedMap(eim, player, mapid) {
    if (mapid != 105200200 && mapid != 105200210) {
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
    if (mobId == 8900100) {//TODO
//        eim.broadcastPlayerMsg(5, "lel.");
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
    eim.disposeIfPlayerBelow(100, 105200000);
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