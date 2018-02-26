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
	    qm.sendNextPrev("The Shadower job branch, unlike Night Lords, uses Daggers as their primary weapon, and Dagger Scabbards or Shields as secondary weapons. Instead of attacking from afar, Shadowers focus on fighting monsters up close. They have a variety of attacks for fighting multiple mobs, including Savage Blow, Phase Dash, and Boomerang Stab, as well as a few attacks for one-on-one combat, including Midnight Carnival and Assassinate. In 3rd job, they gain a skill called Shadow Partner, which mimics the player's attacks, dealing extra damage at a reduced rate. In 4th job, they gain a buff called Shadower Instinct, which increases their attack as well as giving a new method of gaining damage called Body Count; this system allows the player to deal extra damage with Assassinate as well as give the player additional attack power when their Body Count is full and the buff is used.");
	} else if (status == 2) {
	    qm.askAcceptDecline("So, do you want to test your skills against strong enemies, and see if you have what it takes? All you need is 30 Dark Marbles from those monsters! Lets go.");
		qm.forceStartQuest();
	} else if (status == 3) {
	    if (!qm.haveItem(4031013, 30)) {
                qm.warp(910370000);//sin test
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
			qm.sendOk("Congratulations you're now an Bandit!");
            qm.changeJob(420);//bandit
			//qm.gainSp(3);
			qm.forceCompleteQuest();
            qm.dispose();
	    }
	}
	}
}