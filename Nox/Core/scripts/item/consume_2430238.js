/*
 * Continential teleport scroll
 */
var status = -1;
function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    switch (status) {
	case 0:
	    cm.sendSimple("You can teleport to a town on a different continent. Which town would you like to teleport to?#b\r\n\r\n#L100# Henesys#l\r\n#L101# Ellinia#l\r\n#L102# Perion#l\r\n#L103# Kerning City#l\r\n#L104# Lith Harbor#l\r\n#L120# Nautilus#l\r\n#L310# Edelstein#l\r\n#L999# Cancel#l");
	    break;
	case 1:
	    switch (selection) {
		case 100:
		case 101:
		case 102:
		case 103:
		case 104:
		case 120:
		case 310:
		    cm.gainItem(2430238, -1);
		    cm.warp(selection * 1000000);
		    break;
		case 999:
		    break;
		default:
		    cm.autoBanUser("[consume_2430238] Attempting to teleport to invalid map with selection : " + selection);
		    break;
	    }
	    cm.dispose();
	    break;
    }
}