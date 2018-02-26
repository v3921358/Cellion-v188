function start(mode, type, selection) {
	qm.sendOk("~~");
    qm.forceStartQuest(25835);
    qm.dispose();
}

function end(mode, type, selection) {
	qm.sendOk("~~");
    qm.gainItem(200002,40);
	qm.gainEXP(800);
	qm.forceCompleteQuest(25835);
	qm.dispose();
}
