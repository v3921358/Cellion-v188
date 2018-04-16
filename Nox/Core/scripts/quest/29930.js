function start(mode, type, selection) {
    qm.playerMessage(5, "You have earned the title <Summer Girl 2010>");
    qm.showTitleMsg("You have earned the title <Summer Girl 2010>");
    qm.forceCompleteQuest();
    qm.dispose();
}

function end(mode, type, selection) {
    qm.dispose();
}