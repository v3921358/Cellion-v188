/*
 * Energy drink - Bear Power
 */

function action(mode, type, selection) {
    cm.gainItem(2430227, -1);
    cm.increaseProfessionFatigue(-50);
    cm.playerMessage("The Bear Power is kicking in. Your Fatigue decreased by 50.");
    cm.dispose();
}