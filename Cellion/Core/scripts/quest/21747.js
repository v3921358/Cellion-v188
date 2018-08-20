// Aran - Catch the shadow warrior!

var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
	    qm.sendNext("I apologize for not recognizing you sooner. I didn't think I'd get to see that skill unfold before my very own eyes. Please dont feel offended.");
	    qm.dispose();
	    return;
	}
	status--;
    }
    switch (status) {
	case 0:
            qm.setAllowConversationCancel(false); // dont allow this to be cancelled by the client
            
	    qm.askAcceptDecline("Who would have thought that the hero's successor will reappear after hundreds of years...? Will you bring prosperity to the world of Maple, or will you be the end of it all? But then again, it doesn't matter which one of the two you'll become. I'll let you know what the Seal Stone of Mulung is about.");
	    break;
	case 1:
	    qm.sendNext("The place where Seal Stone of Mu Lung is located is at the sealed temple. Deep inside the Mulung Temple, you'll find the entrance to that place. At the pillars for the training center for the advanced, look for a pillar that has entrance engraved on it, and it'll lead you to the sealed temple. The code to enter that temple is #baction speaks louder than words#b.");
	    break;
	case 2:
	    qm.sendNextPrev("Maybe the Shadow Warrior has already made himself available at the sealed temple. But someone with this kind of challange usually means he's not necessarily there just for the item. He's there to see me... but I believe its better for the here's successor to face the Shadow Warrior.");
	    break;
	case 3:
	    qm.sendNextPrev("Please give all you can to prevent the Shadow Warrior from bringing doom to our temple. Please continue the legacy of your heroes.");
	    break;
	case 4:
	    qm.sendOk("#b(I think he's mistaken me for a hero's successor, and he wants me to continue the legacy of the heroes. What does he mean by that? I'll have to stop the Shadow Warrior first, then I'll ask him afterwards.)#k",-1,true);
	    qm.forceStartQuest();
	    qm.dispose();
	    break;
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
	    qm.sendNext("I apologize for not recognizing you sooner. I didn't think I'd get to see that skill unfold before my very own eyes. Please dont feel offended.");
	    qm.dispose();
	    return;
	}
	status--;
    }
    switch (status) {
	case 0:
            qm.setAllowConversationCancel(false); // dont allow this to be cancelled by the client
            
	    qm.sendYesNo("Were you able to defeat Shadow Swordman? But you don't look too happy. I don't think you lost the battle, but...");
	    break;
	case 1:
	    qm.sendNext("I see... so you wound up losing the Seal Stone of Mu Lung. That's unfortunate, but there's nothing you can do about it. I have no clue why the heroes left the Seal Stone a Mu Lung.");
	    break;
	case 2:
	    qm.sendNextPrev("Are you sure the heroes left the Seal Stone at Mu Lung?",-1,true);
	    break;
	case 3:
	    qm.sendNextPrev("Yes, I supposed you didn't know that tidbit. #bA long, long time ago, the heroes left the Seal Stone at Mu Lung, and the chief constructed Sealed Shrine to take care of all, at all times.");
	    break;
	case 4:
	    qm.sendNextPrev("The hero...",-1,true);
	    break;
	case 5:
	    qm.sendNextPrev("Nowadays, hardly anyone knows the existence of all that. Honestly, #bI am not sure if losing the Seal Stone negatively affects Mu Lung.#k We just thought it was an important item since the heroes left it here themselves.");
	    break;
	case 6:
	    qm.sendNextPrev("#b(So the heroes left the Seal Stone at Mu Lung...)#k",-1,true);
	    break;
	case 7:
	    qm.sendNextPrev("It's unfortunate that we wound up losing the item the heroes left here, but at least their successor is here, so that conforts me a little. Please complete what your heroes couldn't.");
	    break;
	case 8:
	    qm.sendPrev("#b(I wound up losing the Seal Stone of Mu Lung, too. I better go talk to Tru.)",-1,true);
	    qm.forceCompleteQuest();
	    qm.dispose();
	    break;
    }
}