/* Ria
	lolcastle NPC
*/

var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    /*if (cm.getMapId() != 101000000) {
	cm.dispose();
	return;
    }*/
    if (mode == 0) {
	cm.sendOk("Alright, see you next time.");
	cm.dispose();
	return;
    }
    status++;
    if (cm.getMapId() == 931050431 || cm.getMapId() == 931050432 || cm.getMapId() == 931050410) {
    	if (status == 0) {
    		cm.sendYesNo("Do you wish to leave the #rField of Judgement#k now?");
    	} else if (status == 1) {
    		cm.warp(910000000, 0);
    		cm.dispose();
    	}
    } else {
    if (status == 0) {
	cm.sendNext("I am Spiegameman ( Field Of Judgement ). For a small fee of #b1000000 meso#k I can send you to the #rField of Judgement#k.");
    } else if (status == 1) {
	cm.sendSimple("#rField of Judgement#k is currently down.");
    } else if (status == 2) {
	var em = cm.getEventManager("lolcastle");
	if (em == null || !em.getProperty("entryPossible").equals("true")) {
	    cm.sendOk("Sorry, but #rField of Judgement#k is currently closed.");
	} else if (cm.getMeso() < 1000000) {
	    cm.sendOk("You do not have enough meso.");
	} else if (cm.getPlayerStat("LVL") < 100) {//21
	    cm.sendOk("You have to be at least level 100 to enter #rField of Judgement.#k");
	} 
	/*else if (cm.getPlayerStat("LVL") >= 21 && cm.getPlayerStat("LVL") < 31) {//21-31
	    cm.gainMeso(-1000000);
	    em.getInstance("lolcastle1").registerPlayer(cm.getChar());
	} else if (cm.getPlayerStat("LVL") >= 61 && cm.getPlayerStat("LVL") < 101) {//31-51
	    cm.gainMeso(-1000000);
	    em.getInstance("lolcastle2").registerPlayer(cm.getChar());
	} */
	else if (cm.getPlayerStat("LVL") >= 100 && cm.getPlayerStat("LVL") < 151) { //51-71
	    cm.gainMeso(-1000000);
	    em.getInstance("lolcastle3").registerPlayer(cm.getChar());
	} else if (cm.getPlayerStat("LVL") >= 151 && cm.getPlayerStat("LVL") < 201) { //71-91
	    cm.gainMeso(-1000000);
	    em.getInstance("lolcastle4").registerPlayer(cm.getChar());
	} else {
	    cm.gainMeso(-1000000);
	    em.getInstance("lolcastle5").registerPlayer(cm.getChar());
	}
	cm.dispose();
    }
    }
}