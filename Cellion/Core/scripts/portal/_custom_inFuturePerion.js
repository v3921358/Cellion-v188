/*
 * Custom script at Raved Forest - Six Path Crossway to Future Perion.
 */
function enter(pi) {
    if (pi.getPlayerStat("GM") == 1) {
        pi.playPortalSE();
        pi.warp(273010000, 1);
    } else {
        pi.playerMessage("This place is unavailable.");
    }
}