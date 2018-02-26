/* Killer Mushroom Spore
    By Charmander
*/
var status = -1;

function action(mode, type, selection) {
    if (mode == 1)
    status++;
    else
    status--;
    if (status == 0) {
        cm.sendYesNo("#bDo you want to use the Killer Mushroom Spore? \r\n #e#r<Caution>#n \r\n Not for human consumption! \r\n If ingested, seek medical attention immediately!");
    } else if (status == 1) {   
        cm.sendSimple("Success! The barrier is broken!");
        cm.dispose();
    }
}