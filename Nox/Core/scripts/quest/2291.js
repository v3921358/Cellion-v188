/* 
	NPC Name: 		Junny
	Map(s): 		Kerning Square: 7th Floor 8th Floor Area A
	Description: 		Quest - Admission to the VIP Zone
*/
var status = -1;

function start(mode, type, selection) {
    qm.dispose();
}

function end(mode, type, selection) {
    if (qm.getQuestStatus(2291) == 0 || qm.getQuestStatus(2291) == 2) {
        qm.forceStartQuest();
    } else {
        if (qm.haveItem(4032521, 10)) {
            qm.gainItem(4032521, -10);
            qm.forceCompleteQuest();
            qm.sendNext("Perfect. You are now a verified VIP member of the Kerning Square!");
            
            // Set quest record... the marker for entry eligiblity.
            qm.setAndUpdateQuestRecord(150050, "1");
        } else {
            qm.sendNext("Fine, then I want you gather up #b10 VIP tickets#k through monsters in Kerning Square. Then, I will give you access to the VIP zone for 10 minutes. Remember, 5 tickets does not equate to 5 minuites. Only 10, okay");
        }
    }
    qm.dispose();
}