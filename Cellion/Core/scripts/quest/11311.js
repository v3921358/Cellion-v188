/*
 * Using the Stone of Virtue
 */

var status = -1;

var minLv = [15, 30, 35, 41, 45, 47,50, 50, 55, 61, 65, 71, 77, 79, 94, 113, 122, 146];
var maxLv = [26, 34, 42, 46, 50, 58, 62, 64, 66, 71, 82, 84, 83, 95, 105, 118, 142, 200];

var allList = [
"\r\n#e<Golem's Temple>\r\n<> Rec. Lv.#n   Lv.15 - Lv.26\r\n#e<> Location    #n   Victoria / South Henesys\r\n#e<> Route#n   Henesys > Spore Hill > Humming Forest Trail > Windflower Forest > Golem's Temple Entrance\r\n#i3800202#",
"\r\n#e<Skyscraper>\r\n<> Rec. Lv.#n   Lv.30 - Lv.34\r\n#e<> Location    #n   Victoria / Mushroom Castle - Skyscraper\r\n#e<> Route#n   Henesys >..> Mushroom Forest Field >..> Castle Wall Edge > Outside Castle Walls > Skyscraper\r\n#i3800205#",
"\r\n#e<Kerning Square>\r\n<> Rec. Lv.#n   Lv.35 - Lv.42\r\n#e<> Location    #n   Victoria / Kerning Square 1F - 8F\r\n#e<> Route#n   Kerning City > Subway Ticketing Booth >..(Subway)..> Kerning Square Station > Kerning Square Lobby > Kerning Square 1F - 8F\r\n#i3800203#",
"\r\n#e<Orbis Garden>\r\n<> Rec. Lv.#n   Lv.41 - Lv.46\r\n#e<> Location    #n   Ossyria / El Nath Mountains / East Orbis\r\n#e<> Route#n   Six Path Crossway >..(Airship)..> Orbis > Cloud Park 1 > The Road to Garden of 3 Colors > Garden of 3 Colors 1 > Garden of 3 Colors 2\r\n#i3800206#",
"\r\n#e<Swamp>\r\n<> Rec. Lv.#n   Lv.45 - Lv.50\r\n#e<> Location    #n   Victoria / East Sleepywood\r\n#e<> Route#n   Six Path Crossway > Sleepywood > Swamp\r\n#i3800204#",
"\r\n#e<Drake Cave>\r\n<> Rec. Lv.#n   Lv.47 - Lv.58\r\n#e<> Location    #n   Victoria / East Sleepywood Swamp\r\n#e<> Route#n   Six Path Crossway > Sleepywood > Swamp > Drake Cave\r\n#i3800218#",
"\r\n#e<Chryse>\r\n<> Rec. Lv.#n   Lv.50 - Lv.62\r\n#e<> Location    #n   Ossyria / El Nath Mountains / North Orbis\r\n#e<> Route#n   Orbis > Orbis Park >..(NPC:Ericsson)..> Chryse\r\n#i3800207#",
"\r\n#e<Black Mountain>\r\n<> Rec. Lv.#n   Lv.50 - Lv.64\r\n#e<> Location    #n   Ossyria / Ludus Lake / East Korean Folk Town\r\n#e<> Route#n   Orbis >..(Airship)..> Ludibrium > Helios Tower > Korean Folk Town > Black Mountain\r\n#i3800208#",
"\r\n#e<Cursed Temple>\r\n<> Rec. Lv.#n   Lv.55 - Lv.66\r\n#e<> Location    #n   Victoria / Sleepywood / Deep Inside the Cave\r\n#e<> Route#n   Six Path Crossway > Sleepywood > Swamp > Drake Cave > Cursed Temple\r\n#i3800219#",
"\r\n#e<Boswell Field>	\r\n<> Rec. Lv.#n   Lv.61 - Lv.71\r\n#e<> Location    #n   Ossyria / Ludus Lake / West Omega Sector\r\n#e<> Route#n   Orbis >..(Airship)..> Ludibrium > Eos Tower > Omega Sector > Restricted Area > Boswell Field\r\n#i3800209#",
"\r\n#e<Verne Mine>\r\n<> Rec. Lv.#n   Lv.65 - Lv.82\r\n#e<> Location    #n   Edelstein Northeast Rocky Mountain\r\n#e<> Route#n   Orbis >..(Airship)..> Edelstein > Edelstein Strolling Path > Road to the Mine 1> Road to the Mine 2> Verne Mine\r\n#i3800210#",
"\r\n#e<Sunset Road>\r\n<> Rec. Lv.#n   Lv.71 - Lv.84\r\n#e<> Location    #n   Ossyria / Nihal Desert / North Ariant\r\n#e<> Route#n   Orbis >..(Airship)..> Ariant > Sunset Road (Toward Magatia)\r\n#i3800211#",
"\r\n#e<Mu Lung Training Center>\r\n<> Rec. Lv.#n   Lv.77 - Lv.83\r\n#e<> Location    #n   Ossyria / Mu Lung Garden / East Mu Lung\r\n#e<> Route#n   Orbis >..(Airship)..> Mu Lung > Mu Lung > Mu Lung Training Center\r\n#i3800213#",
"\r\n#e<Alcadno Research Institute>\r\n<> Rec. Lv.#n   Lv.79 - Lv.95\r\n#e<> Location    #n   Ossyria / Nihal Desert / Magatia Basement\r\n#e<> Route#n   Orbis >..(Airship)..> Ariant > Outside North Entrance of Ariant >..(Taxi:Camel)..> Sahel 1 > Magatia > Alcadno Office > Alcadno Research Institute\r\n#i3800212#",
"\r\n#e<Red-Nose Pirate Den>\r\n<> Rec. Lv.#n   Lv.94 - Lv.105\r\n#e<> Locatio\r\n#e<> Route#n   Orn    #n   Ossyria / Mu Lung Garden / Near Herb Town\r\n#e<> Route#n   Orbis >..(Airship)..> Mu Lung > Mu Lung >..(Taxi:Crane)..> Herb Town > Herb Garden > Bellflower Valley > Isolated Swamp > Red-Nose Pirate Den\r\n#i3800214",
"\r\n#e<Kentaurus Forest>\r\n<> Rec. Lv.#n   Lv.113 - Lv.118\r\n#e<> Location    #n   Ossyria / Minar Forest / South Leafre\r\n#e<> Route#n   Orbis >..(Airship)..> Leafre > West Leafre Forest > Minar Forest : West Border > Cranky Forest > Kentaurus Forest\r\n#i3800215#",
"\r\n#e<Dragon Forest>\r\n<> Rec. Lv.#n   Lv.122 - Lv.142\r\n#e<> Location    #n   Ossyria / Minar Forest / West Forked Road of Forest\r\n#e<> Route#n   Orbis >..(Airship)..> Leafre >..> Minar Forest >..> Forked Road of Forest > Dragon Forest\r\n#i3800216#",
"\r\n#e<Road of Regrets>\r\n<> Rec. Lv.#n   Lv.146 - Lv.157\r\n#e<> Location    #n   Ossyria / Temple of Time / West Minar Forest\r\n#e<> Route#n   Orbis >..(Airship)..> Leafre >..(Dragon)..> Temple of Time Entrance > Three Doors >..> Memory Lane >..> Road of Regrets\r\n#i3800217#"
];

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendSimple("I can guide you to areas where you can increase the #b#t4001527##k gauge by hunting monsters. What can I do for you?\r\n\r\n#b#L0#View the recommended hunting grounds for my level#l\r\n#L1#View all hunting grounds#l\r\n#L2#End conversation#l");
	    break;
	case 1:
	    if (selection == 0) {
		var text = "#b#h0##k, here are some recommended hunting grounds for your level.\r\n#b(When you hunt monsters in the recommended hunting ground, you can obtain the #eEvil Spirit Box#n from Eregos.)\r\n#bYou can press the [W] key to check the world map.#k\r\n\r\n";
		var playerLv = qm.getPlayerStat("LVL");
		for (var z = 0; z < minLv.length; z++) {
		    if (playerLv >= minLv[z] && playerLv <= maxLv[z]) {
			text += allList[z];
		    }
		}
		qm.sendNext(text);
		status = -1;
	    } else if (selection == 1) {
		var text = "Here are all of our recommended hunting grounds, listed by level.\r\n#b(When you hunt monsters in the recommended hunting ground, you can obtain the #eEvil Spirit Box#n from Eregos.)\r\n#bYou can press the [W] key to check the world map.#k\r\n";
		for (var i = 0; i < allList.length; i++) {
		    text += allList[i];
		}
		qm.sendNext(text);
		status = -1;
	    } else {
		qm.dispose();
	    }
	    break;
    }
}

function end(mode, type, selection) {
    qm.dispose();
}