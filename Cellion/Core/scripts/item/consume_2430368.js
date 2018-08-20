/*
 * Evil Spirit Box
 */

function action(mode, type, selection) {
    if (cm.getNumFreeSlot(1) > 0 && cm.getNumFreeSlot(2) > 0 && cm.getNumFreeSlot(3) > 0 && cm.getNumFreeSlot(4) > 0) {
	cm.gainItem(2430368, -1); 
	var rareReward = [
	2049303, // Untradeable AEES
	2049100, // Chaos scroll
	2046303, // Accessory LUK 100%
	2046302, // Accessory DEX 100%
	2046301, // Accessory INT 100%
	2046300, // Accessory STR 100%
	2046203, // Armor 100%
	2046202, // Armor 100%
	2046201, // Armor 100%
	2046200, // Armor 100%
	];
	if (Math.random() * 100 < 5) {
	    // Rare reward
	    // check 5 times limit
	    var item = rareReward[Math.floor(Math.random() * rareReward.length)];
	    cm.gainItem(item, 1);
	    cm.broadcastWorldMsg(6, "[Congrats!] "+cm.getName()+" have acquired a "+cm.getItemName(item)+" from the Evil Spirit box!");
	} else {
	    var commonReward = [
	    2000000, // Red pot
	    2002005, // Sniper potion
	    2002004, // Warrior potion
	    2002003, // Wizard potion
	    2002002, // Magic potion
	    2002001, // Magic potion
	    2002000, // Dex potion
	    2020012, // Melting cheese
	    2020014, // Sunrise dew
	    ];
	    cm.gainItem(commonReward[Math.floor(Math.random() * commonReward.length)], Math.round(10 + Math.random() * 30));
	}
    } else {
	cm.playerMessage("You need one available inventory slot in Eq/Use/Setup and ETC to open this box.");
    }
    cm.dispose();
}