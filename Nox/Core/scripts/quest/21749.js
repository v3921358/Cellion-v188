var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendNext("Oh, hello there. You have leveled up to the point where I had a hard time in recognizing you. You might not have a hard time taking on this. What is it, you ask?");
	    break;
	case 1:
	    qm.sendNextPrev("While you were busy training, #p1201000# and I looked at your past and the Seal Stone in all kinds of angles, and... we ran into an interesting bit of information recently. Do you know of the town that consists of toys for the kids, called #m220000000#?");
	    break;
	case 2:
	    qm.sendNextPrev("#m220000000# contains two clocktowers that control the time of that area. Each tower controls the time, so that ultimately, the time in #m220000000# stays frozen. They keep the time frozen since once the kids grow up, they'll eschew toys for something else.");
	    break;
	case 3:
	    qm.sendNextPrev("But apparently, one of the clocktowers seems to be broken for some reason. That's how #ba hole was created in managing the time of #m220000000#, and you are able to travel to the past#k. This is where it gets really interesting.");
	    break;
	case 4:
	    qm.sendNextPrev("Based on the information gathered from the people that have traveled to the past, #p1201000# came up with the conclusion that the time period is #bsimilar to the time when you were around#k. Which means... we might be able to gather up some information on the Seal Stone there, no?");
	    break;
	case 5:
	    qm.sendNextPrev("I mean, I am not worried about the Seal Stone as much as the possibility of meeting someone that knows you in that time period.");
	    break;
	case 6:
	    qm.forceCompleteQuest();
	    qm.sendNextPrev("The broken clocktower is the #bone on the right#k... Helios Tower. Inside the #bbuilding that resembles a pink bunny head#k, you'll find a time-managing device, and in order to enter that room, you'll have to #breach the top of Helios Tower, and move up the ladder#k. That's how you'll reach the past.");
	    qm.dispose();
	    break;
    }
}

function end(mode, type, selection) {
}