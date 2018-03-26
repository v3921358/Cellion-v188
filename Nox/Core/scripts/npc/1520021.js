function start() {
	var textMsg = "At the center of the Temple of Time an enormous door, the Gate of the Present.";
	textMsg += "\r\n\r\n#L1##bStep through into the Arcane River#l";
    cm.sendNext(textMsg);
	status = -1;
}

function action(mode, type, selection){
	if (selection == 1) {
        if (cm.isQuestFinished(1460)) 
            cm.warp(450001003, 1);
        else
            cm.sendOk("No one have stepped through this door before.");
    }
	cm.dispose();
}