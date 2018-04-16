function init() {
    em.setProperty("state", "0");
}
// http://www.youtube.com/watch?v=MgSS-86gzEE
function setup(eim) {
    em.setProperty("state", "1");

    var eim = em.newInstance("Wedding");

    eim.setPropertyEx("st",0);
    eim.setPropertyEx("banner",0);
    eim.setProperty("Started", "0");

    var fact = em.getMapFactory();
    
    fact.getMap(680000200).resetMap();
    fact.getMap(680000210).resetMap();
    fact.getMap(680000300).resetMap();
    fact.getMap(680000400).resetMap();
    var lastmap = fact.getMap(680000500);
    lastmap.resetMap();
    lastmap.respawn(true);

    eim.schedule("startBroadCast", 3000);

    eim.startEventTimer(600000);

    return eim;
}

function startBroadCast(eim) {
    if (eim.getProperty("Gender").equals("true")) {
	em.broadcastWedding("Gay wedding between %s and %s is about to start in the cathedral in channel %d", eim.getProperty("name1"), eim.getProperty("name2"));
    } else {
	em.broadcastWedding("Wedding between %s and %s is about to start in the cathedral in channel %d", eim.getProperty("name1"), eim.getProperty("name2"));
    }
}

// warpAllPlayer
function playerEntry(eim, player) {
    var map = em.getMapFactory().getMap(680000200);
    player.changeMap(map, map.getPortal(0));
}

function playerRevive(eim, player) {
    return false;
}

function StartBanner(eim) {
    var msg = ["Today, we have gathered here to show our deepest blessings to the young couple.",
    "People say that the one tied with your destiny is connected by the Red Ribbon of a Ribbon Pig.",
    "From what I can see these two people have finally found each other at the end of the red ribbon.",
    "Among all the Maplers, you two are definitely the fortunate ones to find each other.",
    "Now, the quest is given to you two and that is to live happily ever after as beloved ones.",
    "Groom, Will you love your bride until your hair turns white as the yeti's fur?",
    "Bride, will you love your groom until the snow from El Nath mountain melts and turns into a sand in Florina Beach?",
    "Today, all the attended guests will be the witness of this couple's oath.",
    "I shall now declare this couple as husband and wife in the blessing of everyone gathered here.",
    "You may kiss the Bride now!"];
    var id = eim.getPropertyEx("banner");
    if (id == 999) {
	eim.startEventTimer(10000);
    } else {
	if (id >= msg.length) {
	    eim.schedule("StartBanner", 20000);
	    eim.setCoupleMarried();
	    eim.setPropertyEx("banner", 999);
	} else {
	    eim.broadcastMapEffect(em.getMapFactory().getMap(680000210),5120025,msg[id])
	    eim.schedule("StartBanner", 10000);
	    eim.setPropertyEx("banner", id+1);
	}
    }
}

function scheduledTimeout(eim) {
    var st = eim.getPropertyEx("st");
    if (st == 0) {
	eim.warpAllPlayer(680000210);
	eim.startEventTimer(60000);
	eim.setProperty("Started", "1");
    } else if (st == 1) {
	eim.schedule("StartBanner", 2000);
    } else if (st == 2) {
	eim.warpAllPlayer(680000300);
	eim.startEventTimer(120000);

	eim.broadcastPlayerMsg(6, "Wedding Cake Studio! Cut the cakes and take a nice screenshot of everyone!");
    } else if (st == 3) {
	eim.warpAllPlayer(680000400);
	eim.startEventTimer(600000); // 5 min
    } else {
	eim.disposeIfPlayerBelow(100, 680000500);
	em.setProperty("state", "0");
    }
    eim.setPropertyEx("st", st+1);
}

function changedMap(eim, player, mapid) {
    switch (mapid) {
	case 680000200: // Dressing room, delay 15 min
	case 680000210: // Wedding Hall
	case 680000300: // Wedding Ceremony area
	case 680000400: // Wedding Park
	  case 680000401: // Wedding Park  //
	    // 500 = exit
	    return;
    }
    if (player.getName().equals(eim.getProperty("name1")) || player.getName().equals(eim.getProperty("name2"))) {
	eim.broadcastPlayerMsg(6, "The couple have disconnected and the wedding is discontinued.");
	eim.disposeIfPlayerBelow(100, 680000500);
    } else {
	eim.unregisterPlayer(player);
	eim.RemoveWarpbackList(player);

	if (eim.disposeIfPlayerBelow(0, 0)) {
	    em.setProperty("state", "0");
	}
    }
}

function playerDisconnected(eim, player) {
    return 0;
}

function monsterValue(eim, mobId) {
    return 1;
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
	em.setProperty("state", "0");
    }
}

function end(eim) {
    eim.disposeIfPlayerBelow(100, 680000500);

    em.setProperty("state", "0");
}

function clearPQ(eim) {
    end(eim);
}

function disbandParty (eim) {
}

function allMonstersDead(eim) {}
function removePlayer(eim, player) {}
function leftParty (eim, player) {}
function playerDead(eim, player) {}
function cancelSchedule() {}