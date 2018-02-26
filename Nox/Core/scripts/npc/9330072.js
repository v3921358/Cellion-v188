function start() {
    status = -1; 
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else { 
        if (mode == 1) { 
            status++;
        } else { 
            status--;
        } 
    } 
    if(status == 0) { 
        cm.sendSimple("Hello! I am Yulia, I can help you craft Tinkerer equipement! So, what can I do for you?" +
            "#k\r\n#L0##bCraft Tinkerer equipement#k#l\r\n#L1##bUpgrade Tinkerer equipement#k#l");

    } else if (status == 1) {         
        if (selection == 0) {
cm.dispose();
       cm.openNpc(9330022);

        } else if (selection == 1) {
           cm.dispose();
       cm.openNpc(9330027);
        }         else if (selection == 2) {
            if(cm.getPlayer().haveItem(4001521, 10) && cm.canHold(4001522, 1)) {  
	cm.gainItem(4001521, -10);
        cm.gainItem(4001522, 1);
                cm.sendOk("Thank you! I've exchanged 10 #bTiger Stripe Ticket Pieces#k into a #i4001522#");

            } else {
                cm.sendOk("You don't have 10 Tiger Stripe Ticket Pieces, or You don't have enough space.");
                cm.dispose(); 
            }
        } else if (selection == 3) {
            if(cm.getPlayer().getReborns() >= 20 && cm.haveItem(4001126, 2000) && cm.canHold(1142269, 1)) { 
        	cm.gainItem(4001126, -2000);
        	cm.gainItem(1142269, 1);
                cm.sendOk("You have lost 2000 Maple Leaves, Enjoy your new #1142269");
                cm.dispose();

}else    if(cm.getPlayer().getReborns() >= 20 && cm.haveItem(4001126, 2000) && !cm.canHold(1142249, 1)  && cm.haveItem(1142269, 1)) { 

                cm.sendOk("You don't have enough space,  or you already have #1142269.");
                cm.dispose(); 
            } else {
                cm.sendOk("You don't have enough Awakenings, Or you dont have a 2000 #i4001126");
                cm.dispose(); 
            }
        } else if (selection == 4) {
            if(cm.getPlayer().getReborns() >= 25 && cm.haveItem(4001126, 2500) && cm.canHold(1142273, 1)) { 
        	cm.gainItem(4001126, -2500);
        	cm.gainItem(1142273, 1);
                cm.sendOk("You have lost 2500 Maple Leaves, Enjoy your new #1142273");
                cm.dispose();

}else    if(cm.getPlayer().getReborns() >= 25 && cm.haveItem(4001126, 2500) && !cm.canHold(1142249, 1)  && cm.haveItem(1142273, 1)) { 

                cm.sendOk("You don't have enough space,  or you already have #1142273.");
                cm.dispose(); 
            } else {
                cm.sendOk("You don't have enough Awakenings, Or you dont have a 2500 #i4001126");
                cm.dispose(); 
            }
        } else if (selection == 5) {
            if(cm.getPlayer().getReborns() >= 30 && cm.haveItem(4001126, 3000) && cm.canHold(1142274, 1)) { 
        	cm.gainItem(4001126, -3000);
        	cm.gainItem(1142274, 1);
                cm.sendOk("You have lost 3000 Maple Leaves, Enjoy your new #1142274");
                cm.dispose();

}else    if(cm.getPlayer().getReborns() >= 30 && cm.haveItem(4001126, 3000) && !cm.canHold(1142274, 1)  && cm.haveItem(1142274, 1)) { 

                cm.sendOk("You don't have enough space,  or you already have #1142274.");
                cm.dispose(); 
            } else {
                cm.sendOk("You don't have enough Awakenings, Or you dont have a 3000 #i4001126");
                cm.dispose(); 
            }
        } else if (selection == 6) {
            if(cm.getPlayer().getReborns() >= 35 && cm.haveItem(4001126, 3500) && cm.canHold(1142276, 1)) { 
        	cm.gainItem(4001126, -3500);
        	cm.gainItem(1142276, 1);
                cm.sendOk("You have lost 3500 Maple Leaves, Enjoy your new #1142276");
                cm.dispose();

}else    if(cm.getPlayer().getReborns() >= 35 && cm.haveItem(4001126, 3500) && !cm.canHold(1142276, 1)  && cm.haveItem(1142276, 1)) { 

                cm.sendOk("You don't have enough space,  or you already have #1142274.");
                cm.dispose(); 
            } else {
                cm.sendOk("You don't have enough Awakenings, Or you dont have a 3500 #i4001126");
                cm.dispose(); 
            }

        } else if (selection == 7) {
            if(cm.getPlayer().getReborns() >= 40 && cm.haveItem(4001126, 4000) && cm.canHold(1142268, 1)) { 
        	cm.gainItem(4001126, -4000);
        	cm.gainItem(1142268, 1);
                cm.sendOk("You have lost 4000 Maple Leaves, Enjoy your new #1142268");
                cm.dispose();

}else    if(cm.getPlayer().getReborns() >= 40 && cm.haveItem(4001126, 4000) && !cm.canHold(1142268, 1)  && cm.haveItem(1142268, 1)) { 

                cm.sendOk("You don't have enough space,  or you already have #1142268.");
                cm.dispose(); 
            } else {
                cm.sendOk("You don't have enough Awakenings, Or you dont have a 4000 #i4001126");
                cm.dispose(); 
            }

        } else if (selection == 8) {
            if(cm.getPlayer().getPoints() >= 4000 && cm.canHold(1142229, 1)) { 
        	cm.gainItem(1142229, 1);
                cm.sendOk("Enjoy your new #i1142229");
                cm.dispose();

}else    if(cm.getPlayer().getPoints() >= 4000 && !cm.canHold(1142229, 1)  && cm.haveItem(1142229, 1)) { 

                cm.sendOk("You don't have enough space,  or you already have #i1142229.");
                cm.dispose(); 
            } else {
                cm.sendOk("You don't have enough Donation points");
                cm.dispose(); 
            }


        }
    }
    else {
        cm.dispose();
    }
}