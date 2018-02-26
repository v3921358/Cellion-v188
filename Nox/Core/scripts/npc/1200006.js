/*
 NPC Name: 		Puro - Crewmember
 Map(s): 		Snow Island to Lith Harbour 200090077
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
            cm.sendSimple("The current is quite serene, which means we should get to #m104000000# of Victoria Island earlier than expected. This is getting to be quite boring. Do you want to hear more about the Victoria Island? #b\r\n#L0#Yes, sure.#l\r\n#L1#No, I'm fine.#l");
            break;
        case 1:
            if (selection == 0) {
                cm.sendNext("Okay, so check this out. Victoria Island is the largest island in the world of Maple. It is not as big as Ossyria, but it's a continent, and Victoria Island is a close second in size. A long time ago when the Black Wizard overtook Ossyria, apparently someone made  the heroic effort to rescue the remaining residents and took refuge in Victoria Island.");
            } else {
                cm.sendNext("Pssh... like you have anything better to do. ")
                cm.dispose();
            }
            break;
        case 2:
            cm.sendSimple("Ever since then, Victoria Island has become the center of the world of Maple. The monsters there are not as tough as other regions, and each town is equipped with taxi which calls for great public transportation. Ah, do you want to know about the towns in Victoria Island? #b\r\n#L0# #m104000000# #l\r\n#L1# #m101000000# #l\r\n#L2# #m102000000# #l\r\n#L3# #m103000000# #l\r\n#L4# #m120000100# #l\r\n#L5# #m105040300# #l\r\n#L6# #m130000000# #l");
            break;

        // TODO: 2 full chunk of sentence per area in the selection
        case 3:
            cm.sendPrev("#b(#p1200005# begins to share his knowledge non stop, he must have been really bored and alone.)");
            break;
        case 4:
            cm.dispose();
            break;
        default:
            cm.dispose();
            break;
    }
}