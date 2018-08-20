/*
 NPC Name: 		Puro - Crewmember
 Map(s): 		Snow Island to Rien 200090061
 */

var status = -1;

function start() {
    action(1, 0, 0);
}


function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.sendNext("No sweat. Come back and see me when you're ready for a trip!");
            cm.dispose();
            return;
        } else {
            status--;
        }
    }

    switch (status) {
        case 0:
            cm.sendSimple("Ahhh this is so boring. The whale is the one controlling the boat, and all I do is to look up in the sky. Hey, I'm so bored that I'm willing to explain to you what #m140000000# is all about. Are you interested?#b\r\n#L0#Sure.#l\r\n#L1#No, I'm not interested.#l");
            break;
        case 1:
            if (selection == 0 || selection == -1) {
                cm.sendNext("#m140000000# is a tiny island located right next to the biggest island oin all of Maple, Victoria Island. Its exact location is a 1 minute ride west of #m104000000#.");
            } else {
                cm.sendNext("Pssh... like you have anything better to do. ")
                cm.dispose();
            }
            break;
        case 1:
            cm.sendNextPrev("#m140000000# is actually far from the northern regions, yet the temperature there is unusually low, which keeps the town frozen at all times. You can say that #m140000000# consists of ice and little else. I hear that this anomaly in weather is definitely not natural, but man made.");
            break;
        case 2:
            cm.sendNextPrev("Since the whole island is made out of ice, there aren't many plants available in #m140000000#, and nary single fruit comes out of those trees. For penguins, it might be a paradise, but not so for people. That's why everyone else left town but one...");
            break;
        case 3:
            cm.sendNextPrev("But #m140000000# is still a very active town for a place that's constantly on ice. It's choke full of penguins digging up the cave on ice, hoping to discover something new.");
            break;
        case 4:
            cm.sendPrev("#b(#p1200005# begins to share his knowledge non stop, he must have been really bored and alone.)");
            break;
        case 5:
            cm.dispose();
            break;
        default:
            cm.dispose();
            break;
    }
}