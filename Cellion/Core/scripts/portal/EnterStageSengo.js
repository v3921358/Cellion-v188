var PQ = 'RedLeafHigh2';
function enter(pi) {
	if(pi.haveItem(5252017, 2)){
		if (pi.getPQLog(PQ) >= 10){
                pi.playerMessage("You can only do this 10 times a day. Please come back tomorrow");
            } else {
            	pi.gainItem(5252017, -2);
				pi.start_RedLeaf2(false, 744000021);
				pi.setPQLog(PQ);
			}
	}else{
		pi.playerMessage("You need at least 2 Red Leaf High Entry Keys to enter. (Cash Shop)");
	}
}