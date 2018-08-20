/*
 * Unknown Relic - Gaga's relic event.
 */

function action(mode, type, selection) {
    if (cm.haveItem(2430010)) {
	cm.removeAll(2430010);
    }
    if (cm.haveItem(4001301)) {
	var record = cm.getQuestRecord(10310);
	var data = record.getInfoData();
	if (data == null) {
	    data = "0";
	}
	var pt = parseInt(data);
	//
	var usedrecord = cm.getQuestRecord(10358);
	var useddata = usedrecord.getInfoData();
	if (useddata == null) {
	    useddata = "0";
	}
	var usedpt = parseInt(useddata);
	
	var gain = 0;
	var rand = Math.random() * 100;
	if (rand < 5) {
	    gain = 80;
	} else if (rand < 12) {
	    gain = 12;
	} else if (rand < 25) {
	    gain = 8;
	} else {
	    gain = 40000;
	}
	pt += gain;
    
	if (cm.getQuestStatus(10329) == 0) {
	    pt = 1;
	    gain = 1;
	}
	if (pt - usedpt >= 10000) {
	    cm.forceStartQuest(10359, "1");
	}
	cm.setAndUpdateQuestRecord(10310, pt.toString());
	cm.showTitleMsg("[2X] Excavation Point(+"+gain+"pts) : "+pt+" / 100000");
    } else {
	cm.playerMessage("You have not received Gaga's Excavation Permit. You have been accused of being an Artifact Thief and you will not gain any points.");
    }
    //
    var infoquest = cm.getInfoQuest(10357);
    if (infoquest.equals("")) {
	cm.updateInfoExQuestData(10357, "s100k=0;g10k=0;p10k=0;s10k=0;s30k=0;s40k=0;s50k=0;s500=0;s1k=0;s5k=0");
    }
    var infodate = cm.getInfoQuest(10323);
    if (infodate.equals("")) {
	cm.updateInfoExQuestData(10323, "abuse=0;day=0;date=11/12/11/22/11;buff=0");
    }
    cm.dispose();
}