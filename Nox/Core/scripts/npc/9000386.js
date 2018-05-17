/*
 *Rexion Job Advance NPC
 *@author Arcas
 */

var status = -1;
var freeAdvance = true;


function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		if (status == 0) {
			cm.sendNextS("Enjoy your adventure.", 5);
			cm.dispose();
		}
		status -= 1;
	}
	if (status == 0) {
		// 0 is Beginner, 1000 is Cygnus Knight & 2000 is Legend
		if (cm.getPlayer().getLevel() >= 10 && (cm.getPlayer().getJob() == 0 || cm.getPlayer().getJob() == 1000 || cm.getPlayer().getJob() == 2000 || cm.getPlayer().getJob() == 3000 || cm.getPlayer().getJob() == 3001)) {
			switch (cm.getPlayer().getJob()) {
				//Beginner
				case 0:
					cm.sendSimple("You are currently a #bBeginner#k.\r\n"
					+ "Please choose your job advancement.\r\n"
					+ "#r#L1#Warrior#l\r\n"
					+ "#r#L2#Magician#l\r\n"
					+ "#r#L3#Bowman#l\r\n"
					+ "#r#L4#Thief#l\r\n"
					+ "#r#L5#Pirate#l\r\n");
					break;
				//Cygnus Knight
				case 1000:
					cm.getPlayer().dropMessage(5, " Cygnus advancement ");
					cm.sendSimple("You are currently a #bCygnus Knight#k.\r\n"
					+ "Please choose your job advancement.\r\n"
					+ "#r#L1#Dawn Warrior#l\r\n"
					+ "#r#L2#Blaze Wizard#l\r\n"
					+ "#r#L3#Wind Archer#l\r\n"
					+ "#r#L4#Night Walker#l\r\n"
					+ "#r#L5#Thunder Breaker#l\r\n");
					break;
				//Legend
				case 2000:
					cm.sendOk("You succeeded your training to become a Demon Avenger!");
						cm.getPlayer().changeJob(2100);
						cm.dispose();
						break;
				//Citizen
				case 3000:
					cm.sendSimple("You are currently a #bCitizen#k.\r\n"
					+ "Please choose your job advancement.\r\n"
					+ "#r#L2#Battle Mage#l\r\n"
					+ "#r#L3#Wild Hunter#l\r\n"
					+ "#r#L5#Mechanic#l\r\n"
					+ "#r#L7#Blaster#l\r\n");
					break;
				//Demon
				case 3001:
					cm.sendSimple("You are currently a #bDemon#k.\r\n"
					+ "Please choose your job advancement.\r\n"
					+ "#r#L99#Demon Slayer#l\r\n"
					+ "#r#L100#Demon Avenger#l\r\n");
					break;
				case 3002:
					cm.sendOk("You succeeded your training to become a Xenon!");
					cm.getPlayer().changeJob(3600);
					cm.dispose(); 
					break;
			}
		} else if (cm.getPlayer().getLevel() >= 30 && (cm.getPlayer().getJob()%100 == 0 || cm.getPlayer().getJob() == 501 || cm.getPlayer().getJob() == 508 || cm.getPlayer().getJob() == 2210 || cm.getPlayer().getJob() == 3101)) {
			if (freeAdvance || cm.haveItem(4001126, 30)) {
				if (!freeAdvance) {
					cm.gainItem(4001126, -30);
				}
				if (cm.getPlayer().getJob() > 508 && cm.getPlayer().getJob() != 2210 && cm.getPlayer().getJob() != 3101) {
					cm.changeJob(cm.getPlayer().getJob() + 10);
					cm.dispose();
				} else {
					switch(cm.getPlayer().getJob()){
						//Warrior
						case 100:cm.sendSimple("You are currently a #bWarrior#k.\r\n"
							+ "Please choose your job advancement.\r\n"
							+ "#r#L1#Fighter#l\r\n"
							+ "#r#L2#Page#l\r\n"
							+ "#r#L3#Spearman#l\r\n");
							break;
						//Magician
						case 200:cm.sendSimple("You are currently a #bMagician#k.\r\n"
							+ "Please choose your job advancement.\r\n"
							+ "#r#L1#Wizard (Fire/Poison)#l\r\n"
							+ "#r#L2#Wizard (Ice/Lightning)#l\r\n"
							+ "#r#L3#Cleric#l\r\n");
							break;
						//Archer
						case 300:cm.sendSimple("You are currently an #bArcher#k.\r\n"
							+ "Please choose your job advancement.\r\n"
							+ "#r#L1#Hunter#l\r\n"
							+ "#r#L2#Crossbowman#l\r\n");
							break;
						//Thief
						case 400:cm.sendSimple("You are currently a #bThief#k.\r\n"
							+ "Please choose your job advancement.\r\n"
							+ "#r#L1#Assassin#l\r\n"
							+ "#r#L2#Bandit#l\r\n");
							break;
						//Pirate
						case 500:cm.sendSimple("You are currently a #bPirate#k.\r\n"
							+ "Please choose your job advancement.\r\n"
							+ "#r#L1#Brawler#l\r\n"
							+ "#r#L2#Gunslinger#l\r\n");
							break;
						//Cannoneer
						case 501:
						cm.sendOk("You succeeded your training to become a Cannoneer!");
							cm.getPlayer().changeJob(530);
							cm.dispose();
							break;
						//Jett
						case 508:
						cm.sendOk("You succeeded your training to become a Jett!");
							cm.getPlayer().changeJob(570);
							cm.dispose();
							break;
						//Evan
						case 2210:
							cm.sendOk("You succeeded your training to become an Evan");
							cm.getPlayer().changeJob(2212);
							cm.dispose();
							break;
						//Demon Avenger
						case 3101:
							cm.sendOk("You succeeded your training to become a Demon Avenger!");
							cm.getPlayer().changeJob(3120);
							cm.dispose();
							break;
					}
				}
			} else {
			cm.sendSimple("You need 30 of #i4001126##t4001126# to proceed to job advancement.\r\n"
						+ "Please come back when you have enough");
			}
		} else if(cm.getPlayer().getLevel() >= 45 && cm.getPlayer().getJob() == 431){
			cm.getPlayer().changeJob(432);
			cm.dispose();
		} else if(cm.getPlayer().getLevel() >= 60 && (cm.getPlayer().getJob()%10 == 0 || cm.getPlayer().getJob() == 2212 || cm.getPlayer().getJob() == 432)){
			if (freeAdvance || cm.haveItem(4001126, 60)) {
				if (!freeAdvance) {
					cm.gainItem(4001126, -60);
				}
				if (cm.getPlayer().getJob() != 2212 && cm.getPlayer().getJob() != 432) {
					cm.changeJob(cm.getPlayer().getJob() + 1);
					cm.dispose();
				} else if(cm.getPlayer().getJob() == 2212) {
					cm.changeJob(2214);
					cm.dispose();
				} else {
					cm.changeJob(433);
					cm.dispose();
				}
			} else {
				cm.sendSimple("You need 60 of #i4001126##t4001126# to proceed to job advancement.\r\n"
							+ "Please come back when you have enough");
			}
		} else if (cm.getPlayer().getLevel() >= 100 && (cm.getPlayer().getJob()%2 == 1 || cm.getPlayer().getJob() == 2214)){
			if (freeAdvance || cm.haveItem(4001126, 100)) {
				if (!freeAdvance) {
					cm.gainItem(4001126, -100);
				}
				if (cm.getPlayer().getJob() != 2214){
					cm.changeJob(cm.getPlayer().getJob() + 1);
					cm.dispose();
				} else {
					cm.changeJob(2218)
					cm.dispose();
				}
			} else {
				cm.sendSimple("You need 100 of #i4001126##t4001126# to proceed to job advancement.\r\n"
							+ "Please come back when you have enough");
			}
		} else {
			var sText = "Hey there #b#h ##k,\r\nI can help you Job Advance and get stronger!\r\n"
					  + "Come talk to me if you want to advance and meet the requirements!\r\n\r\n"
					  + "\t#bStandard Requirements#k : \r\n\tAdvancement available at #rLv. 30#k, #rLv. 60#k, and #rLv. 100#k.\r\n\rn"
					  + "\t#dDual Blade Requirements#k : \r\n\tAdvancement available at #rLv. 30#k, #rLv. 45#k, #rLv. 60#k, and #rLv. 100#k.\r\n"
					  + "";
			cm.sendOk(sText);
			cm.dispose();
		}
	}
	
	if (status == 1) {
		if (selection > 0) {
			cm.getPlayer().dropMessage(5, " Just before the switch ");
			switch(cm.getPlayer().getJob()) {
				//Beginner
				case 0:
				cm.getPlayer().dropMessage(5, " Beginner advance ");
				//Cygnus Knight
				case 1000:
				cm.getPlayer().dropMessage(5, " Cygnus advance ");
				//Citizen
				case 3000:
					cm.getPlayer().changeJob(cm.getPlayer().getJob() + (100*selection));
					cm.getPlayer().dropMessage(5, " You should have advanced from your basic job ");
					break;
				//Warrior
				case 100:
				//Magician
				case 200:
				//Bowman
				case 300:
				//Thief
				case 400:
				//Pirate
				case 500:
					cm.getPlayer().changeJob(cm.getPlayer().getJob() + (10*selection));
					cm.getPlayer().dropMessage(5, " You are now 2nd job! ");
					break;
				//Demons
				case 3001:
					cm.getPlayer().changeJob(cm.getPlayer().getJob() + selection);
					cm.getPlayer().dropMessage(5, " You are now a true demon ");
					break;
			}
			cm.dispose();
		}
		else {
			cm.getPlayer().dropMessage(5, " Invalid Selection ");
		}
	}
}