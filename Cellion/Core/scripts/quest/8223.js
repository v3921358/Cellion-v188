/*
	NPC Name: 		Lukan
	Description: 		Quest - Storming the castle
*/

function start(mode, type, selection) {
    if (qm.getQuestStatus(8223) == 0 ) {
	qm.forceStartQuest();
	qm.dispose();
    }
}

function end(mode, type, selection) {
    qm.dispose();
}