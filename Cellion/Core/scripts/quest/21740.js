var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 4) {
	    qm.sendNext("Hmmm...? are you thinking you feel too ashamed to see Lirin? Don't think that way. We just feel terrible that you seem to be singlehandedly taking care of the dangerous missions.");
	    qm.dispose();
	    return;
	}
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendNext("Welcome back. How did it go with Orbis? Was it related to Black Wings? And why do you look so down? Please explain it to me in detail.");
	    break;
	case 1:
	    qm.sendNextPrev("#b(I told him about what happened to the Seal Stone of Orbis.)", -1, true);
	    break;
	case 2:
	    qm.sendNextPrev("Hmm. So you;re saying that there was a Seal Stone for Orbis as well. That's valuable information. It stings that you wound up losing it, but.. no no, I'm not blaming you for it. I just think that Black Wings was ready for it this time.");
	    break;
	case 3:
	    qm.sendNextPrev("...", -1, true);
	    break;
	case 4:
	    qm.askAcceptDecline("Keep your head up! it looks like Lirin #bdeciphered a new skill#k. You should go up to #bRien and meet Lirin#k, seeing that you also have to explain to her what happened at orbis.");
	    break;
	case 5:
	    qm.forceStartQuest();
	    qm.sendOk("Lirin is also part of this, and no one knows about your past better than her, so it's always important to #bkeep Lirin in the loop in terms of the information being passed around here#k.");
	    break;
	case 6:
	    qm.dispose();
	    break;
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 4) {
	    qm.sendNext("Don't worry about that. Worry about the skill you'll be receiving");
	    qm.dispose();
	    return;
	}
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendNext("Hey Aran, it has been a while. How's the training going along? I've just found a new skill, so I was going to call you up here anyway. Good thing you dropped by!");
	    break;
	case 1:
	    qm.sendNextPrev("#b(I told her about the Seal Stone of Orbis)#k", -1, true);
	    break;
	case 2:
	    qm.sendNextPrev("Seal Stone of Orbis... okay, this clears up a lot of things. What the Black Wings is looking for are Seal Stones, and there's more than one. This alone is quite coup for is.");
	    break;
	case 3:
	    qm.sendNextPrev("But I did lose the Seal Stone to them...", -1, true);
	    break;
	case 4:
	    qm.sendYesNo("I am sure Black Wings had planned this way in advance. If you think of it that way, even acquiring the Seal Stone of Victoria Island becomes quite a success story. More importantly, please take this skill.");
	    break;
	case 5:
	    qm.forceCompleteQuest();
	    qm.safe_teachSkill(21100004, 0, -1);
	    qm.showWZUOLEffect("Effect/BasicEff.img/AranGetSkill");
	    qm.sendOk("Right now, the most important thing here is for you to get stronger. Mr truth and I will look further info Seal Stones, and you just concentrate on mastering the #bCombo Smash#k");
	    qm.dispose();
	    break;
    }
}