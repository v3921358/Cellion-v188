var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            qm.sendAcceptDecline("(Activate the Music Box to play a gentle melody.)");
        } else if (status == 1) {
            qm.sendOk("(Serene music fills the town. May your people find peace in their dreams.)");
            qm.forceCompleteQuest();
            qm.dispose();
        }
}