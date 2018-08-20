// Grant's gift

var status = 0;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    switch (status) {
	case 1:
	    qm.sendNext("I see you've completed Saffron's first lesson in Herbalism! I've prepared a small gift to congratulate you.");
	    break;
	case 2:
	    qm.sendNextPrev("This is #rSaffron's Secret Herb Patch Ticket#k. Take this to the #bportal on the right side of Adrentmill's lower level#k and you'll be able to enter the Secret Herb Patch. The patch is full of #bHerbs suitable for level 1 or 2 Herbalist.#k");
	    
	    if (qm.getQuestStatus(2552) == 0) {
		qm.forceCompleteQuest();
		qm.gainItem(4001482,1);
	    }
	    break;
	case 3:
	    qm.sendPrev("#bNack#k sells these, if you want more. I look forward to seeing what kind of Herbalist you become!");
	    break;
	case 4:
	    qm.dispose();
	    break;
    }
}

function end(mode, type, selection) {
}