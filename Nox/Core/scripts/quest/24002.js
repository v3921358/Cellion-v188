var status = -1;

function start(mode, type, selection) {
	qm.sendOk("You only need to seal the #bdoor on the left side of town.#k The door to the left only leads to the training center. Please hurry. Your Majesty.");
	qm.forceStartQuest();
	qm.dispose();
}
function end(mode, type, selection) {
	qm.sendNext("I wish you... sweet dreams.");
	qm.forceCompleteQuest();
	qm.dispose();
}
