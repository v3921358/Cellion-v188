function enter(pi) {
	if(pi.getMap().getAllMonstersThreadsafe().size() == 1){
		pi.playerMessage(5,"Your home-room teacher is not opening the door.");
	}else{
		var map = pi.getMapId();
		if(map < 744000015){
			pi.warp(map+1,0);
		}else{
			pi.warp(744000001,0);
		}
	}

}