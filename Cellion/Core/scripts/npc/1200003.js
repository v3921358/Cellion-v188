/*
 NPC Name: 		Puro 
 Map(s): 		Snow Island: Penguin Port 140020300
 */

var status = -1;
var cost = 800;
var cost_rienStrait = 0;
var selection1 = 0;
var selCost = 0;

function start() {
    action(1, 0, 0);
}


function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 1) {
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
            var str = "Heading out?\r\n\r\n#b#L0#Theme dungeon: Riena Strait (Price: " + cost_rienStrait + " meso)#l\r\n#L1##bVictoria Island (Price: " + cost + " mesos)#l";

            cm.sendSimple(str);
            break;
        case 1:
            selection1 = selection;
            if (selection1 == 0) {
                selCost = cost_rienStrait;
                cm.sendYesNo("Looking for a whitewater adventure, are ya? Lucky for you, the first trip to Riena Strait is FREE! Come on board.");
            } else {
                selCost = cost;
                cm.sendYesNo("Next stop, #m104000000#! Hang on! We will be there in a jiffy!");
            }
            break;
        case 2:
            if (cm.getMeso() < selCost) {
                cm.sendNext("Hmmm... are you sure you have #b" +selCost + "#k mesos with you? Check your inventory and see if you have enough.\r\nOtherwise, I can't give you a ride.");
            } else {
                cm.gainMeso(-selCost);

                if (selection1 == 0) {
                    cm.warp(141000100, 0);
                } else {
                    cm.warp(200090077, 0);
                    cm.startSelfTimer(60, 104000000);
                }
            }
            cm.dispose();
            break;
        default:
            cm.dispose();
            break;
    }
}