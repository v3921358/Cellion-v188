/*
 * battle Pouch
 */

var chaosItems = [
1302165, // Chaos Fraute
1332137, // Chaos Deadly Fin
1332138, // Chaos Varkit
1372090, // Chaos Magic Codar
1382114, // Chaos Kage
1402099, // Chaos Sparta
1432091, // Chaos Fairfrozen
1442126, // Chaos Hellslayer
1452119, // Chaos Metus
1462107, // Chaos Casa Crow
1472130, // Chaos Casters
1482092, // Chaos White fangz
1492090, // Chaos Peacemaker
];

var items = [
4330012, // Batle square herb bag
4330013, // Battle square minereal bag
2290285, // Mystery mastery book
4310015, // Gallem Emblem
4310015, // Gallem Emblem
4310015, // Gallem Emblem
4310015, // Gallem Emblem
4310015, // Gallem Emblem
4310015, // Gallem Emblem
2028061, // Mystery scroll
2028062, // Mystery recipe
];

function action(mode, type, selection) {
    cm.gainItem(2430221, -1);
    if (Math.floor(Math.random() * 10) == 0) {
	var gain = chaosItems[Math.floor(Math.random() * chaosItems.length)];
	cm.gainItem(gain, 1);
	cm.broadcastWorldMsg(6, "[Congrats!] "+cm.getName()+" have acquired a "+cm.getItemName(gain)+" from the Battle Pouch!");
    } else {
	cm.gainItem(items[Math.floor(Math.random() * items.length)], 1);
    }
    cm.dispose();
}