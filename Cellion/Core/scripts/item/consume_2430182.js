/* 
 * Special beauty coupon, changes hair, eyes, face randomly.
 */

var status = -1;
var skin = Array(0, 1, 2, 3, 4, 5, 6, 7);

function action(mode, type, selection) {
    cm.gainItem(2430182, -1);
    
    if (cm.getPlayerStat("GENDER") == 0) {
	cm.setRandomBeautyCouponAvatar("MHair");
	cm.setRandomBeautyCouponAvatar("MFace");
    } else {
	cm.setRandomBeautyCouponAvatar("FHair");
	cm.setRandomBeautyCouponAvatar("FFace");
    }
    cm.setAvatar(0, skin[Math.floor(Math.random() * 5)]);
    cm.dispose();
}