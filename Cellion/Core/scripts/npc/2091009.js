// Aran - Catch the shadow warrior!

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    }

    if (status == 0) {
	cm.sendGetText("#b(Only the correct code will let me in.)");
    } else {
	var text = cm.getText();
	if (text.equals("action speaks louder than words")) {
	    if (cm.getPlayerCount(925040100) == 0) {
		cm.warp(925040100,1);
		cm.startSelfTimer(600,250020300);

		var map = cm.getMap(925040100);
		map.resetMap();
		cm.spawnNpc(1204020,907,51);
	    } else {
		cm.sendNext("#b(Someone is already inside, please try again later.)");
	    }
	} else {
	    cm.sendNext("#b(I think I mentioned the wrong code.)");
	}
	cm.dispose();
    }
}