var status = -1;

function start(mode, type, selection) {
	qm.forceStartQuest();
		qm.forceCompleteQuest();
	qm.warp(120040300,0);
	qm.dispose();
}
function end(mode, type, selection) {
	qm.forceCompleteQuest();
		qm.forceCompleteQuest();
	qm.warp(120040300,0);
	qm.dispose();
}
