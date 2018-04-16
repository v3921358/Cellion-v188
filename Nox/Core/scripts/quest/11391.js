/*
 * Find the treasure map!
 */

var status = -1;

function start(mode, type, selection) {
}

function end(mode, type, selection) {
    if (qm.getQuestStatus(11391) == 0) {
	qm.forceStartQuest();
    } else {
	if (qm.haveItem(3994358) && qm.haveItem(3994357) && qm.haveItem(3994356) && qm.haveItem(3994355) && qm.haveItem(3994354) && qm.haveItem(3994353)) {
	    var record = qm.getQuestRecord(11397);
	    var data = record.getInfoData();
	    if (data == null) {
		data = "0";
	    }
	    var intdata = parseInt(data) +1;
	    
	    qm.gainItem(3994358,-1);
	    qm.gainItem(3994357,-1);
	    qm.gainItem(3994356,-1);
	    qm.gainItem(3994355,-1);
	    qm.gainItem(3994354,-1);
	    qm.gainItem(3994353,-1);
	    
	    qm.forceStartQuest(11397, intdata.toString());
	    qm.forceCompleteQuest();
	    qm.sendNext("Find me again when you have found more <#bPirate's King Treasure Map#k>!");
	    qm.dispose();
	}
    }
    qm.dispose();
}