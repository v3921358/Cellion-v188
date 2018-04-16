/*
 * Secret Recipe for Knight Virtu quest
 */

function action(mode, type, selection) {
    if (cm.canHold(2430370)) {
	cm.gainItem(2430370, -1); 
	cm.gainItem(2028062,3);
    } else {
	cm.playerMessage("You need 1 USE inventory slot to open this.");
    }
    cm.dispose();
}