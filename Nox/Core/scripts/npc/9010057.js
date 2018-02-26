/*
 
REXION - Job Advance NPC
Author: Dylan M. (Doomas)
co-Author: Mazen (REXION Owner)


Basic Job Advancing NPC.

In order to advance jobs, must meet level requirement and have the necessary items.
--> To choose which items each job advancement requires, add the item ID's into the particular array (labeled '[first/second/third/fourth]JobItems = [ ... ] ')
--> To choose how many of a given item, adjust next array (make sure to keep the array index in order with the array used above ^ )


Section Descriptions:

Section A: Used to identify which job advancement (1st, 2nd .. ) the player will be attempting to aquire and verify player meets level requirement 
Section B: Player choses job from options
Section C: Inform player of required items for job advance
Section D: Handle the transaction of required items and level advancement
Section E: default situations, always expect the unexpected



  //Items used for testing purposes
  //Format [Snail Shell, Blue Snail Shell, Red Snail shell, Orange Mushroom cap]
  //var itemReq = [4000019, 4000000, 4000016, 4000001];

  
[Rev.2] +added 1st job items 


  
*/
var status = 0;

 var jobId; 
 var level;
 var jobChoice = 0;
 var levelReq = [1, 10, 30, 60, 100];
 
 var beginFormat = ["Beginner", "Noblesse", "Legend", "Citizen", "Demon Slayer"];
 var beginJobs = [0, 1000, 2000, 3000, 3001];
 
 
 var firstFormat = ["Warrior", "Magician", "Bowman", "Theif", "Pirate", "Dawn Warrior", "Blaze Wizard", "Wind Archer", "Night Walker", "Thunder Breaker", "Aran", "Battle Mage", "Wild Hunter", "Mechanic", "Blaster", "Demon Slayer", "Demon Avenger"]
 var firstJobs = [100, 200, 300, 400, 500, 1100, 1200, 1300, 1400, 1500, 2100, 3200, 3300, 3500, 3700, 3100, 3101];
 var firstJobItems = [4000019];
 var firstQuan = [10];
 
 var warriorItems = [1302007];
 var magicianItems = [1372002];
 var bowmanItems = [1452002, 1462001];
 var theifItems = [1332068, 1472000];
 var pirateItems = [1492000, 1482000];
 var aranItems = [1442000];
 
 var secondFormat = ["Fighter", "Page", "Spearmen", "Fire and Poison Wizard", "Ice and Lightning Wizard", "Cleric", "Hunter", "Crossbowman", "Assasin", "Bandit", "Brawler", "Gunslinger", "Dawn Warrior 2", "Blaze Wizard 2", "Wind Archer 2", "Night Walker 2", "Thunder Breaker 2", "Aran 2", "Battle Mage 2", "Wild Hunter 2", "Mechanic 2", "Blaster 2", "Demon Slayer 2", "Demon Avenger 2"]
 var secondJobs = [110, 120, 130, 210, 220,  230, 310, 320, 410, 420, 510, 520, 1110, 1210, 1310, 1410, 1510, 2110, 3210, 3310, 3510 , 3710, 3110, 3120];
 var secondJobItems = [4000000];
 var secondQuan = [10];
 
 var thirdFormat = ["Crusader", "White Knight", "Dragon Knight", "FP-Mage", "IL-Mage", "Priest", "Ranger", "Sniper", "Hermit", "Chief Bandit", "Marauder", "Outlaw", "Dawn Warrior 3", "Blaze Wizard 3", "Wind Archer 3", "Night Walker 3", "Thunder Breaker 3", "Aran 3", "Battle Mage 3", "Wild Hunter 3", "Mechanic 3", "Blaster 3", "Demon Slayer 3", "Demon Avenger 3"]
 var thirdJobs = [111, 121, 131, 211, 221, 231, 311, 321, 411, 421, 511, 521, 1111, 1211, 1311, 1411, 1511, 2111, 3211, 3311, 3511, 3711, 3111, 3121];
 var thirdJobItems = [4000016];
 var thirdQuan = [10];
 
 var fourthFormat = ["Hero", "Paladin", "Dark Knight", "FP-Arch Mage", "IL-Arch Mage", "Bishop", "Bow Master", "Crossbow Master", "Night Lord", "Shadower", "Buccaneer", "Corsair", "Dawn Warrior 4", "Blaze Wizard 4", "Wind Archer 4", "Night Walker 4", "Thunder Breaker 4", "Aran 4", "Battle Mage 4", "Wild Hunter 4", "Mechanic 4", "Blaster 4", "Demon Slayer 4", "Demon Avenger 4"]
 var fourthJobs = [112, 122, 132, 212, 222, 232, 312, 322, 412, 422, 512, 522, 1112, 1212, 1312, 1412, 1512, 2112, 3212, 3312, 3512, 3712, 3112, 3122];
 var fourthJobItems = [4000001];
 var fourthQuan = [10];
  
  

  
 
 
function start() {
	jobId = cm.getPlayer().getJob().getId();
	level = cm.getLevel();
	cm.sendNext("REXION Global Job Advancements!");
    status = -1;  
 }
 
function action(mode, type, selection) {
	
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.sendNext("Goodluck!");
            cm.dispose();
            return;
        }
   
   if (mode == 1)
            status++;
        else
            status--;
	
	
	
	
//-----------------------------------  SECTION 	A  -----------------------------------//
	if(status == 0) {
		
		//Check if player is ready for first job
		for(var i = 0; i < beginJobs.length; i++){
			if(jobId == beginJobs[i]){
			if(level >= 10){	
			status = 0;
			} else {
			status = 99;	
			}
			jobChoice = 1;
			}
		}
		
		//Check if player is ready for second job
		for(var i = 0; i < firstJobs.length; i++){
			if(jobId == firstJobs[i]){
			if(level >= 30){	
			status = 1;
			} else {
			status = 99;	
			}
			jobChoice = 2;
			}
		}
		
		//Check if player is ready for third job
		for(var i = 0; i < secondJobs.length; i++){
			if(jobId == secondJobs[i]){
			if(level >= 70){	
			status = 2;
			} else {
			status = 99;	
			}
			jobChoice = 3;
			}
		}
		
		//Check if player is ready for fourth job
		for(var i = 0; i < thirdJobs.length; i++){
			if(jobId == thirdJobs[i]){
			if(level >= 120){	
			status = 3;
			} else {
			status = 99;	
			}
			jobChoice = 4;
			}
		}
		
		//Check if already a professional
		for(var i = 0; i < fourthJobs.length; i++){
			if(jobId == fourthJobs[i]){
			status = 99;
			jobChoice = 0;
			}
		}
		
		cm.sendNext("Im in charge of Job advancing.");
		
//-----------------------------------  SECTION 	B  -----------------------------------//	
	} else if (status == 1) { //First Job
			firstJobText = "Please choose a path: ";
			for(var i = 0; i < firstJobs.length; i++){
			firstJobText += "\r\n#L" + i + "#" + firstFormat[i] + "#l";  
			}
			cm.sendSimple(firstJobText);
			status = 4;
		
	} else if (status == 2) { //Second Job
			secondJobText = "Please choose a path: ";
			for(var i = 0; i < secondJobs.length; i++){
			if(((secondJobs[i]-jobId) < 100) && (secondJobs[i] >= jobId))
			secondJobText += "\r\n#L" + i + "#" + secondFormat[i] + "#l";  
			}
			cm.sendSimple(secondJobText);
			status = 5;
	} else if (status == 3) { //Third Job
			thirdJobText = "Would you like to advance to: ";
			for(var i = 0; i < thirdJobs.length; i++){
			if((thirdJobs[i]-jobId) == 1)
			thirdJobText += "\r\n#L" + i + "# " + thirdFormat[i] + "#l";  
			}
			cm.sendSimple(thirdJobText);
			status = 6;
	} else if (status == 4) { //Fourth Job
			fourthJobText = "Would you like to advance to:  ";
			for(var i = 0; i < fourthJobs.length; i++){
			if((fourthJobs[i]-jobId) == 1)
			fourthJobText += "\r\n#L" + i + "#" + fourthFormat[i] + "#l";  
			}
			cm.sendSimple(fourthJobText);
			status = 7;
			
//-----------------------------------  SECTION 	C  -----------------------------------//		
	} else if (status == 5){ //first job - describe req. 
		jobChoice = selection;
		itemText = "In order to Job advance, I'll need the folling items: ";
	
		for(var i = 0; i < firstJobItems.length; i++)
			itemText += "#v"+ firstJobItems[i] +"# (Amount: " + firstQuan[i]+ ")";
		
		status = 8;
		cm.sendNext(itemText);
		
	} else if (status == 6) { //second job - describe req.
		jobChoice = selection;
		itemText = "In order to Job advance, I'll need the folling items: ";
		
		for(var i = 0; i < secondJobItems.length; i++)
			itemText += "#v"+ secondJobItems[i] +"# (Amount: " + secondQuan[i]+ ")";
		
		status = 9;
		cm.sendNext(itemText);
		
	} else if (status == 7){ // third job - describe req.
		jobChoice = selection;
		itemText = "In order to Job advance, I'll need the folling items: ";
		
		for(var i = 0; i < thirdJobItems.length; i++)
			itemText += "#v"+ thirdJobItems[i] +"# (Amount: " + thirdQuan[i]+ ")";
		
		status = 10;
		cm.sendNext(itemText);
		
	} else if (status == 8) { // fourth job - describe req.
		jobChoice = selection;
		itemText = "In order to Job advance, I'll need the folling items: ";
		
		for(var i = 0; i < fourthJobItems.length; i++)
			itemText += "#v"+ fourthJobItems[i] +"# (Amount: " + fourthQuan[i]+ ")";
		
		status = 11;
		cm.sendNext(itemText);
		
//-----------------------------------  SECTION 	D  -----------------------------------//		
	} else if (status == 9){ //first job - exchange 
		var truth = true;
		for(var i = 0; i < firstJobItems.length; i++){
				if(cm.hasItem(firstJobItems[i], firstQuan[i])){
				//Do - nothing	
				} else{
				truth = false;
				}
		}
		if(truth){	
		for(var i = 0; i < firstJobItems.length; i++)
				cm.gainItem(firstJobItems[i], -firstQuan[i])
				cm.changeJobById(firstJobs[jobChoice]);
		
						
//-----------------------------------  Beginner Items  --------------------------------//
		if((jobChoice == 0) || (jobChoice == 5)){  //Warrior
			for(var i = 0; i < warriorItems.length; i++)
					cm.gainItem(warriorItems[i],1);
		} else if((jobChoice == 1) || (jobChoice == 6)){ //Magician
			for(var i = 0; i < magicianItems.length; i++)
					cm.gainItem(magicianItems[i],1);
		} else if((jobChoice == 2) || (jobChoice == 7)){ //Bowman
			for(var i = 0; i < bowmanItems.length; i++)
					cm.gainItem(bowmanItems[i],1);
					cm.gainItem(2060000, 2000); //arrow for bow
					cm.gainItem(2061000, 2000); //arrow for crossbow
					
		} else if((jobChoice == 3) || (jobChoice == 8)){ // Theif
			for(var i = 0; i < theifItems.length; i++)
				cm.gainItem(theifItems[i],1);
				cm.gainItem(2070000, 500);
		} else if((jobChoice == 4) || (jobChoice == 9)){ // Pirate
			for(var i = 0; i < pirateItems.length; i++)
				cm.gainItem(pirateItems[i],1);
				cm.gainItem(2330000, 800); //bullet
		} else if((jobChoice == 10)){ // Aran
			for(var i = 0; i < aranItems.length; i++)
				cm.gainItem(aranItems[i],1);
		} else {  //Default? who knows... give them a sword!
			for(var i = 0; i < warriorItems.length; i++)
				cm.gainItem(warriorItems[i],1);
		}
		
		
		
		
		cm.sendOk("Keep up the hard work!");
		cm.dispose();
		} else {
		cm.sendOk("You do not have the required items");	
		cm.dispose();
		}
			
	} else if (status == 10){ //second job - exchange 
		var truth = true;
		for(var i = 0; i < secondJobItems.length; i++){
				if(cm.hasItem(secondJobItems[i], secondQuan[i])){
				//Do - nothing	
				} else{
				truth = false;
				}
		}
		if(truth){	
		for(var i = 0; i < secondJobItems.length; i++)
				cm.gainItem(secondJobItems[i], - secondQuan[i])
		cm.changeJobById(secondJobs[jobChoice]);
		cm.sendOk("Keep up the hard work!");
		cm.dispose();
		} else {
		cm.sendOk("You do not have the required items");	
		cm.dispose();
		}
		
	}  else if (status == 11){ //third job - exchange
		var truth = true;
		for(var i = 0; i < thirdJobItems.length; i++){
				if(cm.hasItem(thirdJobItems[i], thirdQuan[i])){
				//Do - nothing	
				} else{
				truth = false;
				}
		}
		if(truth){	
		for(var i = 0; i < thirdJobItems.length; i++)
				cm.gainItem(thirdJobItems[i], -thirdQuan[i])
		cm.changeJobById(thirdJobs[jobChoice]);
		cm.sendOk("Keep up the hard work!");
		cm.dispose();
		} else {
		cm.sendOk("You do not have the required items");	
		cm.dispose();
		}
		
	} else if (status == 12){ //fourth job - exchange
		var truth = true;
		for(var i = 0; i < fourthJobItems.length; i++){
				if(cm.hasItem(fourthJobItems[i], fourthQuan[i])){
				//Do - nothing	
				} else{
				truth = false;
				}
		}
		if(truth){	
		for(var i = 0; i < fourthJobItems.length; i++)
				cm.gainItem(fourthJobItems[i], -fourthQuan[i])
		cm.changeJobById(fourthJobs[jobChoice]);
		cm.sendOk("Keep up the hard work!");
		cm.dispose();
		} else {
		cm.sendOk("You do not have the required items");	
		cm.dispose();
		}
		
		
//-----------------------------------  SECTION 	E  -----------------------------------//		
	} else if (status > 101){ 
		cm.sendOk("status ? > 101 - ERR");	
		cm.dispose();
	
	
	
	} else if (status = 100) {
		cm.sendOk("You cannot advance your job at this time.");	
		cm.dispose();
		
		
	}else {
		cm.sendOk("Something went wrong~!");	
		cm.dispose();
	}
	
	
	
	}
	
}