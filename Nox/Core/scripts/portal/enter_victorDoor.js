/* Cygnus revamp
	Tyrant's Throne(Heliseum)
    Made by MR pIE
*/

function enter(pi) {
if (pi.getPlayer().haveItem(4033404, 1) {
pi.getPlayer().dropMessage(5, "You cannot enter while having Victor's Relic");
return true;
}
else {
    pi.playPortalSE();
    pi.warp(401051200,1);
    return true;
    }
}