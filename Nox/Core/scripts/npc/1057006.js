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
			cm.sendOk("I see you still haven't completed my tasks... Here is your progress #r#n " + cm.getPlayer().nMobKillsV + "/" + "1000 monster kills(level 200+)#r#nI also need you to kill Magnus, Von Bon, Crimson Queen, Pierre, and Vellum. #r#nTalk to me once you've done all of this, and don't waste my time!");
            cm.dispose();
        } else if (cm.getPlayer().hasVMatrix()) {
            cm.sendOk("You have already received your 5th job advancement. Congratulations, and keep up the hard work!");
            cm.dispose();
        } else if (!cm.hasVMatrix() && cm.getPlayer().nMobKillsV > 999 && cm.getPlayer().nMagnusKillsV > 0 && cm.getPlayer().nVellumKillsV > 0 && cm.getPlayer().nCrimsonQueenKillsV > 0 && cm.getPlayer().nVonBonKillsV > 0 && cm.getPlayer().nPierreKillsV > 0) {
            cm.sendYesNo("Looks like you've finally completed my tasks. You're not so bad after all. #r#n Are you ready to proceed with your 5th job advancement?");
            readyToAdvance = true;
        } else {
            cm.sendOk("Talk to me once you're level 200, otherwise I'm not interested.");
            cm.dispose();
        }
    } else if (status == 1) {
        if (!readyToAdvance) {
            cm.sendSimple("Okay, here's what I you need to do...");
        }
        if (readyToAdvance) {
            if (cm.getPlayer().nMobKillsV > 999 && cm.getPlayer().nMagnusKillsV > 0 && cm.getPlayer().nVellumKillsV > 0 && cm.getPlayer().nCrimsonQueenKillsV > 0 && cm.getPlayer().nVonBonKillsV > 0 && cm.getPlayer().nPierreKillsV > 0) {
                cm.getPlayer().OnUserVMatrix();
                cm.sendOk("I have given you your final job advancement. You can now activate nodestones to unlock your 5th job skills. #r#n PS. Don't reveal my location to anyone");
                cm.dispose();
            } else {
                cm.sendOk("You don't have the required items to advance.");
                cm.dispose();
            }
        }
    } else if (status == 2) {
        cm.sendOk("In order to prove you worth to me,#r#nSlay 1000 monsters above level 200#r#nSlay Magnus#r#nSlay Von Bon#r#nSlay Crimson Queen#r#nSlay Pierre#r#nSlay Vellum");
        cm.dispose();
    }
}