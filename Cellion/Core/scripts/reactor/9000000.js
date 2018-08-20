/*
 * Treasure hunt
 */

var maplist = new Array(109010101, 109010102, 109010103);

function act() {
    rm.playerMessage("You have been moved to a hidden place.")
    rm.warp(maplist[Math.floor(Math.random() * maplist.length)], 0);
}