var status = -1;

function start(mode, type, selection) {
	if (mode == 1)
	    status++;
	 else
	    status--;
	if (status == 0) {
		qm.sendNext("#b(You told Pepper why you are here)#l");
	} else if (status == 1) {
      qm.sendNextPrev("It is a living, breathing space, where all the knowledge in the universe is stored. Much like the Akashic Records of old, where magicians who surpassed time and space gathered to share their knowledge. Rather grandices, is it not?");
	  } else if (status == 2) {
      qm.sendNextPrev("You don't look like the other people of Masteria. Are you from Versal, like us? No.... my people are too weak to protect themselves.\r\n\r\n#b#L3#I came here through the Dimensional Gate is there someone I can get to help you?#l");
	} else if (status == 3) {	
        qm.sendAcceptDecline("You..your're from another dimension?.... I don't know if I can believe you but I guess I don't have any other choice. Besides you are the first person who hasn't attempted to harm us in ages. Masteria is a lawless zone. No countries or cities exist... It's complete anarchy. We were foraging for food when monsters came and took us.\r\n\r\n#b#L4#Why did they kidnap you?#l")    
		} else if (status == 4){
	qm.sendNextPrev("I ..I believe we are meant to be sacrificed.\r\n\r\n#b#L5#Sacrificed?!#l")
	} else if (status == 5){
	qm.sendNextPrev("Maybe you really ARE from a different dimension.. People have been disappearing around here for years. I'm not sure why you came here though... we never sent out a distress call.\r\n\r\n#b#L6#Is there a sorcerer named Ridley here?#l")
	} else if (status == 6){
	qm.sendAcceptDecline("Never heard of him")
		qm.forceStartQuest();
		qm.forceCompleteQuest();
		qm.dispose();
	}
}
function end(mode, type, selection) {
    qm.forceStartQuest();
    qm.forceCompleteQuest();
	qm.dispose();
}