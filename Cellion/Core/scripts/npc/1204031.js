/* Aran quest : ???
*/
var status = -1;

function action(mode, type, selection) {
    if (mode == 0) {
	cm.dispose();
	return;
    } else {
	status++;
    }
    switch (status) {
	case 0:
	    cm.sendNext("Hmm... Just as I heard from the Gentleman... Some nerve you've got, coming here. Whatever, I already have the information I need.");
	    break;
	case 1:
	    cm.sendNextPrev("Since you're here. I can eliminate you once and for all. You've been getting in the way of the Black Wings. Prepare to die. Haha!");
	    break;
	case 2:
	    cm.removeNpc(910050000,1204031);
	    cm.spawnMob(9300355,-264,181);
	    cm.dispose();
	    break;
    }
}
