/*
 * NPC : Dagos  [ Quest 21739 - Aran - To the seal garden]
 */

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    if (status == 0) {
        cm.setAllowConversationCancel(false); // dont allow this to be cancelled by the client
        
	cm.sendNext("Wait... who are you exactly?");
    } else if (status == 1) {
	cm.sendNextPrev("Come to think of it, I heard that the puppeteer was attacked at Victoria Island. Was that you?");
    } else if (status == 2) {
	cm.sendNextPrev("Aha, in that case, this is good news! While taking the #bSeal Stone of Orbis#k, I will just go ahead and eliminate you, and I'll emerge victorious over the puppeteer! Now lets' fight!");
    } else if (status == 3) {
	cm.removeNpc(920030001, 9300348);
	cm.spawnMob(9300348, 801,83);
	cm.dispose();
    }
}