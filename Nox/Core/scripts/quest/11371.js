/*
	NPC Name: 		Gaga
	Description: 		Find my Monkey dolls!
*/

var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    if (status == 0) {
	var level = qm.getPlayerStat("LVL");
	qm.sendNext("#b#h0##k, since your level is "+level+", you can obtain #t03994360#s from #rLv."+(level - 10)+"+ monsters#k. Now, make sure to bring my Monkey Dolls back to me. I will be waiting, with #b#t02430395##k and #b#t02430396##k for you.");
    } else if (status == 1) {
	qm.forceStartQuest();
	qm.dispose();
    }
}

function end(mode, type, selection) {
    if (qm.getQuestStatus(11371) == 0) {
	qm.forceStartQuest();
    } else {
	if (qm.haveItem(3994360,30) && qm.canHold(2430395) && qm.canHold(2430396)) {
	    var record = qm.getQuestRecord(11377);
	    var data = record.getInfoData();
	    if (data == null) {
		data = "0";
	    }
	    var intdata = parseInt(data) +1;
	    
	    qm.gainItem(3994360,-30);
	    qm.gainItem(2430395,1);
	    qm.gainItem(2430396,1);
	    
	    qm.forceStartQuest(11377, intdata.toString());
	    qm.forceCompleteQuest();
	    qm.sendNext("Find me again when you have found more <#bMonkey Doll#k>!");
	    qm.dispose();
	}
    }
    qm.dispose();
}