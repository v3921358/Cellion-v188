function start(mode, type, selection) {
    qm.playerMessage(5, "You have earned the title <I am a lucky guy!>");
    qm.showTitleMsg("You have earned the title <I am a lucky guy!>");
    qm.forceCompleteQuest();
    qm.dispose();
}

function end(mode, type, selection) {
    qm.dispose();
}