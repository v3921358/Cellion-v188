/* Dr. Chops
	Plastic Surgeon
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
    	cm.sendSimple("Hello there! Looking to change your style? If you have a #bFace Coupon#K, I can give you the best look in town. \r\n #b#L0# Change face (use premium coupon)#l \r\n #L1# Change face (use normal coupon)#l");
    } else if (status == 1) {
	if (selection == 0) {
	    var face = cm.getPlayerStat("FACE");

	if (cm.getPlayerStat("GENDER") == 0) {
	    facetype = [20200, 20201, 20202, 20203, 20204, 20205, 20206, 20207, 20208, 20212, 20214, 20216, 20220, 20217, 20213, 20222, 20225, 20227, 20228, 20229, 20231];
	} else {
		facetype = [21200, 21201, 21202, 21203, 21204, 21205, 21206, 21207, 21208, 21212, 21214, 21216, 21220, 21217, 21213, 21222, 21225, 21227, 21228, 21229, 21231];
	}
	for (var i = 0; i < facetype.length; i++) {
	    facetype[i] = facetype[i] + face % 1000 - (face % 100);
	}
	    cm.askAvatar("When your face changes, your life does as well. The future is wide open. All it will cost you is one #b#t5152057##k. This is your chance to pick a new face!", facetype);
	} else if (selection == 2) {
	    var currenthaircolo = Math.floor((cm.getPlayerStat("HAIR") / 10)) * 10;
	    hair_Colo_new = [];
	    beauty = 2;

	    for (var i = 0; i < 6; i++) {
		hair_Colo_new[i] = currenthaircolo + i;
	    }
	    cm.askAvatar("I can dye your hair a completely new color, if you have a #b#t5151036##k. Isn't it time for a new look?", hair_Colo_new);
	} else if(selection == 1) {
		cm.sendNext("If you use a regular Coupon, you will end up with a random face. Do you really want to use a #b#t5152056##k?");
		cm.dispose();
	}else if(selection == 3) {
		cm.sendNext("A regular coupon will give you a random hair color. Are you ready to leave it to me?");
		cm.dispose();
	}
    } else if (status == 2){
	if (beauty == 1){
	    if (cm.setAvatar(5152057, hair_Colo_new[facetype]) == 1) {
		cm.sendNext("Enjoy your new and improved hairstyle!");
	    } else {
		cm.sendNext("Hmm... It doesn't look like you have the Face Coupon. I'm sorry, but no coupon, no surgery. That's the way the world works.");
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
