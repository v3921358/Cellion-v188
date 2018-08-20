function enter(pi) {
    if (pi.getQuestStatus(21610) == 1 && pi.getQuestStatus(21619) == 0) {
	pi.forceStartQuest(21619, "0");
    }
}
