var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 4) {
	    qm.sendNext("Hmmm.. you're busy? But I suggest you take care of the Black Wings situation first. It's more urgent.");
	    qm.dispose();
	    return;
	}
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendNext("Have you been diligently leveling up? I have found an interesting information regarding Black Wings. This time, you'll have to go quite a bit. Do you know of a town called #bMu Lung#k? You'll have to go all the way down there.");
	    break;
	case 1:
	    qm.askAcceptDecline("#bMr. Do#k of Mu Lung apparentely got in touch with Black Wings. I don't know how this came about, but I think I can trust this information. Please go there and find out why Black Wings contacted Mr. Do, and what excatly went on between the two.");
	    break;
	case 2:
	    qm.sendNextPrev("Hmm. So you;re saying that there was a Seal Stone for Orbis as well. That's valuable information. It stings that you wound up losing it, but.. no no, I'm not blaming you for it. I just think that Black Wings was ready for it this time.");
	    break;
	case 3:
	    qm.forceStartQuest();
	    qm.sendOk("Mr. Do has a strange way of talking, so you may have to talk to him with some patience. Talk to him using the keyword I #bhear you met one of the warriorsof Black Wings#k..");
	    break;
	case 4:
	    qm.dispose();
	    break;
    }
}

function end(mode, type, selection) {
    qm.dispose();
}