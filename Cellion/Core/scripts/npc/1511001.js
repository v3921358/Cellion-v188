/*
 NPC Name: 		Puro 
 Map(s): 		Riena Strait: Glacial Observatory 141000000
 */

var status = -1;
var cost = 800;
var cost_rien = 0;
var selection1 = 0;

function start() {
    action(1, 0, 0);
}


function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 1) {
            cm.sendNext("Are you not leaving yet? Let me know when you change your mind.");
            cm.dispose();
            return;
        } else {
            status--;
        }
    }

    switch (status) {
        case 0:
            var str = "Do you plan to leave here? Where do you want to go?\r\n\r\n#b#L0#To Rien (Cost: " + cost_rien + " meso)#l\r\n#L1##b#m104000000# (Cost: " + cost + " mesos)#l";

            cm.sendSimple(str);
            break;
        case 1:
            selection1 = selection;
            if (selection1 == 0) {
                cm.sendYesNo("OK, it won't take long to Rien. Hop in.");
            } else {
                cm.sendYesNo("Would you like to leave for #m104000000#? It will take about 1 min to get there.");
            }
            break;
        case 2:
            var thisselCost = 0;
            if (selection1 == 0) {
                thisselCost = cost_rien;
            } else {
                thisselCost = cost;
            }

            if (cm.getMeso() < thisselCost) {
                cm.sendNext("Hmmm... are you sure you have #b" + thisselCost + "#k mesos with you? Check your inventory and see if you have enough.\r\nOtherwise, I can't give you a ride.");
            } else {
                cm.gainMeso(-thisselCost);

                if (selection1 == 0) {
                    cm.warp(141000200, 0);
                } else {
                    cm.warp(200090079, 0);
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