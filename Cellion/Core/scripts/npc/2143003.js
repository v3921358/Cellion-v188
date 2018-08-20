/*
 *Multi-Map Portal to Knight Stronghold : Hallowed Ground
 *@author Arcas
 */


var status = -1;

function start() {
    var textMsg = "So you want to go to the Hallowed Ground? Oh yeah, we discovered a new Hallowed Ground. I hear the key to the Cygnus Garden can be found there.Please continue doing your best to bring peace to this world.";
    textMsg += "\r\n\r\n#L1#-#bHallowed Ground of Dawn#k#l";
    textMsg += "\r\n#L2#-#bHallowed Ground of Blaze#k#l";
    textMsg += "\r\n#L3#-#bHallowed Ground of Wind#k#l";
    textMsg += "\r\n#L4#-#bHallowed Ground of Night#k#l";
    textMsg += "\r\n#L5#-#bHallowed Ground of Thunder#k#l";
    cm.sendNext(textMsg);
	status = -1;
}

function action(mode, type, selection){
    var mapID = 271030200 + selection;
    cm.warp(mapID, 1);
    cm.dispose();
}