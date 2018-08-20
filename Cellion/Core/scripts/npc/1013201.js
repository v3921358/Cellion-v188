/*
 * Camila - Kidnapped, Evan quest
 */

function action(mode, type, selection) {
    if (cm.getQuestStatus(22557) == 1) {
	cm.forceStartQuest(22598, "2");
    }
    cm.dispose();
}