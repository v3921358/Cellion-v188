function enter(pi) {
        if (pi.getPlayer().getParty() != null) {
                //pi.warpParty_Instanced(921120600);
				var cleared = false;
var cleared1 = false;
				switch(pi.getMapId()) {
					case 861000100:
						cleared = pi.getMap().getAllMonstersThreadsafe().size() == 0;
						break;
					case 861000200:
						cleared = pi.getMap().getAllMonstersThreadsafe().size() == 0;
						break;
					case 861000300:
						cleared = pi.getMap().getAllMonstersThreadsafe().size() == 0;
            					case 861000400:
						cleared = pi.getMap().getAllMonstersThreadsafe().size() == 0;
						break;
            					case 861000500:
						cleared1 = pi.getMap().getAllMonstersThreadsafe().size() == 0;
						break;
				}
				if (cleared) {
					pi.warpParty(pi.getMapId() + 100);
					pi.playPortalSE();
				} else {
					pi.playerMessage(5,"This portal is not available yet.");
				}
				if (cleared1) {
					pi.warpParty(610040810);
					pi.playPortalSE();
				} else {
					pi.playerMessage(5,"This portal is not available yet.");
				}
        } else {
                pi.playerMessage(5,"This portal is not available.");
        }
}