/*
	NPC Name: 		Jack the Fairy Tale
 */

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    switch (status) {
	case 0:
	    if (cm.getMapId() == 910030000) {
		status = 9;
		cm.sendSimple("Do you want to listen to the story of the #e<Jack and the Beanstalk>#n?\r\n#b#L0# What kind of a fairy tale is it?#l#k\r\n#b#L1# I'd like to trade in my golden eggs.#l#k");
	    } else if (cm.getMapId() == 910032000) {
		cm.sendSimple("To grow the beanstalk, just hand me the Warm Sun and let me take care of the rest.\r\n\r\n#b#L0# Give him the Warm Sun#l\r\n#L1# Check the beanstalk's progress#l");
	    } else {
		cm.dispose();
	    }
	    break;
	case 1:
	    if (selection == 1) {
		cm.sendNext("Okay, let's see how much the beanstalk has grown.\r\n#B"+cm.getWarmSun()+"\r\n\r\nTake care of it a bit more, and it'll grow to new heights! Bring me some more Warm Suns.");
		//		cm.sendNext("Status of the tree's growth \n\r #B"+cm.getWarmSun()+"# \n\r If we collect them all, the tree would grow to it's fullest...");
		cm.dispose();
	    } else if (selection == 0) {
		var quantity = cm.itemQuantity(4001246);
		cm.sendGetNumber("Once the beanstalk is fully grown, a total of 100 people can climb up to the land high up in the clouds. Once that happens, someone has to be removed from the area by the Skyland Cat in order to enter. How many Warm Suns are you willing to give me? \r\n#b< Current Count : "+quantity+" >#k", quantity, 0, 5000);
	    }
	    break;
	case 2:
	    var num = selection;
	    if (num <= 0) {
		cm.sendNext("0? You must be kidding, right?");
	    } else if (cm.haveItem(4001246, num)) {
		cm.gainItem(4001246, -num);
		cm.giveWarmSun(num);
		cm.sendOk("Don't forget to give me the warm sun when you obtain them.");
	    }
	    cm.dispose();
	    break;
	case 10:
	    if (selection == 0) {
		cm.sendOk("To complete this fairy tale, the beanstalk must first grow to its absolute maximum height, and the item that's required to do so is the #b'Warm Sun'#k. How about bringing your Warm Sun in here and grow the beanstalk? #bWarm Suns#k can be found through monsters.");
		cm.dispose();
	    } else {
		cm.sendSimple("How many would you like to trade in? \r\n#b#L0# 30#l\r\n#L1# 50#l\r\n#L2# 100#l#k");
	    }
	    break;
	case 11:
	    var amt;
	    var qtn = -1;
	    switch (selection) {
		case 0:
		    amt = 30;
		    qtn = 3;
		    break;
		case 1:
		    amt = 50;
		    qtn = 6;
		    break;
		case 2:
		    amt = 100;
		    qtn = 15;
		    break;
		default:
		    cm.dispose();
		    return;
	    }
	    if (cm.haveItem(4001245,amt)) {
		cm.gainItem(4001245,-amt);
		cm.gainItem(2022428,qtn);
	    } else {
		cm.sendOk("What? I thought you said you were going to trade in "+amt+". Check your eggs again.");
	    }
	    // 4001245
	    cm.dispose();
	    break;
    }
}