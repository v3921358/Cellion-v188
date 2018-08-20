function enter(pi) {
    if(pi.isQuestFinished(3511))
        pi.warp(270020500, 3);
    else
        pi.getPlayer().dropMessage(5, "You don't have the permission to go further down the path of time.");
    pi.dispose();
}