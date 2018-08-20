function enter(pi) {
    if (pi.haveItem(4032473)) {
	pi.playerMessage("Place the Ominous Bone on the altar!");
    } else {
	pi.warp(914100010, 0);
    }
}