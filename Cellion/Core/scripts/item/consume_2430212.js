/*
 * Energy drink - Red Moo
 */

function action(mode, type, selection) {
    if (cm.checkAndSet_player_bosslimit(150034,3)) {
	cm.gainItem(2430212, -1);
	cm.increaseProfessionFatigue(-5);
	cm.playerMessage("The Red Moo is kicking in. Your Fatigue decreased by 5.");
    } else {
	cm.playerMessage("You are not strong enough to use this again for today.");
    }
    cm.dispose();
}