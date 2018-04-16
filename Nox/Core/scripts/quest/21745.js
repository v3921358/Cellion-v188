var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendNext("What do you want here?! This is the Training Center. Keep your attitude in check!");
	    break;
	case 1:
	    qm.sendNextPrev("#bI'd like to meet Mu Gong, and it's very, very important.", -1, true);
	    break;
	case 2:
	    qm.sendNextPrev("Then you must take on the whole Mu Lung Training Center! Mu Gong is on the very top floor, awaiting the challengers!");
	    break;
	case 3:
	    qm.sendNextPrev("#b...hey, is there a way we can settle this behind closed doors...?", -1, true);
	    break;
	case 4:
	    qm.sendNextPrev("Nonsense! What are you saying! How dare you suggest such a dastardly way to reach the top!");
	    break;
	case 5:
	    qm.sendNextPrev("#b...one word, bellflower.", -1, true);
	    break;
	case 6:
	    qm.sendNextPrev("...Hmph...");
	    break;
	case 7:
	    qm.sendNextPrev("#bBellflower. The best your body can get.", -1, true);
	    break;
	case 8:
	    qm.sendNextPrev("...No... no... I may not. I may not.");
	    break;
	case 9:
//	    qm.forceStartQuest();
qm.forceCompleteQuest();
	    qm.sendNextPrev("...Hmmm. Humm.. Hmm. Hmmph. Follow me...");
	    qm.dispose();
	    break;
    }
}

function end(mode, type, selection) {
}