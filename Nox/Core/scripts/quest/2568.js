var status = -1;

function start(mode, type, selection) {

    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        qm.sendAcceptDecline("You're back! Great. I got the Ignition Device all hooked up, so we can get back to civilization. Nothing left to do here, right? Let's roll!");
    } else if (status == 1) {
        qm.forceStartQuest();
        qm.warp(912060200, 2);
        qm.dispose();
    }
}
