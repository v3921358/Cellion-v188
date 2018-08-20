function start(mode, type, selection) {
    var msg = "You have earned the title <Nautilus Donor Medal>";
    
    qm.playerMessage(5, msg);
    qm.showTitleMsg(msg);
    qm.forceCompleteQuest();
    qm.dispose();
}

function end(mode, type, selection) {
    qm.dispose();
}