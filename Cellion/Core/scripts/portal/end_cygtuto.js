function enter(pi) {
    if (pi.isQuestActive(20839)) {
        pi.playPortalSE();
        pi.warp(130030006, 0);
        pi.playerMessage(-9, "Go to the Small Bridge.");
    } else {
        pi.playerMessage(-9, "The course is'nt the ended.");
    }
}