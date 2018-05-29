/*
 * Cellion
 * First Job Advancement NPC
 *
 * @author aa
 */

var status;
var jobId;
var jobName;
var selectedJob;

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.sendNextS("Enjoy your adventure.", 5);
            cm.dispose();
        }
        status--;
    }

    jobId = cm.getPlayer().getJob();

    if (status == 0) {
        if (jobId == 0) {
            cm.sendSimple("Welcome and thank you for choosing #rCellion#k!\r\nWhich path would you like to take? #d\r\n #L100# Warrior #l \r\n #L200# Magician #l \r\n #L300# Bowman #l \r\n #L400# Thief #l \r\n #L500# Pirate #l");
        } else if (jobId == 1000) {
            cm.sendSimple("Welcome and thank you for choosing #rCellion#k!\r\nWhich path would you like to take? #d\r\n #L1100# Dawn Warrior #l \r\n #L1200# Blaze Wizard #l \r\n #L1300# Wind Archer #l \r\n #L1400# Night Walker #l \r\n #L1500# Thunder Breaker #l");
        } else if (jobId == 3000) {
            cm.sendSimple("Welcome and thank you for choosing #rCellion#k!\r\nWhich path would you like to take? #d\r\n #L3200# Battle Mage #l \r\n #L3300# Wild Hunter #l \r\n #L3500# Mechanic #l \r\n #L3700# Blaster #l");
        } else if (jobId == 3001) {
            cm.sendSimple("Welcome and thank you for choosing #rCellion#k!\r\nWhich path would you like to take? #d\r\n #L3100# Demon Slayer #l \r\n #L3101# Demon Avenger #l");
        } else {
            cm.sendNext("Welcome and thank you for choosing #rCellion#k!");
        }
    } else if (status == 1) {
        selectedJob = selection;
        if (selectedJob > 0) {
            cm.sendYesNo("Are you sure you want to choose this path?\r\nThis cannot be changed in the future.");
        } else {
            cm.sendYesNo("Are you ready to start your adventure?");
        }
    } else if (status == 2) {
        if (selectedJob > 0 && jobId != selectedJob) { //selected job check
            cm.changeJob(selectedJob);
        }
		switch (selectedJob) {
			case 100: //WARRIOR
				jobName = "Warrior";
				cm.gainItem(1302007, 1); //10 longsword
				cm.gainItem(1302008, 1); //30 longsword
				cm.gainItem(1312000, 1); //15 1haxe
				cm.gainItem(1312005, 1); //30 1haxe
				cm.gainItem(1322000, 1); //15 1hbw
				cm.gainItem(1322014, 1); //30 1hbw
				cm.gainItem(1402001, 1); //10 2hsword
				cm.gainItem(1402002, 1); //30 2hsword
				cm.gainItem(1412001, 1); //10 2haxe
				cm.gainItem(1412002, 1); //20 2haxe
				cm.gainItem(1422000, 1); //10 2hbw
				cm.gainItem(1422004, 1); //25 2hbw
				cm.gainItem(1432000, 1); //10 spear
				cm.gainItem(1432002, 1); //30 spear
				cm.gainItem(1442000, 1); //10 polearm
				cm.gainItem(1442001, 1); //30 polearm
				break;
			case 200: //MAGICIAN
				jobName = "Magician";
				cm.gainItem(1372005, 1); //8 wand
				cm.gainItem(1372003, 1); //28 wand
				cm.gainItem(1382000, 1); //10 staff
				cm.gainItem(1382017, 1); //30 staff
				break;
			case 300: //BOWMAN
				jobName = "Bowman";
				cm.gainItem(1452002, 1); //10 bow
				cm.gainItem(1452005, 1); //30 bow
				cm.gainItem(1462001, 1); //12 xbow
				cm.gainItem(1462004, 1); //32 xbow
				cm.gainItem(2060001, 5000); //arrows
				cm.gainItem(2061001, 5000); //xbowarrows
				break;
			case 400: //THIEF
				jobName = "Thief";
				cm.gainItem(1472000, 1); //10 claw
				cm.gainItem(1472008, 1); //30 claw
				cm.gainItem(1332007, 1); //8 dagger
				cm.gainItem(1332009, 1); //30 dagger
				cm.gainItem(2070000, 5000); //subis
				break;
			case 500: //PIRATE
				jobName = "Pirate";
				cm.gainItem(1482000, 1); //10 knuckle
				cm.gainItem(1482004, 1); //30 knuckle
				cm.gainItem(1492000, 1); //10 pistol
				cm.gainItem(1492004, 1); //30 pistol
				cm.gainItem(2330000, 5000); //bullet
				break;
			case 1100: //DAWN WARRIOR
				jobName = "Dawn Warrior";
				cm.gainItem(1302007, 1); //10 longsword
				cm.gainItem(1302008, 1); //30 longsword
				cm.gainItem(1402001, 1); //10 2hsword
				cm.gainItem(1402002, 1); //30 2hsword
				cm.gainItem(1352970, 1); //cygnus secondary
				break;
			case 1200: //BLAZE WIZARD
				jobName = "Blaze Wizard";
				cm.gainItem(1372005, 1); //8 wand
				cm.gainItem(1372003, 1); //28 wand
				cm.gainItem(1382000, 1); //10 staff
				cm.gainItem(1382017, 1); //30 staff
				cm.gainItem(1352970, 1); //cygnus secondary
				break;
			case 1300: //WIND ARCHER
				jobName = "Wind Archer";
				cm.gainItem(1452002, 1); //10 bow
				cm.gainItem(1452005, 1); //30 bow
				cm.gainItem(1352970, 1); //cygnus secondary
				cm.gainItem(2060001, 5000); //arrows
				break;
			case 1400: //NIGHT WALKER
				jobName = "Night Walker";
				cm.gainItem(1472000, 1); //10 claw
				cm.gainItem(1472008, 1); //30 claw
				cm.gainItem(1352970, 1); //cygnus secondary
				cm.gainItem(2070000, 5000); //subis
				break;
			case 1500: //THUNDER BREAKER
				jobName = "Thunder Breaker";
				cm.gainItem(1482000, 1); //10 knuckle
				cm.gainItem(1482004, 1); //30 knuckle
				cm.gainItem(1352970, 1); //cygnus secondary
				break;
			case 3200: //BATTLE MAGE
				jobName = "Battle Mage";
				cm.gainItem(1382000, 1); //10 bam
				cm.gainItem(1382017, 1); //30 bam
				cm.gainItem(1392550, 1); //bam secondary
				break;
			case 3300: //WILD HUNTER
				jobName = "Wild Hunter";
				cm.gainItem(1462001, 1); //12 xbow
				cm.gainItem(1462004, 1); //32 xbow
				cm.gainItem(2061001, 5000); //xbowarrows
				cm.gainItem(1352960, 1); //wildhunter secondary
				break;
			case 3500: //MECHANIC
				jobName = "Mechanic";
				cm.gainItem(1492000, 1); //10 mech
				cm.gainItem(1492004, 1); //30 mech
				cm.gainItem(1352700, 1); //mech secondary
				break;
			case 3700: //BLASTER
				jobName = "Blaster";
				cm.gainItem(1582000, 1); //10 blaster
				cm.gainItem(1582001, 1); //30 blaster
				cm.gainItem(1353400, 1); //blaster secondary
				cm.gainItem(2330000, 5000); //bullet
				break;
			case 3100: //DEMON SLAYER
				jobName = "Demon Slayer";
				cm.gainItem(1322122, 1); //10 ds
				cm.gainItem(1322124, 1); //30 ds
				break;
			case 3101: //DEMON AVENGER
				jobName = "Demon Avenger";
				cm.gainItem(1232001, 1); //10 da
				cm.gainItem(1232002, 1); //30 da
				break;
		}
		switch (cm.getPlayer().getJob()) {
			case 430: //DUAL BLADE
				jobName = "Dual Blade";
				cm.gainItem(1332007, 1); //8 dagger
				cm.gainItem(1332009, 1); //30 dagger
				cm.gainItem(1342000, 1); //
				cm.gainItem(1342001, 1); //
				break;
			case 501: //CANNONEER
				jobName = "Cannoneer";
				cm.gainItem(1532000, 1); //10 cannoneer
				cm.gainItem(1532004, 1); //30 cannoneer
				break;
			case 508: //JETT
				jobName = "Jett";
				cm.gainItem(1492000, 1);
				cm.gainItem(1492004, 1);
				break;
			case 6001:
			case 6500: //ANGELIC BUSTER
				jobName = "Angelic Buster";
				cm.gainItem(1222001, 1);
				cm.gainItem(1222002, 1);
				cm.changeJob(6500);
				break;
			case 10000: //ZERO
				jobName = "Zero";
				break;
			case 11200: //BEAST TAMER
				jobName = "Beast Tamer";
				cm.gainItem(1252001, 1);
				cm.gainItem(1252002, 1);
				break;
			case 14000:
			case 14200: //KINESIS
				jobName = "Kinesis";
				cm.changeJob(14200);
				cm.gainItem(1262001, 1);
				break;
			case 2004:
			case 2700: //LUMINOUS
				jobName = "Luminous";
				cm.changeJob(2700);
				cm.gainItem(1212001, 1);
				cm.gainItem(1212002, 1);
				cm.gainItem(1352400, 1);
				break;
			case 4001:
			case 4100: //HAYATO
				jobName = "Hayato";
				cm.changeJob(4100);
				break;
			case 4002:
			case 4200: //KANNA
				jobName = "Kanna";
				cm.gainItem(1552000, 1);
				cm.gainItem(1552002, 1);
				cm.changeJob(4200);
				break;
			case 3002:
			case 3600: //XENON
				jobName = "Xenon";
				cm.gainItem(1242001, 1);
				cm.gainItem(1242002, 1);
				cm.changeJob(3600);
				break;
			case 6000:
			case 6100: //KAISER
				jobName = "Kaiser";
				cm.gainItem(1402001, 1);
				cm.gainItem(1402002, 1);
				cm.changeJob(6100);
				break;
			case 5000:
			case 5100: //MIHILE
				jobName = "Mihile";
				cm.gainItem(1302001, 1);
				cm.gainItem(1302002, 1);
				cm.changeJob(5100);
				break;
			case 2005: //SHADE
			case 2500: //SHADE1
				jobName = "Shade";
				cm.gainItem(1482000, 1);
				cm.gainItem(1482004, 1);
				cm.changeJob(2500);
				cm.teachSkill(25001002, 1);
				cm.teachSkill(25001000, 1);
				break;
			case 2003: //PHANTOM
			case 2400: //PHANTOM1
				jobName = "Phantom";
				cm.gainItem(1362001, 1);
				cm.gainItem(1362005, 1);
				cm.changeJob(2400);
				break;
			case 2002: //MERCEDES
			case 2300: //MERCEDES1
				jobName = "Mercedes";
				cm.gainItem(1522000, 1);
				cm.gainItem(1522004, 1);
				cm.changeJob(2300);
				break;
			case 2001: //EVAN
			case 2210: //EVAN1
				jobName = "Evan"; // 
				cm.gainItem(1372005, 1); //8 wand
				cm.gainItem(1372003, 1); //28 wand
				cm.gainItem(1382000, 1); //10 staff
				cm.gainItem(1382017, 1); //30 staff
				cm.changeJob(2210);
				break;
			case 2000: //ARAN
			case 2100: //ARAN1
				jobName = "Aran";
				cm.gainItem(1442000, 1);
				cm.gainItem(1442001, 1);
				cm.changeJob(2100);
				break;
		}
		cm.gainItem(2000004, 75); //100 elixirs
		cm.gainItem(2000005, 25); //50 power elixirs
		cm.sendNextNoESC("You are now a #b" + jobName + "#k! I trust you will do well with your newfound skills. I've also given you some #bequipment#k to get you started and #bpotions#k to aid you in battle. Good luck in Cellion!");
    } else if (status == 3) {
        cm.getPlayer().sortInventory(2);
        cm.warp(100000000, 0);
        cm.dispose();
    }
}