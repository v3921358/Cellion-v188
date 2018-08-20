/*
	Angelic Buster Introduction
	Childhood Self NPC
	
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
        cm.sendOk("The other kids are making fun of me because I don't have magic!");
		cm.dispose();
	}
}

