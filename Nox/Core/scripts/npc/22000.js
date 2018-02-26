/*
	Vasily
	I attempted to make him GMS-like -Mazen
*/

var status; 

function start() { 
    status = -1; 
    action(1, 0, 0); 
} 

function starterpack() { 
	cm.gainItem(3010000, 1); // Beginner Chair 
    cm.gainItem(2000005, 100); // Power Elixir
	cm.warp(104000000, 0); //Lith Harbor
	cm.sendOk("Welcome to Lith Harbor, enjoy your stay!");
	cm.dispose();
}

function action(mode, type, selection) { 
    if (mode == 1) { 
        status++; 
    }else{ 
        status--; 
    } 
     
    if (status == 0) {
        cm.sendYesNo("Hey there #b#h ##k, seems like you want to leave this place.\r\nFor #r100 mesos#k I can take you with me to #rLith Harbor#k.\r\n\r\nWould you like to go?");
    } else if (status == 1) {
		if (cm.getLevel() >= 7 && cm.getMeso() >= 100) {
			cm.gainMeso(-100);
			starterpack();
		} else if (cm.getLevel() < 7) {
			cm.sendOk("Sorry, you need to be atleast #blevel 7#k to leave this island.");
		} else {
			cm.sendOk("Sorry, looks like you don't have enough mesos.");
		}
	} else {
		cm.dispose();
	}
}
	