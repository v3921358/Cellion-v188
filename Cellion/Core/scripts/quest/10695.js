/*
	NPC Name: 		Maple Admin
	Description: 		MapleStory's Family Loyality Forever
FamilyLoyality_7_Quest =	10696, // 7th anniversary = tracking info
*/

var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    if (status == 0) {
	qm.sendNext("Hello, dear adventurer. It's MapleStory's 7th anniversary! MapleStory has grown and flourished because you never forgot about us.");
    } else if (status == 1) {
	qm.sendNextPrev("If you complete the Attendance Check Board by 15th May, we will give you a medal to show our appreciation. \r\n\r\nFor the Attendance Stamp, you can receive one per day, everyday. \r\nIf you attend for 7 days we will give you the #i1142217:# . \r\nIf you attend for 14 days we will give you the #i1142218:#.");
    } else if (status == 2) {
	qm.sendNextPrev("#i3800016#\r\n1 time(s) is your attendance count. The event will continue until 15th May.");
    } else if (status == 3) {
	qm.sendNextPrev("Also, if you come to me during the event period, you can check your attendance record.");
    } else {
	qm.forceCompleteQuest();
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