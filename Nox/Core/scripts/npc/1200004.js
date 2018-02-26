/*
 NPC Name: 		Puro 
 Map(s): 		Lith Harbour: Lith Harbour 104000000
 */

var status = -1;
var cost = 800;

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
            if (cm.getPlayerStat("LVL") < 30) {
                cost = 100; // it would have been annoying if the player were to hunt 800 meso on a 1x rate. [not official-server like, but its fine]
            }

            // There's an option above Victoria Island for version 14x and above. 'Theme dungeon: Riena Strait (Price: 0 meso)' in blue bold.
            cm.sendYesNo("Are you planning on leaving Victoria Island to head to our town? This boat will take you to #b#m140000000##k, and it'll cost you #b" + cost + "#k mesos. Do you want to go? It'll take you about a minute.");
            break;
        case 1:
            if (cm.getMeso() < cost) {
                cm.sendNext("Hmmm... are you sure you have #b" + cost + "#k mesos with you? Check your inventory and see if you have enough.\r\nOtherwise, I can't give you a ride.");
            } else {
                cm.gainMeso(-cost);

                cm.warp(200090061, 0);
                cm.startSelfTimer(60, 140020300);
                cm.dispose();
            }
            cm.dispose();
            break;
        default:
            cm.dispose();
            break;
    }
}