/* 
 * Charlie - Mini dungeon entrance
 */
var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	cm.sendOk("You will need to carry the entrance ticket with you in order to enter the Premium Mini Dungeon.");
	cm.dispose();
	return;
    }
    switch (status) {
	case 0:
	    cm.sendYesNo("Would you like to enter the Premium Mini Dungeon? \r\n#b[This is a temporary event that ends automatically on 01/06/2011!]");
	    break;
	case 1:
	    if (cm.isDateAfter(1,6,2011)) {
		cm.sendOk("This event has ended, please try again next time.");
		return;
	    }
	    var t = -1;
	    switch (cm.getMapId()) {
		case 100020000: // Rainforest east of henesys
		    t = 553000000;
		    break; 
		case 260020600: // Sahel 2
		    t = 553000100;
		    break;
		case 221023400: // Eos tower 76th
		    t = 553000200;
		    break;
		case 261020300: // Lab C-1 Area
		    t = 553000300;
		    break;
		case 105040304: // Sleepy Dungeon IV
		    t = 553000400;
		    break;
                case 100020400: // Post-bb - Ghost mushroom forest
		case 105050100: // Ant Tunnel 2
		    t = 553000500;
		    break;
		case 240040511: // The dragon nest left behind
		    t = 553000600;
		    break;
		case 240040520: // Destroyed Dragon Nest
		    t = 553000700;
		    break;
		case 240020500: // Battlefield of fire and water
		    t = 553000800;
		    break;
                case 105020400: // Drake Cave - Cave Exit
                    t = 553000900;
                    break;
		case 105090311: // Cold Cradle
		    t = 553000900;
		    break;
		case 251010402: // Red-nose pirate
		    t = 553001000;
                    break;
		case 541000300: // Mysterious path 3 -- Map not available
		    break; // none
		case 551030000: // Fantasy theme park 3; -- Map not available
		    break; // none
		case 54102061: // Destroyed Park 2 -- Map not available
		    break; // none
	    }
	    if (t != -1) {
		if (cm.haveItem(4032055)) {
		    cm.gainItem(4032055,-1);
		    cm.warp(t,0);
		} else {
		    cm.sendOk("You do not have the #i4032055:#  Premium #z4032055:# .");
		}
	    }
	    cm.dispose();
	    break;
    }
}