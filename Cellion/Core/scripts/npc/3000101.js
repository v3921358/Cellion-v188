/*
	Angelic Buster Introduction
	Intro Part 1 - Childhood Velderoth
	
	@author Mazen
*/

var status;    

function start() {
    status = -1; 
    action(1, 0, 0); 
} 
var status; 

function start() { 
    status = -1; 
    action(1, 0, 0); 
} 

function action(mode, type, selection) { 
    if (mode == 1) { 
        status++; 
    }else{ 
        status--; 
    } 
     
    if (status == 0) {
        cm.sendNextNoESC("I don't want to see you getting bullied anymore! Maybe you can learn to use magic like us...");
	} else if (status == 1) {
		cm.sendPlayerToNpc(".......");
	} else if (status == 2) {
		cm.sendNextNoESC("We'll watch your back forever, #h #. We swear it.");
	} else if (status == 3) {
		cm.sendNextNoESC("This is bumming me out! Let's go down to town and get some candy!");
	} else if (status == 4) {
		cm.sendPlayerToNpc("Okay!");
	} else if (status == 5) {
		cm.sendNextNoESC("#h #, you're so much less ugly when you smile!");
	} else if (status == 6) {
		cm.sendPlayerToNpc("Gee thanks. Jerk.");
	} else if (status == 7) {
		cm.warp(400020300, 0); //Warp to Heliseum Hideout.
		cm.sendPlayerToNpc("Super #h # construction!");
	} else if (status == 8) {
		cm.sendNextNoESC("Haha, what? You say the funniest things.");
	} else if (status == 9) {
		cm.sendNextNoESC("What are you gonna call this place?");
	} else if (status == 10) {
		cm.sendNextNoESC("It's gotta be super tough and awesome!");
	} else if (status == 11) {
		cm.sendPlayerToNpc("How about the Justice Lair?");
	} else if (status == 12) {
		cm.sendNextNoESC("Hahahaha, you love justice like Kyle loves pushups!");
	} else if (status == 13) {
		cm.sendNextNoESC("That's too kiddy.");
	} else if (status == 14) {
		cm.sendPlayerToNpc("Then what are we gonna call it?");
	} else if (status == 15) {
		cm.sendNextNoESC("I know!! Let's call ourselves the Heliseum Force.");
	} else if (status == 16) {
		cm.sendPlayerToNpc("Heliseum Force?");
	} else if (status == 17) {
		cm.sendNextNoESC("If we're gonna take back Heliseum from that stupid Magnus, we have to remember it every single day!");
	} else if (status == 18) {
		cm.sendPlayerToNpc("Cool! That's so cool, Velderoth!");
	} else if (status == 19) {
		cm.sendNextNoESC("I know, right?");
	} else if (status == 20) {
		cm.sendPlayerToNpc("Then this place will be the Heliseum Hideout from now on!");
	} else if (status == 21) {
		cm.sendNextNoESC("This is going to be sooo awesome!");
	} else if (status == 22) {
		cm.sendPlayerToNpc("Are you gonna be our captain, Veldie?");
	} else if (status == 23) {
		cm.sendNextNoESC("Uh.. me?");
	} else if (status == 24) {
		cm.sendPlayerToNpc("I don't want to lead people. I just want to be tough!");
	} else if (status == 25) {
		cm.sendNextNoESC("Is that really okay?");
	} else if (status == 26) {
		cm.sendPlayerToNpc("Sure thing, cap'n!");
	} else if (status == 27) {
		cm.sendNextNoESC("Then today is the official founding of the Heliseum Force!");
	} else if (status == 28) {
		cm.warp(931050310, 0); //Warp to dark room.
		cm.sendPlayerToNpc("(Decades Later)");
	} else if (status == 29) {
		cm.warp(940010000, 0); //Warp to Heliseum Vacant Lot.
		cm.sendPlayerToNpc("It's so pretty out today! I wanna take a nap!");
	} else if (status == 30) {
		cm.dispose();
		cm.openNpc(3000104); //Start older Velderoth.
	}
	
}

