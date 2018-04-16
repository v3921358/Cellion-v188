var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 6) {
	    qm.sentNext("If you don't want to, then oh well. I can't tell you anything about Sealed Garden beyond this. That place contains something that must not be......");
	    qm.dispose();
	    return;
	}
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendNext("What is it? You're not invited, and therefore not welcome, but... I feel a strange aura emanating from you, and I feel like I have to check out what you have to offer.");
	    break;
	case 1:
	    qm.sendNextPrev("#b(I told her about the Giant Nependeath.)", -1, true);
	    break;
	case 2:
	    qm.sendNextPrev("Giant Nependeath? Yes, it's a big problem, but.. I don't think it's enough to heavily affect Orbis yet. Wait, where did you say Giant Nependeath is again?");
	    break;
	case 3:
	    qm.sendNextPrev("Sparse Promenade", -1, true);
	    break;
	case 4:
	    qm.sendNextPrev(".. Sparse Promenade? If Giant Nependeath is there, then that must mean someone is trying to enter Sealed Garden. Who would that be, and why?");
	    break;
	case 5:
	    qm.sendNextPrev("Sealed Garden?", -1, true);
	    break;
	case 6:
	    qm.askAcceptDecline(".. I can't tell you why Sealed Garden is important. If you want to know, I'll have to see if you are worthy of the information. Do you mind if I look at your fate?");
	    break;
	case 7:
	    qm.forceStartQuest();
	    qm.sendOk("Well, now let's look at the mirror ball. Hold on one second.");
	    break;
	case 8:
	    qm.dispose();
	    break;
    }
}

function end(mode, type, selection) {
    qm.dispose();
}