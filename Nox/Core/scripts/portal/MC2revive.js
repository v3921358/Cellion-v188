function enter(pi) {
    var map = pi.getMapId() - 100;
    if (pi.getCarnivalParty() == 0) {
	pi.warp(map, "red_revive" );
    } else {
	pi.warp(map, "blue_revive" );
    }
}