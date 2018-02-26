/* Mastery Book 30
    By Charmander
*/
var status = -1;

function action(mode, type, selection) {
    if (mode == 1)
    status++;
    else
    status--;
    if (status == 0) {
        cm.sendSimpleS(cm.constructMastery(false), 5, 2080009);
    } else if (status == 1) {   
        if (selection != 0)
            cm.specialMasteryBook(selection, false);
        cm.dispose();
    }
}