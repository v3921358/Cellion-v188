var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        qm.sendAcceptDecline("#b(You can see the wards to create the seal around Elluel. Speaking the magic word will finish the spell, cutting the village off from the outside world for at least 100 years. Active the seal?)");
    } else if (status == 1) {
        qm.sendOk("#b(The seal is complete, and the town is safe.)#k");
        qm.forceCompleteQuest();
        qm.dispose();
    }
}