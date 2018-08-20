/* 
	Demon Slayer & Avenger Introduction
	@Author Mazen
*/

function start() {
	
	status = -1;
	action(1, 0, 0);
}

function levelToTen() {
	cm.gainExp(15);
	cm.gainExp(40);
	cm.gainExp(60);
	cm.gainExp(100);
	cm.gainExp(200);
	cm.gainExp(300);
	cm.gainExp(641);
	cm.gainExp(1000);
	cm.gainExp(992);
}

function slayerPath() {
	cm.changeJob(3100);
}

function avengerPath() {
	if (cm.getPlayer().getGender() == 0) {
		cm.setHair(36460);
	} else {
		cm.setHair(37450);
	}
	cm.gainItem(1232000, 1);
	cm.changeJob(3101);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (status == 0 && mode == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1)
			status++;
		else
			status--;
		if (status == 0) {
			if (cm.getJobId() != 3001) {//Incase.
				cm.sendOk("Huh?");
				cm.dispose();
			} else {
				cm.sendPlayerToNpc("........");
			}
		} else if (status == 1) {
			cm.sendPlayerToNpc("I'm here to face the Black Mage, my former Master.");
		} else if (status == 2) {
			cm.sendPlayerToNpc("The absolute ruler himself... I can serve him no longer.");
		} else if (status == 3) {
			cm.sendPlayerToNpc("Some might call me a traitor...");
		} else if (status == 4) {
			cm.sendPlayerToNpc("But the real traitor is the Black Mage himself.");
		} else if (status == 5) {
			cm.sendNextNoESC("Ah! It seems the Black Mage wishes to see you after all. It's a shame I cannot end you right here, but as always, I defer to my master.");
		} else if (status == 6) {
			cm.sendNextNoESC("Anyways, #h #. I don't expect I'll see you again. Enjoy the oblivion granted to you from the Black Mage himself! Ha ha ha!");
		} else if (status == 7) {
			cm.warp(931050310, 0); //Warp to dark room.
			cm.sendPlayerToNpc("(Centuries Later)");
		} else if (status == 8) {
			cm.sendPlayerToNpc(".......");
		} else if (status == 9) {
			cm.sendPlayerToNpc("(I think I hear something...)");		
		} else if (status == 10) {
			cm.sendPlayerToNpc("(Where am I? Am I still alive...?)");
		} else if (status == 11) {
			cm.sendNextNoESC("You're very much alive Demon. Although, I am curious. What path are you looking to take?");	
		} else if (status == 12) {
			cm.sendNextNoESC("The Demon Slayer uses a special gauge called Demon Fury for his skills instead of Mana Points (MP), and can recover Fury by inflicting damage on monsters. He is more versatile than an average Warrior, with skills that cause area of effect damage and wings that let him launch high in the air or glide into tactical positions. He has high defense and can perform strong combos, making the Slayer a well-rounded character that can be played in a variety of styles.");
		} else if (status == 13) {
			cm.sendNextNoESC("The Avenger uses Hit Points (HP) for his skills, and thus has no Mana Points (MP). This means the Avenger can stock up on mesos, the in-game currency, as he doesn't need to buy any MP potions. Skills like Life Sap help counter the Avenger's ever–lowering HP from skill consumption, as he can siphon enemies of their life to restore himself. A true rogue, the Avenger uses Desperados as his weapon of choice.");	
		} else if (status == 14) { //Select Demon Slayer or Avenger path.
			cm.sendPlayerToNpc("Huh, me?\r\n"
							 + "#L0##rI'm going down the path of the Slayer!#l\r\n"
							 + "#L1##bI'm going down the path of the #bAvenger!#l");
		} else if (status == 15) {
			levelToTen();
			if (selection == 0) {
				slayerPath();
			} else if (selection == 1) {
				avengerPath();
			}
			cm.warp(957020004, 0); //Warp to Resistance HQ hospital bed.
			cm.sendPlayerToNpc("(I should probably go look around...)");
			cm.dispose();
		}
	}
}