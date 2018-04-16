importPackage(net.sf.odinms.server.maps);
importPackage(net.sf.odinms.net.channel);

/*
Ludi PQ: 4th stage to 4th stage portal
*/

function enter(pi) {
	var nextMap = 922010200;
	var nextPortal = "st00";
	var eim = pi.getPlayer().getEventInstance();
	var target = eim.getMapInstance(nextMap);
	var targetPortal = target.getPortal(nextPortal);
	if(eim == null){
	    pi.warp(nextMap, nextPortal);
	}else{
	    pi.warp(target, targetPortal);
	}
	return true;
}
