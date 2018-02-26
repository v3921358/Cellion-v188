/* White Whitney
	Skin Care Expert
	By Charmander
*/
var status = -1;
var beauty = 0;
var hair_Colo_new;

function start() {
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
    	cm.sendSimple("Do you see all these pretty trees? I pruned them all to perfection. I can do the same for your hair if you have a #bHair Shop Coupon#k or a #bHair Color Coupon#k. \r\n #b#L0# Change Hair(Use VIP Coupon)#l \r\n #L1# Change Hair (Use Regular Coupon)#l \r\n #b#L2# Dye Hair(Use VIP Coupon)#l \r\n #L3# Dye Hair(Use Regular Coupon)#l");
    } else if (status == 1) {
	if (selection == 0) {
	    var hair = cm.getPlayerStat("HAIR");
	    hair_Colo_new = [];
	    beauty = 1;

	    if (cm.getPlayerStat("GENDER") == 0) {
		hair_Colo_new = [30030, 30020, 30000, 30310, 30330, 30060, 30150, 30410, 30210, 30140, 30120, 30200];
	    } else {
		hair_Colo_new = [31050, 31040, 31000, 31150, 31310, 31300, 31160, 31100, 31410, 31030, 31080, 31070];
	    }
	    for (var i = 0; i < hair_Colo_new.length; i++) {
		hair_Colo_new[i] = hair_Colo_new[i] + (hair % 10);
	    }
	    cm.askAvatar("#b#t5150053##k is all you need to change your hairstyle. Pick a new hairdo, and watch in awe as I give you a whole new look!", hair_Colo_new);
	} else if (selection == 2) {
	    var currenthaircolo = Math.floor((cm.getPlayerStat("HAIR") / 10)) * 10;
	    hair_Colo_new = [];
	    beauty = 2;

	    for (var i = 0; i < 6; i++) {
		hair_Colo_new[i] = currenthaircolo + i;
	    }
	    cm.askAvatar("I can dye your hair a completely new color, if you have a #b#t5151036##k. Isn't it time for a new look?", hair_Colo_new);
	} else if(selection == 1) {
		cm.sendNext("I'll recommend a style if you use a regular coupon. Are you sure you want to change your hair style using #b#t5150052##k?");
		cm.dispose();
	}else if(selection == 3) {
		cm.sendNext("A regular coupon will give you a random hair color. Are you ready to leave it to me?");
		cm.dispose();
	}
    } else if (status == 2){
	if (beauty == 1){
	    if (cm.setAvatar(5150001, hair_Colo_new[selection]) == 1) {
		cm.sendNext("Enjoy your new and improved hairstyle!");
	    } else {
		cm.sendNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't give you a haircut without it. I'm sorry.");
	    }
	} else {
	    if (cm.setAvatar(5151001, hair_Colo_new[selection]) == 1) {
		cm.sendNext("Enjoy your new and improved haircolor!");
	    } else {
		cm.sendNext("Hmmm...it looks like you don't have our designated coupon...I'm afraid I can't dye your hair without it. I'm sorry.");
	    }
	}
	cm.dispose();
    }
}
