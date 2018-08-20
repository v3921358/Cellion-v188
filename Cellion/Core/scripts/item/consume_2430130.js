/*
 * Resistance energy capsule
 */

function action(mode, type, selection) {
    if (cm.getJob() > 3000 && cm.getPlayerStat("LVL") >= 30) {
	cm.gainItem(2430130,-1);
	cm.gainExp(30000);
    } else {
	cm.playerMessage("This item can only be used by the Resistance member who are above Lv. 30.");
    }
    cm.dispose();
}