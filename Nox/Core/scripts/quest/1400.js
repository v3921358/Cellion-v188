/* Dawnveil
	The 5 paths 
	Mai
    Made by Daenerys
	Updated by Mazen
*/
var status = -1;
var sel = 0;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
	    qm.safeDispose();
	    return;
	}
	status--;
    }
    if (status == 0) {
        qm.sendAcceptDecline("Hmm, you're making good progress. Have you decided on which job path you want to take? You could be a Warrior with great strength and high HP, a Magician with many spells, a Bowman that shoots arrows from afar, a Thief that uses quick, sneaky attacks, or a Pirate with all kinds of flashy chain skills!");
	} else if (status == 1) {
        qm.sendSimple("If you head on over to Victoria Island, you can advance to the job of your choice by going to the corresponding Job Instructor. Before that, let me know which job you're interested in, and I'll let them know you're coming! So, which job path will you choose?\r\n#b#L0#I want to become a brute Warrior!#l\r\n#b#L1#I want to become a mystical Magician!#l\r\n#b#L2#I want to become a sharp-shooting Bowman!#l\r\n#b#L3#I want to become a sneaky Thief!#l\r\n#b#L4#I want to become a swashbuckling Pirate!#l");
    } else if (status == 2) {
        sel = selection;
	if (selection == 0) {
        qm.sendNext("A Warrior, huh? Boy, you're going to get really strong! They can take tons of damage, and dish plenty out, too. Okay, I'll talk to #bDances with Balrog#k, the Warrior Job Instructor.");
        } else if (selection == 1) {
		qm.sendNext("You want to become a Magician? They sure are mysterious! Their magic is super powerful and has all kind of effects. Just don't get hit...Magicians aren't known for their endurance! Okay, I'll talk to #bGrendel the Really Old#k, the Magician Job Instructor.");
        } else if (selection == 2) {
		qm.sendNext("You want to become a Bowman? I hope you have really good aim! With their great dexterity, they have no problem avoiding attacks and firing off plenty of their own. Okay, I'll talk to #bAthena Pierce#k, the Bowman Job Instructor.");
        } else if (selection == 3) {
		qm.sendNext("Going to become a Thief, are you? They're so quick and sneaky, their enemies don't see them coming until it's too late. They're so cool! Okay, I'll talk to #bDark Lord#k, the Thief Job Instructor.");
        } else if (selection == 4) {
		qm.sendNext("A Pirate? Yarr! Whether in a gunfight or a hand-to-hand brawl, Pirates fight with style! I think you're up to the challenge. Okay, I'll talk to #bKyrin#k, the Pirate Job Instructor.");
        }
    } else if (status == 3) {
	    if (sel == 0) {
		qm.sendOk("Go talk to #bDances with Balrog#k when you reach #blevel 10#k. He'll be waiting for you in #rPerion#k.");
		qm.forceStartQuest(1401);
	    qm.forceCompleteQuest(1400);
		qm.dispose();
	    } else if (sel == 1) {
		qm.sendOk("Go talk to #bGrendel the Really Old#k when you reach #blevel 8#k. He'll be waiting for you in #rEllinia#k.");
		qm.forceStartQuest(1402);
		qm.forceCompleteQuest(1400);
		qm.dispose();
		} else if (sel == 2) {
		qm.sendOk("Go talk to #bAthena Pierce#k when you reach #blevel 10#k. She'll be waiting for you in #rHenesys#k.");
		qm.forceStartQuest(1403);
		qm.forceCompleteQuest(1400);
		qm.dispose();
		} else if (sel == 3) {
		qm.sendOk("Go talk to #bDark Lord#k when you reach #blevel 10#k. He'll be waiting for you in #rKerning City#k.");
		qm.forceStartQuest(1404);
		qm.forceCompleteQuest(1400);
		qm.dispose();
		} else if (sel == 4) {
		qm.sendOk("Go talk to #bKyrin#k when you reach #blevel 10#k. She'll be waiting for you in #rNautilus#k.");
		qm.forceStartQuest(1405);
		qm.forceCompleteQuest(1400);
		qm.dispose();
	   }
	    qm.dispose();
    }
}