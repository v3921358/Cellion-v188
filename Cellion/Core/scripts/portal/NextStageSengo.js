function enter(pi) {
    if (!pi.haveMonster(9410217)) {
		pi.playerMessage("Defeat your opponent before trying to move on.");
    } else {
    	var map = pi.getMapId();
		pi.redLeaf2_NextMap(map);
		pi.gainItem(4310075, 2);
    }
}