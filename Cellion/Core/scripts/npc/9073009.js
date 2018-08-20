function start() {
    var status = cm.getQuestStatus(1628);
    
    if (status == 0) {
        cm.sendNext("It looks like there's nothing suspecious in the area.");
    } else if (status == 1) {
        cm.forceCompleteQuest(1628);
		cm.gainExp(77790);
		cm.gainItem(1112677, 1);
		cm.gainItem(2000025, 50);
		cm.gainItem(2000028, 50);
        cm.sendNext("A Bomb has been spotted! Better report to #p2144003#.");
    } else if (status == 2) {
        cm.sendNext("The shadow has already been spotted. Better report to #p2144003#.");
    }
    cm.dispose();
}
function action(mode, type, selection) {
    cm.dispose();
}