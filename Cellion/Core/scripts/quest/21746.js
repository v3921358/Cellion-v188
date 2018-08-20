var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 2) {
	    qm.sendNext("If you are against the test, that means you're not confident in yourself, no? I cannot tell someone like that the secrets of Seal Stone");
	    qm.dispose();
	    return;
	}
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendNext("Seal Stone... It's an item that been protected by Mu Lung for a long, long time. And now, someone is after it.");
	    break;
	case 1:
	    qm.sendNextPrev("Please tell me all you know about the Seal Stone", -1, true);
	    break;
	case 2:
	    qm.askAcceptDecline("I can't do that. How do I know that you are even more dangerous of a person than Shadow Swordman? This means I'll ahve to test you on something. Are you okay with it?");
	    break;
	case 3:
	    if (qm.getPlayerCount(925040001) == 0) {
		qm.forceStartQuest();
		qm.warp(925040001,0);
		qm.spawnMob(9300350,246,7);
	    } else {
		qm.sendOk("Someone is already inside defeating Mu Gong's shadow, hold on a while more!");
	    }
	    qm.dispose();
	    break;
    }
}

function end(mode, type, selection) {
    qm.dispose();
}