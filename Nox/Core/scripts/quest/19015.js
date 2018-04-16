function start(mode, type, selection) {
    qm.playerMessage(5, "You have earned the title <Title - 2011 Mystical Artifact Pioneer>");
    qm.showTitleMsg("You have earned the title <Title - 2011 Mystical Artifact Pioneer>");
    qm.forceCompleteQuest();
    qm.dispose();
}

function end(mode, type, selection) {
    qm.dispose();
}