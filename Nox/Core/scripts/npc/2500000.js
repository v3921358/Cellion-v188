/*  
	Cellion Boss Warper
	@author Mazen Massoud
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
			var sendString = "\r\n#d";
			
			if (cm.getPlayer().getLevel() >= 50) {
				sendString += "#L0#(Lv.  50+ ) Portal to Zakum, in the Mines of El Nath#l\r\n";
			} else {
				sendString += "#L1000##r(Lv.  50  Required) Portal to Zakum, in Mines of El Nath#d\r\n"
			}
			
			if (cm.getPlayer().getLevel() >= 70) {
				sendString += "#L1#(Lv.  70+ ) Portal to Horntail, in the Cave of Life#l\r\n";
			} else {
				sendString += "#L1001##r(Lv.  70  Required) Portal to Horntail, in the Cave of Life #l#d\r\n"
			}
			
			if (cm.getPlayer().getLevel() >= 110) {
				sendString += "#L6#(Lv. 110+) Portal to Arkarium, in Dimensional Schism#l\r\n";
			} else { 
				sendString += "#L1006##r(Lv. 110 Required) Portal to Arkarium, in Dimensional Schism#d\r\n"
			}
			
			if (cm.getPlayer().getLevel() >= 120) {
				sendString += "#L4#(Lv. 120+) Portal to Mori Ranmaru, in the Dead Mine#l\r\n";
			} else {
				sendString += "#L1004##r(Lv. 120 Required) Portal to Mori Ranmaru, in the Dead Mine#l#d\r\n"
			}
			
			if (cm.getPlayer().getLevel() >= 130) {
				sendString += "#L8#(Lv. 130+) Portal to Hilla, in Hilla's Tower#l\r\n";
			} else {
				sendString += "#L1004##r(Lv. 130 Required) Portal to Hilla, in Hilla's Tower#l#d\r\n"
			}
			
			if (cm.getPlayer().getLevel() >= 140) {
				sendString += "#L7#(Lv. 140+) Portal to Damien, in World Tree#l\r\n";
			} else {
				sendString += "#L1007##r(Lv. 140 Required) Portal to Damien, in World Tree#d\r\n"
			}
			
			if (cm.getPlayer().getLevel() >= 150) {
				sendString += "#L5#(Lv. 150+) Portal to Root Abyss, at the Colossal Root#l\r\n";
			} else {
				sendString += "#L1005##r(Lv. 150 Required) Portal to Root Abyss, at the Colossal Root#l#d\r\n"
			}
			
			if (cm.getPlayer().getLevel() >= 170) {
				sendString += "#L2#(Lv. 170+) Portal to Magnus, at the Tyrant's Throne#l\r\n";
			} else {
				sendString += "#L1002##r(Lv. 170 Required) Portal to Magnus, at the Tyrant's Throne#l#d\r\n"
			}
			
			cm.sendNextPrevS(sendString + "\t #fs0# #fs13#", 2);
						
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
				
				case 4: // Mori Ranmaru
					cm.warp(807300200, 0);
					cm.dispose();
					break;
				case 5: // Root Abyss
					cm.warp(105200000, 0);
					cm.dispose();
					break;
				case 6: // Arkarium
					cm.warp(272020110, 0);
					break;
				case 7: // Damien
					cm.warp(350141000, 0);
					break;
				case 8: // Hilla
					cm.warp(262031200, 0); // Hilla
					break;
				
				//cm.warp(262031200, 0); // Hilla
				//cm.warp(350060000, 0); // Lotus
				
				default:
					cm.dispose();
					break;
			}
		}
	}
}