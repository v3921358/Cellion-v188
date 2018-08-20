importPackage(java.lang);

var status = 0;
var minLevel = 140; 
var maxLevel = 250; 
var minPlayers = 1; 
var maxPlayers = 6; 
var open = true; //open or not
var PQ = 'DimensionInvasion';

function start() {
    status = -1;
    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (status >= 1 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 0 && status == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1)
        status++;
    else
        status--;

    if (status == 0) {
    if (cm.getPlayer().getMapId() != 940020000) { // not in pq lobby
        cm.sendSimple("Do you really want to leave this map now, and have to start all over if you want to try again?#b\r\n#L0#Yes! Get me out of here..#l");
    } else if (cm.getPlayer().getMapId() == 220000306 || cm.getPlayer().getMapId() == 940020000) {
        cm.sendSimple("Some creeps from Maple World have invaded Grandis. We need to find out who it is, and shut them down. \r\n#L1#Enter Dimension Invasion.#l\r\n#L3#Claim Items#l\r\n#L2#What's Dimension Invasion?#l\r\n#L5#How many more times can I try today?#l");
    } else {
        cm.dispose();
    }
} else if (status == 1) {
    if (selection == 0) {
        cm.warp(940020000, 0);
        cm.dispose();
    } else if (selection == 1) {
     if (cm.getParty() == null) { // No Party
        cm.sendSimple("It's a warzone in there, not a playground. I need a small group, six or less, to infiltrate the region and shut down their offenses from behind. Just get your party together and talk to me when you're ready.");
        cm.dispose();
    } else if (!cm.isLeader()) { // Not Party Leader
        cm.sendOk("It is up to your party leader to proceed.");
        cm.dispose();
    } else if (cm.getPQLogAll(PQ) >= 10){
        cm.sendOk("You have exceeded the max number of tries for today. Please come back tomorrow");
        cm.dispose();
    } else if (!cm.allMembersHere()) {
        cm.sendOk("Some of your party members are in a different map. Please try again once everyone is together.");
        cm.dispose();
    } else {
    // Check if all party members are over lvl 50
    var party = cm.getParty().getMembers();
    var mapId = cm.getMapId();
    var next = true;
    var levelValid = 0;
    var inMap = 0;
    var it = party.iterator();
    while (it.hasNext()) {
        var cPlayer = it.next();
        if (cPlayer.getLevel() >= minLevel && cPlayer.getLevel() <= maxLevel) {
            levelValid += 1;
        } else {
            cm.sendOk("You need to be between level " + minLevel + " and " + maxLevel + " to take on this epic challenge!");
            cm.dispose();
            next = false;
        } 
        if (cPlayer.getMapid() == mapId) {
            inMap += 1;
        }
    }
    if (party.size() > maxPlayers || inMap < minPlayers) {
        next = false;
    }
    if (next) {
        var em = cm.getEventManager("DimensionInvasion");
        if (em == null || open == false) {
            cm.sendSimple("This PQ is not currently available.");
            cm.dispose();
        } else {
            var prop = em.getProperty("state");
            if (prop == null || prop.equals("0")) {
                em.startInstance(cm.getParty(),cm.getMap(), 70);
            } else {
                cm.sendSimple("Someone is already attempting the PQ. Please wait for them to finish, or find another channel.");
            }
            //cm.start_DimensionInvasion(false, 100000000);
            cm.setPQLogAll(PQ);
            cm.dispose();
        } 
    } else {
        cm.sendYesNo("Your party is not a party between " + minPlayers + " and " + maxPlayers + " party members. Please come back when you have between " + minPlayers + " and " + maxPlayers + " party members.");
    } 
}
        } else if (selection == 2) {
            cm.sendNext("This is a fight that transcends dimensions. Somebody opened up a hole between Maple World and Grandis, and now we've got jerks from every corner of the universe trying to take over. As if Magnus wasn't bad enough...");
        } else if (selection == 3) {
            cm.sendNext("You have no items to claim. You sure you tried out Dimension Invasion?");//todo check if have item
            cm.dispose();
         } else if (selection == 5) {
            var pqtry = 10 - cm.getPQLog(PQ);
            cm.sendOk("You can do this quest " + pqtry + " time(s) today.");
            cm.dispose();
        }
    } else if (status == 2) {
        cm.sendNextPrev("You'll have to face down five waves of enemies inside. If too many enemies get by, we all lose.");
    } else if (status == 3) {
       cm.sendNextPrev("I've heard there's a hidden phase in there somewhere, but you have to win quick, or right in the nick of time. Just watch out, because I think it's pretty rough.");
    } else if (status == 4) {
        cm.sendOk("Every phase you deal with has different rewards, especially the hidden stage. So you get some loot for saving our dimension. All I get is a bunch of chuckleheads looking for loot. Lucky you...");
        cm.dispose();
    } else if (mode == 0) { 
        cm.dispose();
    } 
}