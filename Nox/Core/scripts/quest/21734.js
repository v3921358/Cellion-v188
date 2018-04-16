var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 2) {
	    qm.sendNext("Hmmm? Hey, what are you saying? It is a certified information gathered up by the finest source of information from me! Trust me on this");
	    qm.dispose();
	    return;
	}
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendNext("Are you busy there? I have been looking all over Victoria Island in search of valuable information, and found one that will intrigue you. It has to do with Puppeteer, and...");
	    break;
	case 1:
	    qm.sendNextPrev("I don't know if you know this, but ever since you taught Pupeteer a lesson, the entrance that was on the Evail Eye Cave no longer works. It looks like Puppeteer has #bmoved to a new hideout#k.");
	    break;
	case 2:
	    qm.askAcceptDecline("I recived a report that Puppeteer build a #bsmall cabin#k inside #bHunting Ground in the Deep Forest ll#k of Sleepywood. I think this information is quite reliable. Let's head over there and eliminate #rPuppeteer#k once and for all");
	    break;
	case 3:
	    qm.forceStartQuest();
	    qm.dispose();
	    break;
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    switch (status) {
	case 0:
	    if (qm.getQuestStatus(21734) == 1) {
		qm.forceCompleteQuest();
		qm.gainExp(17100);
	    }
	    qm.sendNext("You must be on your way back from defeating Puppeteer, but why do you look so down? Is something going on?");
	    break;
	case 1:
	    qm.sendNextPrev("There's no discrenible trace of info on Seal Stone on Victoria Island", -1, true);
	    break;
	case 2:
	    qm.sendPrev("Ahh, you mean that! hahaha... you shouldn't have to worry about that.");
	    qm.dispose();
	    break;
    }
}