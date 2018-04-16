/*
	NPC Name: 		Taxi to Eldestein
	Map(s): 		Edelstein
*/
var status = -1;
var cost;
function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	cm.sendNext("Let me know if you change your mind.");
	cm.dispose();
	return;
    }
    switch (status) {
	case 0:
	    cm.sendYesNo("Hello, welcome to the Edelstein Taxi. I can take members of the Black Wings safely and quickly to #bVerne Mines#k. And if you're not part of the Black Wings? Well, I guess I'll take you as long as you pay... So, are you going to the mines?");
	    break;
	case 1:
	    if (cm.getJob() >= 3000 && cm.getJob() < 4000) {
		cost = 3000;
		cm.sendYesNo("Oh, you're a member of the Black Wings. I have a special discount for the Black Wings. You can ride for a mere #b3000 Mesos#k. Hop on.");
	    } else {
		cost = 10000;
		cm.sendYesNo("I see that you are not a member of the Black Wings. I do not have any special discount for the non Black Wings. You can ride for a #b10000 Mesos#k instead. Would you like to hop on?");
	    }
	    break;
	case 2:
	    if (cm.getMeso() >= cost) {
		cm.gainMeso(-cost);
		cm.warp(310040200,0);
	    } else {
		cm.sendOk("It appears that you do not have meso to ride this.");
	    }
	    cm.dispose();
	    break;
    }
}