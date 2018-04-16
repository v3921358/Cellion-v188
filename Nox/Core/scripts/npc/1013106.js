/*
 * Evan - Lvl 200 NPC creator/update
 */

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    switch (status) {
	case 0:
	    if (cm.getJob() == 2218 && cm.getPlayerStat("LVL") >= 200) {
		var create = cm.createPlayerNPC();
		switch (create) {
		    case -1:
			cm.sendYesNo("#b(The Legendary Dragon Master, would you like to update your georgous status?");
                        status = 99;
			break;
		    case 1:
			cm.sendOk("#b(The Dragon Master shrines as it is set.)");
			cm.dispose();
			break;
		    case -2:
			cm.sendOk("#b(Something went wrong with the Dragon Master.)");
			cm.dispose();
			break;
		    case -3:
			cm.sendOk("#b(There's a headstone with message craved into it.)");
			cm.dispose();
			break;
		}
	    } else {
		cm.sendOk("#b(There's a headstone with message craved into it.)");
		cm.dispose();
	    }
	    break;
	case 100:
            if (cm.getQuestStatus(150014) == 2) {
                var updateStatus = cm.updateImitedPlayerNPCStats();
                
                switch (updateStatus) {
                    case -2: // db error
                    case -3: // world server error
                        cm.sendOk("The georgeous status is unavailable for update right now.");
                        break;
                    case 0:
                        cm.sendOk("#b(The Cygnus Knight shrines as it is set.)");
                        break;
                    case -4: 
                        cm.sendOk("#b(The Cygnus Knight shrines may only be redesigned once per day..)");
                        break;
                }
            }
	    cm.dispose();
	    break;
    }
}
