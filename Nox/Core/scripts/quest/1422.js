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
	    qm.sendNextPrev("The Night Lord job branch is centered around using Throwing Stars alongside their primary weapon, Claws, and Charms as secondary weapons. They throw stars at enemies from afar to deal damage, with a new single-target attack in later job advancements. They have several attacks for dealing with multiple enemies, including Shuriken Burst, which tosses a flaming star at enemies, Gust Charm, which pushes enemies back, Shade Splitter, which uses 3 stars and has shadows attack enemies in front of the player, and Showdown, which deals significant damage and raises EXP and drop rate gained from enemies attacked. In 3rd job, they gain a skill called Shadow Partner, which mimics the player's attacks, using extra stars but dealing extra damage at a reduced rate. Their job specialty skill is Assassin's Mark, which launches randomly flying stars around to damage enemies while attacking. This skill is upgraded in 4th job with Night Lord's Mark, making it stronger. Their second job is Assassin, their third job is Hermit, and their fourth job is Night Lord.");
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
			qm.sendOk("Congratulations you're now an Assassin!");
            qm.changeJob(410);//sin
			//qm.gainSp(3);
			qm.forceCompleteQuest();
            qm.dispose();
	    }
	}
	}
}