/*
 * Energy drink - Carror Water
 */

function action(mode, type, selection) {
    if (cm.checkAndSet_player_bosslimit(150034,3)) {
	cm.gainItem(2430231, -1);
	cm.increaseProfessionFatigue(-40);
	cm.playerMessage("The Carrot Water is kicking in. Your Fatigue decreased by 40.");
    } else {
	cm.playerMessage("You are not strong enough to use this again for today.");
    }
    cm.dispose();
}