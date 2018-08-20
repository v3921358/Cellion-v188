var eventtime = 600000;
var pvpName = "PVP_Party_2";
var basemapid = 960020100;
var pvpid = 0;
var maxPlayer = 10;
var reqPlayer = 4;
var pvpLvlmode = 2; // 0 = Lvl 30, 1 = Lvl 70, 2 = Lvl 120, 3 = Lvl 180
var pvpType = 1; // 0 = Solo, 1 = Party, 2 = Iceknight, 3 = Capture
var expectedRemainingPlayer = 3;

function init() {
}

function setup(name) {
    em.setPropertyEx("Started", 0);
    
    // Setup the instance when invoked, EG : start PQ
    var eim = em.newInstance(pvpName);
    eim.setPropertyEx("ItemDropTrack", 0);
    
    eim.setPropertyEx("reduser", 0);
    eim.setPropertyEx("blueuser", 0);
    eim.setPropertyEx("redScore", 0);
    eim.setPropertyEx("blueScore", 0);
    eim.setPropertyEx("AutoAssignRedBlueTeam", 1); // Property for team handler
    
    eim.setProperty("allow_warpback", "false");
    eim.setPropertyEx("NettPyramidBuff", 1);
    eim.setProperty("allow_warpback", "false");
    eim.setPropertyEx("mapLoadMethod", 1); // Enabling changeMap_Success
    eim.setPropertyEx("EnterAccess", 1);
    eim.setPropertyEx("type", pvpType);
    eim.setPropertyEx("ice", 0); // Ice Knight charid
    eim.setPropertyEx("lvl", pvpLvlmode);
    eim.setPropertyEx("started", 0);
    
    pvpid = basemapid + Math.floor(Math.random() * 3);
    
    var available = eim.createInstanceMapS(pvpid, 960000000, null);
    available.resetMap();
    
    return eim;
}

// eim.getPlayers().get(0).dropMessage(5, eim.getProperty("massacre_hit"));
function playerEntry(eim, player) {
    var available = eim.getMapInstance(0);
    
    if (eim.getPlayerVariableEx(player, "EventTeam") == 0) {
	player.changeMap(available, available.getPortal("RedRespawn"));
    } else {
	player.changeMap(available, available.getPortal("BlueRespawn"));
    }
    eim.broadcastTitle(player.getName() + " has entered.");
    eim.setPropertyEx("PT_Track_" + player.getId().toString(), 0)
    
    if (em.getPropertyEx("Started") == 0) {
	var NumPlayer = eim.getPlayerCount();
	eim.broadcastStaticTopMsg(1, false, "Current: "+NumPlayer+" / Needed to Start: " + reqPlayer);
	eim.broadcastStaticTopMsg(0, false, "Currently recruiting players for Battle Mode.");
    
	if (NumPlayer >= reqPlayer) { // Start
	    em.setPropertyEx("Started", 1);
	    eim.setPropertyEx("started", 1);
	    eim.schedule("ScoreTask", 5000);
	    eim.broadcastPvpMapEffect("START_COUNT");
	    
	    eim.schedule("startPvp", 6000);
	}
    } else {
	eim.PvpMode(player, pvpLvlmode);
	eim.PvpStart(player, true);
    }
    reupdatePlayerCount(eim);
}

function ScoreTask(eim) {
    var Drop = eim.getPropertyEx("ItemDropTrack");
    if (Drop == 3) { // every 30 sec, drop item.
	eim.setPropertyEx("ItemDropTrack", 1);
	eim.PVP_DropItem(pvpType);
    } else {
	eim.setPropertyEx("ItemDropTrack", Drop + 1);
    }
    eim.Revive_AllExistingPlayer(true);
    eim.updatePVPScore();
    eim.schedule("ScoreTask", 10000); // loop
}

function startPvp(eim) {
    eim.broadcastPvpMapEffect("TEAM_START");
    eim.broadcastPvpMode(pvpLvlmode);
    eim.broadcastPvpStart(true);
    eim.startEventTimer(eventtime);
}

function changedMap(eim, player, mapid) {
    // What to do when player've changed map, based on the mapid
    if (eim.getPropertyEx("started") == 1) {
	eim.PvpMode(player, pvpLvlmode);
	eim.PvpStart(player, true);
	eim.PVP_TeamScore(player);
    }
    if (mapid >= 960020100 && mapid <= 960020103) {
	return;
    }
    eim.unregisterPlayer(player);
    reupdatePlayerCount(eim);
    
    if (eim.getPropertyEx("started") == 1 && eim.getPlayerCount() <= expectedRemainingPlayer) {
	if (eim.disposeIfPlayerBelow(99, 960000000)) {
	    em.setPropertyEx("Started", 0);
	}
    }
// 926010010, 926010070 = tomb
}

function scheduledTimeout(eim) {
    eim.stopEventTimer();

    eim.cancelOtherSchedule(false);
    eim.schedule("endPvp", 10000);
    eim.setPropertyEx("EnterAccess", 0);
    eim.PVP_Solo_End();
    eim.broadcastPvpMapEffect("CHALLENGER_WIN");
}

function endPvp(eim) {
    eim.disposeIfPlayerBelow(99, 960000000);
}

function allMonstersDead(eim) {
// When invoking unregisterMonster(MapleMonster mob) OR killed
// Happens only when size = 0
}

function playerDead(eim, player) {
}

function playerRevive(eim, player) {
    if (eim.getPropertyEx("started") == 1) {
	eim.PvpMode(player, pvpLvlmode);
	eim.PvpStart(player, true);
    }
}

function playerDisconnected(eim, player) {
    // return 0 - Deregister player normally + Dispose instance if there are zero player left
    // return x that is > 0 - Deregister player normally + Dispose instance if there x player or below
    // return x that is < 0 - Deregister player normally + Dispose instance if there x player or below, if it's leader = boot all
    eim.unregisterPlayer(player);
    reupdatePlayerCount(eim);
    
    if (eim.getPlayerCount() <= expectedRemainingPlayer) {
	if (eim.disposeIfPlayerBelow(99, 960000000)) {
	    eim.cancelOtherSchedule(false);
	    em.setPropertyEx("Started", 0);
	}
    }
    return 0;
}

function monsterValue(eim, mobid, chr) {
    // Invoked when a monster that's registered has been killed
    // return x amount for this player - "Saved Points"
    return 0;
}

function leftParty(eim, player) {
}

function disbandParty(eim, player) {
}

function clearPQ(eim) {
    // Happens when the function EventInstanceManager.finishPQ() is invoked by NPC/Reactor script
    //  eim.unregisterPlayer(player);
    reupdatePlayerCount(eim);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	eim.cancelOtherSchedule(false);
	em.setPropertyEx("Started", 0);
    }
}

function removePlayer(eim, player) {
    // Happens when the funtion NPCConversationalManager.removePlayerFromInstance() is invoked
    eim.unregisterPlayer(player);

    if (eim.getPlayerCount() <= expectedRemainingPlayer) {
	if (eim.disposeIfPlayerBelow(0, 0)) {
	    eim.cancelOtherSchedule(false);
	    em.setPropertyEx("Started", 0);
	}
    }
}

function registerCarnivalParty(eim, carnivalparty) {
// Happens when carnival PQ is started. - Unused for now.
}

function changeMap_Success(eim, player, mapid) {
    // Happens when player change map - Unused for now.
    if (eim.getPropertyEx("started") == 1) {
	eim.PvpMode(player, pvpLvlmode);
	eim.PvpStart(player, true);
	eim.PVP_TeamScore(player);
    }
}

function cancelSchedule() {
}

function OnHitMiss(eim, chr) {
}

function reupdatePlayerCount(eim) {
    if (eim.getPlayerCount() < maxPlayer) {
	eim.setPropertyEx("EnterAccess", 1);
    } else {
	eim.setPropertyEx("EnterAccess", 0);
    }
}