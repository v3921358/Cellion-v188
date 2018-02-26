var status = -1;

function start(mode, type, selection) {
    if (mode == -1) {
	qm.dispose();
    } else {
	if (mode == 1)
	    status++;
	else
	    status--;
	
	if (status == 0) {
	    qm.sendNext("Looks like you're ready for your next job advancement!\r\nJob advancements are available upon reaching level 30, 60, and 100.");
	} else if (status == 1) {
	    qm.sendNextPrev("Paladins specialize in Swords and Blunt Weapons as primary weapons, and either Shields or Rosaries as secondary weapons. They utilize the power of fire, ice, lightning, and holy elements to scar the battlefield. Their job specialty skill is Elemental Charge, which increases their damage as well as reduces the damage they take from enemies when chaining different elemental attacks together, giving them superior defensive abilities to their counterparts. Their second job is Page, their third job is White Knight, and their fourth job is Paladin.");
	} else if (status == 2) {
	    qm.askAcceptDecline("So, do you want to test your skills against strong enemies, and see if you have what it takes? All you need is 30 Dark Marbles from those monsters! Lets go.");
		qm.forceStartQuest();
	} else if (status == 3) {
	    if (!qm.haveItem(4031013, 30)) {
                qm.warp(910230000);//warrrior test
                qm.dispose();
	    }else {
		qm.dispose();
		}
            }
            }
            }

function end(mode, type, selection) {
    if (mode == -1) {
	qm.dispose();
    } else {
	if (mode == 1)
	    status++;
	else
	    status--;
	if (status == 0) {
	    if (qm.haveItem(4031013, 30) ) {
			qm.removeAll(4031013);
			qm.sendOk("Congratulations you're now a Page!");
            qm.changeJob(120);
			//qm.gainSp(3);
			qm.forceCompleteQuest();
            qm.dispose();
	    }
	}
	}
}