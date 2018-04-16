/*
	Former Dark Lord's Diary
*/
var status = -1;

function action(mode, type, selection) {
    if (!cm.haveMonster(9300216)) {
	cm.spawnMobEx(9001019, 10, 101,149,910350100);
	cm.spawnMob(9300216,101,149);
    } else {
	if (!cm.haveMonster(9001019) && !cm.haveItem(4032617)) {
	    cm.sendNext("You have obtained the Former Dark Lord's Diary. You better leave before someone comes in.");
	    cm.gainItem(4032617, 1);
	}
    }
    cm.dispose();
}
