// keroben - Dragon master

var status = -1;

function action(mode, type, selection) {
    if(mode == 1) {
	status++;
    } else {
	if (status == 1) {
	    cm.sendOk("What's the matter? Are you scared? Haha... I heard you were extremely strong, but I guess the rumors aren't true.?");
	    cm.dispose();
	    return;
	}
	status--;
    }
    switch (status) {
	case 0:
	    cm.sendNext("Haha... I suppose you are the new Dragon Master that everyone in Maple World has been buzzing about? Indeed, you have a very powerful-looking dragon with you. I doubt that it's enough to defeat the Dragon in Leafre, though.");
	    break;
	case 1:
	    cm.sendYesNo("Would you like to challenge yourself to a test? Let's see how you compare to the Dragon in Leafre. If you're up for the challenge, I'll let you in to the Forest of Confrontation.");
	    break;
    }
}