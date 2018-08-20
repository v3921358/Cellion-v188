var mapId = 940021000;

function init() {
    em.setProperty("state", "0");
//    em.setProperty("leader", "true");
}

function setup(level, leaderid) {
    em.setProperty("state", "1");
//    em.setProperty("leader", "true");
    var eim = em.newInstance("DimensionInvasion" + leaderid);
    eim.setInstanceMap(940021000);
    eim.setInstanceMap(940022000);
    eim.setInstanceMap(940023000);
    eim.setInstanceMap(940024000);
    var map = eim.setInstanceMap(mapId);
    map.resetFully(true);
//    var level = em.startInstance(chr);
    map.setSpawns(false);
//    map.resetSpawnLevel(level);
    em.stage1DI(eim, map, level);
    em.registerMobOnMap(mapId);
    em.registerMobOnMap(940022000);

    

    eim.startEventTimer(3600000); //60 mins
//    eim.setProperty("entryTimestamp", "" + java.lang.System.currentTimeMillis());
    
    return eim;
}

function playerEntry(eim, player) {
    var map = eim.getMapInstance(0);
    player.changeMap(map, map.getPortal(0));
    em.environmentChange(true, mapId, "killing/first/start");
    em.mobOnMap(mapId, 50, 0);
    //player.tryPartyQuest(1214);
}

function playerRevive(eim, player) {
    eim.unregisterPlayer(player);
    if (eim.disposeIfPlayerBelow(0, 0)) {
        em.setProperty("state", "0");
        em.setProperty("leader", "true");
    }
    return true;
}

function scheduledTimeout(eim) {
        end(eim);
}

function changedMap(eim, player, mapid) {
    switch (mapid) {
    case 940021000: // 1st Stage kroko
    case 940022000: // 2nd Stage ropes
    case 940023000: // 3rd Stage cats
    case 940024000: // 4th Stage kill evileye
        return; 
    }
    eim.unregisterPlayer(player);

//    if (eim.disposeIfPlayerBelow(2, 910340000)) {
//    em.setProperty("state", "0");
//    }
}

//function changedMap(eim, player, mapid) {
//    if (mapid != mapId && mapid != 940024000 && mapid != 940023000 && mapid != 940022000) {
//        eim.unregisterPlayer(player);
//        em.setProperty("state", "0");
//        em.setProperty("leader", "true");
//    }
//}

function playerDisconnected(eim, player) {
    end(eim);
    return -2;
}


function monsterValue(eim, mobId) {
    em.mobOnMap(mapId, 50, eim.getMapInstance(mapId).getAllMonstersThreadsafe().size());
    if(mobId == 9300622) {
        if(eim.getMapInstance(mapId).getAllMonstersThreadsafe().size() == 0){
            em.broadcastSessionValue(eim, mapId, "2400000", false, 0);
            var map = eim.getMapInstance(0);
            em.stage3DI(eim, map, 0)
        }
    } if(mobId == 9300634) {
        if(eim.getMapInstance(mapId).getAllMonstersThreadsafe().size() == 0) {
            em.broadcastSessionValue(eim, mapId, "6000000", true, 2431127);
            var map = eim.getMapInstance(0);
            em.stage6DI(eim, map, 0)//bonus
        }
    }
    if(mobId == 9300627) {
        if(eim.getMapInstance(mapId).getAllMonstersThreadsafe().size() == 0) {
            em.broadcastSessionValue(eim, mapId, "7200000", true, 2431128);
        }
    }
    if(eim.getMapInstance(mapId).getAllMonstersThreadsafe().size() > 50){
        em.environmentChange(true, mapId, "hillah/fail");
        eim.startEventTimer(3000);
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
    eim.disposeIfPlayerBelow(100, 940020000);
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
/*
var eventmapid = 940021000;
var returnmap = 940020000;

monster = new Array(
    9300618, // 3
    9300619, // 30
    9400734, // Coco
    9300028, // Ergoth/Eregos
    6090002, // Bamboo Warrior
    9800058, // Poison Golem
    9800063, // Tae Roon
    9800066, // Giant Centipede
    //9300479, // Master Hoblin
    //9300480, // Master Harp
    //9300481, // Master Birk
    8220001, // Snowman
    8220011, // Aufheben
    8220012 // Oberon
    );
    

function init() {
// After loading, ChannelServer
}

function setup(partyid) {
    var instanceName = "DimensionInvasion" + partyid;

    var eim = em.newInstance(instanceName);
    // If there are more than 1 map for this, you'll need to do mapid + instancename
    var map = eim.createInstanceMapS(eventmapid);
    map.toggleDrops();

    eim.setProperty("monster_number", 0);

    beginQuest(eim);
    return eim;
}

function beginQuest(eim) { // Custom function
    if (eim != null) {
        eim.startEventTimer(5000); // After 5 seconds -> scheduledTimeout()
    }
}

function monsterSpawn(eim) { // Custom function
    var mob = em.getMonster(monster[parseInt(eim.getProperty("monster_number"))]);

    eim.registerMonster(mob);

    var map = eim.getMapInstance(0);
    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(2574, 29));
}

function playerEntry(eim, player) {
    var map = eim.getMapInstance(0);
    player.changeMap(map, map.getPortal(0));
}

function changedMap(eim, player, mapid) {
    if (mapid != eventmapid) {
        eim.unregisterPlayer(player);

        eim.disposeIfPlayerBelow(0, 0);
    }
}

function scheduledTimeout(eim) {
    var num = parseInt(eim.getProperty("monster_number"));
    if (num < monster.length) {
        monsterSpawn(eim);
        eim.setProperty("monster_number", num + 1);
    } else {
        eim.disposeIfPlayerBelow(100, returnmap);
    }
// When event timeout..

// restartEventTimer(long time)
// stopEventTimer()
// startEventTimer(long time)
// isTimerStarted()
}

function allMonstersDead(eim) {
    eim.restartEventTimer(3000);

    var mobnum = parseInt(eim.getProperty("monster_number"));

    if (mobnum < monster.length) {
        eim.broadcastPlayerMsg(6, "Prepare! The next boss will appear in a glimpse of an eye!");
    } else {
        eim.broadcastPlayerMsg(5, "Your team've beaten the EASY mode and have gained an extra 1000 points!");
    }
// When invoking unregisterMonster(MapleMonster mob) OR killed
// Happens only when size = 0
}

function playerDead(eim, player) {
// Happens when player dies
}

function playerRevive(eim, player) {
    return true;
// Happens when player's revived.
// @Param : returns true/false
}

function playerDisconnected(eim, player) {
    return 0;
// return 0 - Deregister player normally + Dispose instance if there are zero player left
// return x that is > 0 - Deregister player normally + Dispose instance if there x player or below
// return x that is < 0 - Deregister player normally + Dispose instance if there x player or below, if it's leader = boot all
}

function monsterValue(eim, mobid) {
    return 0;
// Invoked when a monster that's registered has been killed
// return x amount for this player - "Saved Points"
}

function leftParty(eim, player) {
    // Happens when a player left the party
    eim.unregisterPlayer(player);

    var map = em.getMapFactory().getMap(returnmap);
    player.changeMap(map, map.getPortal(0));

    eim.disposeIfPlayerBelow(0, 0);
}

function disbandParty(eim, player) {
    // Boot whole party and end
    eim.disposeIfPlayerBelow(100, returnmap);
}

function clearPQ(eim) {
// Happens when the function EventInstanceManager.finishPQ() is invoked by NPC/Reactor script
}

function removePlayer(eim, player) {
    eim.dispose();
// Happens when the funtion NPCConversationalManager.removePlayerFromInstance() is invoked
}

function registerCarnivalParty(eim, carnivalparty) {
// Happens when carnival PQ is started. - Unused for now.
}

function onMapLoad(eim, player) {
// Happens when player change map - Unused for now.
}

function cancelSchedule() {
}*/