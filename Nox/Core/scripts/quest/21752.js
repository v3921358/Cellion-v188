var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 8) {
	    qm.sendOk("I pray that the lost key turns up.");
	    qm.dispose();
	    return;
	}
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendNext("Aren't you... Aran?! You're alive? Well, look at you, of course you're alive! You're the master of the polearm. No way you'd let the Black Mage stop you!");
	    break;
	case 1:
	    qm.sendNextPrev("#b(...Um...no...)", -1, true);
	    break;
	case 2:
	    qm.sendNextPrev("But anyway what brought you here? Are you here to look for the new land as well? You should let others take care of easy tasks like that, and you should take on tasks that are up to your level, no?");
	    break;
	case 3:
	    qm.sendNextPrev("I'm here to take the #t4032325#.", -1, true);
	    break;
	case 4:
	    qm.askAcceptDecline("Ah... You mean #t4032325#? Hold on one second. Let me see......");
	    break;
	case 5:
	    qm.sendNext("Aran, I'm so sorry, I don't know what to say. I know it's around here somewhere, but I must have lost all of my keys... It's all my fault! You reminded me so many times to be careful, but I became weak and vulnerable!");
	    qm.forceStartQuest();
	    break;
	case 6:
	    qm.sendNextPrev("But, don't you worry! I'm sure the #r#o9001024#s#k have the keys! You'll just need to teach them a lesson and find the lost key! Since I lost #b10 #t4032326#s#k, there's no way to tell which key is yours... Please find all 10 of them!");
	    break;
	case 7:
	    qm.sendNextPrev("You think I should do it myself? I suppose you have a point... but, no! I can't! I have a very important job to do here. I need to tend to the injured soldiers. Please, show us your strength once more, oh mighty hero!");
	    break;
	case 8:
	    qm.sendYesNo("I can send you right to the monsters. Would you like to go now?");
	    break;
	case 9:
	    qm.warp(930010001,0);
	    qm.dispose();
	    break;
    }
}

function end(mode, type, selection) {
}