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
	    qm.sendNextPrev("The Arch Mage (Ice, Lightning) job branch focuses on combining the elements of Ice and Lightning to deal extra damage to monsters. Their job specialty skill is Freezing Crush, which allows one to deal extra Lightning damage on monsters frozen by Ice skills. Ice skills can slow down the enemy, with a maximum of 5 stacks; the more stacks, the higher the damage boost for Lightning skills. Their second job is Wizard (Ice, Lightning) third job is Mage (Ice, Lightning), and their fourth job is Arch Mage (Ice, Lightning).");
	} else if (status == 2) {
	    qm.askAcceptDecline("So, do you want to test your skills against strong enemies, and see if you have what it takes? All you need is 30 Dark Marbles from those monsters! Lets go.");
		qm.forceStartQuest();
	} else if (status == 3) {
	    if (!qm.haveItem(4031013, 30)) {
                qm.warp(910140000);//mage test
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
			qm.sendOk("Congratulations you're now a Wizard of Ice & Lightning!");
            qm.changeJob(220);
			//qm.gainSp(3);
			qm.forceCompleteQuest();
            qm.dispose();
	    }
	}
	}
}