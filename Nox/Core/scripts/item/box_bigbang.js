/*
 * Cassandra's Supply box
 */

var items = [
    2000000, // red pot
    2101120, // fish sack
    2020017, // Cream cake
    2020023, // Dark Chocolate
    2022002, // Cider
    2022179, // Onyx apple
    2100001,2100002,2100003, 2100004, 2100005, 2100006, 2100007 // sack
];

function action(mode, type, selection) {
    cm.gainItem(2430133, -1);
    cm.gainItem(items[Math.floor(Math.random() * items.length)], 1);
    cm.dispose();
}