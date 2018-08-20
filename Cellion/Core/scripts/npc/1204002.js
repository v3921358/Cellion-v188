/*
 * NPC : Francis (Doll master)
 * Map : 910510000
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
        
	cm.sendNext("I'm Francis, the Puppeteer of the Black Wings. How dare you disturb my puppets... It really upsets me, but I'll let it slide this time. Now I catch you doing it again, I swear in the name of the Black Mage, I will make you pay for it.");
    } else if (status == 1) {
	cm.removeNpc(910510000, 1204002);
	cm.spawnMob(9300344, 460, 248);
	cm.dispose();
    }
}