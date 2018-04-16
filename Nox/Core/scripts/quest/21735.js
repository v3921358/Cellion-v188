var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 2) {
	    qm.sendNext("Haha... maybe we should have made puppeteer's life more miserable. No? But he's still just a little kid. This should be enough, no?");
	    qm.dispose();
	    return;
	}
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendNext("Seal Stone of Victoria Island? I got it already!");
	    break;
	case 1:
	    qm.sendNextPrev("!!\r\n... How, did you get this?", -1, true);
	    break;
	case 2:
	    qm.askAcceptDecline("After being ambushed by the puppeteer last time, I used every source of information I found find to look through ever single corner of Victoria Island, and that's how I found it. I can't just take it and not fish it back, you know? Our goal is to take away what they are looking for, first. Wouldn't that be considered a great revenge?");
	    break;
	case 3:
	    if (qm.getQuestStatus(21735) == 0) {
		qm.forceStartQuest();
		qm.gainItem(4032323, 1);
	    }
	    qm.sendNext("But the Black Wings already know me. Holding into this may not be the smartest idea, and you holding on to it might mean losing it in a battle. I think we should let #bLirin#k hold on to it");
	    break;
	case 4:
	    qm.sendNextPrev("The island of Rien used to be only populated by teh Rien race, and it's covered with spells that disables other humans from entering the island, so even someone like Black Wings will not find it easy finding the place. Tell this to Lirin");
	    break;
	case 5:
	    qm.sendNextPrev("I will no longer give you tasks that had to do with gathering up information. I think you already know a thing or two about the world of Maple, so... you should now be able to gather up information on your own!");
	    break;
	case 6:
	    qm.sendPrev("If nothing else, I want to to really work on gathering up valuable information on Black Wings. Furthermore, #bkeep asking around for the existence of Seal Stone, and let me know if you find any#k.");
	    break;
	case 7:
	    qm.dispose();
	    break;
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 1) {
	    qm.sendNext("Are you not confident that you can do the job? Are you a bit worried? No worries, you're Aran!");
	    qm.dispose();
	    return;
	}
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendNext("I have been contiuously up to date on Black Wings though Mr. Truth. He also had been ambushed by them, as well. Are you alright, as well? Wait, is that really Seal Sone of Victoria Island? So Mr. Truth wound up finding it faster than others.");
	    break;
	case 1:
	    qm.sendYesNo("I don't know what this Jade will do, but we all know it's closely related to the Black Wizard. As long as they're looking for this, we'll have to protect this asset of ours. You'll have to make yourself stronger regardless of the circumstances.");
	    break;
	case 2:
	    if (qm.haveItem(4032323)) {
		qm.gainItem(4032323, -1);
		qm.gainExp(500);
		qm.safe_teachSkill(21100005, 0, -1); // Combo drain
		qm.forceCompleteQuest();
	    }
	    qm.showWZUOLEffect("Effect/BasicEff.img/AranGetSkill");
	    qm.sendNext("Okay, the document that was recently deciphered had a new skill called #bCombo Drain#k. You used to use this skill right? Nowadays, I only need to take just a glimpse of the skills, and I already know if you used it in real combat.");
	    break;
	case 3:
	    qm.sendPrev("Black Wings.. I am sure their plan does not end here. \r\nPlease tell Mr. Truth to keep digging up new information on the Black Wings. As for you, please keep training.");
	    break;
	case 4:
	    qm.dispose();
	    break;
    }
}