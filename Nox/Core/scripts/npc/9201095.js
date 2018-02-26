/*  
 *	NPC (9201095)
 *	All-In-One Tutorial
 *	REXION
 *
 *	@author Mazen
 */
 
var status = -1;

// Item given to check which state of the tutorial the player is on.
var tutorialItemCheck = 4310203;

var tutorialItemRequirement = 4000011;

// Different parts of the tutorial.
var tutorialMapPart1 = 552000010; // Space Station - Starting Room (NPC Here)
var tutorialMapPart2 = 552000021; // Space Station - Training Room (NPC Here)
var tutorialMapPart3 = 552000020; // Space Station - Spaceship Room
var tutorialMapPart4 = 552000030; // Island - Landing Area
var tutorialMapPart5 = 552000040; // Island - Final Room (NPC Here)

// Demon Slayer/Avenger Variables
var isDemonSlayer = false;
var isDemonAvenger = false;

function action(mode, type, selection) {
	if (mode != 1) {
		cm.dispose();
	} else {
		
		status++;
		
		if (cm.getPlayer().getMapId() == tutorialMapPart1) {
			
			if (status == 0) {
				cm.sendNextNoESC("Huh? Who are you?... What are you doing here?");
			} else if (status == 1) {
				cm.sendNextPrevS("Uhh... I'm #h #,\r\nI-I just woke up... I don't know how I got here, where are we?", 3);
			} else if (status == 2) {
				cm.sendNextPrevS("...Ohhh, you must be the new recruit for #dProject: REXION#k. I didn't realize the shuttle came back already.", 1);
			} else if (status == 3) {
				cm.sendNextPrevS("What's #dProject: REXION#k? ... Why don't I remember this?", 3);
			} else if (status == 4) {
				cm.sendNextPrevS("A secret organization from planet #rCerberus#k started #dProject: REXION#k with the intention of rebuilding the #rMaple World#k.", 1);
			} else if (status == 5) {
				cm.sendNextPrevS("#dProject: REXION#k brings in new recruits from the #rMaple World#k and sends them back stronger, with the goal of making the #rMaple World#k a better place. Your memory was wiped as a precaution.", 1);
			} else if (status == 6) {
				cm.sendNextPrevS("WAIT! WE'RE IN SPACE?!", 3);
			} else if (status == 7) {
				cm.sendNextPrevS("Haha, yes we are. Relax, there's nothing to worry about. You'll be back in the #rMaple World#k shortly.", 1);
			} else if (status == 8) {
				cm.sendNextPrevS("First things first, let's make sure you know what you're doing. Come with me.", 1);
			} else if (status == 9) {
				cm.warp(tutorialMapPart2, 0);
				cm.dispose();
			} 
			
		} else if (cm.getPlayer().getMapId() == tutorialMapPart2) {
			
			if (!cm.haveItem(tutorialItemCheck)) {
				if (status == 0) {
					cm.sendNextPrevS("Alright, try and kill all these monsters. Let's see what you got!", 1);
				} else if (status == 1) {
					cm.spawnMonster(9480282, 4, -300, -120);
					cm.spawnMonster(9480283, 3, -200, -120);
					cm.gainItem(tutorialItemCheck, 1);
					cm.dispose();
				}
			} else {
				if (cm.getMonsterCount(tutorialMapPart2) > 0) {
					cm.sendOk("Stop loafing around! I need to make sure you're ready, kill all the monsters.");
					cm.dispose();
				} else {
					if (status == 0) {
						cm.sendNextNoESC("Wow, very impressive! You're stronger than I expected. Perhaps you are ready. ", 1);
					} else if (status == 1) {
						cm.sendNextPrevS("(I can't believe I just did that.)", 3);
					} else if (status == 2) {
						cm.sendNextPrevS("I'll be waiting for you on the #rSpaceship#k, meet me there when you're ready.", 1);
					} else if (status == 3) {
						cm.warp(tutorialMapPart3, 0);
						cm.dispose();
					} else {
						cm.dispose();
					}
				}
			}
		
		// NPC is not used in Part 3 and 4 of tutorial.
		} else if (cm.getPlayer().getMapId() == tutorialMapPart5) {
		
			if (!cm.haveItem(4000011, 4)) {
				if (status == 0) {
					cm.sendNext("Oh hey #h #, how's it going?");
				} else if (status == 1) {
					cm.sendNextPrevS("You crashed the #rSpaceship#k.....", 2);
				} else if (status == 2) {
					cm.sendNextPrev("Oh yeah, sorry about that. I was pretty clumsy, but hey, atleast we both survived!");
				} else if (status == 3) {
					cm.sendNextPrevS("Yeah....", 2);
				} else if (status == 4) {
					cm.sendNextPrev("Don't worry about a thing, I'll get you where you need to go.");
				} else if (status == 5) {
					cm.sendNextPrev("I need you to get me #b4 Mushroom Spores#k in order to power my #dTeleporter#k, you can get some by killing those #rSpore#k monsters over there.");
					cm.dispose();
				} 
			} else {
				if (status == 0) {
					cm.sendNextNoESC("Perfect! The #dTeleporter#k is now ready!\r\nAre you prepared to start your adventure?");
				} else if (status == 1) {
					cm.sendNextPrevS("Yeah, but what about you?", 3);
				} else if (status == 2) {
					cm.sendNextNoESC("Don't worry about me, all that matters is the success of #dProject: REXION#k. Now go on and make the #rMaple World#k a better place!");
				} else if (status == 3) {
					cm.sendNextNoESC("Oh, I should brief you on some changes #dProject: REXION#k have made throughout the #rMaple World#k. First of all, #dMaple Points (NX)#k can be earned randomly by killing monsters.");
				} else if (status == 4) {
					cm.sendNextNoESC("After #blevel 15#k, you can access the #dREXION Quick Access Menu#k by typing #b@rexion#k, this will be very useful later on! You can find out the rest of the commands by typing #b@help#k.");
				} else if (status == 5) {
					cm.sendNextNoESC("#bChannel 6 to Channel 10#k are known as #rBloodless Channels#k. Killing monsters in these channels grants #dAdditional Experience#k as well as larger amounts of #dMaple Points (NX)#k. Although, monsters there #btake reduced damage#k and #rdeal increased damage#k. On top of all that, upon reaching the required experience for the next level, there's a very small chance to #blevel up twice#k or #rnot level entirely#k.");
				} else if (status == 6) {
					if(!(cm.getJobId() == 3001)) { // Not a Demon character.
						cm.sendNextNoESC("Good luck, #h #.\r\nHave fun, and remember to always stay strong!");
					} else { // Demons need to choose a path.
						cm.sendNextPrevS("As a #bDemon#k, you have the choice of going down two different paths. You can either become a #rDemon Slayer#k or #dDemon Avenger#k.", 3);
					}
				} else if (status == 7) { // Just for Demons. (Slayer explanation)
					if (!(cm.getJobId() == 3001)) {
						completeTutorial(); // Complete tutorial now.
						cm.dispose();
					} else {
					cm.sendNextPrevS("The Demon Slayer uses a special gauge called Demon Fury for his skills instead of Mana Points (MP), and can recover Fury by inflicting damage on monsters. He is more versatile than an average Warrior, with skills that cause area of effect damage and wings that let him launch high in the air or glide into tactical positions. He has high defense and can perform strong combos, making the Slayer a well-rounded character that can be played in a variety of styles.", 3);
					}
				} else if ((status == 8)) { // Avenger explanation
					cm.sendNextPrevS("The Demon Avenger uses Hit Points (HP) for his skills, and thus has no Mana Points (MP). This means the Avenger can stock up on mesos, the in-game currency, as he doesn't need to buy any MP potions. Skills like Life Sap help counter the Avenger's ever–lowering HP from skill consumption, as he can siphon enemies of their life to restore himself. A true rogue, the Avenger uses Desperados as his weapon of choice.", 3);
				} else if (status == 9) { // Selection choice
					cm.sendNextPrevS("Which path would you like to take?\r\n#r#L0#Path of the Slayer#l\r\n#d#L1#Path of the Avenger#l", 3);
				} else if (status == 10) { // Selection handler
					switch(selection) {
						case 0:
							cm.sendNextNoESC("Good luck, #h #.\r\nHave fun, and remember to always stay strong!");
							isDemonSlayer = true;
							break;
						case 1:
							cm.sendNextNoESC("Good luck, #h #.\r\nHave fun, and remember to always stay strong!");
							isDemonAvenger = true;
							break;
					}
				} else if (status == 11) {
					completeTutorial(); // Complete tutorial now.
					cm.dispose();
				}
			}
		
		} else {
			
			cm.sendOk("Enjoy your time in #dREXION#k!");
			cm.dispose();
			
		}
	}
}

function completeTutorial() {
	
	cm.gainItem(tutorialItemRequirement, -4); // Consume the collected items from tutorial quest.
	
	// Warp to Town depending on Job
	switch(cm.getJobId()) {
		case 0: // Explorer
			//cm.warp(104000000, 0); // Lith Harbor
			cm.warp(101071300, 0); // Home Map
			break;
		case 430: // Dual Blade
			cm.gainItem(1332007 , 1);  // Fruit Knife
			cm.gainItem(1342000, 1);  // Champion Katara
			//cm.warp(103050000, 0); // Kerning City - Outside Dual Blade HQ
			cm.warp(101071300, 0); // Home Map
			break;
		case 501: // Cannoneer
			//cm.warp(120000000, 0); // Nautilus Harbor
			cm.warp(101071300, 0); // Home Map
			break;
		case 508: // Jett
			//cm.warp(120000000, 0); // Nautilus Harbor
			cm.warp(101071300, 0); // Home Map
			break;
		case 1000: // Cygnus
			//cm.warp(130000000, 0); // Ereve
			cm.warp(101071300, 0); // Home Map
			break;
		case 2000: // Aran
			//cm.warp(140000000, 0); // Rien
			cm.warp(101071300, 0); // Home Map
			break;
		case 2001: // Evan
			//cm.warp(100000000, 0); // Henesys
			cm.warp(101071300, 0); // Home Map
			break;
		case 2002: // Mercedes
			cm.gainItem(1352004, 1);
			//cm.warp(910150300, 0); // Elluel
			cm.warp(101071300, 0); // Home Map
			break;
		case 2003: // Phantom
			//cm.warp(150000000, 0); // Lumiere
			cm.warp(101071300, 0); // Home Map
			break;
		case 2004: // Luminous
		case 2700: // Luminous
			cm.gainItem(1352400, 1);  // Light Orb (Luminous Secondary Weapon)
			//cm.warp(101000000, 0); // Ellinia
			cm.warp(101071300, 0); // Home Map
			break;
		case 2005: // Shade
			//cm.warp(410000000, 0); // Vulpes
			cm.gainItem(1353100, 1);  // Blue Fox Marble (Shade Secondary Weapon)
			cm.warp(101071300, 0); // Home Map
			break;
		case 3000: // Resistance
			//cm.warp(310000000, 0); // Edelstein
			cm.warp(101071300, 0); // Home Map
			break;
		case 3001: // Demon
			// Demon Class configuration
			if (isDemonSlayer) {
				cm.changeJob(3100); // Demon Slayer Job
			}
			if (isDemonAvenger) {
				if (cm.getPlayer().getGender() == 0) { // Demon Avenger Hair
					cm.setHair(36460);
				} else {
					cm.setHair(37450);
				}
				cm.gainItem(1232000, 1);  // Demon Avenger Weapon
				cm.changeJob(3101); // Demon Avenger Job
			}
			//cm.warp(310000000, 0); // Edelstein
			cm.warp(101071300, 0); // Home Map
			break;	
		case 3002: // Xenon
			//cm.warp(310000000, 0); // Edelstein
			cm.warp(101071300, 0); // Home Map
			break;
		case 4001: // Hayato
			//cm.warp(104000000, 0); // Lith Harbor (I guess?)
			cm.warp(101071300, 0); // Home Map
			break;
		case 4002: // Kanna
			//cm.warp(104000000, 0); // Lith Harbor (I guess?)
			cm.warp(101071300, 0); // Home Map
			break;
		case 5000: // Mihile
			//cm.warp(130000000, 0); // Ereve
			cm.warp(101071300, 0); // Home Map
			break;
		case 6000: // Kaiser
			//cm.warp(400000000, 0); // Pantheon
			cm.warp(101071300, 0); // Home Map
			break;
		case 6001: // Angelic Buster
			//cm.warp(400000000, 0); // Pantheon
			cm.warp(101071300, 0); // Home Map
			break;
		case 10112: // Zero
			//cm.warp(104000000, 0); // Lith Harbor (I guess?)
			cm.warp(101071300, 0); // Home Map
			break;
		case 11212: // Beast Tamer
			//cm.warp(866000000, 0); // Arboren
			cm.warp(101071300, 0); // Home Map
			break;
		case 14000: // Kinesis
			//cm.warp(104000000, 0); // Lith Harbor (I guess?)
			cm.warp(101071300, 0); // Home Map
			break;
		default: // Just Incase!
			//cm.warp(104000000, 0); // Lith Harbor
			cm.warp(101071300, 0); // Home Map
			break;
	}
	
	if (cm.haveItem(tutorialItemCheck)) { // Incase they didn't talk to the box (NPC 9270082) for the starter pack.
		cm.gainItem(tutorialItemCheck, -1);
	}
	
	cm.getPlayer().levelUp(); // Levels up the player so they reach level 10, handles auto job advancement if applicable.
}