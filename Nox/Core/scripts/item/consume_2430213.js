/*
 * Energy drink - Vita C
 */

function action(mode, type, selection) {
    if (cm.checkAndSet_player_bosslimit(150034,3)) {
	cm.gainItem(2430213, -1);
	cm.increaseProfessionFatigue(-10);
	cm.playerMessage("The Vita C is kicking in. Your Fatigue decreased by 10.");
    } else {
	cm.playerMessage("You are not strong enough to use this again for today.");
    }
    cm.dispose();
}