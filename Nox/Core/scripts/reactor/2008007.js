function act() {
    var eim = rm.getEventInstance();
    if (eim != null) {
	eim.setPropertyEx("stg_onthewayup_moves", eim.getPropertyEx("stg_onthewayup_moves") + 1);
    }
}