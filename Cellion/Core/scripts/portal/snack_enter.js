function enter(pi) {
    if (pi.getParty() != null) {
	if (!pi.isLeader()) {
	    pi.playerMessage("You are not the party leader.");
	} else {
	    var em = pi.getEventManager("biscuitHouse_WitchNightOut");
	    if (em == null) {
		pi.playerMessage("You're not allowed to enter with unknown reason. Try again." );
	    } else {
		em.startInstance(pi.getParty(), pi.getMap());
		return true;
	    }
	}
    } else {
	pi.playerMessage(5, "You don't have a party. You can challenge with party.");
    }
    return false;
}