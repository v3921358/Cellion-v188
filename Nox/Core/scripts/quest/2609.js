/* 
	NPC Name: 		Lady syl
	Map(s): 		103050101
	Description: 		Quest - Becoming a Blade Specialist 2
*/
var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
	if (status == 0) {
	    qm.forceStartQuest();
	    qm.useItem(2022963);
        qm.sendOk("Ah, look at the effect so that poison! You're turning purple already. How delightful! Unfortunately, I don't have the antidote. For that, you'll have to speak to #bLady Syl#k. You might want to do it sooner rather than later.");
        qm.dispose();
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
	if (status == 0) {
        qm.sendNext("#h #, you look rather green. Are you ill? You're poisoned? Did Ryden tell you that? The potion was just apple juice. Couldn't you tell? Anyway, Ryden was just making a point...");
    } else if (status == 1) {
        qm.sendNextPrev("Don't even THINK about betraying us. The Dual Blades do not forgive their enemies.");
    } else if (status == 2) {
        qm.sendYesNo("The look in your eyes, the lift in your shoulders. You seem ready. Do you wish to advance to #bRouge#k? Once you do, you can begin your REAL missions");
    } else if (status == 3) {
        qm.forceCompleteQuest();
        qm.expandInventory(1, 4);
        qm.expandInventory(3, 4);
        qm.changeJob(430);
        qm.getPlayer().gainSP(5, 0);
        qm.sendNext("You're not a Thief, though you haven't learned any Dual Blade skills you. But you should be able to approach the Dark Lord as our spy.");
    } else if (status == 4) {
        qm.sendNext("Dual Blades and Thieves value the same stats, specifically LUK with DEX as a secondary. Use the #bAuto-Assign#k feature if you're unsure how to allocate your stats.");
    } else if (status == 5) {
        qm.sendNextPrev("You'll need a lot of items to be a successful spy, so I've increased your Equip and ETC tab slots.");
    } else if (status == 6) {
        qm.sendNextPrev("That's all from me. Ryden will fill you in on the details. I look forward to fruitful results.");
        qm.dispose();
    }
}