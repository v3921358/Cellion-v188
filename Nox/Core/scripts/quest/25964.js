var status = -1;
var rank = "C";//current rank
var points = 1056;
var nextRank = "B";
var nextPoints = 1100;
var battles = 5;
var wins = 4;
var draw = 0;
var loss = 1;
var todayBattle = 0;
var todayWin = 0;
var todayDraw = 0;
var todayLoss = 0;

function start(mode, type, selection) {
	if (mode == 1) {
		status++;
    } else {
    	//status--;
		qm.dispose();
		return;
    }
        if (status == 0) {
        	qm.sendSimple("What do you want to do for the Boss Arena? \r\n #b#L0# Register to participate in the Boss Arena. \r\n (When matching is successful, all your current buffs will be deactivated.)#l \r\n #L1# Check my Boss Arena ranking and my win loss/record.#l \r\n #L2# Learn about the Boss Arena.#l \r\n #L3# Do nothing.#l");
        } else if (status == 1) {
    		sel = selection;
		    if (sel == 0) { //join
		    	qm.sendSimple("Not complete yet. Please come back later.");
	    	    //qm.searchBossPvp(1);
	    	    var name = qm.getPlayer().getName();
	    	    //qm.worldMessage(6, name + " is searching for a Boss Pvp! Use @pvp to fight them.");
	    	    qm.dispose();
	        } else if (sel == 1) { //boss ranking
	    	    qm.sendNext("So you want to see your Boss Arena information. Let me pull that up for you...");
		    } else if (sel == 2) { //learn about
		        qm.sendNext("Hee hee... You want to learn about the Boss Arena? It's gonna be long but listen up.");
		    } else if (sel == 3) { //nothing
	    	    qm.sendSimple("You're not gonna do anything? Boring!");
	    	    qm.dispose();
		    }
        } else if (status == 2) {
        	 if (sel == 1) {
	        	qm.sendSimple("#e[Boss Arena Info] \r\n #bCurrent Rank: #r"+rank+" #bRank#k ("+points+")#b \r\n Next Rank: #r"+nextRank+"-#b Rank #k ("+nextPoints+")#b \r\n Cumulative Score: "+battles+" Battles "+wins+" Wins "+draw+" Draws "+loss+" Losses \r\n #eToday's Score: "+todayBattle+" Battles "+todayWin+" Wins "+todayDraw+" Draws "+todayLoss+" Losses");
            } else if (sel == 2) {
				qm.sendNextPrev("The Boss Arena is where we let you transform into a boss and fight other players. It'd be unfair if only you got to be the boss, so you'll each get a turn as the boss to fight two matches.");
	    	}
		} else if (status == 3) {
			if (sel == 1) {
				qm.sendOk("Try reaching a higher rank.");
				qm.dispose();
			} else if (sel == 2) {
					qm.sendNextPrev("When you request a match, you'll have to wait for an opponent to turn up. Then you choose the boss you want to play as.");
		    }
		} else if (status == 4) {
			if (sel == 2) {
				qm.sendNextPrev("You also need to select your handicap and skills. Handicap how much DEF you will get when you're the boss. The easier you go, the more DEF you'll get. You'd think higher DEF is always better, right?");
			}
		} else if (status == 5) {
			if (sel == 2) {
				qm.sendNextPrev("Well, it's not just about the DEF boost. The higher your DEF, the lower your ATT. The lower your DEF, the higher your ATT. Nothing is free! Except event items.");
			}
		} else if (status == 6) {
			if (sel == 2) {
				qm.sendNextPrev("You'll get a choice of four skills, and you can pick two. I can see the confusion on your face but don't worry, I'll give you the rundown.");
			}
		} else if (status == 7) {
			if (sel == 2) {
				qm.sendNextPrev("You have 3 attacks and 3 skills. With 2 more skills to choose, you'll end up with 3 attacks and 5 skills.");
			}
		} else if (status == 8) {
			if (sel == 2) {
				qm.sendNextPrev("Attacks are pretty easy to use if you take note of the Cooldown times, but skills might be a bit trickier.");
			}
		} else if (status == 9) {
			if (sel == 2) {
				qm.sendNextPrev("Using skills will add them to the skill queue in the middle of the screen. They'll go up in the order used, and you have to wait for each skill to be used in order.");
			}
		} else if (status == 10) {
			if (sel == 2) {
				qm.sendNextPrev("I see you're glazing over so I'll get to the point. You should only use skills when you REALLY need them, 'cause otherwise you'll end up waiting. Of course, you can also press Tab to cancel the skill at the front.");
			}
		} else if (status == 11) {
			if (sel == 2) {
				qm.sendNextPrev("Did you get all that all right? I'm almost done with how the Boss Arena works, just a few more things to know.");
			}
		} else if (status == 12) {
			if (sel == 2) {
				qm.sendNextPrev("Players also have some restrictions on their skills. Stuff like Double Jump or Quick Move. All jobs can use Flash Jump, just in limited amounts.");
			}
		} else if (status == 13) {
			if (sel == 2) {
				qm.sendNextPrev("You can say it's unfair, but this is to make it fair for the slower bosses.");
			}
		} else if (status == 14) {
			if (sel == 2) {
				qm.sendNextPrev("You'll also find potions spawning on the map. Touch them to use them. Only players can do this, so use it to your advantage.");
			}
		} else if (status == 15) {
			if (sel == 2) {
				qm.sendNextPrev("Anyway, bosses just need to use their attacks and skills to run the player out of lives and win the round.");
			}
		} else if (status == 16) {
			if (sel == 2) {
				qm.sendNextPrev("And players need to take out all of the boss's HP to win. Don't worry, we think we balanced this pretty well.");
			}
		} else if (status == 17) {
			if (sel == 2) {
				qm.sendNextPrev("So you get to fight one round as a boss and one round as a player, with a total of 2 rounds to decide who wins and loses. Whoever wins more will win, naturally.");
			}
		} else if (status == 18) {
			if (sel == 2) {
				qm.sendNextPrev("What happens when each person wins each round? A draw? Nah, those are so outdated. The win will go to whoever used the fewest lives. And if that's the same, the lower-level player will win.");
			}
		} else if (status == 19) {
			if (sel == 2) {
				qm.sendNextPrev("And if THAT'S also the same, then it'll be a draw. We can only take it so far!");
			}
		} else if (status == 20) {
			if (sel == 2) {
				qm.sendNextPrev("The Boss Arena scores will change depending on you and your opponent's standings. Score enough points, and your ranking will go up!");
			}
		} else if (status == 21) {
			if (sel == 2) {
				qm.sendNextPrev("I know that look, 'Why should I care about rankings?' Because rankings give you emblems! Really neat emblems!");
			}
		} else if (status == 22) {
			if (sel == 2) {
				qm.sendNextPrev("You can also get EXP and Chaos Scroll 100%. isn't that exciting?");
			}
		} else if (status == 23) {
			if (sel == 2) {
				qm.sendNextPrev("Then try reaching a higher rank.");
				qm.dispose();
			}
		}
}

function end(mode, type, selection) {
	qm.sendSimple("What do you want to do for the Boss Arena?");
    //qm.forceCompleteQuest();
    //qm.getPlayer().dropMessage(5, "test");
    qm.dispose();
}