/*
	NPC Name: 		Hankie
	Description: 		Quest - Liar Liar
*/

var status = -1;
var exp;

function start(mode, type, selection) {
}

function end(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    switch (status) {
	case 0:
	    if (qm.getQuestStatus(9730) == 0 || qm.getNpc() == 9270035) {
		qm.forceStartQuest();
		qm.dispose();
		return;
	    }
	    qm.sendSimple("Hey, aren't you a human being? What's a human being doing here? Something smells fishy here...!  \r\n#L0##b You're the one that's planning on attacking the Maplers, right? I can't let you do that!#l\r\n#L1# Hey, I am just here to help you out.#l\r\n#L2# I... actually... um... like you. I've been meaning to say that.#l");
	    break;
	case 1:
	    switch (selection) {
		case 0:
		    qm.sendNext("What? How did you find out? My friends here won't let you leave, that's for sure!");
		    qm.dispose();
		    break;
		case 1:
		    qm.sendSimple("Help? What help? How can I trust you? You look creepy!\r\n#L0##b What are you saying? That's not true! Look at me in the eyes! My innocent-looking eyes...!~#l\r\n#L1# Who's calling me creepy-looking? You look creepier than me!!#l\r\n#L2# Hmph... honestly, I've been hearing that for a while...#l");
		    break;
		case 2:
		    qm.sendNext("Re...really? Well, I think you're a great person, but monsters and humans aren't meant for one another...");
		    qm.dispose();
		    break;
	    }
	    break;
	case 2:
	    switch (selection) {
		case 0:
		    qm.sendSimple("Hmmm... fine, I am still not sure about this, but I'll hear you out. What do you want from me?\r\n#L0##b I heard you were looking for something that will turn you into a human being, so I brought the best transformation item out there.#l\r\n#L1# I thought it was interesting to see a monster talk, so....#l\r\n#L2# I am going to make sure you will never turn into a human being!#l");
		    break;
		case 1:
		    qm.sendNext("What? What part of my face screams deception? I am about as pure as one can get!");
		    qm.dispose();
		    break;
		case 2:
		    qm.sendNext("I see... it's okay. One day you'll find someone that'll look past your creepy looks.");
		    qm.dispose();
		    break;
	    }
	    break;
	case 3:
	    switch (selection) {
		case 0:
		    qm.sendSimple("What? You brought the stuff needed to transform? Thanks!... wait, you really thought I meant that? Are you kidding me? How am I supposed to know that what you brought is real? Isn't it really something else? \r\n#L0##b Dang, how did I get caught. Well... how about 100 mesos for a Red Potion?#l\r\n#L1# What, how did you know? Well... this is actually a leaf of transformation where you won't be able to transform at all...!#l\r\n#L2# The truth is...I'm a monster too. I took this potion to transform. You should take this potion too to change into another form.#l");
		    break;
		case 1:
		    qm.sendNext("What are you saying? If you're really that bored, go talk to the game admin.");
		    qm.dispose();
		    break;
		case 2:
		    qm.sendNext("wait a minute, how did you know of my plans?");
		    qm.dispose();
		    break;
	    }
	    break;
	case 4:
	    switch (selection) {
		case 0:
		    qm.sendNext("What? 100 mesos for a Red Potion? What a rip off! I am not buying anything from you!");
		    qm.dispose();
		    break;
		case 1:
		    qm.sendNext("I knew it. I knew something was weird when a human being came up to me to talk about something. I am not falling for your lies, so get out of here right now!");
		    qm.dispose();
		    break;
		case 2:
		    //		    exp = 100487;
		    exp = qm.getPlayerStat("LVL") * 800;
		    qm.sendNext("What? You're a monster too? I see... well, your eyes did seem a little like that of a monster's... Okay, I feel like I can trust you now. I'll take your transformation leaf. Thanks!\r\n\r\n#fUI/UIWindow.img/QuestIcon/5/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0#"+exp+" exp");
		    break;
	    }
	    break;
	case 5:
	    qm.sendNext("Wait a minute, what's going on here? Why am I not turning into a human being? I just sniffed this, yet I'm still... me. Wait... is this... is this #t4031585#?! Did you just stop me from turning into human? You lied!! How dare you! ");
	    if (qm.haveItem(4031585, 10)) {
		qm.gainExp(exp);
		qm.gainItem(4031585,-10);
		qm.gainItem(1012058 + Math.random() * 3, 1, false, 864000000);
		qm.forceCompleteQuest();
	    }
	    qm.dispose();
	    break;
    }
}