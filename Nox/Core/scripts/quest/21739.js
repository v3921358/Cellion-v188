var status = -1;

function end(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	qm.sendNext("Hmmm? You didn't get to face the instruder? Well then that must be taken care of first.");
	qm.dispose();
	return;
    }
    switch (status) {
	case 0:
	    if (qm.getQuestStatus(21739) == 0) {
		qm.forceStartQuest();
		qm.dispose();
	    } else {
		qm.sendYesNo("Did you wind up preventing the instruder? But you don't look too happy for someone that did just that. What? You lost the Seal Stone?");
	    }
	    break;
	case 1:
            qm.setAllowConversationCancel(false); // dont allow this to be cancelled by the client
            
	    if (qm.getQuestStatus(21739) == 1) {
		qm.forceCompleteQuest();
		qm.gainExp(29500);
	    }
	    qm.sendNext("Really... so you wind up losing the Seal Stone? In that case, there's nothing we can do about it. I don't know exactly what the Seal Stone is for, either. Just because it is missing, doesn't mean #bOrbis is in imminent danger#k. That one, I'm sure.");
	    break;
	case 2:
	    qm.sendNextPrev("But it does feel like a prelude to something catastrophic, perhaps.It's just borne out of instanct, no fortune telling required. I hereby wish you good luck, because you'll need it.");
	    break;
	case 3:
	    qm.sendPrev("#b(I lost the Seal Stone of Orbis. What should I do/ I better talk to Tru.)",-1,true);
	    break;
	case 4:
	    qm.dispose();
	    break;
    }
}