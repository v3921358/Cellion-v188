/*
 * Evolving belt upgrade item
 */

function action(mode, type, selection) {
    var use = false;
    for (var i = 1132116; i <= 1132121; i++) {
	if (cm.haveItem(i)) {
	    cm.gainItem(2430501, -1); 
	    cm.gainItem(i, -1);
	    cm.gainItem(i + 1, 1);
	    cm.playerMessage(6,"The Evolving belt has been upgraded!");
	    use = true;
	    break;
	}
    }
    if (!use)
	cm.playerMessage("Nothing has happened.");
    cm.dispose();
}