/**
Cellion
@author aa
**/

var status = -1;
var readyToAdvance = false;
var hmagbossDrop = 4001126;
var cra1stbossDrop = 4001126;
var cra2ndbossDrop = 4001126;
var cra3rdbossDrop = 4001126;
var cra4thbossDrop = 4001126;

function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
        return;
    }
    status++;
    if (status == 0) {
        //if (cm.getPlayer().getVMatrixRequirement() && cm.getPlayer().getVMatrixKills() < 1) {
        //    cm.sendYesNo("Huh? You look pretty strong, but have you heard of the #bV Matrix#k? It allows you to crush #dNodestones#k and access the #r5#kth Job of your class to raise your strength to new heights!\r\n\r\nDo you have what it takes to start your #r5#kth Job Quest?");
        //} else if (cm.getPlayer().getVMatrixRequirement() && cm.getPlayer().getVMatrixKills() > 0 && cm.getPlayer().getVMatrixKills() < 1000 || cm.getPlayer().getVMatrixRequirement() && !cm.haveItem(hmagbossDrop, 1) || cm.getPlayer().getVMatrixRequirement() && !cm.haveItem(cra1stbossDrop, 1) || cm.getPlayer().getVMatrixRequirement() && !cm.haveItem(cra2ndbossDrop, 1) || cm.getPlayer().getVMatrixRequirement() && !cm.haveItem(cra3rdbossDrop, 1) || cm.getPlayer().getVMatrixRequirement() && !cm.haveItem(cra4thbossDrop, 1)) {
            cm.getPlayer().getVMatrixKills();
			cm.sendOk("I see you still haven't completed my tasks... Here is your progress #r#n " + cm.getPlayer().getVMatrixKills + "/" + "1000 monster kills(level 200+)#r#nI also need you to get me 1 __ from Von Bon, 1 __ from Crimson Queen, 1 __ from Pierre, and 1 __ from Vellum. #r#nTalk to me once you've done all of this, don't waste my time!");
            cm.dispose();
        /*} else if (cm.getPlayer().hasVMatrix()) {
            cm.sendOk("You have already received your 5th job advancement. Congratulations, and keep up the hard work!");
            cm.dispose();
        } else if (!cm.hasVMatrix() && cm.getPlayer().getVMatrixKills() >= 1000 && cm.haveItem(hmagbossDrop, 1) && cm.haveItem(cra1stbossDrop, 1) && cm.haveItem(cra2ndbossDrop, 1) && cm.haveItem(cra3rdbossDrop, 1) && cm.haveItem(cra4thbossDrop, 1)) {
            cm.sendYesNo("Looks like you've finally completed my tasks. You're not so bad after all. #r#n Are you ready to proceed with your 5th job advancement?");
            readyToAdvance = true;
        } else {
            cm.sendOk("You do not have the required level to start your 5th job advancement quest. Please talk to me when you do. (Level 200)");
            cm.dispose();
        }*/
    } else if (status == 1) {
        if (!readyToAdvance) {
            cm.sendSimple("Okay, here's what I you need to do...");
        }
        if (readyToAdvance) {
            if (cm.getPlayer().getVMatrixKills() >= 1000 && cm.haveItem(hmagbossDrop, 1) && cm.haveItem(cra1stbossDrop, 1) && cm.haveItem(cra2ndbossDrop, 1) && cm.haveItem(cra3rdbossDrop, 1) && cm.haveItem(cra4thbossDrop, 1)) {
                cm.gainItem(hmagbossDrop, -1) && cm.gainItem(cra1stbossDrop, -1) && cm.gainItem(cra2ndbossDrop, -1) && cm.gainItem(cra3rdbossDrop, -1) && cm.gainItem(cra4thbossDrop, -1);
                cm.getPlayer().OnUserVMatrix();
                cm.sendOk("I have given you your final job advancement. You can now activate nodestones to unlock your 5th job skills.");
                cm.dispose();
            } else {
                cm.sendOk("You don't have the required items to advance.");
                cm.dispose();
            }
        }
    } else if (status == 2) {
        cm.sendOk("In order to prove you worth to me,#r#nSlay 1000 monsters above level 200#r#nGet 1 __ from Magnus#r#n Get 1 __ from Von Bon#r#nGet 1 __ from Crimson Queen#r#nGet 1 __ from Pierre#r#nGet 1 __ from Vellum");
        cm.dispose();
    }
}