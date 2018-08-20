/*
 *  Gaga Relic , Acquire 1000 points
 */

var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    if (status == 0) {
	qm.sendNext("#b#h ##k you're truly talented. It's not easy earning over 1000 pts. Well, let me see if I can help you get more points in less time! You will earn 2X points until you reach 10000pts.t doesn't overlap with the Weekend 2X, but I hope this will help you reach your goal.");
    } else if (status == 1) {
	qm.sendNextPrev("I will cast a magic spell on you and give you a boost in your attacks to celebrate your 1000hit. And you will be awarded the Participation Prize if you earn over 5000 pts.Good luck!");
    } else {
	qm.useItem(2022562);
	qm.forceCompleteQuest();
	qm.dispose();
    }
}

function end(mode, type, selection) {
    qm.dispose();
}