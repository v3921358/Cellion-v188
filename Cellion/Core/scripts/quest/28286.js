function start(mode, type, selection) {
    qm.forceStartQuest();
    qm.dispose();
}

function end(mode, type, selection) {
    if (qm.getQuestStatus(28286) == 0) {
	qm.forceStartQuest();
    } else {
	if (qm.haveItem(4032446, 31)) {
	    qm.gainItem(4032446, -31);

	    var itemlist = new Array(2022256, 2022245, 2210043);

	    qm.gainItem(itemlist[Math.floor(Math.random() * itemlist.length)], Math.floor(Math.random() * 10));

	    qm.forceCompleteQuest();
	}
    }
    qm.dispose();
}