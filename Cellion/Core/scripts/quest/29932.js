function start(mode, type, selection) {
    qm.playerMessage(5, "You have earned the title <Protector of Pharaoh>");
    qm.showTitleMsg("You have earned the title <Protector of Pharaoh>");
    qm.forceCompleteQuest();
    qm.dispose();
}

function end(mode, type, selection) {
    qm.dispose();
}