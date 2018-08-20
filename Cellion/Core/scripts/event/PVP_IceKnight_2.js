var eventtime = 420000;
var pvpName = "PVP_IceKnight_2";
var pvpid = 960030100;
var maxPlayer = 10;
var reqPlayer = 6;
var pvpLvlmode = 2;
var pvpType = 2; // 0 = Solo, 1 = Party, 2 = Iceknight, 3 = Capture
var expectedRemainingPlayer = 4; // 7

function init() {
}

function setup(name) {
    em.setPropertyEx("Started", 0);
    
    // Setup the instance when invoked, EG : start PQ
    var eim = em.newInstance(pvpName);
    eim.setPropertyEx("ItemDropTrack", 0);
    
    eim.setProperty("allow_warpback", "false");
    eim.setPropertyEx("NettPyramidBuff", 1);
    eim.setProperty("allow_warpback", "false");
    eim.setPropertyEx("mapLoadMethod", 1); // Enabling changeMap_Success
    eim.setPropertyEx("EnterAccess", 1);
    eim.setPropertyEx("type", pvpType); // Ice knight event type
    eim.setPropertyEx("icegage", 0); // ice knight's gauge
    eim.setPropertyEx("ice", 0); // Ice Knight charid
    eim.setPropertyEx("lvl", pvpLvlmode);
    eim.setPropertyEx("started", 0);
    
    var available = eim.createInstanceMapS(pvpid, 960000000, null);
    available.resetMap();
    
    return eim;
}

// eim.getPlayers().get(0).dropMessage(5, eim.getProperty("massacre_hit"));
function playerEntry(eim, player) {
    var available = eim.getMapInstance(0);

    player.changeMap(available, available.getPortal(0));
    eim.broadcastTitle(player.getName() + " has entered.");
    eim.setPropertyEx("PT_Track_" + player.getId().toString(), 0)
    
    if (em.getPropertyEx("Started") == 0) {
	var NumPlayer = eim.getPlayerCount();
	eim.broadcastStaticTopMsg(1, true, "Current: "+NumPlayer+" / Needed to Start: " + reqPlayer);
	eim.broadcastStaticTopMsg(0, true, "Currently recruiting players for Battle Mode.");
    
	if (NumPlayer >= reqPlayer) { // Start
	    em.setPropertyEx("Started", 1);
	    eim.setPropertyEx("started", 1);
	    eim.schedule("ScoreTask", 5000);
	    eim.broadcastPvpMapEffect("START_COUNT");
	    
	    eim.schedule("startPvp", 10000);
	}
    } else {
	eim.PvpMode(player, pvpLvlmode);
	eim.PvpStart(player, true);
    }
    reupdatePlayerCount(eim);
}

function ScoreTask(eim) {
    var Drop = eim.getPropertyEx("ItemDropTrack");
    if (Drop == 2) { // every 20 sec, drop item.
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
    eim.broadcastPvpMapEffect("ICE_START");
    eim.PVP_IceKnight();
    eim.broadcastPvpMode(pvpLvlmode);
    eim.broadcastPvpStart(true);
    eim.startEventTimer(eventtime);
    eim.broadcastStaticTopMsg(0, false, "");
}

function changedMap(eim, player, mapid) {
    // What to do when player've changed map, based on the mapid
    if (eim.getPropertyEx("started") == 1) {
	eim.PvpMode(player, pvpLvlmode);
	eim.PvpStart(player, true);
    }
    if (mapid == pvpid) {
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
    eim.broadcastPvpMapEffect("ICE_KNIGHT_WIN");
}

function endPvp(eim) {
    eim.disposeIfPlayerBelow(99, 960000000);
}

function allMonstersDead(eim) {
// When invoking unregisterMonster(MapleMonster mob) OR killed
// Happens only when size = 0
}

function playerDead(eim, player) {
    if (player.getId() == eim.getPropertyEx("ice")) { // Ice knight
	eim.stopEventTimer();

	eim.cancelOtherSchedule(false);
	eim.schedule("endPvp", 10000);
	eim.setPropertyEx("EnterAccess", 0);
	eim.PVP_Solo_End();
	eim.broadcastPvpMapEffect("CHALLENGER_WIN");
    }
}

function playerRevive(eim, player) {
    if (eim.getPropertyEx("started") == 1) {
	eim.PvpMode(player, pvpLvlmode);
	eim.PvpStart(player, true);
    }
}

function playerDisconnected(eim, player) {
    eim.unregisterPlayer(player);
    reupdatePlayerCount(eim);
    
    if (eim.getPlayerCount() <= expectedRemainingPlayer) {
	if (eim.disposeIfPlayerBelow(99, 960000000)) {
	    eim.cancelOtherSchedule(false);
	    em.setPropertyEx("Started", 0);
	}
    } else {
	if (player.getId() == eim.getPropertyEx("ice")) { // Ice knight
	    eim.stopEventTimer();

	    eim.cancelOtherSchedule(false);
	    eim.schedule("endPvp", 10000);
	    eim.setPropertyEx("EnterAccess", 0);
	    eim.PVP_Solo_End();
	    eim.broadcastPvpMapEffect("CHALLENGER_WIN");
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
	eim.PvpIceKnightTeam(player);
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