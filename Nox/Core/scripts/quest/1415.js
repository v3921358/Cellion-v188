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
	    qm.sendNextPrev("The Arch Mage (Fire, Poison) job branch is renowned for having a lot of DoT (damage over time) with skills such as Poison Breath, Poison Mist, and Paralyze, combining the elements of Fire and Poison, and their signature screen clearing Mist Eruption, which explodes any mists set up by Poison Mist or Flame Haze. Their job specialty skill is Elemental Drain, which gives extra damage based on the number of different DoT stacks one places on monsters. Their second job is Wizard (Fire, Poison), their third job is Mage (Fire, Poison), and their fourth job is Arch Mage (Fire, Poison).");
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
			qm.sendOk("Congratulations you're now a Wizard of Fire & Poison!");
            qm.changeJob(210);
			//qm.gainSp(3);
			qm.forceCompleteQuest();
            qm.dispose();
	    }
	}
	}
}