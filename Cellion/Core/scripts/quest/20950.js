/* Cygnus revamp
	Call of Cygnus skill
    Made by Charmander
    Not GMS like!
*/

var status = -1;

function start(mode, type, selection) {
    qm.forceStartQuest();
    qm.dispose();
}

function end(mode, type, selection) {
    if (mode == 0) {
	if (status == 0) {
	    qm.sendNext("This is an important decision to make.");
	    qm.safeDispose();
	    return;
	}
	status--;
    } else {
	status++;
    }
    if (status == 0) {
	qm.sendYesNo("So, do you want to learn the skill #bCall of Cygnus?");
    } else if (status == 1) {
    	if(qm.getPlayer().getJob() == 1112){
    		qm.teachSkill(11121000, 0, 30);
    	}else if(qm.getPlayer().getJob() == 1312){
    		qm.teachSkill(13121000, 0, 30);
    	}else if(qm.getPlayer().getJob() == 1512){
    		qm.teachSkill(15121000, 0, 30);
    	}
	qm.sendNext("I have taught you the skill! Enjoy");
	qm.forceCompleteQuest();
	qm.safeDispose();
}
}