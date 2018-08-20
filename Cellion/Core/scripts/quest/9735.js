/*
	NPC Name: 		Joyce
	Description: 		Quest - Help out Gordon
*/

var status = -1;
var exp;

function start(mode, type, selection) {
}

function end(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    switch (status) {
	case 0:
	    if (qm.getQuestStatus(9735) == 0 || qm.getNpc() == 9270035) {
		qm.forceStartQuest();
		qm.dispose();
		return;
	    }
	    qm.sendNext("Did you bring all the materials I asked you to get? I understand that those materials are hard to get, but Gordon needs them to complete the face, so please get them for him! He'll be very happy to see those materials so he can finish the face!");
	    break;
	case 1:
	    exp = qm.getPlayerStat("LVL") * 800;

	    qm.sendNext("Oh wow, that was fast! I knew Maplers like you would come through with this! I'm going to give these to Gordon. Great work!\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0#"+exp+" exp");
	    if (qm.haveItem(4031935, 30)) {
		qm.gainItem(4031935,-30);
		qm.forceCompleteQuest();
		qm.gainExp(exp);
	    }
	    qm.dispose();
	    break;
    }
}