/*	
	REXION
	Noblesse Job Quest
	Path of the Thunder Breaker
	
    @author Mazen
*/

var status = -1;

function start(mode, type, selection) {
    qm.forceStartQuest();
    qm.dispose();
}

function advanceJob() {
	if (qm.getJob() != 1500) {
	    qm.gainItem(1482014, 1);
	    qm.gainItem(1142066, 1);
	    qm.expandInventory(1, 4);
	    qm.expandInventory(4, 4);
	    qm.changeJob(1500);
		qm.getPlayer().gainSP(5, 0);
		qm.gainExp(1242);
		qm.getPlayer().resetAp(); //Method to reset ap for first advancement.
	}
}

function end(mode, type, selection) {
    if (mode == 0) {
		if (status == 0) {
			qm.sendOk("Huh? Come back to me when you've made up your mind.");
			qm.safeDispose();
			return;
		}
		status--;
    } else {
		status++;
    }
    if (status == 0) {//Start Up
		qm.sendYesNo("Thunder Breakers are pirates with a love of storms. Why? What kind of madman would sail head-on into a typhoon??!! Well, Thunder Breakers are Cygnus Knights blessed by Lightning, the spirit of- you guessed it -lightning, meaning that choppy waters are their favorite place to be.\r\n\r\nSo, do you want to become a Thunder Breaker?\r\nThis decision will be final, so think carefully.");
    } else if (status == 1) { //Quiz Intro
		qm.sendAcceptDecline("Before I turn you into a Thunder Breaker, you're going to need to prove yourself. Answer every question of the #dREXION Quiz#k correctly and then I'll help you.");
	} else if (status == 2) { //Quiz - Question 1
		qm.sendSimple("What do you happens when you '#bvote#k' for #dREXION#k?\r\n#b"
					+ "\r\n#L0#You receive a fair amount of mesos.#l"
					+ "\r\n#L1#You get Vote Points to spend in the Free Market.#l"
					+ "\r\n#L2#Nothing happens at all.#l");
	} else if (status == 3) { //Quiz - Answer 1
		if (selection == 1) {
			qm.sendNext("By voting, you recieve #bVote Points#k and support the server!\r\nThat's #bcorrect#k! Let's move on.");
		} else {
			qm.sendPrev("Sorry, that answer is #rincorrect#k.");
		}
	} else if (status == 4) { //Quiz - Question 2
		qm.sendSimple("How do you get #bNX#k here around #dREXION#k?\r\n#b"
					+ "\r\n#L3#You buy NX with real money.#l"
					+ "\r\n#L4#Occasionally received upon login.#l"
					+ "\r\n#L5#NX can only be obtained from events.#l"
					+ "\r\n#L6#Received when killing monsters.#l");
	} else if (status == 5) { //Quiz - Answer 2
		if (selection == 6) {
			qm.sendNext("You get #bNX#k by killing monsters around #dREXION#k, alternatively you can exchange #bVote Points#k for #rMaple Points#k!\r\nThat's #bcorrect#k! Let's move on.");
		} else {
			qm.sendPrev("Sorry, that answer is #rincorrect#k.");
		}
	} else if (status == 6) { //Quiz - Question 3
		qm.sendSimple("What are #rBloodless Channels#k in #dREXION#k?\r\n#b"
					+ "\r\n#L7#Channels only available after level 100.#l"
					+ "\r\n#L8#Channels that have easier monsters.#l"
					+ "\r\n#L9#Channels that offer more challenging game play.#l"
					+ "\r\n#L10#Channels that smell bad.#l");
	} else if (status == 7) { //Quiz - Answer 3
		if (selection == 9) {
			qm.sendNext("#rBloodless Channels#k are the second half of the channel list and monsters there have a lot more #ddamage reduction#k. Killing monsters in these channels also grants you significantly more #bNX#k. There is also a very small chance to level up twice or not level.\r\nThat's #bcorrect#k! Let's move on.");
		} else {
			qm.sendPrev("Sorry, that answer is #rincorrect#k.");
		}
	} else if (status == 8) { //Quiz - Question 4
		qm.sendSimple("How do you travel around #dREXION#k?\r\n#b"
					+ "\r\n#L11#The only form of travel is taxis.#l"
					+ "\r\n#L12#By typing @rexion for our travel system.#l"
					+ "\r\n#L13#You can just walk everywhere.#l"
					+ "\r\n#L14#You call for an Uber.#l");
	} else if (status == 9) { //Quiz - Answer 4
		if (selection == 12) {
			qm.sendNext("Typing #r@rexion#k opens the general NPC, which can be used to access a few #dREXION#k features very easily. Although, you need to be at least #blevel 15#k to use it.\r\nThat's #bcorrect#k! Let's move on.");
		} else {
			qm.sendPrev("Sorry, that answer is #rincorrect#k.");
		}
	} else if (status == 10) {
		qm.sendOk("Also, they live on the floating island of Ereve, so there's that. Thunder Breakers use knuckles as their main weapon and are useful over both short and long distances. They can also literally throw sharks at people. SHARKS, you guys. SHARKS.");
		qm.forceCompleteQuest();
		advanceJob();
		qm.safeDispose();
    }
}