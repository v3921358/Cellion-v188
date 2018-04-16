/*
	NPC Name: 		Big Headward
	Map(s): 		Henesys Ruins
 */

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    if (status == 0) {
        cm.setAllowConversationCancel(false); // dont allow this to be cancelled by the client
        
	cm.sendNext("Aren't you.. Big Headward? The hair stylist?!\r\n(The size of his head certainly hasn't changed..) I-I'm no one suspicious...", -1, true);
    } else if (status == 1) {
	cm.sendNextPrev("How'd you know I used to be a hair stylist? In any case, it's go see Chief Alex...");
    } else if (status == 2) {
	cm.sendNextPrev("Chief Alex?\r\n(It couldn't be that kid, could it?)", -1, true);
    } else if (status == 3) {
	cm.sendNextPrev("Hurry up! Move!");
    } else {
	cm.forceStartQuest(31103);
	cm.warp(271010000,2);
	cm.dispose();
    }
}