/*function end(mode, type, selection) {
	qm.gainItem(1142499,1);
	qm.teachSkill(60010217,1,1); //True Heart Inheritance 3000018/3000132
	qm.teachSkill(60011005,1,1); //Exclusive Spell
    qm.forceCompleteQuest();
    qm.dispose();
}*/
/*var status = -1;
//this quest is POWER B FORE
function start(mode, type, selection) {
    qm.forceStartQuest();
    qm.forceCompleteQuest();
    qm.dispose();
}

function end(mode, type, selection) {
	if (!qm.canHold(1142499,1)) {
		qm.sendNext("Please make Equip space.");
		qm.dispose();
		return;
	}
    qm.forceCompleteQuest(25828);
	qm.gainItem(1142499,1);
	qm.teachSkill(60010217,1,0); //True Heart Inheritance 3000018/3000132
	qm.teachSkill(60011005,1,0); //Exclusive Spell
	qm.dispose();
}*/

var status = -1;

function start(mode, type, selection) {
	if (!qm.canHold(1142499, 1)) {
	    qm.sendOk("Please make some EQP/ETC space.");
	} else {
	   qm.gainItem(1142499,1);
	   qm.teachSkill(60010217,1,0); //True Heart Inheritance 3000018/3000132
	   qm.teachSkill(60011005,1,0); //Exclusive Spell
	   qm.forceCompleteQuest(25828);
	    qm.sendOk("Congratulation on reaching level 200!");
	    qm.forceCompleteQuest();
	}
	qm.dispose();
}
function end(mode, type, selection) {
	if (!qm.canHold(1142499, 1)) {
	    qm.sendOk("Please make some EQP/ETC space.");
	} else {
	   qm.gainItem(1142499,1);
	   qm.teachSkill(60010217,1,0); //True Heart Inheritance 3000018/3000132
	   qm.teachSkill(60011005,1,0); //Exclusive Spell
	   qm.forceCompleteQuest(25828);
	    qm.sendOk("Congratulation on reaching level 200!");
	    qm.forceCompleteQuest();
	}
	qm.dispose();
}