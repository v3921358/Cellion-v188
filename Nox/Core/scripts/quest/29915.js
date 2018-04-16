function start(mode, type, selection) {
    qm.playerMessage(5, "You have earned the title <King of Mu Lung Training Center>");
    qm.showTitleMsg("You have earned the title <King of Mu Lung Training Center>");
    qm.forceCompleteQuest();
    qm.dispose();
}

function end(mode, type, selection) {
    qm.dispose();
}