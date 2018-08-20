/* White Whitney
	Skin Care Expert
	By Charmander
*/
var status = 0;
var skin = Array(0, 1, 2, 3, 4, 9, 10);

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0) {
	cm.dispose();
	return;
    } else {
	status++;
    }

    if (status == 0) {
	cm.sendNext("Wow, look at all the organic food and purified water this town has to offer. Don't eat and drink it though! Bring me some #b#t5153015##k and I'll help you dye your skin whatever color you want.");
    } else if (status == 1) {
	cm.sendStyle("All-natural, organic colors here. Pick one.", skin);
    } else if (status == 2){
	if (cm.setAvatar(5153015, skin[selection]) == 1) {
	    cm.sendOk("Enjoy your new and improved skin!");
	} else {
	    cm.sendNext("Um...you don't have the skin-care coupon you need to receive the treatment. Sorry, but I am afraid we can't do it for you.");
	}
	cm.dispose();
    }
}
