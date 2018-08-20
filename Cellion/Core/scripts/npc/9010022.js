/**
 * Dimensional Mirror
 * Warps you to Party Quests/Special Maps
 */

function start() {
    cm.sendSlideMenu(0, cm.getSlideMenuSelection(0));
}

function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }
    var mapid = 100000000;
    var portal = 0;
    switch (selection) {
        case 0: // Ariant Coliseum
            mapid = 682020000, portal = 3;
            break;
        case 1: // Mu Lung Training Center
            mapid = 925020000, portal = 4;
            break;
        case 2: // Monster Carnival 1
            mapid = 980000000, portal = 4;
            break;
        case 3: // Monster Carnival 2
            mapid = 980030000, portal = 4;
            break;
        case 4: // Dual Raid
            mapid = 923020000, portal = 0;
            break;
        case 5: // Nett's Pyramid
            mapid = 926010000, portal = 4;
            break;
        case 6: // Kerning Subway
            mapid = 910320000, portal = 2;
            break;
        case 7: // Happyville
            mapid = 209000000, portal = 0;
            break;
        case 8: // Golden Temple
            mapid = 950100000, portal = 9;
            break;
        case 9: // Moon Bunny
            mapid = 910010500, portal = 0;
            break;
        case 10: // First Time Together
            mapid = 910340700, portal = 0;
            break;
        case 11: // Dimensional Crack
            mapid = 221023300, portal = 2;
            break;
        case 12: // Forest of Poison Haze
            mapid = 300030100, portal = 1;
            break;
        case 13: // Remnant of the Goddess
            mapid = 200080101, portal = 1;
            break;
        case 14: // Lord Pirate
            mapid = 251010404, portal = 2;
            break;
        case 15: // Romeo and Juliet
            mapid = 261000021, portal = 5;
            break;
        case 16: // Resurrection of the Hoblin King
            mapid = 211000002, portal = 0;
            break;
        case 17: // Dragon's Nest
            mapid = 240080000, portal = 2;
            break;
        case 19: // Haunted Mansion
            mapid = 682000000, portal = 0;
            break;
        case 21: // Kenta In Danger
            mapid = 923040000, portal = 0;
            break;
        case 22: // Escape
            mapid = 921160000, portal = 0;
            break;
        case 23: // Ice Knight
            mapid = 932000000, portal = 0;
            break;
        case 25: // Alliance Union
            mapid = 913050010, portal = 0;
            break;
        case 26: // Halloween
            mapid = 682000700, portal = 0;
            break;
        case 27: //Fight for Azwan
            mapid = 262010000, portal = 0;
            break;
	    case 32: //Evolution Lab
		    mapid = 957000000, portal = 1;
			cm.forceCompleteQuest(1802);
			break;
		case 36: //Party Quests
			mapid = 910002000, portal = 2;
			break;
		case 38: //CrimsonHeart
			mapid = 301000000, portal = 2;
			break;
        case 87: // Old Maple
            mapid = 690000040, portal = 0;
            break;
        case 88: // NLC
            mapid = 600000000, portal = 0;
            break;
        case 98: // Astaroth
            mapid = 677000010, portal = 0;
            break;
        case 99: // Dragon's Nest
            mapid = 683010000, portal = 0;
            break;
	    case 200: //MesoRangers
		    mapid = 922100000, portal = 0;
			break;
		case 201: // Twisted Aqua
			mapid = 860000000, portal = 6;
			break; 
        default:
            cm.dispose();
            return;
    }
    cm.saveReturnLocation("MULUNG_TC");
    cm.warp(mapid, portal);
    cm.dispose();
}