function start(mode, type, selection) {
}

var status = -1;

function end(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 20) {
	    qm.dispose();
	    return;
	}
	status--;
    }
    switch (status) {
	case 0:
	    if (qm.getQuestStatus(21302) == 0) {
		qm.forceStartQuest();
		qm.dispose();
	    } else {
		if (qm.haveItem(4032312)) {
		    status = 19;
		    qm.sendNext("Oh, isn't that... Hey, did you remember how to make the Red Jade? You may be a dummy who has amnesia, but this is why I can't leave you. Now hurry, give me the gem!");
		} else {
		    qm.sendNext("Did you slay the Thief Crow? Yippy! You're my master indeed! Now, give me the Red Jade you found! I'll reattach it and ... Wait, why arn't you saying anything? Don't tell me you didn't bring it back...");
		}
	    }
	    break;
	case 1:
	    qm.sendNext("What? You didn't bring the Red jake?! Why Not?! Did you forget?! Yikes, I never thought the Black Mage's curse would turn you into a dummy...");
	    break;
	case 2:
	    qm.sendNextPrev("No, I can't let this drive me to dispair. Now more than ever, I must stay optimistic and alert. Argh..");
	    break;
	case 3:
	    qm.sendNextPrev("You can go back if you want, but I'm sure the thief has already fled the scene. You'll just have to make a new Red Jake. You've made one before, so you remember the required materials, don't you? So hurry it up.");
	    break;
	case 4:
	    qm.sendNextPrev("#b(He's lost his memory!)");
	    break;
	case 5:
	    qm.sendNextPrev("No hope. No dreams. Noooo!");
	    break;
	case 6:
	    qm.sendNextPrev("#b(Maha is becoming volatile. You should leave the permise for now. You're sure Lirin could help you somehow.)",-1,true);
	    break;
	case 7:
	    qm.forceCompleteQuest();
	    qm.dispose();
	    break;
	case 20:
	    qm.sendYesNo("Okay, now that I have the power of Red Jake, I'll restore more of your abilities. Your level has gotten much higher since the last time we met, so I'm sure I can work my magic a bit more this time!");
	    break;
	case 21:
	    if (qm.haveItem(4032312)) {
		if (qm.canHold(1142131)) {
		    qm.gainItem(4032312,-1);
		    qm.gainItem(1142131,1);
		    qm.changeJob(2111);
		    qm.safe_teachSkill(21110002, 0, 20);
		    qm.forceCompleteQuest();
		} else {
		    qm.sendOk("Please make space in your EQ inventory.");
		    qm.dispose();
		    break;
		}
	    }
	    qm.sendNext("Please get back all of your abilities soon. I want to explore with you like we did in the good old days.");
	    qm.dispose();
	    break;
    }
}