/* Divine bird NPC chat
 */

var status = -1;

function action(mode, type, selection) {
    if (mode == 0) {
	status--;
    } else {
	status++;
    }
    switch (status) {
	case 0:
            cm.setAllowConversationCancel(false); // dont allow this to be cancelled by the client
            
	    cm.sendNext("..!!");
	    break;
	case 1:
	    cm.sendNextPrev("Divine bird? Why? It seems like he was trying to say something... I better head deeper into the future. What could Divine bird have been trying to say?", -1, true);
	    break;
	case 2:
	    cm.forceCompleteQuest(31102);
//	    cm.forceStartQuest(31102, "end");
	    cm.dispose();
	    break;
    }
}