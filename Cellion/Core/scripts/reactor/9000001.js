/*
 * Treasure hunt
 */

var maplist = new Array(109010107, 109010108, 109010109, 109010110);

function act() {
    rm.playerMessage("You have been moved to a hidden place.")
    rm.warp(maplist[Math.floor(Math.random() * maplist.length)], 0);
}