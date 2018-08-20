var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 6) {
	    qm.sendNext("Hmmm.. I was just going to make you do the work if you were interested, but you noticed that didn't you? You look like you'll survie in this world");
	    qm.dispose();
	    return;
	}
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendNext("Well, I am not really busy or anything, but I don't feel like making medicine or anything, so you can stop sometime later, or anything, so... if you don't mind, move or anything.");
	    break;
	case 1:
	    qm.sendNextPrev("I hear you met one of the warriors of Black Wings", -1, true);
	    break;
	case 2:
	    qm.sendNextPrev("Ahhh, you mean that guy dressed in pitch black with a meancing wrinkle on his forehead? Yes, I did, I did! I even held an item for him. He gave me this item, and asked me to deliver it to Mu Gong");
	    break;
	case 3:
	    qm.sendNextPrev("An Item?", -1, true);
	    break;
	case 4:
	    qm.sendNextPrev("Yes, #ba giant Picture Scroll#k. He just gave it to me, and asked me to deliver it to him. He had that meanacing look, and it looked like if I didn't deliver it to him, and the guy would actually chase me down. That was scary");
	    break;
	case 5:
	    qm.sendNextPrev("So did you deliver Picture Scroll to him?", -1, true);
	    break;
	case 6:
	    qm.askAcceptDecline("Well, the thing is... there's a slight problem. Care to listen?");
	    break;
	case 7:
	    qm.forceStartQuest();
	    qm.forceStartQuest(21763);
	    if (!qm.haveItem(4220151))
		qm.gainItem(4220151,1);
	    qm.sendNext("Okay so what happened was that I was making a new ingredient for medicine, so I filled up a pot with water and herb, and boiled it. That's when I made the mistake of putting Picture Scroll in it as well. I pulled it out as soon as I could, but Picture Scroll was already soaked, which meant all the writings are also gone");
	    break;
	case 8:
	    qm.sendPrev("In this case, what's the point of delivering it to Mu Gong? Anyway, we'll have to restore Picture Scroll first. That's why... I need you to do something for me. The guy down there wrote something on the Picture Scroll is the greateat painter in all of Mu lung, #bJin Jin#k. He might be good enough to restore Picture Scroll");
	    break;
	case 9:
	    qm.dispose();
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendNext("Hey, so how's the restoring of the scroll going on? What? Its complete? Then let's see what's on it.");
	    break;
	case 1:
	    if (qm.haveItem(4220151)) {
		qm.gainItem(4220151,-1);
		qm.forceCompleteQuest();
		qm.gainExp(43000);
	    }
	    qm.sendPrev("Ehhhh? What is this??");
	    qm.dispose();
	    break;
    }
}