/* 
 * Rexion Welcome Board
 * @author Mazen
 */

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
		    cm.sendNextS("Enjoy your adventure.",5);
            cm.dispose();
		}
        status--;
    }
    if (status == 0) {
	    cm.sendSimple("Welcome to the world of #dREXION#k!\r\n"
			+ "You are currently in the #rRexion Hideout#k.\r\n"
			+ "You can come back here anytime by typing #b@home#k.\r\n\r\n"
			+ "Typing #b@rexion#k opens the quick access menu, this menu can be used for a variety of different operations. A list of available player commands can be accessed by typing #b@help#k.\r\n\r\n"
			+ "Channels #r8 - 15#k are known as #rBloodless Channels#k where monsters are much more difficult, but grant better rewards.\r\n"
			+ "#b#L0#Go to Recommended Training Map (Lv. 10 - 20)#l\r\n"
			+ "#d#L1#Go to Recommended Training Map (Lv. 20 - 40)#l\r\n"
			+ "#r#L2#Go to Recommended Training Map (Lv. 40 - 100)#l\r\n");
	} else if (status == 1) {
		switch (selection) {
			case 0:
				cm.warp(100010000, 0);
				cm.getPlayer().dropMessage(5, "Remember, you can go back to the Rexion hideout anytime by typing @home.");
				cm.dispose();
				break;
			case 1:
				cm.warp(100040000, 0);
				cm.getPlayer().dropMessage(5, "Remember, you can go back to the Rexion hideout anytime by typing @home.");
				cm.dispose();
				break;
			case 2:
				cm.warp(682010200, 0);
				cm.getPlayer().dropMessage(5, "Remember, you can go back to the Rexion hideout anytime by typing @home.");
				cm.dispose();
				break;
		}
    }
}