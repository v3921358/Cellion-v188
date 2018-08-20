var status = -1;

function start(mode, type, selection) {
	if (mode == 1)
	    status++;
	 else
	    status--;
	if (status == 0) {
		qm.sendNext("Before we proceed, you must understand that the #bGrand Athenereum#l is no simple archive of books. There is great magic here.");
	} else if (status == 1) {
      qm.sendNextPrev("It is a living, breathing space, where all the knowledge in the universe is stored. Much like the Akashic Records of old, where magicians who surpassed time and space gathered to share their knowledge. Rather grandices, is it not?");
	  } else if (status == 2) {
      qm.sendNextPrev("The records there can be rather troublesome. Some of the books semm to have personalities of their own, fitting about from room to room. There is a rather peculliar tome that seems to have taken a liking to the coffee percolator in my reading room. No matter how often I return it to its rightful place. It returns to my chambers.");
	} else if (status == 3) {	
        qm.sendAcceptDecline("Aren't you curious to know what that book contained?")    
		}else if (status == 4){
		qm.sendNextPrev("Luckily, as i was completing my morning routine. I found the book open to a page full of odd writing. Once I deciphered the meaning I realized it was a call for help. It said.. \r\n\r\n#bMasteria is on the varge of collapse. Chaos rules, law is gone. The Demons of Versal have fallen to ruin. Naricain returns. Send help.\r\n- Ridey#l")
	} else if (status == 5){
	qm.sendNextPrev("The name #Masteria#l brings to mind stirrings of knowledge I had thought forgotten. I can recall little though I believe it may have once been the #bhome of the Demons#l.")
		} else if (status == 6){
	qm.sendNextPrev("I believe this strange message could warrant an investigation. Particularly with the string of Demons that have arrived in Maple World as of late.")
		} else if (status == 7){
	qm.sendNextPrev("Masteria could give us a better understanding of their people...and what happened in the past that led us to where we are today")
		qm.forceStartQuest();
		qm.forceCompleteQuest();
		qm.dispose();
	}
}
function end(mode, type, selection) {
	qm.dispose();
}