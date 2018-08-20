function start(mode, type, selection) {
    qm.forceStartQuest();
    qm.dispose();
}

var status = -1;

function end(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    switch (status) {
	case 0:
	    if (qm.getQuestStatus(21748) == 0) {
		qm.forceStartQuest();
		qm.dispose();
	    } else {
		qm.sendNext("Aran, I heard that you went to Mu Lung to look into a Black Wings-related incident. Did... they trap you again..?");
	    }
	    break;
	case 1:
	    qm.sendNextPrev("#b(I told her about the Seal Stone of Mu Lung.)", -1, true);
	    break;
	case 2:
	    qm.sendNextPrev("...What? The hero... which means you are the one responsible for leaving the Seal Stone there? Its okay that the Seal Stone of Mu Lung has been taken away from us. This is an amazing new lead!");
	    break;
	case 3:
	    qm.sendNextPrev("An amazing new lead?", -1, true);
	    break;
	case 4:
	    qm.sendNextPrev("The face that the heroes ad the Seal Stone means that if #bwe look into the small facts on heroes, and piece the puzzle together, then we may be able to find out where the Seal Stones are,#k no? That means we may be able to get out hands on the Seal Stoes before they do!");
	    break;
	case 5:
	    qm.sendNextPrev("That's it! Brillant!", -1, true);
	    break;
	case 6:
	    qm.sendYesNo("Ahaha, this is awesome! I am pumped for this, are you?\r\nAran, please take this skill!");
	    break;
	case 7:
	    qm.sendOk("I'll have to retrace the steps of these heroes now! Mr. Truth will take care of the information on Black Wings, so you should just concentrate on mastering the #bFinal Charge#k! It should be the skill that brings an end to Black Wings!");
	    qm.safe_teachSkill(21100002,0,-1);
	    qm.forceCompleteQuest();
	    qm.dispose();
    }
}