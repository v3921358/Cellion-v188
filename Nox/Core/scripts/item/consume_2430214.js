/*
 * Energy drink - Mobstar
 */

function action(mode, type, selection) {
    cm.gainItem(2430214, -1);
    cm.increaseProfessionFatigue(-30);
    cm.playerMessage("The Mobstar is kicking in. Your Fatigue decreased by 30.");
    cm.dispose();
}