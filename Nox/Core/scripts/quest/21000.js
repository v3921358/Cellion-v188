var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	    status++;
    } else {
	    status--;
    }
    if (status == 0) {
	    qm.sendAcceptDecline("Wait, where's the kid? Oh no, he must be stuck in the forest! We need to bring the kid back here before the ark leaves! Aran... please go in there and find the kid for me! I know it's a lot to ask considering you're injured... but you're our only hope!");
    } else if (status == 1) {
      	qm.forceStartQuest();
      	qm.sendNext("#bThe kid is probably somewhere deep in the forest#k! We need to leave right now before the Black Wizard finds us, so please hurry!");
    }  else if (status == 1) {
      	qm.sendNextPrev("The most important thing right now is not to panic, Aran. If you want to see how far you've gone with your quest, press #bQ#k to open the quest window.");
    } else if (status == 2) {
        qm.sendNextPrev("Please rescue the kid from the forest, Aran! We cannot afford any more casualties at the hands of the Black Wizard!");
    } else if (status == 3) {
        qm.showWZUOLEffect("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialArrow1");
        qm.dispose();
    }
}

function end(mode, type, selection) {
    qm.dispose();
}