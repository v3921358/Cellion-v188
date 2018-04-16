/*
 *  Gaga Relic , Artifact reward
 */

var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    if (status == 0) {
	qm.sendNext("Wow, you have hit another 10,000 points. As I give out gifts for every 10,000 additional points, you are immediately eligible for it! Are you ready?");
    } else if (status == 1) {
	var record = qm.getQuestRecord(10310);
	var data = record.getInfoData();
	if (data == null) {
	    data = "0";
	}
	var pt = parseInt(data);
	
	var usedrecord = qm.getQuestRecord(10358);
	var useddata = usedrecord.getInfoData();
	if (useddata == null) {
	    useddata = "0";
	}
	var usedpt = parseInt(useddata);
	//
	var boxes = parseInt((pt - usedpt) / 10000);
	if (boxes > 0) {
	    if (qm.getNumFreeSlot(2) >= boxes / 100) { // 100 box in a single slot.
		usedrecord.setInfoData(pt.toString());
		qm.forceStartQuest(10359, "0");
		qm.gainItem(2028075, boxes);
	    
		qm.sendNext("Did you receive my gift? If you still haven't received the gift event after hitting 10000pts, then come back to me.");
	    } else {
		qm.sendNext("Please make space in your USE inventory.");
	    }
	} else {
	    qm.forceStartQuest(10359, "0");
	}
	qm.dispose();
    }
}

function end(mode, type, selection) {
    qm.dispose();
}