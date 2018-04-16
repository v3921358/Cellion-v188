function start(mode, type, selection) {
}

var status = 0;

function end(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	qm.sendOk("No? I don't understand what exactly is going on here. Please explain it to me one more time.");
	qm.dispose();
	return;
    }
    if (status == 1) {
	if (qm.getQuestStatus(21750) == 0) {
	    qm.forceStartQuest();
	    qm.sendOk("#bAthena Pierce#k is waiting for you, you should make your way to the #bCamp Conference Room#k now to let Athena Pierce know that you are alive and well.");
	    qm.dispose();
	} else {
	    qm.sendNext("Aran? ...Am I seeing something? Aran... is that... is that really you? Oh thank goodness!!! Oh.......... Thank you... thank you.");
	}
    } else if (status == 2) {
	qm.sendNextPrev("Um... I'm sorry, but I don't remember you.", -1, true);
    } else if (status == 3) {
	qm.sendNextPrev("What? What do you mean, Aran? You are Aran, right? The hero that saved all of us... that's you, no?");
    } else if (status == 4) {
	qm.sendNextPrev("#b(I wound up explaining to her what happened hundreds of years later.)#k", -1, true);
    } else if (status == 5) {
	qm.sendYesNo("I see... so you have lost all of your memories, and you woke up hundreds of years later, so... this for you is a past.");
    } else if (status == 6) {
	qm.sendNext("..Okay, so I'll have to re-introduce myself to you. My name is #b#p2131000#, a good friend of Aran. A few months ago, I left you here to battle the Black Wizard in anticipation of his attack..");
	if (qm.getQuestStatus(21750) == 1) {
	    qm.gainExp(500);
	    qm.forceCompleteQuest();
	    qm.forceStartQuest(21764, "1");
	    qm.forceStartQuest(21765, "2");
	}
    } else if (status == 7) {
	qm.sendNextPrev("While you were battling the Black Wizard, we were on the ark that led us to Victoria Island, but a dragon attacked us, so we wound up doing an emergency-landing on this forest.");
    } else if (status == 8) {
	qm.sendNextPrev("But we couldn't give up now, so we decided to establish our new lives here. We've been slowly setting up a new town in hopes of starting anew.");
    } else if (status == 9) {
	qm.sendNextPrev("Because we're trying to establish something out of nothing here in Victoria Island, all the young men that came with us are spread out all over the island. In this town, it's either the injured, the young, or the women.");
    } else if (status == 10) {
	qm.sendPrev("Anyway, how did you wind up here, Aran?");
    } else {
	qm.dispose();
    }
}