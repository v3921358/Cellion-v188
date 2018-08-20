/*
 * Treasure hunt
 */

var maplist = new Array(109010104, 109010105, 109010106);

function act() {
    rm.playerMessage("You have been moved to a hidden place.")
    rm.warp(maplist[Math.floor(Math.random() * maplist.length)], 0);
}