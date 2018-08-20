/*
	Angelic Buster Introduction
	Intro Part 2 - Velderoth
	
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
        cm.sendNextNoESC("You're such a lazy bum, #h #. Kyle and I manage to become knights aready, and here you are trying to sleep more!");
	} else if (status == 1) {
		cm.sendPlayerToNpc("Hey, I'm not a fighter ike you guys! Unless I magically sprout a set of super powers, I'm gonna lounge around allll day every day.");
	} else if (status == 2) {
		cm.sendNextNoESC("I'm pretty sure you've told me that about a thousand times.");
	} else if (status == 3) {
		cm.sendPlayerToNpc("Oh, I'm sorry, am I boring you? Should I be congratulating you two on your fancy new titles? I'll join you one day!");
	} else if (status == 4) {
		cm.sendNextNoESC("I don't think you really need to be a knight, #h #.");
	} else if (status == 5) {
		cm.sendPlayerToNpc("What are you talking about? We're the Heliseum Force! We have to fight!");
	} else if (status == 6) {
		cm.sendNextNoESC("Yeah, but you don't use magic. You have to face the truth sometime...");
	} else if (status == 7) {
		cm.sendPlayerToNpc("Ugh, not everybody HAS to use magic, ya know? You're so thickheaded sometimes...");
	} else if (status == 8) {
		cm.sendNextNoESC("I just want you to think sometimes. Anyway, I gotta get back.");
	} else if (status == 9) {
		cm.sendPlayerToNpc("Oh, I wish I could go...");
	} else if (status == 10) {
		cm.sendNextNoESC("What was that?");
	} else if (status == 11) {
		cm.sendPlayerToNpc("Huh?");
	} else if (status == 12) {
		cm.sendNextNoESC("Something's wrong! We need to get to the East Sanctum!");
	} else if (status == 13) {
		cm.sendPlayerToNpc("Let's get moving! Heliseum Force, go!");
	} else if (status == 14) {
		cm.warp(940001210, 0); //Warp to Pantheon East Sanctum Pre-Event.
		cm.sendPlayerToNpc("Nothing here, big surprise...");
	} else if (status == 15) {
		cm.sendNextNoESC("Hey, who are those priests? I've never seen them before.");
	} else if (status == 16) {
		cm.sendPlayerToNpc("Velderoth, this isn't right!");
	} else if (status == 17) {
		cm.sendNextNoESC("You're right. They look suspicious. I'm going to run back to base and get help. You stay here and keep an eye on them, okay? But no heroics. You get out of here if they spot you.");
	} else if (status == 18) {
		cm.sendPlayerToNpc("What're they talking about?");
	} else if (status == 19) {
		cm.sendPlayerToNpc("('Nefarious Priest: The relic's disappearance should weaken the shields.')");
	} else if (status == 20) {
		cm.sendPlayerToNpc("('Nefarious Priest:  I thought the relic was cursed... should we really be touching it?')");
	} else if (status == 21) {
		cm.sendPlayerToNpc("('Nefarious Priest: I did not realize they allowed superstitious nincompoops entry to our order! Will you balk at the call of destiny?')");
	} else if (status == 22) {
		cm.sendPlayerToNpc("(Are they trying to steal the relic?)");
	} else if (status == 23) {
		cm.sendPlayerToNpc("(They're gonna take the relic away!)");
	} else if (status == 24) {
		cm.sendPlayerToNpc("Let's stop them!");
	} else if (status == 25) {
		cm.warp(931050310, 0); //Warp to dark room.
		cm.sendPlayerToNpc("('Nefarious Priest: W-what is this madness?!')");
	} else if (status == 26) {
		cm.sendPlayerToNpc("('Nefarious Priest: How could a mere child have that kind of power?!')");
	} else if (status == 27) {
		cm.sendPlayerToNpc("('Nefarious Priest: Seems to be unconscious. We are lucky.')");
	} else if (status == 28) {
		cm.sendPlayerToNpc("('Nefarious Priest: They're waking up!')");
	} else if (status == 29) {
		cm.warp(940001220, 0); //Warp to Pantheon East Sanctum Post-Event.
		cm.sendNextNoESC("#h #!");
	} else if (status == 30) {
		cm.sendNextNoESC("Kaiser's emblem!");
	} else if (status == 31) {
		cm.sendNextNoESC("Kaiser has finally made himself known. Please take them to the clinic.");
	} else if (status == 32) {
		cm.warp(940001240, 0); //Warp to Pantheon Clinic.
		cm.sendNextNoESC("#h #, you have finally come to.");
	} else if (status == 33) {
		cm.sendPlayerToNpc("Umm, where am I?");
	} else if (status == 34) {
		cm.sendNextNoESC("Patheon. How do you feel?");
	} else if (status == 35) {
		cm.sendPlayerToNpc("My head feels like a ripe watermelon, but I think I'm okay otherwise.");
	} else if (status == 36) {
		cm.sendPlayerToNpc("Huh? Why is there a pink thing on my arm?");
	} else if (status == 37) {
		cm.sendNextNoESC("I wish I had better news, but I fear you have been cursed by the East Sanctum relic. In fact, it is quite stuck to your arm.");
	} else if (status == 38) {
		cm.sendPlayerToNpc("What?! What do I do?! How do I get it off?!");
	} else if (status == 39) {
		cm.sendNextNoESC("The security threat of having a young, defenseless girl wandering around with one of our relics strapped to her arm has not escaped me.");
	} else if (status == 40) {
		cm.sendNextNoESC("Hmm, that relic would be entirely gone if it were not for you and Kaiser.");
	} else if (status == 41) {
		cm.sendPlayerToNpc("Ha... hahaha... what? I don't remember anything...");
	} else if (status == 42) {
		cm.sendPlayerToNpc("Are you saying that the relic grabbed me and turned into a bracelet when I touched it? Who the heck is Kaiser? What is going on ?!");
	} else if (status == 43) {
		cm.sendNextNoESC("Hey, it's going to be okay. We don't have a way to remove that relic from your arm, but it won't cause you any harm. Think of it like a nice accessory.");
	} else if (status == 44) {
		cm.sendPlayerToNpc("I-I didn't mean to take it! I don't even like pink!");
	} else if (status == 45) {
		cm.sendNextNoESC("#h #, no one is blaming you for this. Three relics remain in Pantheon. We are quite safe.");
	} else if (status == 46) {
		cm.sendPlayerToNpc("B-but, I...");
	} else if (status == 47) {
		cm.sendNextNoESC("#h #, please don't start crying. I'm a very sensitive sympathy-weeper.");
	} else if (status == 48) {
		cm.sendPlayerToNpc("Ugh...");
	} else if (status == 49) {
		cm.sendPlayerToNpc(".......");
	} else if (status == 50) {
		cm.sendNextNoESC("I'm sorry. I have very little control over my tearducts.");
	} else if (status == 51) {
		cm.warp(940011070, 0); //Warp to Pantheon Great Templer Interior
		cm.sendPlayerToNpc("I-I stole something! I've never stolen aanything. I didn't mean to, I swear!");
	} else if (status == 52) {
		cm.sendPlayerToNpc("I don't even have any MP... I'm incapable of doing anything... I'm useless...");
	} else if (status == 53) {
		cm.sendPlayerToNpc("Sniff... snif...");
	} else if (status == 54) {
		cm.sendNextNoESC("#h #, wait!");
	} else if (status == 55) {
		cm.sendNextNoESC("This 'curse' is not as ominous as it sounds. That relic has never reacted to any other priest, yet it clung to you like a nurturing mother.");
	} else if (status == 56) {
		cm.sendPlayerToNpc("But I don't want any of this! I didn't mean to take anything!");
	} else if (status == 57) {
		cm.sendNextNoESC("Ah, geez....");
	} else if (status == 58) {
		cm.warp(400020300, 0); //Warp to Heliseum Hideout.
		cm.sendPlayerToNpc("What am I gonna do? Maybe I can just hide here until I die of old age.");
	} else if (status == 59) {
		cm.sendPlayerToNpc("Bwaaa... why... does... nothing... ever... work for me?!!");
	} else if (status == 60) {
		cm.sendPlayerToNpc("Oh, Velderoth! *sniff* I-I heard you were some kind of superhero now... That's great. Great for you. *sniff*");
	} else if (status == 61) {
		cm.sendNextNoESC("#h #, I was looking for you. A-are you all right?");
	} else if (status == 62) {
		cm.sendPlayerToNpc("Me? Why do you wanna see me? Is it this thing on my arm? I didn't mean to get it stuck on there but then it just...");
	} else if (status == 63) {
		cm.sendPlayerToNpc("I shoulda known something bad was gonna happen to me...");
	} else if (status == 64) {
		cm.sendNextNoESC("#h #...");
	} else if (status == 65) {
		cm.sendPlayerToNpc("I... I just thought maybe I'd finally get to use magic like you guys. Instead, I get a big stupid pink bracelet and a whole lot of people mad at me... I never shoulda come with you guys.");
	} else if (status == 66) {
		cm.sendNextNoESC("#h #, I... I mean me and Kyle are worried about you.");
	} else if (status == 67) {
		cm.sendPlayerToNpc("I'm sorry. I'm so sorry you two always have to worry about me. I'm just gonna stay here so you never have to worry about me again.");
	} else if (status == 68) {
		cm.sendPlayerToNpc("You should go on, okay? I need some time aone.");
	} else if (status == 69) {
		cm.warp(940010000, 0); //Warp to Heliseum Vacant Lot.
		cm.sendPlayerToNpc("(Stupid. You can't just sit here and cry.)");
	} else if (status == 70) {
		cm.sendPlayerToNpc("Maybe I can just put this dumb thing back at the East Sanctum. Then this will all go away!");
	} else if (status == 71) {
		cm.sendPlayerToNpc("('???: Hey...')");
	} else if (status == 72) {
		cm.sendPlayerToNpc("Who's that?");
	} else if (status == 73) {
		cm.sendPlayerToNpc("('Eskalade: Behold... Eskalade! Look at your wrist.')");
	} else if (status == 74) {
		cm.sendPlayerToNpc("Ahh!! Talking bracelet!");
	} else if (status == 75) {
		cm.sendPlayerToNpc("('Eskalade: Calm down, and focus. I'm going to give you my power.')");
	} else if (status == 76) {
		cm.sendPlayerToNpc("('Eskalade: Hey! Girly. Can you see me?')");
	} else if (status == 77) {
		cm.sendPlayerToNpc("WHAT THE~~");
	} else if (status == 78) {
		cm.sendPlayerToNpc("('Eskalade: AH! Relax! I'm Eskalade. I, uh, I live in that bracelet on your pillowy-soft wrist. I was thinking, maybe since we're, like, attached that I'd give you a little of my power.')");
	} else if (status == 79) {
		cm.sendPlayerToNpc("Power? What're you talking about?");
	} else if (status == 80) {
		cm.sendPlayerToNpc("('Eskalade: Just go over to the spot where you grabbed that relic.')");
	} else if (status == 81) {
		cm.sendPlayerToNpc("Fine, but I'm not going to do it just because you told me. I was already going there.");
	} else if (status == 82) {
		cm.sendPlayerToNpc("('Eskalade: Fine! How did I end up with such a disobedient little brat?')");
	} else if (status == 83) {
		cm.sendPlayerToNpc("I'm not going to take you anywhere if you're going to be rude.");
	} else if (status == 84) {
		cm.sendPlayerToNpc("('Eskalade: Who's being rude?! I'm offering you ultimate power in exchange for a little trip to a place you were already going!')");
	} else if (status == 85) {
		cm.sendPlayerToNpc("Oh, yeah, I guess that's true.");
	} else if (status == 86) {
		cm.warp(940001220, 0); //Warp to Pantheon East Sanctum Post-Event.
		cm.sendPlayerToNpc("Okay, dragon~");
	} else if (status == 87) {
		cm.sendPlayerToNpc("('Eskalade: Do you see a ring where the relic used to be?')");
	} else if (status == 88) {
		cm.sendPlayerToNpc("Is this it?");
	} else if (status == 89) {
		cm.sendPlayerToNpc("('Eskalade: Just put that on your little finger.')");
	} else if (status == 90) {
		cm.sendPlayerToNpc("Is this gonna shock me or something? I hate pranks...");
	} else if (status == 91) {
		cm.sendPlayerToNpc("('Eskalade: Would you just put the stupid thing on so I can make you powerful?!')");
	} else if (status == 92) {
		cm.changeJob(6500);
		cm.sendPlayerToNpc("What's going on?!");
	} else if (status == 93) {
		cm.sendPlayerToNpc("('Eskalade: Holy moly, you look amazing! Look at those legs! And that hair! And you~~ why are you looking at me like that?')");
	} else if (status == 94) {
		cm.sendPlayerToNpc("W-what am I wearing?! Where are my pants?!");
	} else if (status == 95) {
		cm.sendPlayerToNpc("Oh no! The priests are back!");
	} else if (status == 96) {
		cm.sendPlayerToNpc("('Nefarious Priest: There! That girl has the relic on her arm!')");
	} else if (status == 97) {
		cm.sendPlayerToNpc("('Nefarious Priest: Do we really need to hurt a pretty thing like her? The relic is out of the Sanctum, it won't cause us any trouble.')");
	} else if (status == 98) {
		cm.sendPlayerToNpc("('Nefarious Priest: There! Don't be stupid! We can't let some little bimbo take credit for our actions!')");
	} else if (status == 99) {
		cm.sendPlayerToNpc("('Nefarious Priest: I-I'm sorry, um, ma'am, but would you please give me that relic?')");
	} else if (status == 100) {
		cm.sendPlayerToNpc("You're one of those creeps I saw before!");
	} else if (status == 101) {
		cm.sendPlayerToNpc("('Nefarious Priest: Well, I wouldn't say 'creep' so much as liberator. Look, maybe you can give me that beautiful bracelet of yours for a bit and I can take you out for dinner...')");
	} else if (status == 102) {
		cm.sendPlayerToNpc("No way, you old weirdo! I can't even get this thing off my arm.");
	} else if (status == 103) {
		cm.sendPlayerToNpc("('Nefarious Priest: Would you stop flirting?! Just grab the bimbo and we'll get out of here!')");
	} else if (status == 104) {
		cm.sendPlayerToNpc("('Eskalade: #h #! Now would be a good time to kick the tar out of them.')");
	} else if (status == 105) {
		cm.sendPlayerToNpc("H-how do I do that?");
	} else if (status == 106) {
		cm.sendPlayerToNpc("('Nefarious Priest: What mad devilry is going on inside that lovely little head of yours? Come now! Let's go!')");
	} else if (status == 107) {
		cm.warp(940001220, 0); //Warp to Pantheon East Sanctum Post-Event.
		cm.sendPlayerToNpc("Wow! This is cool!");
	} else if (status == 108) {
		cm.sendPlayerToNpc("('Eskalade: Mwahahaha, how's THAT for power?!')");
	} else if (status == 109) {
		cm.sendPlayerToNpc("That was so much fun! It was all PEW PEW KABLOOOM!!");
	} else if (status == 110) {
		cm.sendPlayerToNpc("('Eskalade: Uh, yes. Yes it was sort of like that, only a lot less stupid.')");
	} else if (status == 111) {
		cm.sendPlayerToNpc("Hehehe, hahahaha, HAHAHAHAHA!");
	} else if (status == 112) {
		cm.sendPlayerToNpc("('Eskalade: You didn't hear a single word I said, did you? HEY! Enough with the terrifying laughter. You need some practice to use my powers properly.')");
	} else if (status == 113) {
		cm.sendPlayerToNpc("Awww, practice? I don't wannnaaaaa....");
	} else if (status == 114) {
		cm.warp(400000000, 0); //Warp to Heliseum
		cm.forceCompleteQuest(25829);
		cm.forceCompleteQuest(25835);
		cm.forceCompleteQuest(25836);
		cm.forceCompleteQuest(25837);
		cm.gainItem(200002,40);
		cm.gainEXP(800);
	} 
}

