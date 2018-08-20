/*
 * Alcaster Crystal
 */

function action(mode, type, selection) {
    if (cm.getMapId() == 211060400) {
	cm.gainItem(2430159,-1);
	cm.forceStartQuest(3182, "211060400");
    } else {
	cm.sendNext("Murt isn't in this map!");
    }
    cm.dispose();
}