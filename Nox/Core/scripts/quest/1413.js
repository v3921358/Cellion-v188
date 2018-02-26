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
	    qm.sendNextPrev("Dark Knights specialize in Spears and Polearms as primary weapons, and Iron Chains as secondary weapons. They use a variety of supportive buffs to support their party members, as well as their own. Their job specialty skill is Evil Eye, a dark spirit that heals, buffs, and attacks alongside the player. Later on, the dark spirit can be absorbed to give a boost to damage, as well as to allow the Gungnir's Descent attack to be used without a cooldown. They also can do this with the skill Final Pact, which can be activated when their HP reaches 0. However, the skill has a 10 minute cooldown, and if they do not eliminate 30 monsters or attack a boss 20 times within the time limit, they will lose the buff and die anyway. Their second job is Spearman, their third job is Berserker, and their fourth job is Dark Knight.");
	} else if (status == 2) {
	    qm.askAcceptDecline("So, do you want to test your skills against strong enemies, and see if you have what it takes? All you need is 30 Dark Marbles from those monsters! Lets go.");
		qm.forceStartQuest();
	} else if (status == 3) {
	    if (!qm.haveItem(4031013, 30)) {
                qm.warp(910230000);// warrior test
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
			qm.sendOk("Congratulations you're now a Spearman!");
            qm.changeJob(130);
			//qm.gainSp(3);
			qm.forceCompleteQuest();
            qm.dispose();
	    }
	}
	}
}