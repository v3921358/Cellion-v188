/*
	NPC Name: 		Adobis
	Map(s): 		El Nath : Entrance to Zakum Altar
	Description: 		Zakum battle starter
*/
var status = 0;

function start() {
    status =0;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (cm.getPlayer().getMapId() == 211041700) {
        if (selection < 100) {
            cm.sendSimple("#e#b<Ranmaru Expedition>##\r\nSelect the Expedition you wish to challenge.\r\n\r\n#r#L100#Ranmaru Expedition#l\r\n#L101#Madman Ranmaru Expedition#l");
        } else {
            if (selection == 100) {
                cm.warp(807300100,0);
            } else if (selection == 101) {
                cm.warp(807300200,0);
            }
            cm.dispose();
        }
        return;
    } else if (cm.getPlayer().getMapId() == 807300200) {
        switch (status) {
            case 0:
                if (cm.getPlayer().getLevel() < 180) {
                    cm.sendOk("There is a level requirement of 180 to attempt Madman Ranmaru.");
                    cm.dispose();
                    return;
                }
                if (cm.getPlayer().getClient().getChannel() != 1) {
                    cm.sendOk("Madman Ranmaru may only be attempted on channel 1.");
                    cm.dispose();
                    return;
                }
                var em = cm.getEventManager("RanmaruBattle");

                if (em == null) {
                    cm.sendOk("The event isn't started, please contact a GM.");
                    cm.safeDispose();
                    return;
                }
                var prop = em.getProperty("state");
                var marr = cm.getQuestRecord(160112);
                var data = marr.getCustomData();
                if (data == null) {
                    marr.setCustomData("0");
                    data = "0";
                }
                var time = parseInt(data);
                if (prop == null || prop.equals("0")) {
                    var squadAvailability = cm.getSquadAvailability("RanmaruMad");
                    if (squadAvailability == -1) {
                        status = 1;
                        if (time + (24 * 3600000) >= cm.getCurrentTime() && !cm.getPlayer().isGM()) {
                            cm.sendOk("You have already went to Madman Ranmaru in the past 24 hours. Time left: " + cm.getReadableMillis(cm.getCurrentTime(), time + (12 * 3600000)));
                            cm.dispose();
                            return;
                        }
                        cm.sendYesNo("Are you interested in becoming the leader of the expedition Squad?");

                    } else if (squadAvailability == 1) {
                        if (time + (24 * 3600000) >= cm.getCurrentTime() && !cm.getPlayer().isGM()) {
                            cm.sendOk("You have already went to Madman Ranmaru in the past 24 hours. Time left: " + cm.getReadableMillis(cm.getCurrentTime(), time + (24 * 3600000)));
                            cm.dispose();
                            return;
                        }
                        // -1 = Cancelled, 0 = not, 1 = true
                        var type = cm.isSquadLeader("RanmaruMad");
                        if (type == -1) {
                            cm.sendOk("The squad has ended, please re-register.");
                            cm.safeDispose();
                        } else if (type == 0) {
                            var memberType = cm.isSquadMember("RanmaruMad");
                            if (memberType == 2) {
                                cm.sendOk("You been banned from the squad.");
                                cm.safeDispose();
                            } else if (memberType == 1) {
                                status = 5;
                                cm.sendSimple("What do you want to do? \r\n#b#L0#Check out members#l \r\n#b#L1#Join the squad#l \r\n#b#L2#Withdraw from squad#l");
                            } else if (memberType == -1) {
                                cm.sendOk("The squad has ended, please re-register.");
                                cm.safeDispose();
                            } else {
                                status = 5;
                                cm.sendSimple("What do you want to do? \r\n#b#L0#Check out members#l \r\n#b#L1#Join the squad#l \r\n#b#L2#Withdraw from squad#l");
                            }
                        } else { // Is leader
                            status = 10;
                            cm.sendSimple("What do you want to do? \r\n#b#L0#Check out members#l \r\n#b#L1#Remove member#l \r\n#b#L2#Edit restricted list#l \r\n#r#L3#Enter map#l");
                        // TODO viewing!
                        }
                    } else {
                        var eim = cm.getDisconnected("RanmaruBattle");
                        if (eim == null) {
                            var squd = cm.getSquad("RanmaruMad");
                            if (squd != null) {
                                if (time + (24 * 3600000) >= cm.getCurrentTime() && !cm.getPlayer().isGM()) {
                                    cm.sendOk("You have already went to Madman Ranmaru in the past 24 hours. Time left: " + cm.getReadableMillis(cm.getCurrentTime(), time + (24 * 3600000)));
                                    cm.dispose();
                                    return;
                                }
                                cm.sendYesNo("The squad's battle against the boss has already begun.\r\n" + squd.getNextPlayer());
                                status = 3;
                            } else {
                                cm.sendOk("The squad's battle against the boss has already begun.");
                                cm.safeDispose();
                            }
                        } else {
                            cm.sendYesNo("Ah, you have returned. Would you like to join your squad in the fight again?");
                            status = 2;
                        }
                    }
                } else {
                    var eim = cm.getDisconnected("RanmaruBattle");
                    if (eim == null) {
                        var squd = cm.getSquad("RanmaruMad");
                        if (squd != null) {
                            if (time + (24 * 3600000) >= cm.getCurrentTime() && !cm.getPlayer().isGM()) {
                                cm.sendOk("You have already went to Madman Ranmaru in the past 24 hours. Time left: " + cm.getReadableMillis(cm.getCurrentTime(), time + (24 * 3600000)));
                                cm.dispose();
                                return;
                            }
                            cm.sendYesNo("The squad's battle against the boss has already begun.\r\n" + squd.getNextPlayer());
                            status = 3;
                        } else {
                            cm.sendOk("The squad's battle against the boss has already begun.");
                            cm.safeDispose();
                        }
                    } else {
                        cm.sendYesNo("Ah, you have returned. Would you like to join your squad in the fight again?");
                        status = 2;
                    }
                }
                break;
            case 1:
                if (mode == 1) {
                    if (cm.registerSquad("RanmaruMad", 5, " has been named the Leader of the squad (Chaos). If you would you like to join please register for the Expedition Squad within the time period.")) {
                        cm.sendOk("You have been named the Leader of the Squad. For the next 5 minutes, you can add the members of the Expedition Squad.");
                    } else {
                        cm.sendOk("An error has occurred adding your squad.");
                    }
                } else {
                    cm.sendOk("Talk to me if you want to become the leader of the Expedition squad.")
                }
                cm.safeDispose();
                break;
            case 2:
                if (!cm.reAdd("RanmaruBattle", "RanmaruMad")) {
                    cm.sendOk("Error... please try again.");
                }
                cm.dispose();
                break;
            case 3:
                if (mode == 1) {
                    var squd = cm.getSquad("RanmaruMad");
                    if (squd != null && !squd.getAllNextPlayer().contains(cm.getPlayer().getName())) {
                        squd.setNextPlayer(cm.getPlayer().getName());
                        cm.sendOk("You have reserved the spot.");
                    }
                }
                cm.dispose();
                break;
            case 5:
                if (selection == 0) {
                    if (!cm.getSquadList("RanmaruMad", 0)) {
                        cm.sendOk("Due to an unknown error, the request for squad has been denied.");
                        cm.safeDispose();
                    } else {
                        cm.dispose();
                    }
                } else if (selection == 1) { // join
                    var ba = cm.addMember("RanmaruMad", true);
                    if (ba == 2) {
                        cm.sendOk("The squad is currently full, please try again later.");
                        cm.safeDispose();
                    } else if (ba == 1) {
                        cm.sendOk("You have joined the squad successfully");
                        cm.safeDispose();
                    } else {
                        cm.sendOk("You are already part of the squad.");
                        cm.safeDispose();
                    }
                } else {// withdraw
                    var baa = cm.addMember("RanmaruMad", false);
                    if (baa == 1) {
                        cm.sendOk("You have withdrew from the squad successfully");
                        cm.safeDispose();
                    } else {
                        cm.sendOk("You are not part of the squad.");
                        cm.safeDispose();
                    }
                }
                break;
            case 10:
                if (selection == 0) {
                    if (!cm.getSquadList("RanmaruMad", 0)) {
                        cm.sendOk("Due to an unknown error, the request for squad has been denied.");
                    }
                    cm.safeDispose();
                } else if (selection == 1) {
                    status = 11;
                    if (!cm.getSquadList("RanmaruMad", 1)) {
                        cm.sendOk("Due to an unknown error, the request for squad has been denied.");
                        cm.safeDispose();
                    }

                } else if (selection == 2) {
                    status = 12;
                    if (!cm.getSquadList("RanmaruMad", 2)) {
                        cm.sendOk("Due to an unknown error, the request for squad has been denied.");
                        cm.safeDispose();
                    }

                } else if (selection == 3) { // get insode
                    if (cm.getSquad("RanmaruMad") != null) {
                        var dd = cm.getEventManager("RanmaruBattle");
                        dd.startInstance(cm.getSquad("RanmaruMad"), cm.getMap(), 160112);
                        cm.dispose();
                    } else {
                        cm.sendOk("Due to an unknown error, the request for squad has been denied.");
                        cm.safeDispose();
                    }
                }
                break;
            case 11:
                cm.banMember("RanmaruMad", selection);
                cm.dispose();
                break;
            case 12:
                if (selection != -1) {
                    cm.acceptMember("RanmaruMad", selection);
                }
                cm.dispose();
                break;
        }
    } else {
        switch (status) {
            case 0:
                if (cm.getPlayer().getLevel() < 120) {
                    cm.sendOk("There is a level requirement of 120 to attempt Mori Ranmaru.");
                    cm.dispose();
                    return;
                }
                if (cm.getPlayer().getClient().getChannel() != 2 && cm.getPlayer().getClient().getChannel() != 3) {
                    cm.sendOk("Mori Ranmaru may only be attempted on channel 2, 3");
                    cm.dispose();
                    return;
                }
                var em = cm.getEventManager("RanmaruNorm");

                if (em == null) {
                    cm.sendOk("The event isn't started, please contact a GM.");
                    cm.safeDispose();
                    return;
                }
                var prop = em.getProperty("state");
                var marr = cm.getQuestRecord(160113);
                var data = marr.getCustomData();
                if (data == null) {
                    marr.setCustomData("0");
                    data = "0";
                }
                var time = parseInt(data);
                if (prop == null || prop.equals("0")) {
                    var squadAvailability = cm.getSquadAvailability("Ranmaru");
                    if (squadAvailability == -1) {
                        status = 1;
                        if (time + (12 * 3600000) >= cm.getCurrentTime() && !cm.getPlayer().isGM()) {
                            cm.sendOk("You have already went to Mori Ranmaru in the past 12 hours. Time left: " + cm.getReadableMillis(cm.getCurrentTime(), time + (24 * 360000)));
                            cm.dispose();
                            return;
                        }
                        cm.sendYesNo("Are you interested in becoming the leader of the expedition Squad?");

                    } else if (squadAvailability == 1) {
                        if (time + (12 * 3600000) >= cm.getCurrentTime() && !cm.getPlayer().isGM()) {
                            cm.sendOk("You have already went to Mori Ranmaru in the past 12 hours. Time left: " + cm.getReadableMillis(cm.getCurrentTime(), time + (24 * 360000)));
                            cm.dispose();
                            return;
                        }
                        // -1 = Cancelled, 0 = not, 1 = true
                        var type = cm.isSquadLeader("Ranmaru");
                        if (type == -1) {
                            cm.sendOk("The squad has ended, please re-register.");
                            cm.safeDispose();
                        } else if (type == 0) {
                            var memberType = cm.isSquadMember("Ranmaru");
                            if (memberType == 2) {
                                cm.sendOk("You been banned from the squad.");
                                cm.safeDispose();
                            } else if (memberType == 1) {
                                status = 5;
                                cm.sendSimple("What do you want to do? \r\n#b#L0#Check out members#l \r\n#b#L1#Join the squad#l \r\n#b#L2#Withdraw from squad#l");
                            } else if (memberType == -1) {
                                cm.sendOk("The squad has ended, please re-register.");
                                cm.safeDispose();
                            } else {
                                status = 5;
                                cm.sendSimple("What do you want to do? \r\n#b#L0#Check out members#l \r\n#b#L1#Join the squad#l \r\n#b#L2#Withdraw from squad#l");
                            }
                        } else { // Is leader
                            status = 10;
                            cm.sendSimple("What do you want to do? \r\n#b#L0#Check out members#l \r\n#b#L1#Remove member#l \r\n#b#L2#Edit restricted list#l \r\n#r#L3#Enter map#l");
                        // TODO viewing!
                        }
                    } else {
                        var eim = cm.getDisconnected("RanmaruNorm");
                        if (eim == null) {
                            var squd = cm.getSquad("Ranmaru");
                            if (squd != null) {
                                if (time + (12 * 3600000) >= cm.getCurrentTime() && !cm.getPlayer().isGM()) {
                                    cm.sendOk("You have already went to Mori Ranmaru in the past 12 hours. Time left: " + cm.getReadableMillis(cm.getCurrentTime(), time + (24 * 360000)));
                                    cm.dispose();
                                    return;
                                }
                                cm.sendYesNo("The squad's battle against the boss has already begun.\r\n" + squd.getNextPlayer());
                                status = 3;
                            } else {
                                cm.sendOk("The squad's battle against the boss has already begun.");
                                cm.safeDispose();
                            }
                        } else {
                            cm.sendYesNo("Ah, you have returned. Would you like to join your squad in the fight again?");
                            status = 1;
                        }
                    }
                } else {
                    var eim = cm.getDisconnected("RanmaruNorm");
                    if (eim == null) {
                        var squd = cm.getSquad("Ranmaru");
                        if (squd != null) {
                            if (time + (12 * 3600000) >= cm.getCurrentTime() && !cm.getPlayer().isGM()) {
                                cm.sendOk("You have already went to Mori Ranmaru in the past 12 hours. Time left: " + cm.getReadableMillis(cm.getCurrentTime(), time + (24 * 360000)));
                                cm.dispose();
                                return;
                            }
                            cm.sendYesNo("The squad's battle against the boss has already begun.\r\n" + squd.getNextPlayer());
                            status = 3;
                        } else {
                            cm.sendOk("The squad's battle against the boss has already begun.");
                            cm.safeDispose();
                        }
                    } else {
                        cm.sendYesNo("Ah, you have returned. Would you like to join your squad in the fight again?");
                        status = 1;
                    }
                }
                break;
            case 1:
                if (mode == 1) {
                    if (cm.registerSquad("Ranmaru", 5, " has been named the Leader of the squad (Regular). If you would you like to join please register for the Expedition Squad within the time period.")) {
                        cm.sendOk("You have been named the Leader of the Squad. For the next 5 minutes, you can add the members of the Expedition Squad.");
                    } else {
                        cm.sendOk("An error has occurred adding your squad.");
                    }
                } else {
                    cm.sendOk("Talk to me if you want to become the leader of the Expedition squad.")
                }
                cm.safeDispose();
                break;
            case 2:
                if (!cm.reAdd("RanmaruNorm", "Ranmaru")) {
                    cm.sendOk("Error... please try again.");
                }
                cm.safeDispose();
                break;
            case 3:
                if (mode == 1) {
                    var squd = cm.getSquad("Ranmaru");
                    if (squd != null && !squd.getAllNextPlayer().contains(cm.getPlayer().getName())) {
                        squd.setNextPlayer(cm.getPlayer().getName());
                        cm.sendOk("You have reserved the spot.");
                    }
                }
                cm.dispose();
                break;
            case 5:
                if (selection == 0) {
                    if (!cm.getSquadList("Ranmaru", 0)) {
                        cm.sendOk("Due to an unknown error, the request for squad has been denied.");
                        cm.safeDispose();
                    } else {
                        cm.dispose();
                    }
                } else if (selection == 1) { // join
                    var ba = cm.addMember("Ranmaru", true);
                    if (ba == 2) {
                        cm.sendOk("The squad is currently full, please try again later.");
                        cm.safeDispose();
                    } else if (ba == 1) {
                        cm.sendOk("You have joined the squad successfully");
                        cm.safeDispose();
                    } else {
                        cm.sendOk("You are already part of the squad.");
                        cm.safeDispose();
                    }
                } else {// withdraw
                    var baa = cm.addMember("Ranmaru", false);
                    if (baa == 1) {
                        cm.sendOk("You have withdrew from the squad successfully");
                        cm.safeDispose();
                    } else {
                        cm.sendOk("You are not part of the squad.");
                        cm.safeDispose();
                    }
                }
                break;
            case 10:
                if (selection == 0) {
                    if (!cm.getSquadList("Ranmaru", 0)) {
                        cm.sendOk("Due to an unknown error, the request for squad has been denied.");
                    }
                    cm.safeDispose();
                } else if (selection == 1) {
                    status = 11;
                    if (!cm.getSquadList("Ranmaru", 1)) {
                        cm.sendOk("Due to an unknown error, the request for squad has been denied.");
                        cm.safeDispose();
                    }

                } else if (selection == 2) {
                    status = 12;
                    if (!cm.getSquadList("Ranmaru", 2)) {
                        cm.sendOk("Due to an unknown error, the request for squad has been denied.");
                        cm.safeDispose();
                    }

                } else if (selection == 3) { // get insode
                    if (cm.getSquad("Ranmaru") != null) {
                        var dd = cm.getEventManager("RanmaruNorm");
                        dd.startInstance(cm.getSquad("Ranmaru"), cm.getMap(), 160113);
                        cm.dispose();
                    } else {
                        cm.sendOk("Due to an unknown error, the request for squad has been denied.");
                        cm.safeDispose();
                    }
                }
                break;
            case 11:
                cm.banMember("Ranmaru", selection);
                cm.dispose();
                break;
            case 12:
                if (selection != -1) {
                    cm.acceptMember("Ranmaru", selection);
                }
                cm.dispose();
                break;
        }
    }
}