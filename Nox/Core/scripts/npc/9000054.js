/* 	Owner of Pature
*/

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	cm.sendOk("What do you mean? You're not annoyed?");
	cm.dispose();
	return;
    }
    if (status == 0) {
	if (cm.getMapId() == 910040002) {
	    var em = cm.getEventManager("SheepRanch");
	    
	    if (em == null) {
		cm.sendOk("The event is currently unavailable.");
		cm.dispose();
		return;
	    }
	    var prop = em.getProperty("state");
	    if (prop == null || prop.equals("0")) {
		var count = cm.getPlayerCount(910040002);
		if (count < 1) {
		    cm.sendNext("The total number of current users is "+count+". A minimum of 4 players is required to start the game. The event will start any moment once the number of players are all gathered. Don't leave and just hang on for a while!");
		} else {
		    em.startInstanceSheepRanch(cm.getMap());
		}
	    } else {
		cm.sendOk("Some wolves and sheeps are currently battling!");
	    }
	    cm.dispose();
	} else {
	    cm.sendSimple("Welcome to Sheep Ranch. Hmm....you have something to say to me? What is it??\r\n\r\n#b#L0# Did you know that shepherd is crying out that wolves have appeard?#l");
	}
    } else if (status == 1) {
	cm.sendSimple("Oh really? That little boy~ He can't keep himself boring.But I don't think he's totally lying. Cause there has been a spate of wool robberies in Sheep Ranch recently. You just came down in right moment.Why don't you help us patrolling the Sheep Ranch. You need to in be in a team of 4 or more member. \r\n Which team do you want to be in?\r\n\r\n#b#L0# team of 4 or more member#l\r\n#L2# See the instruction#l");
    } else if (status == 2) {
	switch (selection) {
	    case 2:
		cm.sendOk("#b<Instruction>#k\r\n1. Enter the Fenced Street, and you can start the game in a team of 4 or more members.(No need to be in a party.)\r\n2. If Sheeps loses all of the wools, it will get naked and lose points. \r\n3. To Wolves win the victory, wolves have to steal the wools from sheeps as many as they can in certain period of time, else Sheeps will win the victory.\r\n4. Only the ones who win the victory will get the reward EXP.\r\n5. Wolves will get more EXPs when they have more wools.\r\n6. Sheeps will get more EXP when they have more Shepherd Boy's Lunch.");
		cm.dispose();
		break;
	    case 1:
	    case 0:
		cm.warp(910040002,0);
		cm.dispose();
		break;
	}
    }
}