function start(mode, type, selection) {
    qm.forceStartQuest();
    qm.dispose();
}

function end(mode, type, selection) {
    if (qm.getQuestStatus(10594) == 0) {
	qm.forceStartQuest();
    }
    qm.dispose();
}