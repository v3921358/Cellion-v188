



function enter(pi) {
    var em = pi.getEventManager("QueenBattle");
	var em = pi.getEventManager("PierreBattle");

    if (em != null) {
	var map = pi.getMapId();
    
	if (map == 863010200) {
			if (pi.getMap().getAllMonstersThreadsafe().size() > 1) {
			//	pi.getPlayer().gainExp(30000, true, true, true);
			//	pi.getPlayer().addHonourExp(100 * pi.getPlayer().getHonourLevel());
			//	pi.getPlayer().dropMessage(5, 100 * pi.getPlayer().getHonourLevel()+" Honor Exp gained.");
				//pi.warp(105200000,0);
			//	pi.spawnMonster(8810025, 1, new java.awt.Point(-303, 230));
			    pi.playerMessage("Please Eliminate all the mobs in the map...");
			//	pi.worldMessage(6, "[Azwan] " + pi.getPlayer().getName() + " finished the Azwan Liberation of Hilla's Gang in Channel "+ pi.getClient().getChannel() +".");
			 } else { 
	
		pi.warp(863010500,0);
	    }
	} else if (map == 105200810) {
			if (pi.getMap().getAllMonstersThreadsafe().size() == 1) {
			//	pi.getPlayer().gainExp(30000, true, true, true);
			//	pi.getPlayer().addHonourExp(100 * pi.getPlayer().getHonourLevel());
			//	pi.getPlayer().dropMessage(5, 100 * pi.getPlayer().getHonourLevel()+" Honor Exp gained.");
				pi.warp(105200000,0);
				pi.playerMessage(6, "You sir are not yet ready to take on the Earth Dragon Vellum...");
			//	pi.worldMessage(6, "[Azwan] " + pi.getPlayer().getName() + " finished the Azwan Liberation of Hilla's Gang in Channel "+ pi.getClient().getChannel() +".");
			 }
	        else {
	//    pi.gainExp(45000000);
		pi.playerMessage(6, "Congratz on defeating [Chaos] Vellum");
				pi.warp(105200000,0);
      if (pi.getPlayer().getParty() != null && pi.isLeader());
	  pi.worldMessage(6, "[Chaos Mode] "  + pi.getPlayer().getName() + "'s party defeated Chaos Vellum");
	    }
    }
}
}