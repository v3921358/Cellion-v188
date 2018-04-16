/*
 * Start Gaga Relic treasure [Gaga's Lost Glasses]
 */

var status = -1;

function start(mode, type, selection) {
    qm.dispose();
}

function end(mode, type, selection) {
    if (qm.getQuestStatus(10317) == 0) {
	qm.sendOk("Manji can be found at Perion. What are you waiting for?");
	qm.forceStartQuest();
    } else {
	qm.sendOk("Is that an #bUnknown Relic#k that Gaga is looking for? Here's a token of appreciation for your help.\r\n#fUI/UIWindow.img/QuestIcon/8/0# +5%");
	if (qm.haveItem(4001309)) {
	    qm.removeAll(4001309);
	}
	qm.gainExp_Percentage(5);
	qm.forceCompleteQuest();
    }
    qm.dispose();
}