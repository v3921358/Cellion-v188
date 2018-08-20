/*
	NPC Name: 		Maple Admin
	Description: 		Quest - Elite Bowman Blessing
*/

var books, item;
var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 2) {
	    qm.sendNext("Do let me know if you need it.");
	    qm.dispose();
	    return;
	}
	status--;
    }
    if (status == 0) {
	qm.sendNext("Hello, #h0#. Have you heard about this big changes to bowmans recently? Well, we decided to give a little something special to those players who stuck with their bowman even through tough times. Would you like this gift?");
    } else if (status == 1) {
	books = [2290285, 2290294, 2290317, 2290318, 2290319, 2290320, 2290321];
	var str = "I've been giving out Mastery Books, you see. You can only get one, so choose wisely!#b\r\n";
	for (var i = 0; i < books.length; i++) {
	    str += "#L"+i+"##i"+books[i]+"# #t"+books[i]+"##l\r\n";
	}
	qm.sendSimple(str)
    } else if (status == 2) {
	if (selection >= 0 && selection <= books.length) {
	    item = books[selection];
	    qm.sendNext("Are you sure you want the following special mastery book? You can't change this!\r\n#i"+item+"# #t"+item+"##l");
	} else {
	    qm.dispose();
	}
    } else if (status == 3) {
	if (qm.canHold(item)) {
	    qm.forceCompleteQuest();
	    if (item == 2290285) {
		qm.gainItem(2290285,1);
	    } else {
		qm.gainItem(item,1,false,1000 * 60 * 60 * 24 * 30);
	    }
	}
	qm.dispose();
    }
}

function end(mode, type, selection) {
}