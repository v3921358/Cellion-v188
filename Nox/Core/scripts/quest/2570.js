var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        qm.forceStartQuest();
        qm.sendOk("Here, go through here and meet Kyrin. She'll make your promotion to Cannoneer official.");
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
        qm.sendNext("Good to see you, #h0#. So...you're the one that helped Cutter return. I saw you were hurt when you first arrived, are you okay now? You must be made of stern stuff. No wonder Cutter regards you so highly. My name is Kyrin. I'm captain of the Nautilus, as well as the Job Instructor for Pirates.");
    } else if (status == 1) {
        qm.sendNextPrev("Cutter told you that he wants you to become a #bCannoneer#k, right? I agree with him, but I'm worried that your heart might not be in it.. Maybe if you knew more about the pirates, you would be more interested. Let me tell you a little about us.");
    } else if (status == 2) {
        qm.sendNextPrev("I brought the pirates together to start working against the Black Mage, the great evil that threatens all of Maple World. Turns out the hero business is more profitable than looting and pillaging!");
    } else if (status == 3) {
        qm.sendNextPrev("If you become a Pirate, you can help investigate the Black Mage's plots, and assist in the defense of Maple World. Keep in mind that I won't make you do anything...I'm primarily a Job Instructor, and just guide the pirates in a general sense.")
    } else if (status == 4) {
        qm.sendNextPrev("But, I know you would help us fight the Black Mage. You have that gleam in your eye that all heroes do. Anyway, I've said my piece.");
    } else if (status == 5) {
        qm.sendAcceptDecline("Now, it's all up to you. Do you wish to join the pirates? I would be quite pleased if you became a Cannoneer.");
    } else if (status == 6) {
        qm.forceCompleteQuest();
        qm.gainItem(1532000, 1);
        qm.expandInventory(1, 4);
        qm.expandInventory(2, 4);
        qm.expandInventory(4, 4);
        qm.changeJob(501);
        qm.getPlayer().gainSP(5, 0);
        qm.gainExp(1242);
        qm.getPlayer().resetAp();
        qm.sendNext("Well, you are truly one of us now. Open up your Skill window and check out your new Pirate abilities. I also gave you a few extra SP, so you go ahead and boost some of your new skills. You'll get more skills at higher levels, so I suggest you have a plan for your training.");
    } else if (status == 7) {
        qm.sendNext("Skills alone do not make you a great pirate. You have to distribute your stats like a pirate, too! If you're hoping to become a Cannoneer, invest heavily in STR so you can hold that heavy cannon of yours. And if you just have no idea, use the #bauto-distribute#k option. Simple, and effective.");
    } else if (status == 8) {
        qm.sendNextPrev("Oh, I gave you a little gift, too. I expanded a few slots in your Equip and ETC item tabs, so you should have plenty of room for your spoils!");
    } else if (status == 9) {
        qm.sendNextPrev("Now, there is one last thing that you need to remember. More than anything else, you need to keep your HP up. If you fall in battle, you'll lose some of your EXP. And I'm SURE you don't want that, right?");
    } else if (status == 10) {
        qm.sendNextPrev("Well, that's it! I have taught you everything you need to know. I also gave you a few decent weapons, so make good use of them. Now, go forth, grow stronger and kick around the Black Mage's minions, if you get the chance!");
        qm.dispose();
    }
}