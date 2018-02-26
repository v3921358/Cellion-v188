function enter(pi) {
    var em = pi.getEventManager("LotusBattle");
	//var em = pi.getEventManager("PierreBattle");

    if (em != null) {
	var map = pi.getMapId();
    
	if (map == 350060600) {
			if (pi.getMap().getAllMonstersThreadsafe().size() == 1) {
			//	pi.getPlayer().gainExp(30000, true, true, true);
			//	pi.getPlayer().addHonourExp(100 * pi.getPlayer().getHonourLevel());
			//	pi.getPlayer().dropMessage(5, 100 * pi.getPlayer().getHonourLevel()+" Honor Exp gained.");
				pi.warp(350060300,0);
			//	pi.spawnMonster(8810025, 1, new java.awt.Point(-303, 230));
			    pi.playerMessage("You sir are not yet ready to take on the Black Heaven Lotus...");
//em.unregisterPlayer(pi.getPlayer());			
//	pi.worldMessage(6, "[Azwan] " + pi.getPlayer().getName() + " finished the Azwan Liberation of Hilla's Gang in Channel "+ pi.getClient().getChannel() +".");
			 } else { 
		//	 pi.gainExp(45000000);
		pi.playerMessage(6, "Congratz on defeating [Normal] Vellum.");
								if (pi.isLeader());
		//pi.worldMessage(6, "[Normal Mode] Vellum has been defeated");
		pi.warp(350060300,0);
		//em.unregisterPlayer(pi.getPlayer());
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
pi.getPlayer().setDeathCount(0);
pi.getPlayer().dropMessage(-1, "Death Count Disabled");
pi.getPlayer().dropMessage(5, "Death Count Disabled");
em.unregisterPlayer(pi.getPlayer());
}