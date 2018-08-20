/*
	NPC Name: 		Kiru
	Map(s): 		
	Description: 		In-ship desc
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
	    cm.sendSimple("Ahh, the nice gentle breeze. This should allow us to get to Orbis quite quickly. Hey you, I suggest you don't run around and do something silly while we're flying. Don't use skills either. What? Do you want to konw what Orbis is like? \r\n#b#L0#Yes, I do.#l \r\n#b#L1#No, I don't...#l");
	    break;
	case 1:
	    if (selection == 0) {
		cm.sendNext("Orbis is a town that is built on top of a tower that was built at the very northern part of Ossyria. Yes, it's a town that is literally above the clouds. In that case, it's a bit similar to Erev, the difference being that Erev has the whole island to herself.");
	    } else {
		cm.sendNext("You must be well-aware of the distinctive features Victoria Island offers. Have a safe flight!");
		cm.dispose();
	    }
	    break;
	case 2:
	    cm.sendNext("Orbis is the mecca of continental transportation. Orbis Station is filled with flying ships ready to take off to the next destination. There are also others that don't exactly resemble the flying ship, but... mostly, if you are looking to head over to a different region, you'll go through Orbis first.");
	    break;
	case 3:
	    cm.sendNextPrev("Orbis is a town full of Fairies, and the difference between these Fairies and the ones in Ellinia is that those Fairies all possess wings with feathers attached to them. They seem much more peaceful and on friendly terms with the humans. Of course, there are exceptions to every rule, but most are like that.");
	    break;
	case 4:
	    cm.sendNextPrev("We the blue-feathered Piyos are on friendly terms with the humans. We can only live in places with very high altitude, so we hardly cross paths with the humans, but we are always on friendly terms with those that visit Erev.");
	    break;
	case 5:
	    cm.sendPrev("#b(...and Kiru's rants continued on for quite some time...)#k");
	    break;
	case 6:
	    cm.dispose();
	    return;
    }
}