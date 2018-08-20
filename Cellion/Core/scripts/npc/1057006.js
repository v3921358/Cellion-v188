/**
Cellion
@author aa
**/

var status = -1;
var readyToAdvance = false;

function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }
    status++;
    if (status == 0) {
        if (cm.getPlayer().getVMatrixRequirement() && cm.getPlayer().nMobKillsV < 1) {
            cm.sendYesNo("Huh? You look pretty strong, but have you heard of the #bV Matrix#k? It allows you to crush #dNodestones#k and access the #r5#kth Job of your class to raise your strength to new heights!\r\n\r\nDo you have what it takes to start your #r5#kth Job Quest?");
        } else if (cm.getPlayer().getVMatrixRequirement() && cm.getPlayer().nMobKillsV > 0 && cm.getPlayer().nMobKillsV < 1000 || cm.getPlayer().getVMatrixRequirement() && cm.getPlayer().nMagnusKillsV < 1 || cm.getPlayer().getVMatrixRequirement() && cm.getPlayer().nVellumKillsV < 1 || cm.getPlayer().getVMatrixRequirement() && cm.getPlayer().nCrimsonQueenKillsV < 1 || cm.getPlayer().getVMatrixRequirement() && cm.getPlayer().nVonBonKillsV < 1 || cm.getPlayer().getVMatrixRequirement() && cm.getPlayer().nPierreKillsV < 1) {
            cm.sendOk("I see you still haven't completed my tasks, don't waste my time.\r\n\r\n"
					+ "\t#rKill Lv. 200+ Monsters#k (#b" + cm.getPlayer().nMobKillsV + "#k / " + "#b1000#k)\r\n"
					+ "\t#rKill Magnus#k (#b" + cm.getPlayer().nMagnusKillsV + "#k / " + "#b1#k)\r\n"
					+ "\t#rKill Normal Vellum#k (#b" + cm.getPlayer().nVellumKillsV + "#k / " + "#b1#k)\r\n"
					+ "\t#rKill Normal Crimson Queen#k (#b" + cm.getPlayer().nCrimsonQueenKillsV + "#k / " + "#b1#k)\r\n"
					+ "\t#rKill Normal Von Bon#k (#b" + cm.getPlayer().nVonBonKillsV + "#k / " + "#b1#k)\r\n"
					+ "\t#rKill Normal Pierre#k (#b" + cm.getPlayer().nPierreKillsV + "#k / " + "#b1#k)\r\n");
            cm.dispose();
        } else if (cm.getPlayer().hasVMatrix()) {
            cm.sendOk("You have already received your #r5#kth job advancement. Congratulations, and keep up the hard work!");
            cm.dispose();
        } else if (!cm.getPlayer().hasVMatrix() && cm.getPlayer().nMobKillsV > 999 && cm.getPlayer().nMagnusKillsV > 0 && cm.getPlayer().nVellumKillsV > 0 && cm.getPlayer().nCrimsonQueenKillsV > 0 && cm.getPlayer().nVonBonKillsV > 0 && cm.getPlayer().nPierreKillsV > 0) {
            cm.sendYesNo("Looks like you've finally completed my tasks.\r\nAre you ready to proceed with your #r5#kth job advancement?");
            readyToAdvance = true;
        } else {
            cm.sendOk("Talk to me once you're level 200, otherwise I'm not interested.");
            cm.dispose();
        }
    } else if (status == 1) {
        if (!readyToAdvance) {
            cm.sendSimple("Okay, so here's what I you need to do...");
        }
        if (readyToAdvance) {
            if (cm.getPlayer().nMobKillsV > 999 && cm.getPlayer().nMagnusKillsV > 0 && cm.getPlayer().nVellumKillsV > 0 && cm.getPlayer().nCrimsonQueenKillsV > 0 && cm.getPlayer().nVonBonKillsV > 0 && cm.getPlayer().nPierreKillsV > 0) {
                cm.getPlayer().OnUserVMatrix();
                cm.sendOk("You have achieved your final job advancement. You can now crush #dNodestones#k to unlock #dV Matrix#k skills.\r\n\r\nPS. Don't reveal my location to anyone");
                cm.dispose();
            } else {
                cm.sendOk("You don't have the required items to advance.");
                cm.dispose();
            }
        }
    } else if (status == 2) {
        cm.sendOk("In order to unlock the #dV Matrix#k, you'll need to complete these tasks.\r\n\r\n"
					+ "\t#rKill Lv. 200+ Monsters#k (#b" + cm.getPlayer().nMobKillsV + "#k / " + "#b1000#k)\r\n"
					+ "\t#rKill Magnus#k (#b" + cm.getPlayer().nMagnusKillsV + "#k / " + "#b1#k)\r\n"
					+ "\t#rKill Normal Vellum#k (#b" + cm.getPlayer().nVellumKillsV + "#k / " + "#b1#k)\r\n"
					+ "\t#rKill Normal Crimson Queen#k (#b" + cm.getPlayer().nCrimsonQueenKillsV + "#k / " + "#b1#k)\r\n"
					+ "\t#rKill Normal Von Bon#k (#b" + cm.getPlayer().nVonBonKillsV + "#k / " + "#b1#k)\r\n"
					+ "\t#rKill Normal Pierre#k (#b" + cm.getPlayer().nPierreKillsV + "#k / " + "#b1#k)\r\n");
        cm.dispose();
    }
}