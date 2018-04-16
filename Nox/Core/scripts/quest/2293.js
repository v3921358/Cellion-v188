/* 
	NPC Name: 		Tanye East
	Map(s): 		Kerning Square Lobby
	Description: 		Quest - The last Song
*/
var status = -1;

function start(mode, type, selection) {
    if (mode == -1) {
	qm.dispose();
    } else {
	if (mode == 1)
	    status++;
	else
	    status--;
	
	if (status == 0) {
	    qm.sendNext("Do you remember the song played by the #o4300013#? I shall give you a test to see if you can remember that!");
	} else if (status == 1) {
            qm.sendSimple("Here. I'll give you some samples. Please listen to them and choose one! You can only listen to it once.#b \r\n\r\n#L0#Listen to Song No. 1#l\r\n#L1#Listen to Song No. 2#l\r\n#L2#Listen to Song No. 3#l\r\n\r\n#L3##eEnter the correct song.#l");
        } else if (status == 2) {
            if (selection >= 0 && selection <= 2) {
                // play song
                if (selection == 0) {
                    qm.playSound(false, "Mob/4300013/Attack3");
                } else if (selection == 1) {
                    qm.playSound(false, "Mob/4300013/Attack4");
                } else {
                    qm.playSound(false, "Mob/4300013/Die");
                }
                status = 0; // Loop back to status 1
                qm.sendNext("Got it? ");
            } else {
                qm.sendGetNumber("Now, please tell me the answer. You only get #b one chance so please choose carefully.", 1, 1, 3);
            }
        } else if (status == 3) {
            var songSelected = selection;

            if (songSelected >= 1 && songSelected <= 3) {
                if (songSelected == 3) {
                    if (qm.getNumFreeSlot(4) >= 1) {
                        qm.forceCompleteQuest();
                        qm.gainItem(4032507,1);
                        // qm.forceStartQuest(2289);
                        qm.gainExp(3300);
                        qm.sendNext("So that was the song he was playing... Well, it wasn't my song after all, but I'm glad that I know with certainly right now. Thank you so much.");
                    } else {
                        qm.sendNext("Please ensure that you have 1 available ETC slot.");
                    }
                } else {
                    qm.sendNext("That was not the song he was playing. Are you sure you heard of it at all?");
                }
            } else {
                cm.dispose();
            }
	} else if (status == 4) {
	    qm.dispose();
	} else if (status == 5) {
	//    qm.forceStartQuest();
	    qm.dispose();
	}
    }
}

function end(mode, type, selection) {
    qm.dispose();
}