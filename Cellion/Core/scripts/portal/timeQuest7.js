function enter(pi) {
    if(pi.isQuestFinished(3509))
        pi.warp(270020210, 4);
    else
        pi.getPlayer().dropMessage(5, "You don't have the permission to go further down the path of time.");
    pi.dispose();
}