/*
 * NPC : Tru
 */

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    if (status == 0) {
	//	cm.forceCompleteQuest(21733);
	//	cm.sendOk("Skipped for now.");
	//	cm.dispose();
	if (cm.haveMonster(9300382)) {
	    cm.sendNext("They... just ambushed me, and... don't worry about me, just eliminate him first!");
	} else if (cm.haveMonster(9300345)) {
	    cm.sendNext("Please defeat them!!!");
	    cm.dispose();
	} else {
	    cm.sendNext("What... were you able to defeat them? Wow... of course, you're Aran he Herp. Hmph... let's clean this place up first.");
	    status = 9;
	}
    } else if (status == 1) { // todo, display black magician image instead
        cm.setAllowConversationCancel(false); // dont allow this to be cancelled by the client
        
	cm.sendNext("Hmmm, hard to believe you really came. Not bad. Your morphing skill comes in handy every once in a while, no? Baroque, you can leave now.", 1204001, false);
    } else if (status == 2) { // todo, display morph image instead
	cm.sendNextPrev("You owe me one..", 1204004, false);
	cm.killAllMob();
    } else if (status == 3) {
	cm.sendNextPrev("Good thing you came. Last time, I lost because I expended all my energy battling the Cygnus Knights. This time, that will not be the case. Prepare to die!", 1204001, false);
    } else if (status == 4) {
	cm.spawnMob(9300345, 140, 120);
	cm.removeNpc(910510000, 1204003);
	cm.dispose();
    } else if (status == 10) {
	cm.forceStartQuest(21762, "2");
	cm.warp(104000004,1);
	cm.dispose();
    }
}