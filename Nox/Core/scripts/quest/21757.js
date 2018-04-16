function start(mode, type, selection) {
}

var status = -1;

function end(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
	    qm.sendOk("Leave if you are not interested.");
	    qm.dispose();
	    return;
	}
	status--;
    }
    switch (status) {
	case 0:
	    if (qm.getQuestStatus(21757) == 0) {
		qm.forceStartQuest();
		qm.dispose();
	    } else {
		qm.sendYesNo("How can I help you? If you're not here to become a knight, you're not welcome here. And what is that you have there? Is that for the Empress? I can't let you deliver that, as it might be something dangerous even if llji isn't aware of. I'll have to look at it.");
	    }
	    break;
	case 1:
	    qm.sendNext("Hmmmm, this contains some very interesting tidbits. It even has some stuff about the Teardrop of Shinsoo... Well, anyway, I'll look it over more carefully.");
	    break;
	case 2:
	    qm.sendNextPrev("The Black Wings might be targetting this place next.", -1, true);
	    break;
	case 3:
	    qm.forceCompleteQuest();
	    qm.sendPrev("Even if that's the case, it is a matter that will be handled in Ereve. It is really none of your business. There is no garantee that you are not one of the Black Wings. Thanks for the information but I'm going to have to ask you to leave.");
	    break;
	case 4:
	    qm.dispose();
	    break;
    }
}