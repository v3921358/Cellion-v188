/*
	Angelic Buster Introduction
	Fenelle NPC
	
	@author Mazen
*/

var status;    

function start() {
    status = -1; 
    action(1, 0, 0); 
} 
var status; 

function start() { 
    status = -1; 
    action(1, 0, 0); 
} 

function action(mode, type, selection) { 
    if (mode == 1) { 
        status++; 
    }else{ 
        status--; 
    } 
     
    if (status == 0) {
        cm.sendNext("I'm sorry the other kids bully you #h #. I know you can not help the way you were born.");
	} else if (status == 1) {
		cm.sendOk("(Dear little #h #...)");
		cm.dispose();
	}
}

