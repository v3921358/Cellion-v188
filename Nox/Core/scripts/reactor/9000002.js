/* 
 * Treasure hunt
 */

var maplist = new Array(109010201, 109010202, 109010203, 109010204, 109010205, 109010206);

function act() {
    rm.playerMessage("You have been moved to a hidden place.")
    rm.warp(maplist[Math.floor(Math.random() * maplist.length)], 0);
}