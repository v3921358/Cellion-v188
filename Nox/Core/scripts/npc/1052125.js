/* 
 NPC Name: 		Junny
 Map(s): 		Kerning Square: 7th Floor 8th Floor Area A
 */

var status = -1;

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else
        status--;

    if (status == 0) {
        cm.sendSimple("Hold up! Access to this area is restriced. \r\n\r\n#L0##bI'm helping #eBlake#n right now#l\r\n#L1#I'm a #rVIP#b at this Shopping Center!#k#l");

    } else if (status == 1) {
        if (selection == 0) {
            if (cm.getQuestStatus(2286) == 1 || cm.getQuestStatus(2287) == 1 || cm.getQuestStatus(2288) == 1 || cm.getPlayerStat("GM") == 1) {
                cm.sendNext("Oh, you must be #h #., the person helping #bBlake#k. I will let you enter the 7th, 8th floor #bNormal Zone#k. The VIP Zone can be used once every 30 minutes. Go ahead and enter now.");

                status = 9;
            } else {
                cm.sendOk("I dont think you're the person that is #bBlake#k. I cannot allow you to enter this place.");
                cm.dispose();
            }
        } else {
            if (cm.getQuestStatus(2291) >= 2) {
                status = 19;
                cm.sendNext("Oh, you must be #h #., the VIP person helped #bBlake#k. I will let you enter the 7th, 8th floor #bVIP Zone#k. The VIP Zone can be used once every 30 minutes. Go ahead and enter now.");
            } else {
                cm.sendOk("I dont think you're a #bVIP person#k. I cannot allow you to enter this place.");
                cm.dispose();
            }
        }
    } else if (status == 10) { // helping blake
       //         cm.enterMiniDungeon(103040400, 103040410, 10, 1, null); // baseid, targetid, num dungeons, portalid, portalname
     //   cm.startSelfTimer(60 * 50, 103040400);
     //   cm.dispose();


        var em = cm.getEventManager("KerningSquare_NormalZone");
        if (em == null) {
            cm.playerMessage("The Normal zone is closed for now. Please try again later :(");
        } else {
            if (cm.getParty() != null) {
                em.startInstance(cm.getParty(), cm.getMap());
            } else {
                em.startInstance(cm.getPlayer());
            }
        }
        cm.dispose();
    } else if (status == 20) { // VIP zone
        var questRecord = cm.getQuestRecord(150050);
        if (questRecord.getInfoData() == "1") {
            if (cm.getParty() != null) {
                cm.sendOk("The VIP zone is only available for 1 person. Please leave party before proceeding.");
            } else {
                var em = cm.getEventManager("KerningSquare_VIPZone");
                if (em == null) {
                    cm.playerMessage("The VIP zone is closed for now. Please try again later :(");
                } else {
                    em.startInstance(cm.getPlayer());
                    
                    // Update quest record to disable further entry
                    questRecord.setInfoData("0");
                }
            }
        } else {
            cm.sendNext("Please wait 30 minutes between entry of the VIP area..");
        }
        cm.dispose();
    }
}