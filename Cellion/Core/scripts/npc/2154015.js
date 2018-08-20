/*
	NPC Name: 		Elevator
	Map(s): 		Edelstein
	Description: 		To Edelstein Strolling Path 3
*/
function action(mode, type, selection) {
    cm.warp(310030211,0);
    cm.startSelfTimer(60, 310030200);
    cm.dispose();
}