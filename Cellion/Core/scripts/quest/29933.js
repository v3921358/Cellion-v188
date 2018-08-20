function start(mode, type, selection) {
    qm.playerMessage(5, "You have earned the title <King of Donation in Lith Harbor>");
    qm.showTitleMsg("You have earned the title <King of Donation in Lith Harbor>");
    qm.forceCompleteQuest();
    qm.dispose();
}

function end(mode, type, selection) {
    qm.dispose();
}