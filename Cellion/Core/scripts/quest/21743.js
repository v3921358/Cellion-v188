var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendNext("How can I help you? What? Are you asking me how to restore this #t4220151#? Let's see...how in the world did this get soaked and steamed in the boiling water? Only a dummy like #p2090004# would do such thing. Good thing the paper is very tightly wound, so the scroll should be fine.");
	    break;
	case 1:
	    qm.askAcceptDecline("Well, restoring #t4220151# is not impossible. As long as I have the special ink with me, it will be returned to normal. I can make #t4032342# myself, so all you'll need to do is get me the materials needed for one. Yes, I also do charge service fee.");
	    break;
	case 2:
	    if (qm.canHold(4032342)) {
		qm.forceStartQuest();
		qm.sendNext("Okay, here are 8 batches of #t4032342#. Now open up the #t4220151#, and spray #t4032342# all over the scroll to restore the content. If you happen to lose #t4032342#, then I'll make you another one, so please let him know of that.");
		qm.gainItem(4032342,8);
	    } else {
		qm.sendOk("Please check if you have enough inventory space.");
	    }
	    qm.dispose();
	    break;
    }
}

function end(mode, type, selection) {
}