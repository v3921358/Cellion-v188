var state = -1;
var n, r;

function start() {
	var text = "What kind of NX do you want to convert?\r\n";
	text += "#L1#NX Credit (You Have: " + cm.getPlayer().getCSPoints(1) + ")#l\r\n";
	text += "#L2#Maple Points (You Have: " + cm.getPlayer().getCSPoints(2) + ")#l\r\n";
	text += "#L4#NX Prepaid (You Have: " + cm.getPlayer().getCSPoints(4) + ")#l\r\n";
	cm.sendSimple(text);
	
}

function action(m, t, s) {
	state++;
	if (state == 0) {
		n = s;
		if (cm.getPlayer().getCSPoints(n) <= 0) {
			cm.sendOk("You don't have any NX of that type.");
			cm.dispose();
			return;
		}
		cm.sendSimple("What would you like to convert it to?\r\n#L1#NX Credit#l\r\n#L2#Maple Points#l\r\n#L4#NX Prepaid#l");
	} else if (state == 1) {
		if (s == 1 || s == 2 || s == 4) {
			r = cm.getPlayer().getCSPoints(n);
			cm.getPlayer().modifyCSPoints(n, -r);
			cm.getPlayer().modifyCSPoints(n, r);
			cm.sendOk("Done.");
		}
		cm.dispose();
	}
}
		
		

