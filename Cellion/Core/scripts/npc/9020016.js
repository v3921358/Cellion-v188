/*
	Dragon Rider - Party Map
*/
importPackage(java.lang);

var status = 0;
var minLevel = 120; // GMS = 50 
var maxLevel = 250; // GMS = 200? recommended 50 - 69
var minPlayers = 3; // GMS = 3
var maxPlayers = 6; // GMS = 4 || but 6 makes it better :p
var open = true;//open or not
var PQ = 'dragonpq';

function start() {
	status = -1;
	action(1, 0, 0);
}
function action(mode, type, selection) {
	if (status >= 1 && mode == 0) {
        cm.sendOk("Ask your friends to join your party. You can use the Party Search funtion (hotkey O) to find a party anywhere, anytime.");
        cm.dispose();
        return;
    }
    if (mode == 1)
    	status++;
    else
    	status--;

    if (status == 0) {
	if (cm.getPlayer().getMapId() != 910002000) { // not in first time together lobby
		cm.sendSimple("#e <Party Quest: Dragon Rider>#n \r\n  How would you like to complete a quest by working with your party members? Inside, you will find many obstacles that you will have to overcome with help of your party members.#b\r\n#L0#Go to Party Lobby.")
	} else if (cm.getPlayer().getMapId() == 910002000) {
		cm.sendSimple("#e <Party Quest: Dragon Rider>#n \r\n Help an old man out, will you? Defeat the Dragon Rider and bring peace back to Leafre! #b\r\n#L1#Enter the Crimson SKy Dock. (Lv. 120+ /Party of 2+)#l\r\n#L2#Find a party.#l\r\n#L3#Give me the details.#l\r\n#L5#How many more times can I enter today?#k");
	} else {
		cm.dispose();
	}
} else if (status == 1) {
	if (selection == 0) {
	    cm.saveLocation("MULUNG_TC");
	    cm.warp(910002000,0);
	    cm.dispose();
	} else if (selection == 1) {
     if (cm.getParty() == null) { // No Party
     	cm.sendOk("You have to be in a party to enter.");
     	cm.dispose();
    } else if (!cm.isLeader()) { // Not Party Leader
    	cm.sendOk("It is up to your party leader to proceed.");
    	cm.dispose();
    } else if (cm.getPQLogAll(PQ) >= 10){
		cm.sendOk("Sorry... you've done it 10 times today. Please come back tomorrow.");
		cm.dispose();
	} else if (!cm.allMembersHere()) {
        cm.sendOk("Some of your party members are in a different map. Please try again once everyone is together."); // check if working..
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
        	var em = cm.getEventManager("Dragonica");
        	if (em == null || open == false) {
        		cm.sendSimple("This PQ is not currently available.");
        	} else {
        		var prop = em.getProperty("state");
        		if (prop == null || prop.equals("0")) {
        			em.startInstance(cm.getParty(),cm.getMap(), 70);
        		} else {
		    cm.sendSimple("Someone is already attempting the PQ. Please wait for them to finish, or find another channel.");
		}
		cm.removeAll(4001008);
		cm.removeAll(4001007);
		cm.setPQLogAll(PQ);
		cm.dispose();
	} 
} else {
            cm.sendOk("Your party is not a party between " + minPlayers + " and " + maxPlayers + " party members. Please come back when you have between " + minPlayers + " and " + maxPlayers + " party members.");
        } 
    }
} else if (selection == 2) {
	cm.OpenUI("21");
	    cm.dispose();
	} else if (selection == 3) {
		cm.sendOk("Enter the #bCrimson Sky Doorway#k and learn the identity of the #rDragon Rider#k. Use the #bFlying#k skill to soar through the skies and chase down the Wyverns to find him.\r\n #e - Level:#n 120 or above. \r\n #e - Time Limit:#n 30 minutes. \r\n #e - Players:#n 2 - 6 \r\n #e - Requirements:#n Flying Skill.");
		cm.dispose();
	}  else if (selection == 5) {
            var pqtry = 10 - cm.getPQLog(PQ);
            //if (pqtry >= 10){
            	cm.sendOk("You can attempt the Party Quest " + pqtry + " more time(s) today.");
				cm.dispose();
	//}
}
}else if (status == 2) { 
      cm.dispose();         
  } else if (mode == 0) { 
  	cm.dispose();
  } 
}