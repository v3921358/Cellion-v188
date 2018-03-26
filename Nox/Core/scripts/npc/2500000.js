/*  
	Grand Athenaeum
    Skylark Rita
	
	Rexion Boss Warper
	@author Mazen
*/

load("nashorn:mozilla_compat.js");
importPackage(Packages.tools.packet);

var status = -1;

function action(mode, type, selection) {
	if (mode != 1) {
		cm.dispose();
	} else {
		status++;
		if (status == 0) {
			var sendString = "You feel a mystical presence from the room. Looks like this portal leads to a few mysterious places of strong power.#d\r\n";
			
			if (cm.getPlayer().getLevel() >= 80) {
				sendString += "#L0#Portal to Zakum, in the Mines of El Nath (Lv. 80+)#l\r\n";
			} else {
				sendString += "#L1000##rPortal to Zakum, in Mines of El Nath (Lv. 80 Required)#d\r\n"
			}
			
			if (cm.getPlayer().getLevel() >= 130) {
				sendString += "#L1#Portal to Horntail, in the Cave of Life (Lv. 130+)#l\r\n";
			} else {
				sendString += "#L1001##rPortal to Horntail, in the Cave of Life (Lv. 130 Required)#l#d\r\n"
			}
			
			if (cm.getPlayer().getLevel() >= 180) {
				sendString += "#L2#Portal to Magnus, at the Tyrant's Throne (Lv. 180+)#l\r\n";
			} else {
				sendString += "#L1002##rPortal to Magnus, at the Tyrant's Throne (Lv. 180 Required)#l#d\r\n"
			}
			
			if (cm.getPlayer().getLevel() >= 190) {
				sendString += "#L3#Portal to Lotus, in Black Heaven (Lv. 190+)#l\r\n";
			} else {
				sendString += "#L1003##rPortal to Lotus, in Black Heaven (Lv. 190 Required)#d\r\n"
			}
			
			if (cm.getPlayer().getLevel() >= 195) {
				sendString += "#L4#Portal to Mori Ranmaru, in the Dead Mine (Lv. 195+)#l\r\n";
			} else {
				sendString += "#L1004##rPortal to Mori Ranmaru, in the Dead Mine (Lv. 195 Required)#l#d\r\n"
			}
			
			if (cm.getPlayer().getLevel() >= 200) {
				sendString += "#L5#Portal to Root Abyss, at the Colossal Root (Lv. 200+)#l\r\n";
			} else {
				sendString += "#L1005##rPortal to Root Abyss, at the Colossal Root (Lv. 200 Required)#l#d\r\n"
			}
			
			cm.sendNextPrevS(sendString + "\r\n", 2);
						
		} else if (status == 1) {
			if (mode != 0) {
				cm.dispose();
			}
			switch(selection) {
				case 0: // Zakum
					cm.warp(211042300, 0);
					cm.dispose();
					break;
				case 1: // Horntail
					cm.warp(240050400, 0);
					cm.dispose();
					break;
				case 2: // Magnus
					cm.warp(401060000, 0);
					cm.dispose();
					break;
				case 3: // Lotus
					cm.warp(350060000, 0);
					cm.dispose();
					break;
				case 4: // Mori Ranmaru
					cm.warp(807300200, 0);
					cm.dispose();
					break;
				case 5: // Root Abyss
					cm.warp(105200000, 0);
					cm.dispose();
					break;
				default:
					cm.dispose();
					break;
			}
		}
	}
}