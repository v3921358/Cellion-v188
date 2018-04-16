// Baby moon bunny

var status = -1;

function action(mode, type, selection) {
    if (mode == 1)
	status++;
    else {
	cm.sendNext("What if something really horrible happens to him while we're dawdling?");
	cm.dispose();
	return;
    }
    if (status == 0) {
	cm.sendNext("Whoa! Did you just see that?! A UFO just appeared! Eeek! Look over there. Oh my! Someone is being pulled up into the UFO... Oh no, it's Mr. Gaga! #rMr. Gaga has been kidnapped by a UFO!");
    } else if (status == 1) {
	cm.sendYesNo("What do we do? It's only a rumor, but I heard that aliens will do frightening things to you if you're kidnapped... Oh, poor Mr. Gaga! Please rescue Mr. Gaga! \r\n#bMr. Gaga may seem a bit indecisive, slow, and immature at times, but#k he's really a great person. We can't just sit and wait while horrible things are being done. Wait, I know! The grandpa on the moon must know how to rescue Mr. Gaga! I'll send you to the moon immediately. Please rescue Mr. Gaga!");
    } else if (status == 2) {
	cm.sendNext("Thank you so much. Please rescue Mr. Gaga! I'm sure that the grandpa on the moon will be able to help you.");
    } else if (status == 3) {
	cm.saveLocation("MULUNG_TC");
	cm.warp(922240200,0);
	cm.dispose();
    }
}