// Aran - Slowing down the pole arm

var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendNext("Why do I look like this, you ask? I don't want to talk about it, but I suppose I can't hide from you since you're my master..");
	    break;
	case 1:
	    qm.sendNextPrev("While you were trapped inside ice for hundreds of years, I too, was frozen. It was a long time to be away from you. That's when the seed of darkness was planted in my heart.");
	    break;
	case 2:
	    qm.sendNextPrev("But since you awoke, I thought the darkness had gone away. I thought things would return to the way they were, but I was mistaken.");
	    break;
	case 3:
	    qm.askAcceptDecline("Please, Aran. Please stop be from becoming enraged. Only you can control me. Its out of my hands now. Please do whatever it takes to #rstop me from going berserk!#k");
	    break;
	case 4:
	    if (qm.getPlayerCount(914020000) == 0) {
		qm.resetMap(914020000);
		qm.warp(914020000,0);
		qm.spawnMob(9001014,-852,86);
		qm.startSelfTimer(1200, 140000000);
		qm.forceStartQuest();
	    } else {
		qm.sendOk("Someone is already inside, try again later.");
	    }
	    qm.dispose();
	    break;
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 1) {
	    qm.sendOk("What?!? You are not ready for more abilities?");
	    qm.dispose();
	    return;
	}
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendNext("Thank you, Aran. If it weren't for you, I would have become enraged and who knows what could have happened. Thank you. NOT! Its only your duty as my master..");
	    break;
	case 1:
	    qm.sendYesNo("Anyway, I just noticed how high of a level you've reached. If you were able to control me in my state of rage, I think you're ready to handle more abilities.");
	    break;
	case 2:
	    if (qm.canHold(2280003) && qm.canHold(1142132)) {
		if (qm.getQuestStatus(21401) == 1) {
		    qm.changeJob(2112);
		    qm.gainAp(5);
		    qm.gainItem(2280003, 1);
		    qm.gainItem(1142132, 1);
		    qm.safe_teachSkill(21120002, 0, 10);
		    qm.safe_teachSkill(21120001, 0, 10);
		    qm.safe_teachSkill(21121003, 0, 10);
		    qm.forceCompleteQuest();
		}
		qm.sendNext("Your skills have been restored. These skills have been dormant for so long that you'll have to re-train yourself, but you'll be as good as new once you complete your training.");
	    } else {
		qm.sendOk("Please check if you have sufficient inventory space.");
	    }
	    break;
	case 3:
	    qm.sendNext("Oh, and I've given you a Skill Book. I compiled that consist of a skill called Maple Hero. It isn't one of the skills you had in the past, but it could come in handy sometimes.");
	    break;
	case 4:
	    qm.sendNextPrev("Even with all that, however, you still have a long way to go until you returned to the old you. I heard the skills you have forgotten are floating aorund in the form of Skill Books. You'll be able to return to the old you if you find and train all those skills.");
	    break;
	case 5:
	    qm.dispose(); // TODO show skill UI
	    break;
    }
}
