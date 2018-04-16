var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 4) {
	    qm.sendNext("In that case, there's nothing we can do about it. If you change your mind, let me know. I do need someone to look into the Green Mushrooms...");
	    qm.dispose();
	    return;
	}
	status--;
    }
    if (status == 0) {
	qm.sendNext("I don't know how you knew, but you're right. The #o1110100#s in the South Forest have been becoming more and more vicious. It's so strange that they've become so evil.");
    } else if (status == 1) {
	qm.sendNextPrev("But according to the rumors, things like this are happening in other places, as well. So, I looked into it a little more and found out that it has to do with a puppet. Isn't that weird?");
    } else if (status == 2) {
	qm.sendNextPrev("I don't know if the rumors are true, but it may have to do with #o1110100#s this time, too. I know you're curious about why the #o1110100#s have become so vicious, so could you join me in the investigation?");
    } else if (status == 3) {
	qm.askAcceptDecline("I need to find out if the reason the #o1110100#s have changed is because of a puppet. Please defeat #r25 #o1110130#s#k and find the #b#o1110130# Puppet#k.");
    } else if (status == 4) {
	qm.sendYesNo("Thank you. If I use my magic, I can send you to the Secret Forest, where the #o1110100#s live. Would you like to go there now?");
    } else if (status == 5) {
	qm.forceStartQuest();
	qm.warp(910100002,0);
	qm.dispose();
    }
}

function end(mode, type, selection) {
    qm.dispose();
}