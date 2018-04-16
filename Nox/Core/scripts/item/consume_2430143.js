/*
 * Unideified love letter
 */

var status = -1;

function action(mode, type, selection) {
    cm.gainItem(2430143, -1);
    if (Math.floor(Math.random() * 100) < 5) {
	cm.sendNext("Gotcha! This is a chain letter. If you don't make 7 copies of this letter and hand it to 7 friends within 7 days, you'll... Okay, okay, I'm just kidding!");
    } else {
	cm.gainFame(1 + Math.floor((Math.random() * 10)));
	cm.playerMessage("The love poured into the letter was so pure and strong, your fame went up as a result of it! However, you don't know who have sent such a desirable letter.")
    }
    cm.dispose();
}