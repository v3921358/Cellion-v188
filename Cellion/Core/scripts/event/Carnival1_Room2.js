function init() {
    em.setProperty("state", "0");
}

function setup(eim, leaderid) {
    em.setProperty("state", "1");

    var eim = em.newInstance("Carnival1_Room2");

    var fac = em.getMapFactory();
    var map = fac.getMap(980000200); // Start Map
    map.resetMap();
    map = fac.getMap(980000201); // Main
    map.resetMap();
    map = fac.getMap(980000202); // Revive
    map.resetMap();
    // 980000103 = Victory
    // 980000103 = Lose
    // 
    eim.startEventTimer(180000); // 3 minute waiting time
    
    eim.setProperty("allow_warpback", "false");
    eim.setPropertyEx("mapLoadMethod", 1); // Enabling changeMap_Success
    eim.setPropertyEx("MainMap",980000201);
    
    eim.setPropertyEx("SMob_0", 0);
    eim.setPropertyEx("SMob_1", 0);
    eim.setPropertyEx("CP_Red", 0);
    eim.setPropertyEx("CP_Blue", 0);
    eim.setPropertyEx("TotalCP_Red", 0);
    eim.setPropertyEx("TotalCP_Blue", 0);
    eim.setPropertyEx("EnterState", 0); // 0 = Waiting, 1 = Found
    eim.setPropertyEx("AverageLevel", 0);

    return eim;
}

function playerEntry(eim, player) {
    var map = em.getMapFactory().getMap(980000200);

    player.changeMap(map, map.getPortal(0));
    
    // TODO checks for this msg later on.
    player.dropMessage(6,"You will receive invitations from other parties for the next 3 minutes.");
    
    var state = eim.getPropertyEx("EnterState");
    
    if (state == 0) { // Start
	eim.setPlayerVariableEx(player, "MONSTER_CARNIVAL_TEAM", 0);
	eim.sent_CarnivalStart(player,0,eim.getPropertyEx("CP_Red"),eim.getPropertyEx("TotalCP_Red"),eim.getPropertyEx("CP_Blue"),eim.getPropertyEx("TotalCP_Blue"),0,0);
    } else if (state == 1) {
	eim.setPlayerVariableEx(player, "MONSTER_CARNIVAL_TEAM", 1);
	eim.sent_CarnivalStart(player,1,eim.getPropertyEx("CP_Red"),eim.getPropertyEx("TotalCP_Red"),eim.getPropertyEx("CP_Blue"),eim.getPropertyEx("TotalCP_Blue"),0,0);
    } else if (state == 2) { // DEV MODE
	eim.setPlayerVariableEx(player, "MONSTER_CARNIVAL_TEAM", 1);
	eim.sent_CarnivalStart(player,1,eim.getPropertyEx("CP_Red"),eim.getPropertyEx("TotalCP_Red"),eim.getPropertyEx("CP_Blue"),eim.getPropertyEx("TotalCP_Blue"),0,0);
    } else { // How can the player ever get back anyway?
	eim.unregisterPlayer(player);

	if (eim.disposeIfPlayerBelow(0, 0)) {
	    em.setProperty("state", "0");
	}
	return;
    }
    eim.setPlayerVariableEx(player, "MONSTER_CARNIVAL_POINTS", 0);
    eim.setPlayerVariableEx(player, "MONSTER_CARNIVAL_TPOINTS", 0);
    
    if (eim.getProperty("LeaderName") == null) {
	eim.setProperty("LeaderName", player.getName());
    }
    eim.setPropertyEx("AverageLevel", eim.getPropertyEx("AverageLevel") + player.getLevel());
}

function playerRevive(eim, player) {
    return false;
}

function scheduledTimeout(eim) {
    switch (eim.getPropertyEx("EnterState")) { // Set one after player enter.
	case 0:
	case 1:
	    eim.disposeIfPlayerBelow(100, 980000000);
	    em.setProperty("state", "0");
	    break;
	case 2:
	    eim.broadcastPlayerMsg(6,"The Monster Carnival will begin in 10 seconds!");
	    eim.setPropertyEx("EnterState", 3);
	    eim.restartEventTimer(10000);
	    break;
	case 3:
	    eim.setPropertyEx("EnterState", 4);
	    eim.warpAllPlayer(980000201);
	    eim.restartEventTimer(600000);
	    break;
	case 4:
	    eim.carnival_Complete();
	    break;
	case 5:
	    eim.carnival_Out();
	    break;
    }
}

function changeMap_Success(eim,player,mapid) {
    switch (mapid) {
	case 980000201:
	    var t = eim.getPlayerVariableEx(player, "MONSTER_CARNIVAL_TEAM");
	    if (t != null) {
		eim.sent_CarnivalStart(player,t,eim.getPropertyEx("CP_Red"),eim.getPropertyEx("TotalCP_Red"),eim.getPropertyEx("CP_Blue"),eim.getPropertyEx("TotalCP_Blue"),0,0);
	    }
	    return;
    }
}

function changedMap(eim, player, mapid) {
    switch (mapid) {
	case 980000201:
	case 980000200:
	case 980000202:
	case 980000203:
	case 980000204:
	    return;
    }
    eim.unregisterPlayer(player);
    eim.RemoveWarpbackList(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	em.setProperty("state", "0");
    }
}

function playerDisconnected(eim, player) {
    return 0;
}

function monsterValue(eim, mobId, chr) {
    eim.modifyCarnivalPoint(chr,5,true);
    return 1;
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	em.setProperty("state", "0");
    }
}

function end(eim) {
    eim.disposeIfPlayerBelow(100, 980000000);

    em.setProperty("state", "0");
}

function clearPQ(eim) {
    end(eim);
}

function disbandParty (eim) {
    end(eim);
}

function leftParty (eim, player) {
    end(eim);
}

function allMonstersDead(eim) {}
function removePlayer(eim, player) {}
function playerDead(eim, player) {}
function cancelSchedule() {}