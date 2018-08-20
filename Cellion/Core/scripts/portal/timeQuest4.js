function enter(pi) {
    if(pi.isQuestFinished(3504))
        pi.warp(270010500, 7);
    else
        pi.getPlayer().dropMessage(5, "You don't have the permission to go further down the path of time.");
    pi.dispose();
}