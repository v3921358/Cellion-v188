/* RED 1st impact
    The New Explorer
	Mai
    Made by Daenerys
*/
var status = -1;

function start(mode, type, selection) {
	if (mode == 1)
	    status++;
	 else
	    status--;
	if (status == 0) {
		qm.sendNextS("Ohmygoodness! Hi! I'm Mai, an aspiring hero. It took me four years, but I just completed my freshman year at hero school. You must be a new #bExplorer#k!",1);
    } else if (status == 1) {	
        qm.sendNextPrevS("A new... #bExplorer?#k What's that?",17);	
	} else if (status == 2) {	
        qm.sendNextPrevS("This was on that test I flunked ten times... Oh, right! Explorers are people who come to Maple World from other worlds! They start their journey right here on #bMaple Island#k.",1);		
    } else if (status == 3) {	
	    qm.sendNextPrevS("I'm on... #bMaple Island?#k",17);	
	} else if (status == 4) {	
	    qm.sendNextPrevS("You sure are! We used to be just some tiny island, but then Explorers started popping out. Now we've even got our own outhouse!",1);		
	} else if (status == 5) {	
	    qm.sendNextPrevS("So, your name is #h0#, right? You have two options now. You can listen to some explanations about starting out, take a few small tests, get some free gifts, and become my new best friend in the entire world...",1);		
	} else if (status == 6) {	
	    qm.sendNextPrevS("Or you can be teleported straight to town, but you'll miss out on my gifts... and I'll be super lonely and sad.",1);		
	} else if (status == 7) {
	    qm.sendNextS("What do you say?\r\n#b#L0# I'll be your friend, Mai! (Go through tutorial and get free equipment.)#l\r\n#L1# I don't need you, Mai! (Skip tutorial and teleport straight to town.)#l#k",1);		
	} else if (status == 8) {
	    qm.sendNextPrevS("REALLY?! I'll fill you in on everything you need to know, I promise!",1);	
	} else if (status == 9) {
	    qm.forceStartQuest();
		qm.forceCompleteQuest();
		qm.gainExp(20);
		qm.dispose();
	}
}