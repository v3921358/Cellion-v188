/* Cygnus revamp
	Tyrant's Throne(Heliseum)
    Made by MR pIE
*/

function enter(pi) {
if (pi.getPlayer().haveItem(4033405, 1) {
pi.getPlayer().dropMessage(5, "You cannot enter while having Treglow's Relic");
return true;
}
else {
    pi.playPortalSE();
    pi.warp(401052200,1);
    return true;
    }
}