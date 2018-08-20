function enter(pi) {
    if (pi.getMonsterCount(914030000) == 0) {
	pi.forceStartQuest(21613, null);
	pi.forceStartQuest(21620, "0")
	pi.warp(140010210, 0);
    } else {
	pi.playerMessage("There are still some monsters remaining.");
    }
}