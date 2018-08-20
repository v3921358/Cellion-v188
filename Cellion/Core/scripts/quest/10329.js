/*
 * Start Gaga Relic event
 */

var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendNext("Wow, you must have been eager to get started, seeing as you went ahead before I even said anything. But you know, you may be accused of being an Artifact Robber. You must have an Excavation Permit if you want to earn more points in the future.");
	    break;
	case 1:
	    qm.sendNextPrev("What do you mean?", -1,true);
	    break;
	case 2:
	    qm.sendNextPrev("Are you sure you really discovered this without knowing anything about the Artifact Hunt? How strange yet amazing. I've been getting into collecting ancient artifacts lately. But I can't give you any Excavation Points if you don't have an Excavation Permit.");
	    break;
	case 3:
	    qm.sendNextPrev("An artifact? Are you talking about this shiny rock?", -1,true);
	    break;
	case 4:
	    qm.sendNextPrev("Hey, I'm mildly offended! No one knows what's inside that. I'll give you points if you hunt monsters and collect shiny rocks. You'll be able to trade in your points for a reward.");
	    break;
	case 5:
	    qm.sendNextPrev("What kind of rewards are there?", -1,true);
	    break;
	case 6:
	    qm.sendNextPrev("If you hit 5000pts you'll get  #i01142305:# and if hit 10000, can get #i2028075:# No one knows what's inside.");
	    break;
	case 7:
	    qm.sendNextPrev("And if you get ranked in Top 10 you can achieve #i1142306:# so, way to go!!!!");
	    break;
	case 8:
	    qm.sendNextPrev("Ahhh~~~I almost forgot!! And if you hit 30000pts you'll acheive #i1003230:#, #i1012265:# for 40000pts, #i1022118:# (5 days) for 50000pts. And lastely if you hit 100000pts you can have one of it among these three without expiry date, So what do you say?Won't it be worth to challenge?");
	    break;
	case 9:
	    qm.sendNextPrev("Rankings are calculated on every Sundays between 5pm~6pm and Top 10 will be awarded. So earn many points as possible before then. Ah!! And don't for get the 2X point chance on weekends!! Good Luck!");
	    break;
	case 10:
	    if (qm.canHold(4001301)) {
		qm.gainItem(4001301,1,false,1000*60*60);
		qm.forceCompleteQuest();
	    } else {
		qm.sendOk("Please make sure you have an empty ETC slot to collect the permit from me again.");
	    }
	    qm.dispose();
	    break;
    }
}

function end(mode, type, selection) {
    qm.dispose();
}