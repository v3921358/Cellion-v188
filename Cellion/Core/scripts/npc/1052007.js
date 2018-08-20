/*
 *Kerning Subway ticket boot
 *@author Arcas
 */


var status = -1;

function start() {
	var textMsg = "Pick your destination.";
	if(cm.getQuestCustomData(1600) != null || cm.getQuestCustomData(1601) != null || cm.getQuestCustomData(1602) != null)
    	textMsg += "\r\n\r\n#L1##bSubway Construction Site#k#l";
    textMsg += "\r\n#L2#Kerning City Subway (Beware of Stirges and Wraiths)#l";
    textMsg += "\r\n#L3#Kerning Square Shopping Center (Get on the Subway)#l";
    textMsg += "\r\n\r\n#L4#Enter Construction#l";
    textMsg += "\r\n#L5#New Leaf City#l";
    cm.sendNext(textMsg);
	status = -1;
}

function action(mode, type, selection){
	if (selection == 1) {
		cm.warp(931050400, 1);
	} else if (selection == 2) {
		cm.warp(103020100, 2);
	} else if (selection == 3) {
		cm.warp(103020020, 0);
	} else if (selection == 4) {
		cm.warp(690000041, 0);
	} else if (selection == 5) {
		cm.warp(600010001, 0);
	}
	cm.dispose();
}