var status = -1;

function start(mode, type, selection) {
    qm.sendNext("I will instantly warping you towards Cubrock's Hideout. To Restart this quest forfeit it.");
	qm.forceStartQuest();
    qm.warp(600050000,0);
	qm.dispose();
}
function end(mode, type, selection) {
	qm.forceCompleteQuest();
	qm.dispose();
}
