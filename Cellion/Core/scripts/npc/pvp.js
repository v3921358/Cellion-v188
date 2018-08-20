var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
} 

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else if (mode == -1)
        status--;
    else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        cm.sendSimple("So you want to fight in the pvp. \r\n Select a player you want to fight! \r\n" + cm.getBossPvp());
        cm.dispose();
    }
}