/*
 * Energy drink - Super Moo
 */

function action(mode, type, selection) {
    cm.gainItem(2430220, -1);
    cm.increaseProfessionFatigue(-30);
    cm.playerMessage("The Super Moo is kicking in. Your Fatigue decreased by 30.");
    cm.dispose();
}