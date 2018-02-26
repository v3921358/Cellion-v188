importPackage(Packages.tools);
importPackage(Packages.constants);


var MC = 2500;
var PMC = 3750;
var SMC = 5500;
var RMC = 7250;
var EMC = 9500;
var picked = 0;


var status = -1;
var slot = Array();
var inv;
var sel = -1;
var keepline = -1;
var anotherkeepline = -1;
var itemid = -1;
var state = -1;


function start() {
    status = -1;
    action(1, 0, 0);
}


function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else {
        cm.dispose();
        return;
    }
    if (status == 0) {
        cm.sendSimple("Ah hello there!~\r\nI am a Cubing NPC.\r\n\r\n#L100#I want to Cube!#l\r\n#L101#I want to buy some cubes!#l\r\n#L102#I just cubed - refresh my inventory!#l");
    } else if (status == 1) {
        state = selection;
        if (state == 101) {
            cm.sendSimple("Welcome to cube seller! Choose which cubes you want to buy \r #L0##i5062000#Miracle Cube - 2500 NX\r\n#L1##i5062001#Premium Miracle Cube - 3750 NX\r\n#L2##i5062002#Super Miracle Cube - 5500 NX\r\n#L3##i5062003#Revolutionary Miracle Cube - 7250 NX\r\n#L4##i5062005#Enlightening Miracle Cube - 9500 NX");
        } else if (state == 100) {
            cm.sendYesNo("Ah hello there. Do you want me to check if you can cube anything? \r\nKeeping one line will cost 5k NX! \r\n(After you closed the NPC chat talk to me again to refresh your inventory.)");
        } else if (state == 102) {
            cm.getPlayer().fakeRelog();
            cm.dispose();
            return;
        } else {
            cm.dispose();
            return;
        }
    } else if (status == 2) {
        if (state == 101) {
            picked = selection;
            cm.sendGetText("Enter the amount of cubes you want");
        } else if (state == 100) {
            if (!cm.haveItem(5062000, 1) && !cm.haveItem(5062001, 1) && !cm.haveItem(5062002, 1) && !cm.haveItem(5062003, 1) && !cm.haveItem(5062005, 1)) {
                cm.sendOk("You do not have any of these:\r\n #v5062000# #t5062000#\r\n #v5062001# #t5062001#\r\n #v5062002# #t5062002#\r\n #v5062003# #t5062003#\r\n #v5062005# #t5062005#");
                cm.dispose();
                return;
            } else {
                inv = cm.getInventory(1);
                var bbb = false;
                var selStr = "I can only cube items which have 2 (or more potential lines):\r\n\r\n#b";
                for (var i = 0; i <= inv.getSlotLimit(); i++) {
                    slot.push(i);
                    var it = inv.getItem(i);
                    if (it == null || it.getPotential1() == 0 || it.getPotential2() == 0) {
                        continue;
                    }
                    itemid = it.getItemId();
                    //bwg - 7, with hammer is 9.
                    //therefore, we should make the max slots (natural+7)
                    /*if (cm.getNaturalStats(itemid, "tuc") < 0 || itemid == 1122080 || cm.isCash(itemid)) {
                    continue;
                }*/
                    bbb = true;
                    selStr += "#L" + i + "##v" + itemid + "##t" + itemid + "##l\r\n";
                }
                if (!bbb) {
                    cm.sendOk("You don't have any equipments with two (or more) potential lines on them.");
                    cm.dispose();
                    return;
                }
                cm.sendSimple(selStr + "#k");
            }
        }
    } else if (status == 3) {
        if (state == 101) {
            if (cm.getText() * 0 != 0) {
                cm.sendOk("Numbers only!");
                cm.dispose();
                return;
            }
            if (picked == 0) {
                cm.sendYesNo("This will cost you " + cm.getText() * MC + " NX, do you wish to proceed?");
            }
            if (picked == 1) {
                cm.sendYesNo("This will cost you " + cm.getText() * PMC + " NX, do you wish to proceed?");
            }
            if (picked == 2) {
                cm.sendYesNo("This will cost you " + cm.getText() * SMC + " NX, do you wish to proceed?");
            }
            if (picked == 3) {
                cm.sendYesNo("This will cost you " + cm.getText() * RMC + " NX, do you wish to proceed?");
            }
            if (picked == 4) {
                cm.sendYesNo("This will cost you " + cm.getText() * EMC + " NX, do you wish to proceed?");
            }
        } else if (state == 100) {
            statsSel = inv.getItem(slot[selection]);
            var slots = slot[selection];
            var cuubee = "Which cube to you want to use " + slots + "?";
            if (cm.haveItem(5062000, 1))
                cuubee += "\r\n#L0##v5062000##t5062000##l\r\n";
            if (cm.haveItem(5062001, 1))
                cuubee += "\r\n#L1##v5062001##t5062001##l\r\n";
            if (cm.haveItem(5062002, 1))
                cuubee += "\r\n#L2##v5062002##t5062002##l\r\n";
            if (cm.haveItem(5062005, 1) || cm.haveItem(5062003, 1))
                cuubee += "\r\n#L3##v5062005##t5062005# or #v5062003##t5062003##l\r\n";
            cm.sendSimple(cuubee);
        }
    } else if (status == 4) {
        if (state == 101) {
            if (picked == 0) {
                if (cm.getPlayer().getCSPoints(1) >= cm.getText() * MC) {
                    cm.gainItem(5062000, cm.getText());
                    cm.getPlayer().modifyCSPoints(1, -cm.getText() * MC);
                } else {
                    cm.sendOk("You don't have enough NX");
                }
            }
            if (picked == 1) {
                if (cm.getPlayer().getCSPoints(1) >= cm.getText() * PMC) {
                    cm.gainItem(5062001, cm.getText());
                    cm.getPlayer().modifyCSPoints(1, -cm.getText() * PMC);
                } else {
                    cm.sendOk("You don't have enough NX");
                }
            }
            if (picked == 2) {
                if (cm.getPlayer().getCSPoints(1) >= cm.getText() * SMC) {
                    cm.gainItem(5062002, cm.getText());
                    cm.getPlayer().modifyCSPoints(1, -cm.getText() * SMC);
                } else {
                    cm.sendOk("You don't have enough NX");
                }
            }
            if (picked == 3) {
                if (cm.getPlayer().getCSPoints(1) >= cm.getText() * RMC) {
                    cm.gainItem(5062003, cm.getText());
                    cm.getPlayer().modifyCSPoints(1, -cm.getText() * RMC);
                } else {
                    cm.sendOk("You don't have enough NX");
                }
            }
            if (picked == 4) {
                if (cm.getPlayer().getCSPoints(1) >= cm.getText() * EMC) {
                    cm.gainItem(5062005, cm.getText());
                    cm.getPlayer().modifyCSPoints(1, -cm.getText() * EMC);
                } else {
                    cm.sendOk("You don't have enough NX");
                }
            }
            cm.dispose();
        } else if (state == 100) {
            if (sel == -1) {
                sel = selection;
            }
            if (cm.getPlayer().getCSPoints(1) >= 5000) {
                cm.sendSimple("Do you want to keep one of these? (Costs 5k NX!)\r\nCurrent potentials:\r\n#L1#ID:" + statsSel.getPotential1() + " Name:" + potentialname(statsSel.getPotential1(), itemid) + "#l\r\n#L2#ID:" + statsSel.getPotential2() + " Name:" + potentialname(statsSel.getPotential2(), itemid) + "#l\r\n#L3#ID:" + statsSel.getPotential3() + " Name:" + potentialname(statsSel.getPotential3(), itemid) + "#l\r\n\r\n#L0#No.#l");
            } else {
                cm.sendSimple("Are you sure you want to cube these potentials away?\r\nCurrent potentials:\r\nID:" + statsSel.getPotential1() + " Name:" + potentialname(statsSel.getPotential1(), itemid) + "\r\nID:" + statsSel.getPotential2() + " Name:" + potentialname(statsSel.getPotential2(), itemid) + "\r\nID:" + statsSel.getPotential3() + " Name:" + potentialname(statsSel.getPotential3(), itemid) + "\r\n\r\n#L0#Yes.#l");
            }
        }
    } else if (status == 5) {
        if (keepline == -1) {
            keepline = selection;
        }
        if (selection != 0) {
            if (cm.getPlayer().getCSPoints(1) >= 10000) {
                cm.sendSimple("Do you want to keep another of these? (Costs 10k NX!)\r\nCurrent potentials:\r\n#L1#ID:" + statsSel.getPotential1() + " Name:" + potentialname(statsSel.getPotential1(), itemid) + "#l\r\n#L2#ID:" + statsSel.getPotential2() + " Name:" + potentialname(statsSel.getPotential2(), itemid) + "#l\r\n#L3#ID:" + statsSel.getPotential3() + " Name:" + potentialname(statsSel.getPotential3(), itemid) + "#l\r\n\r\n#L0#No.#l");
            } else {
                cm.sendSimple("Are you sure you want to cube these potentials away?\r\nCurrent potentials:\r\nID:" + statsSel.getPotential1() + " Name:" + potentialname(statsSel.getPotential1(), itemid) + "\r\nID:" + statsSel.getPotential2() + " Name:" + potentialname(statsSel.getPotential2(), itemid) + "\r\nID:" + statsSel.getPotential3() + " Name:" + potentialname(statsSel.getPotential3(), itemid) + "\r\n\r\n#L0#Yes.#l");
            }
        } else {
            if (anotherkeepline == -1) {
                anotherkeepline = 0;
                if (keepline != 0)
                    cm.getPlayer().modifyCSPoints(1, -5000);
            }
            renew(statsSel, sel, keepline, anotherkeepline);
        }
    } else if (status == 6) {
        if (anotherkeepline == -1) {
            anotherkeepline = selection;
            if (keepline != 0)
                cm.getPlayer().modifyCSPoints(1, -15000);
        }
        renew(statsSel, sel, keepline, anotherkeepline);
        status--;
    } else if (status == 7) {
        renew(statsSel, sel, keepline, anotherkeepline);
    } else cm.dispose();
}


function renew(statsSel, selection, keepline, anotherkeepline) {
    if (selection == 0) {
        if (!cm.haveItem(5062000, 1)) {
            cm.sendOk("You have no cubes left!");
            cm.getPlayer().fakeRelog();
            cm.dispose();
            return;
        } else {
            poteee = Math.floor((Math.random()*3)+1);


            if (anotherkeepline == 0 && keepline == 0) {
                if (poteee = 1)
                    statsSel.setPotential1(cm.getRandomPotential());
                if (poteee = 2)
                    statsSel.setPotential2(cm.getRandomPotential());
                if (poteee = 3)
                    statsSel.setPotential3(cm.getRandomPotential());
            } else if (anotherkeepline == 1 || keepline == 1) {
                if (poteee = 2)
                    statsSel.setPotential2(cm.getRandomPotential());
                if (poteee = 3)
                    statsSel.setPotential3(cm.getRandomPotential());
            } else if (anotherkeepline == 2 || keepline == 2) {
                if (poteee = 1)
                    statsSel.setPotential1(cm.getRandomPotential());
                if (poteee = 3)
                    statsSel.setPotential3(cm.getRandomPotential());
            } else if (anotherkeepline == 3 || keepline == 3) {
                if (poteee = 1)
                    statsSel.setPotential1(cm.getRandomPotential());
                if (poteee = 2)
                    statsSel.setPotential2(cm.getRandomPotential());
            }
            cm.gainItem(5062000, -1);
            cm.sendNext("Do you want to keep going?\r\nCurrent potentials:\r\nID:" + statsSel.getPotential1() + " Name:" + potentialname(statsSel.getPotential1(), itemid) + "\r\nID:" + statsSel.getPotential2() + " Name:" + potentialname(statsSel.getPotential2(), itemid) + "\r\nID:" + statsSel.getPotential3() + " Name:" + potentialname(statsSel.getPotential3(), itemid) + "\r\n");
        }
    } else if (selection == 1) {
        if (!cm.haveItem(5062001, 1)) {
            cm.sendOk("You have no cubes left!");
            cm.getPlayer().fakeRelog();
            cm.dispose();
            return;
        } else {
            poteee = Math.floor((Math.random()*3)+1);
            poteeee = Math.floor((Math.random()*3)+1);


            if (anotherkeepline == 0 && keepline == 0) {
                if (poteee = 1)
                    statsSel.setPotential1(cm.getRandomPotential());
                if (poteee = 2)
                    statsSel.setPotential2(cm.getRandomPotential());
                if (poteee = 3)
                    statsSel.setPotential3(cm.getRandomPotential());
                if (poteeee = 1)
                    statsSel.setPotential1(cm.getRandomPotential());
                if (poteeee = 2)
                    statsSel.setPotential2(cm.getRandomPotential());
                if (poteeee = 3)
                    statsSel.setPotential3(cm.getRandomPotential());
            } else if (anotherkeepline == 0 && keepline != 0) {
                cm.getPlayer().modifyCSPoints(1, -5000);
                if (keepline != 1)
                    statsSel.setPotential1(cm.getRandomPotential());
                if (keepline != 2)
                    statsSel.setPotential2(cm.getRandomPotential());
                if (keepline != 3)
                    statsSel.setPotential3(cm.getRandomPotential());
            } else if (anotherkeepline != 0 && keepline != 0) {
                cm.getPlayer().modifyCSPoints(1, -15000);
                if (anotherkeepline != 1 && keepline != 1)
                    statsSel.setPotential1(cm.getRandomPotential());
                if (anotherkeepline != 2 && keepline != 2)
                    statsSel.setPotential2(cm.getRandomPotential());
                if (anotherkeepline != 3 && keepline != 3)
                    statsSel.setPotential3(cm.getRandomPotential());
            }
            cm.gainItem(5062001, -1);
            cm.sendNext("Do you want to keep going?\r\nCurrent potentials:\r\nID:" + statsSel.getPotential1() + " Name:" + potentialname(statsSel.getPotential1(), itemid) + "\r\nID:" + statsSel.getPotential2() + " Name:" + potentialname(statsSel.getPotential2(), itemid) + "\r\nID:" + statsSel.getPotential3() + " Name:" + potentialname(statsSel.getPotential3(), itemid) + "\r\n");
        }
    } else if (selection == 2) {
        if (!cm.haveItem(5062002, 1)) {
            cm.sendOk("You have no cubes left!");
            cm.getPlayer().fakeRelog();
            cm.dispose();
            return;
        } else {
            if (anotherkeepline == 0 && keepline == 0) {
                statsSel.setPotential1(cm.getRandomPotential());
                statsSel.setPotential2(cm.getRandomPotential());
                statsSel.setPotential3(cm.getRandomPotential());
            } else if (anotherkeepline == 0 && keepline != 0) {
                if (keepline != 1)
                    statsSel.setPotential1(cm.getRandomPotential());
                if (keepline != 2)
                    statsSel.setPotential2(cm.getRandomPotential());
                if (keepline != 3)
                    statsSel.setPotential3(cm.getRandomPotential());
            } else if (anotherkeepline != 0 && keepline != 0) {
                if (anotherkeepline != 1 && keepline != 1)
                    statsSel.setPotential1(cm.getRandomPotential());
                if (anotherkeepline != 2 && keepline != 2)
                    statsSel.setPotential2(cm.getRandomPotential());
                if (anotherkeepline != 3 && keepline != 3)
                    statsSel.setPotential3(cm.getRandomPotential());
            }
            cm.gainItem(5062002, -1);
            cm.sendNext("Do you want to keep going?\r\nCurrent potentials:\r\nID:" + statsSel.getPotential1() + " Name:" + potentialname(statsSel.getPotential1(), itemid) + "\r\nID:" + statsSel.getPotential2() + " Name:" + potentialname(statsSel.getPotential2(), itemid) + "\r\nID:" + statsSel.getPotential3() + " Name:" + potentialname(statsSel.getPotential3(), itemid) + "\r\n");
        }
    } else if (selection == 3) {
        if (!cm.haveItem(5062005, 1) && !cm.haveItem(5062003, 1)) {
            cm.sendOk("You have no cubes left!");
            cm.getPlayer().fakeRelog();
            cm.dispose();
            return;
        } else {
            if (anotherkeepline == 0 && keepline == 0) {
                statsSel.setPotential1(cm.getRandomPotential());
                statsSel.setPotential2(cm.getRandomPotential());
                statsSel.setPotential3(cm.getRandomPotential());
            } else if (anotherkeepline == 0 && keepline != 0) {
                if (keepline != 1)
                    statsSel.setPotential1(cm.getRandomPotential());
                if (keepline != 2)
                    statsSel.setPotential2(cm.getRandomPotential());
                if (keepline != 3)
                    statsSel.setPotential3(cm.getRandomPotential());
            } else if (anotherkeepline != 0 && keepline != 0) {
                if (anotherkeepline != 1 && keepline != 1)
                    statsSel.setPotential1(cm.getRandomPotential());
                if (anotherkeepline != 2 && keepline != 2)
                    statsSel.setPotential2(cm.getRandomPotential());
                if (anotherkeepline != 3 && keepline != 3)
                    statsSel.setPotential3(cm.getRandomPotential());
            }
            if (cm.haveItem(5062003, 1))
                cm.gainItem(5062003, -1);
            else
                cm.gainItem(5062005, -1);
            cm.sendNext("Do you want to keep going?\r\nCurrent potentials:\r\nID:" + statsSel.getPotential1() + " Name:" + potentialname(statsSel.getPotential1(), itemid) + "\r\nID:" + statsSel.getPotential2() + " Name:" + potentialname(statsSel.getPotential2(), itemid) + "\r\nID:" + statsSel.getPotential3() + " Name:" + potentialname(statsSel.getPotential3(), itemid) + "\r\n");
        }
    }
}


function potentialname(potpot, item) { 
cm.resolvePotentialID(item, potpot); 
}