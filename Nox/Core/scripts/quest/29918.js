function start(mode, type, selection) {
    qm.playerMessage(5, "You have earned the title <Perion Donor Medal>");
    qm.showTitleMsg("You have earned the title <Perion Donor Medal>");
    qm.forceCompleteQuest();
    qm.dispose();
}

function end(mode, type, selection) {
    qm.dispose();
}