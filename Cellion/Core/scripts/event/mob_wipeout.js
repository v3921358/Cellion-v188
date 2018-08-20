var eventmapid = 221000200;
var returnmap = 910000000;

var monster = new Array(
    4230116, // Slime King
    4230117,
    4230118,
    4230119,
    4230120,
    4230121,
    4130103,
    4230110,
    4230109,
    3230103,
    3230302,
    6220001
    );

var mobname = new Array(
    "Bernard Grey",
    "Zeta Grey",
    "Ultra Grey",
    "Mateon",
    "Plateon",
    "Mecateon",
    "Rombot",
    "King Block Golen",
    "Block Golem",
    "King Bloctopus",
    "Bloctopus",
    "Jenu"
    );

var posx = new Array(
    49,
    -107,
    228,
    -175,
    76,
    -119	
    );

var posy = new Array(
    1918,
    1497,
    1022,
    517,
    120,
    -716
    );

function init() {
// After loading, ChannelServer
}

function setup(partyid) {
    var instanceName = "wipeout" + partyid;

    var eim = em.newInstance(instanceName);
    // If there are more than 1 map for this, you'll need to do mapid + instancename
    var map = eim.createInstanceMapS(eventmapid, 910000000, null);
    map.toggleDrops();

    eim.setProperty("points", 0);
    eim.setProperty("monster_number", 0);
    
    eim.setPropertyEx("SelectedMobCleared",0);
    eim.setPropertyEx("SelectedMob",0);
    eim.setPropertyEx("SelectedCount",0);
    eim.setPropertyEx("SelectedKilled",0);
    eim.setProperty("SelectedName","");

    eim.schedule("beginQuest", 10000);
    
    return eim;
}

function beginQuest(eim) { // Custom function
    eim.setFieldAverageLevel(eim.getMapInstance(0));
    eim.broadcastPlayerMsg(6, "Commandos : Aliens invasion! Beep! beep! beep! WARNING..... ");
    eim.startEventTimer(10000); // After 5 seconds -> scheduledTimeout()
}

function playerEntry(eim, player) {
    var map = eim.getMapInstance(0);
    player.changeMap(map, map.getPortal(0));
    player.dropMessage(5, "Woah! ..? What's happening here?!?!?");
}

function changedMap(eim, player, mapid) {
    if (mapid != eventmapid) {
	eim.unregisterPlayer(player);
	eim.RemoveWarpbackList(player);

	eim.disposeIfPlayerBelow(0, 0);
    }
}

function monsterSpawn(eim) { // Custom function
    var mob = em.getMonster(monster[parseInt(eim.getProperty("monster_number"))]);

    eim.registerMonster(mob);

    var map = eim.getMapInstance(0);
    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(-364, 640));
}

function scheduledTimeout(eim) {
    eim.setPropertyEx("SelectedMob",0); // Reset
    eim.setPropertyEx("SelectedCount",0);
    eim.setPropertyEx("SelectedKilled",0);
    
    var num = parseInt(eim.getProperty("monster_number"));
    if (num < 5) {
	var map = eim.getMapInstance(0);

	if (num == 0) {
	    eim.broadcastChangeMusic(map, "Bgm23/CrimsonTower");
	    
	    for (var z = 0; z < posx.length; z++) {
		var pos = new java.awt.Point(posx[z], posy[z]);

		for (var i = 0; i < monster.length; i++) {
		    var mob = em.getMonster(monster[Math.floor(Math.random() * monster.length)]);

		    eim.registerMonster(mob);

		    map.spawnMonsterOnGroundBelow(mob, pos);
		}
	    }
	    eim.broadcastPlayerMsg(5, "Comamndos : Defeat all the monsters!");
	} else {
	    var selMob = Math.floor(Math.random() * monster.length);
	    var selMobId = monster[selMob];
	    var selMobName = mobname[selMob];
	    
	    var count = 0;

	    for (var z = 0; z < posx.length; z++) {
		var pos = new java.awt.Point(posx[z], posy[z]);

		for (var i = 0; i < monster.length; i++) {
		    var mobid = monster[Math.floor(Math.random() * monster.length)]
		    var mob = em.getMonster(mobid);
		    
		    if (selMobId == mobid) {
			count++;
		    }
		    map.spawnMonsterOnGroundBelow(mob, pos);
		}
	    }
	    eim.setPropertyEx("SelectedMob",selMobId);
	    eim.setPropertyEx("SelectedCount",count);
	    eim.setPropertyEx("SelectedKilled",0);
	    eim.setProperty("SelectedName",selMobName);
	    
	    eim.broadcastPlayerMsg(5, "Comamndos : Defeat "+count+" "+selMobName+" !!!");
	}
	eim.setProperty("monster_number", num + 1);
    } else {
	eim.BossQuestAchievement(4);
	    
	eim.broadcastshowEffect("quest/party/clear");
	eim.broadcastplaySound("Party1/Clear");
	eim.givePartyReward(3800031,2,1,0); // itemid, count, exp %, NX
	
	eim.disposeIfPlayerBelow(100, returnmap);
    }
// When event timeout..

// restartEventTimer(long time)
// stopEventTimer()
// startEventTimer(long time)
// isTimerStarted()
}

function monsterValue(eim, mobid) {
    if (mobid == eim.getPropertyEx("SelectedMob")) {
	var max = eim.getPropertyEx("SelectedCount");
	var killed = eim.getPropertyEx("SelectedKilled") + 1;

	if (killed >= max) {
	    eim.setPropertyEx("SelectedMobCleared",1);
	    allMonstersDead(eim);
	} else {
	    var name = eim.getProperty("SelectedName");
	    
	    eim.broadcastPlayerMsg(5, name + " remaining : " + (max - killed));
	}
	eim.setPropertyEx("SelectedKilled", killed);
    }
    return 0;
// Invoked when a monster that's registered has been killed
// return x amount for this player - "Saved Points"
}

function allMonstersDead(eim) {
    if (eim.getProperty("monster_number") == 1 || eim.getPropertyEx("SelectedMobCleared") == 1) {
	eim.setPropertyEx("SelectedMobCleared",0);
	
	eim.restartEventTimer(10000);

	var mobnum = parseInt(eim.getProperty("monster_number"));
	var num = mobnum * 100 / eim.getPlayerCount(); // 765 points in total
	var totalp = parseInt(eim.getProperty("points")) + num;

	eim.setProperty("points", totalp);

//	eim.broadcastMapEffect(eim.getMapInstance(0), 5120019, "Received "+num+" points for the invasion takedown!");

//	eim.saveBossQuest(num, 150001, false);

	if (mobnum < 5) {
	    switch (Math.floor(Math.random() * 5)) {
		case 0:
		    eim.broadcastPlayerMsg(6, "Commandos : Oh no! Next wave of monsters are coming in!!! Bulk up your buckets for the next fight!");
		    break;
		case 1:
		    eim.broadcastPlayerMsg(6, "Commandos : Are there anymore monsters..? everything seems safe hm..");
		    break;
		default:
		    eim.broadcastPlayerMsg(6, "Commandos : Monsters are getting more and more powerful....");
		    break;
	    }
	}
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

function changeMap_Success(eim, player, mapid) {
// Happens when player change map - Unused for now.
}

function cancelSchedule() {
}