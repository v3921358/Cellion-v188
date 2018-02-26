function enter(pi) {
    if (pi.isQuestActive(20827)) {
        pi.playPortalSE();
        pi.warp(130030102, 0);
        pi.playerMessage(-9, "Go to the Physical Training Yard.");
    } else {
        pi.playerMessage(-9, "The course is'nt the ended.");
    }
}