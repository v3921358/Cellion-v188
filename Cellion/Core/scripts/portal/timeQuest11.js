function enter(pi) {
    if(pi.isQuestFinished(3515))
        pi.warp(2700302000, 5);
    else
        pi.getPlayer().dropMessage(5, "You don't have the permission to go further down the path of time.");
    pi.dispose();
}