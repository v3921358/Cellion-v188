function init() {
    scheduleNew();
}

function scheduleNew() {
    em.setProperty("canenter", "false");

    if (em.getChannel() == 1) {
	//em.schedule("startevent", 600000);
    }
}

function startevent() {
    em.broadcastWorldMsg(6, "[Event] Treasure hunt event will start in 2 minutes Find Maple Administrator at Channel 1 or @treasurehunt command now!");
    em.schedule("openevent", 120000); // delay 2 minutes before starting it
    em.schedule("time_up", 1320000); // total 20 minutes here + 2
}

function openevent() {
    em.broadcastWorldMsg(6, "[Event] Treasure hunt event has started! Find Maple Administrator at Channel 1 or @treasurehunt command now!");
    em.setProperty("canenter", "true");
    
    //eim.startEventTimer(1000 * 60 * 20); // 20 minutes
}

function time_up() {
    em.setProperty("canenter", "false");
    var msg = "The time limit for the Treasure hunt has passed. The event has ended, and you'll be sent to a different map. Thank you for participating in this event!";

    em.broadcastMapMsg(109010000, msg, 0); // Find the Jewel
    em.broadcastMapMsg(109010200, msg, 0); // Southern Field
    em.broadcastMapMsg(109010201, msg, 0); // Hidden Place in South
    em.broadcastMapMsg(109010202, msg, 0); // Hidden Place in South
    em.broadcastMapMsg(109010203, msg, 0); // Hidden Place in South
    em.broadcastMapMsg(109010204, msg, 0); // Hidden Place in South
    em.broadcastMapMsg(109010205, msg, 0); // Hidden Place in South
    em.broadcastMapMsg(109010206, msg, 0); // Hidden Place in South
    
    em.broadcastMapMsg(109010100, msg, 0); // Eastern Field
    em.broadcastMapMsg(109010101, msg, 0); // Hidden Place in East
    em.broadcastMapMsg(109010102, msg, 0); // Hidden Place in East
    em.broadcastMapMsg(109010103, msg, 0); // Hidden Place in East
    em.broadcastMapMsg(109010104, msg, 0); // Hidden Place in East
    em.broadcastMapMsg(109010105, msg, 0); // Hidden Place in East
    em.broadcastMapMsg(109010106, msg, 0); // Hidden Place in East
    em.broadcastMapMsg(109010107, msg, 0); // Hidden Place in East
    em.broadcastMapMsg(109010108, msg, 0); // Hidden Place in East
    em.broadcastMapMsg(109010109, msg, 0); // Hidden Place in East
    em.broadcastMapMsg(109010110, msg, 0); // Hidden Place in East

    em.schedule("warpExit", 5000);
}

function warpExit() {
    var out = 109050001;

    em.warpAllPlayer(109010000, out); // Find the Jewel
    em.warpAllPlayer(109010200, out); // Southern Field
    em.warpAllPlayer(109010201, out); // Hidden Place in South
    em.warpAllPlayer(109010202, out); // Hidden Place in South
    em.warpAllPlayer(109010203, out); // Hidden Place in South
    em.warpAllPlayer(109010204, out); // Hidden Place in South
    em.warpAllPlayer(109010205, out); // Hidden Place in South
    em.warpAllPlayer(109010206, out); // Hidden Place in South
    em.warpAllPlayer(109010100, out); // Eastern Field
    em.warpAllPlayer(109010101, out); // Hidden Place in East
    em.warpAllPlayer(109010102, out); // Hidden Place in East
    em.warpAllPlayer(109010103, out); // Hidden Place in East
    em.warpAllPlayer(109010104, out); // Hidden Place in East
    em.warpAllPlayer(109010105, out); // Hidden Place in East
    em.warpAllPlayer(109010106, out); // Hidden Place in East
    em.warpAllPlayer(109010107, out); // Hidden Place in East
    em.warpAllPlayer(109010108, out); // Hidden Place in East
    em.warpAllPlayer(109010109, out); // Hidden Place in East
    em.warpAllPlayer(109010110, out); // Hidden Place in East

    em.schedule("startevent", 5400000);
}

function cancelSchedule() {
}

function scheduledTimeout(eim) {
}