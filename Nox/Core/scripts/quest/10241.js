/*
 * Cassandra's Spring Flower
 */

var status = -1;

function end(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    switch (status) {
	case 0:
	    if (qm.getQuestStatus(10241) == 0 || qm.getQuestStatus(10241) == 2) {
		qm.forceStartQuest();
		qm.dispose();
	    } else 
		qm.sendNext("Ohhh! You brought the flower seeds! Time for spring bloom!");
	    break;
	case 1:
	    if (qm.canHold(2022530) && qm.haveItem(4032265,10) && qm.haveItem(4032270, 10)) {
		qm.removeAll(4032265); // Spring Flower Petal
		qm.removeAll(4032270); // Glistening Sunlight
		//		qm.gainItem(4032265,-10); // Spring Flower Petal
		//		qm.gainItem(4032270,-10); // Glistening Sunlight
		qm.forceCompleteQuest();
		
		var language, name;
		if (Math.random() * 100 < 90) {
		    qm.useItem(2022529);
		    qm.gainItem(2022526,1);
		    
		    name = "Azaleas";
		    language = "Love";
		} else if (Math.random() * 100 < 20) {
		    qm.useItem(2022530); // Forsythia
		    qm.gainItem(2022527,1);
		    
		    name = "Forsythia";
		    language = "Hope";
		} else {
		    qm.useItem(2022529); // Meaning of Clovers - 2x Item drop for 30 min
		    qm.gainItem(2022528,1);
		    
		    name = "Clovers";
		    language = "I promise";
		}
		qm.sendPrev("Look, the flower's blooming!! The flower language for "+name+" is "+language+"!\r\n#b I'll give you a spell that matches the flower of "+name+"#k and its #bflower language#k!\r\nOh, and make sure to keep gathering up the flower seeds!");
	    } else {
		qm.sendNext("Make space in your USE inventory for something amazing!");
	    }
	    qm.dispose();
	    break;
    }
}