var status = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status >= 1) {
	    status--;
	} else {
	    cm.dispose();
	    return;
	}
    }
    if (status == 0) {
	cm.sendNext("Are you ready to return to your original time period? The dimensional crack is currently open. Remember, this is the first time I've ever done this, so there's a possibility of failure. That being said, I am pretty confident this will work! I will make sure you get sent back to your original time period!");
    } else if (status == 1) {
	    cm.teachSkill(11121000, 0, 10);
		cm.teachSkill(13121000, 0, 10);
		cm.teachSkill(15121000, 0, 10);
		cm.dispose();
	}
	}
	}