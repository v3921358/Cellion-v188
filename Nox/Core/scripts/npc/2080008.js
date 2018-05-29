/*
 *	Rexion Mastery Book Handler
 *	@author Mazen
 */

 var nMasteryBook20 = 2431789;
 var nMasteryBook30 = 2431790;
 var bLv30Book; // 1 = Activate Mastery Book 20, 2 = Activate Mastery Book 30, 3 = Buy Mastery Book 20, 4 = Buy Mastery Book 30.

function start() {
	text = "Welcome to the Mastery Book Manager#k!\r\n"
		+ "What would you like to do?\r\n";
	
	if (cm.haveItem(nMasteryBook20, 1)) {
		text += "#r#fs13##L1001##i" + nMasteryBook20 + "# Activate Lv. 20 Mastery Book#l\r\n"
	} 
	if (cm.haveItem(nMasteryBook30, 1)) {
		text += "#r#fs13##L1002##i" + nMasteryBook30 + "# Activate Lv. 30 Mastery Book#l\r\n\r\n"
	}
	
	text += "#r#fs11##L1003#3,000,000 Meso#k - #dPurchase Lv. 20 Mastery Book#l\r\n"
		+ "#r#L1004#4,000,000 Meso#k - #dPurchase Lv. 30 Mastery Book#l"
	
    cm.sendSimple(text);
	status = -1;
}

var nQuantity;
var nCost;
var nChoice;
var nSkill;

function action(mode, type, selection){
	if (mode == 1) {
		status++;
	} else {
		if (status == 0){
			cm.sendNextS("Enjoy your adventure!", 5);
            cm.dispose();
		}
        status -= 1;
	}
	
	if (selection == 1002) {
		bLv30Book = true;
	} else if (selection == 1003) {
		if (cm.getMeso() >= 3000000) {
			cm.gainMeso(-3000000);
			cm.gainItem(nMasteryBook20, 1);
			cm.sendOk("#rYou have successfully purchased a #rLv. 20 Mastery Book#k.")
			cm.dispose();
		} else {
			cm.sendOk("#rSorry, looks like you don't have enough meso.")
			cm.dispose();
		}
		return;
	} else if (selection == 1004) {
		if (cm.getMeso() >= 4000000) {
			cm.gainMeso(-4000000);
			cm.gainItem(nMasteryBook30, 1);
			cm.sendOk("#rYou have successfully purchased a #rLv. 30 Mastery Book#k.")
			cm.dispose();
		} else {
			cm.sendOk("#rSorry, looks like you don't have enough meso.")
			cm.dispose();
		}
		return;
	}
	
	if (status == 0) {
		
		nSkill = cm.getPlayer().masteryBookRequest(bLv30Book ? true : false); // Method created to retrieve all Skill IDs available for mastery book usage. 
		
		text = "You can level up the following skills:\r\n#d";
		for (i = 0; i < nSkill.length; i++ ) {
			text += "#fs14# #L" + i + "##s" + nSkill[i] + "# #q" + nSkill[i] + "##l\r\n"
		}
		
		text += "\r\n#r#fs14# #L999#Stop using the Mastery Book.#l"
		cm.sendSimple(text);
	} else if (status == 1) {
		
		if (selection == 999) {
			cm.dispose();
			return;
		}
		
		if (cm.haveItem(bLv30Book ? nMasteryBook30 : nMasteryBook20, 1)) {
			cm.getPlayer().setSkillMasterLevel(nSkill[selection], bLv30Book ? 30 : 20);
			cm.gainItem(bLv30Book ? nMasteryBook30 : nMasteryBook20, -1);
			cm.dispose();
		} else {
			cm.sendOk("#rSorry, looks like you don't have a Mastery Book available.")
			cm.dispose();
		}
	}
	
}
