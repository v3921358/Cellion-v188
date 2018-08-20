/* Joko
	Masteria - CWK Exchange Quest
*/
var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    switch (status) {
	case 0:
	    if (cm.getQuestStatus(8225) == 2) {
		cm.sendSimple("Oh, huh-lo!#l\r\n#b#L0#Phantom Forest#l\r\n#b#L1#Badge Redemption#l\r\n#b#L2#<Raven Ninjas>#l#k")
	    } else {
		cm.dispose();
	    }
	    break;
	case 1:
	    switch (selection) {
		case 0:
		    cm.sendSimple("Oh, it's you, again.  Still wandering around this forest, huh?  It's pretty creepy, isn't it? You got some questions?#l\r\n#b#L0#Taggrin mentioned that a man named Jack Barricade came through here...#l#k");
		    break;
		case 1:
		    status = 29;
		    cm.sendSimple("Yes, Taggrin put me in charge of handling bounties.  And yes, I'm authorized to speak to you about them, so, um... what do you need?#l\r\n#b#L0#How many badges do I need to redeem?#l\r\n#b#L1#What do I get for turning in badges?#l\r\n#b#L2#I'd like to turn in a set of badges for the bounty.#l#k");
		    break;
		case 2:
		    status = 9;
		    cm.sendSimple("Yes, Taggrin put me in charge of handling bounties.  And yes, I'm authorized to speak to you about them, so, um... what do you need?#l\r\n#b#L0#Taggrin#l\r\n#b#L1#Joko#l\r\n#b#L2#Mo#l\r\n#b#L3#Fiona#l#k");
		    break;
	    }
	    break;
	case 2:
	    cm.sendSimple("Oh, yeah!  He was a friendly guy, and told me a lot of jokes!  I liked him!  I know he played Omok with Taggrin and Taggrin got mad.  Taggrin hates to lose.#l\r\n#b#L0#What happened afterward?#l#k");
	    break;
	case 3:
	    cm.sendNext("Nothing.  Jack let it go.  Jack came by afterward that night, said he really enjoyed the chili I had made the night before!  He told me he had a special seasoning on him that would make it even better!  It didn't make my chili taste that much better but, man, I slept so well that night!  At least, until I woke up from Taggrin's boot.  Seems we all slept very well that night.  Taggrin's still kinda mad about that.");
	    cm.dispose();
	    break;

	case 10:
	    switch(selection) {
		case 0:
		    cm.sendNext("Taggrin's my big brother!  He's the best ninja in our clan!  I'm not supposed to say that, but everyone thinks so anyway!  He was chosen by our clan leader to lead our band on this expedition.  He's very serious, and I know he can seem, um, a bit harsh when you first meet him, but he's got a lot of responsibility to handle.");
		    cm.dispose();
		    break;
		case 1:
		    cm.sendSimple("That's me!  I'm Taggrin's little brother!  Well, not so little, heh-heh!  Taggrin has put me in charge of maintaining the camp.  I also hand out the rewards for any badges you've collected, so come see me if you've got any.  If there's anything you need, just let me know!#l\r\n#b#L0#Er... aren't you a little big to be a ninja?#l#k");
		    break;
		case 2:
		    status = 15;
		    cm.sendSimple("Mo is always scowling.  Plus, he never talks.  I never know what he's thinking.  To tell you the truth, I'm a little afraid of him#l\r\n#b#L0#What, a big guy like you?#l#k");
		    break;
		case 3:
		    status = 17;
		    cm.sendNext("Fiona is pretty, and smart, and can beat people up!  What else can you ask for in a girl?  She doesn't always show it but she's got a really caring heart.  Plus, she knows everything there is to know about weapons!  In addition to being a deadly ninja, she keeps our weapons in tip-top condition!");
		    break;
	    }
	    break;
	case 11:
	    cm.sendNext("I know...  To tell you the truth, I don't particularly want to be a ninja.  But Taggrin says that it's a family tradition, so I do my best.  I don't mind being a ninja, but to tell you the truth, I don't particularly like fighting.  I like sneaking around though!  That's fun!  <sadly>  But I'm not very good at it, and I haven't gotten the hang of Dark Sight quite yet.  Plus, it's hard to find Raven Ninja armor in my size... but Fiona is very nice, and she does her best to make it fit for me!");
	    cm.dispose();
	    break;
	case 16:
	    cm.sendNext("Don't be fooled.  Mo may be short, but that just means you won't see him until after he's kicked your butt.  I once saw Mo take on a whole slew of Cold Sharks by himself... we were eating shark steaks for a whole week!  Yuck.  Even though I know he's usually in the supply tent, I always look around every once in a while, just to make sure he's not sneaking up behind me or anything.  Pssss!  He's not behind me right now, is he!?");
	    cm.dispose();
	    break;
	case 18:
	    cm.sendNext("Why with the right items, she can actually upgrade them and make them even better.  If you ask her nicely, she might do it for you too!  I think she's also been experimenting with creating her own weapons!  Oops... I think that was supposed to be a secret.  Sorry, Fiona!");
	    cm.dispose();
	    break;
	case 30:
	    cm.dispose();
	    break;
    }
}