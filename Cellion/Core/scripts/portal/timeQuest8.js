function enter(pi) {
    if(pi.isQuestFinished(3510))
        pi.warp(270020300, 6);
    else
        pi.getPlayer().dropMessage(5, "You don't have the permission to go further down the path of time.");
    pi.dispose();
}