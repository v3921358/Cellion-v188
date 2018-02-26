
var status = -1;

function start(mode, type, selection) {
	if (mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
	    qm.sendMapleBookQuest("hi",9010010);
	    qm.forceStartQuest();
	    qm.forceCompleteQuest();//for now lel
	    qm.dispose();
	}
}

function end(mode, type, selection) {
	if (mode == 1)
	    status++;
	 else
	    status--;
	if (status == 0) {
	    qm.sendNextS("An #bExplorer Book#k? So, I can record all my adventures here?",16);
	} else if (status == 1) {
	    qm.sendNextPrevS("I guess it's been a while since I first started my journey. Looking back, I...",16);//not sure
	} else if (status == 3) {
	    qm.sendNextPrevS("#b#L0#Trained hard to level up.#l\r\n#L1#Worked hard to make money.#l\r\n#L2#Partied hard to make new friends.#l\r\n#L3#Didn't do much at all.#l",4);//not sure
	} else if (status == 4) {
	    qm.sendNextS("I trained myself hard and leveled up a lot. Though I still have a long way to go, I am definitely a lot stronger than before.",16);//not sure
	} else if (status == 5) {
	    qm.sendNextPrevS("Oh well, the past is past. I should focus on future. New adventures! New horizons! ...Huh?",16);//not sure
	} else if (status == 6) {
        qm.showMapleLeafScene();
        qm.forceStartQuest();
	    qm.forceCompleteQuest();
		qm.gainItem(2040804,1);
	    qm.dispose();
	}
}