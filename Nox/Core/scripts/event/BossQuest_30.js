var eventmapid = new Array(551030200,802000823,270050100,220080001,240060200);
var posX = new Array(-364,174,-182,-406,429);
var posY = new Array(640,335,-42,-386,260);
var returnmap = 910000000;

var monster = new Array(
    9400289, // Auf Haven
    9400409, // Emperor Toad
    8800002, // Zakum 3
    8300007, // Dragon
    8820001 // Pink bean
    );

function init() {
// After loading, ChannelServer
}

function setup(partyid) {
    var instanceName = "BossQuest_30" + partyid;

    var eim = em.newInstance(instanceName);
    // If there are more than 1 map for this, you'll need to do mapid + instancename
    var rand = Math.floor(Math.random() * eventmapid.length);
    eim.setPropertyEx("rand", rand)
    
    var map = eim.createInstanceMapS(eventmapid[rand], returnmap, null);
    map.toggleDrops();
    map.spawnNpc(9001000, new java.awt.Point(posX[rand],posY[rand]), true);

    eim.setProperty("points", 0);
    eim.setPropertyEx("AbleToGainPT", 0);
    eim.setProperty("monster_number", 0);
    eim.setPropertyEx("diecounter", 0);
    
    eim.setPropertyEx("FirstTimer", 0);
    
    eim.startEventTimer(30000);
    return eim;
}

function beginQuest(eim) { // Custom function
    eim.broadcastPlayerMsg(6, "Prepare!!!");
    eim.startEventTimer(5000); // After 5 seconds -> scheduledTimeout()
}

function monsterSpawn(eim) { // Custom function
    var monsterid = monster[parseInt(eim.getProperty("monster_number"))];
    var mob = em.getMonster(monsterid);
    var setBgm = null;

    switch (monsterid) {
	case 8820001: // Pink Bean
	    var modified = em.newMonsterStats(monsterid);
	    modified.setOExp(mob.getMobExp() / 15);
	    modified.setOHp(750000000);
	    modified.setOMp(1000000);

	    mob.setOverrideStats(modified);
	    
	    setBgm = "Bgm16/FightingPinkBeen";
	    break;
	case 8300007: // Dragon
	    var modified = em.newMonsterStats(monsterid);
	    modified.setOExp(mob.getMobExp() * 20);
	    modified.setOHp(mob.getMobMaxHp() * 2.5);
	    modified.setOMp(3000000);

	    mob.setOverrideStats(modified);
	    
	    setBgm = "Bgm17/MureungSchool4";
	    break;
	case 8800002: // Zakum 3
	    var modified = em.newMonsterStats(monsterid);
	    modified.setOExp(mob.getMobExp() * 0.4);
	    modified.setOHp(mob.getMobMaxHp() * 7);
	    modified.setOMp(5000000);

	    mob.setOverrideStats(modified);
	    
	    setBgm = "Bgm06/FinalFight";
	    break;
	case 9400289: // Auf haven [ Neo Tokyo ]
	    var modified = em.newMonsterStats(monsterid);
	    modified.setOExp(mob.getMobExp() / 10);
	    modified.setOHp(mob.getMobMaxHp() / 2);
	    modified.setOMp(5000000);

	    mob.setOverrideStats(modified);
	    
	    setBgm = "BgmJp2/Rockbongi2";
	    break;
	case 9400409: // Emperor Toad
	    var modified = em.newMonsterStats(monsterid);
	    modified.setOExp(mob.getMobExp() / 10);
	    modified.setOHp(1000000000);
	    modified.setOMp(1000000);

	    mob.setOverrideStats(modified);
	    
	    setBgm = "BgmJp/CastleBoss";
	    break;
    }
    if (setBgm != null) {
	eim.broadcastChangeMusic(eim.getMapInstance(0), setBgm);
    }
    eim.registerMonster(mob);

    var map = eim.getMapInstance(0);
    var rand = eim.getPropertyEx("rand");
    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(posX[rand], posY[rand]));
}

function playerEntry(eim, player) {
    var map = eim.getMapInstance(0);
    player.changeMap(map, map.getPortal(0));
}

function changedMap(eim, player, mapid) {
    switch (mapid) {
	case 551030200:
	case 802000823:
	case 802000611:
	case 270050100:
	case 220080001:
	case 240060200:
	    return;
    }
    eim.unregisterPlayer(player);
    eim.RemoveWarpbackList(player);

    eim.disposeIfPlayerBelow(0, 0);
}

function scheduledTimeout(eim) {
    if (eim.getPropertyEx("FirstTimer") == 0) {
	eim.setPropertyEx("FirstTimer", 1);
	
	beginQuest(eim);
    } else {
	var num = parseInt(eim.getProperty("monster_number"));
	if (num < monster.length) {
	    if (eim.getPlayerCount() < 8) {
		eim.broadcastPlayerMsg(6, "This battle may not continue with 8 or less player.");
		eim.disposeIfPlayerBelow(100, returnmap);
		return;
	    }
	    eim.setPropertyEx("AbleToGainPT", 1);
	    monsterSpawn(eim);
	    eim.setProperty("monster_number", num + 1);
	} else {
	    eim.disposeIfPlayerBelow(100, returnmap);
	}
    }
// When event timeout..

// restartEventTimer(long time)
// stopEventTimer()
// startEventTimer(long time)
// isTimerStarted()
}

function allMonstersDead(eim) {
    eim.restartEventTimer(15000);

    var mobnum = parseInt(eim.getProperty("monster_number"));

    if (mobnum < monster.length) {
	eim.broadcastPlayerMsg(6, "Prepare for the next battle!");
    }
    if (mobnum <= monster.length && eim.getPropertyEx("AbleToGainPT") == 1) {
	eim.setPropertyEx("AbleToGainPT", 0);
	    
	if (mobnum == monster.length) {
	    eim.BossQuestAchievement(6);
	    eim.saveBossQuest(5000, 150001, true);	
	    eim.broadcastPlayerMsg(5, "Gained 5000 boss PQ points.");
	    eim.saveBossQuest(3, 150008, true);
	    eim.broadcastPlayerMsg(5, "Gained 3 special Boss PQ point!");
	}
    }
}

function playerDead(eim, player) {
    var counter = eim.getPropertyEx("diecounter");
    if (counter < 3) {
	eim.broadcastPlayerMsg(5, player.getName() + " has died, 3 or more causality will result in the mission failing.");
	eim.setPropertyEx("diecounter", counter+1);
    } else {
	eim.broadcastPlayerMsg(5, "Mission has failed as a result of players' death.");
	eim.disposeIfPlayerBelow(100, returnmap);
    }
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

function changeMap_Success(eim, player, mapid) {
// Happens when player change map - Unused for now.
}

function cancelSchedule() {
}