/*
 * Finding the right people - Big Bang
 */

var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	qm.sendOk("#b#h0##k, I know a place that would be perfect for you...");
	qm.dispose();
	return;
    }
    switch (status) {
	case 0:
	    qm.sendNext("Hello, #b#h0##k! Huge news!\r\nThe Explorers of Maple World have decided to join forces to fight against the Black Mage.");
	    break;
	case 1:
	    qm.sendNextPrev("But the Black Mage has destroyed the path to Sleepywood and granted new powers to the monsters. A lot of stuff has changed... It's a chaotic time, but I have faith that you possess the courage to overcome any circumstance.");
	    break;
	case 2:
	    qm.sendYesNo("The sages of each town have requested that a qualified person be chosen to serve as a guiding light for the confused, scared residents of each town. What do you think? #b#h0##k, would you like to see which town needs your help?");
	    break;
	case 3:
	    qm.sendSimple("Would you like to select a location to travel to? Choose carefully! Once you've made your choice, you cannot change your mind.\r\nm#b\r\n#L100#  Stay where you are.#l");
	    break;
	case 4:
	    qm.sendNext("#h0#, I see you've decided to stay put. That's fine with me. I look forward to seeing what you accomplish next!");
	    qm.forceCompleteQuest();
	    qm.dispose();
	    break;
    }
}

function end(mode, type, selection) {
    qm.dispose();
}