var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 1) {
	    qm.sendNext("What are you saying? I'm in a rush right now! Just bring your butt over here immediately!");
	    qm.dispose();
	    return;
	}
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendSimple("Yo, where are you? We have a situation here! \r\n#b#L0#(Yo? I've never heard Tru called me YO...)#k#l");
	    break;
	case 1:
	    qm.askAcceptDecline("It's a very important information! I need you here at #bLith Harbor Info Store#k right now!");
	    break;
	case 2:
	    qm.forceStartQuest();
	    qm.dispose();
	    break;
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 6) {
	    qm.sendNext("What, you don't know? I thought you'd know all skills that had the word pole arm to it.");
	    qm.dispose();
	    return;
	}
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendNext("Wow... never did I think this would happen. Never in my wildest dreams did I think the puppeteer would enter here. I should have trained too. I just got completely ambushed.");
	    break;
	case 1:
	    qm.sendNextPrev("I'm so sorry. It's all because of me...", -1, true);
	    break;
	case 2:
	    qm.sendNextPrev("Eh? Why would you feel sorry for this? You wouldn't have known that they'd take this route, either. No need to apologize. If nothing else, they just revealed their weakness.");
	    break;
	case 3:
	    qm.sendNextPrev("Weakness?",  -1, true);
	    break;
	case 4:
	    qm.sendNextPrev("If the document that the puppeteer lost is fake, then he wouldn't have acted so urgently, bringing along his men. This proces that the document is the real deal, and that Black Wing's ultimate target is the Seal Stone at Victoria Island.");
	    break;
	case 5:
	    qm.sendNextPrev("But your location is also exposed...",  -1, true);
	    break;
	case 6:
	    qm.sendYesNo("No worries. I was attacked this time while waiting for some items from Lirin, but usually, I'm not this careless. Never underestimate the power and the alertness of the information merchant! I always come up with an escape route wherever I go. Before that, do you know of a skill called #bPole Arm Mastery#k?");
	    break;
	case 7:
	    if (qm.getQuestStatus(21733) == 1) {
		qm.safe_teachSkill(21100000, 0, -1);
		qm.gainExp(9850);
		qm.showWZUOLEffect("Effect/BasicEff.img/AranGetSkill");
		qm.forceCompleteQuest();
	    }
	    qm.sendNext("Oh, so it must be a skill that you used to use. It's a skill that Lirin found while deciphering the Record of Heroes, and sent it to me thinking it might be prefect for you. I was worried I might lose this, so that's why I didn't put up as much of a fight, because I was trying to protect it. Mission accomplished?");
	    break;
	case 8:
	    qm.sendPrev("Regardless of how much the Black Wings act up, they will not be able to prevent you from your progress towards returning to your heroic form. Let's keep training until you can manhandle the Black Wizard, okay? I'll try my best to gather as much information as possible.");
	    break;
	case 9:
	    qm.dispose();
	    break;
    }
}