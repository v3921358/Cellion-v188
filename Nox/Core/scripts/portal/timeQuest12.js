function enter(pi) {
    if(pi.isQuestFinished(3516))
        pi.warp(270030300, 4);
    else
        pi.getPlayer().dropMessage(5, "You don't have the permission to go further down the path of time.");
    pi.dispose();
}