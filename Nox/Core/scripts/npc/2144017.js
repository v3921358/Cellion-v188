/* Roudolph Happyville Warp NPC
   By Moogra
*/

function start() {
    cm.sendYesNo("Do you want to leave this place?");
}

function action(mode, type, selection) {
    if (mode > 0)
        cm.warp(272020110, 0);
    cm.dispose();
}