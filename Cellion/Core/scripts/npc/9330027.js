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
        cm.sendSimple("Which item would you like upgrading?" +
            "#k\r\n#L0##bUpgrade Yellow Tinkerer Shoulder Accessory#k#l\r\n#L100##bUpgrade Green Tinkerer Shoulder Accessory#k#l\r\n#L122##rUpgrade Blue Tinkerer Shoulder Accessory#k#l\r\n#L123##rUpgrade Red Tinkerer Shoulder Accessory#k#l\r\n\r\n#L2##bUpgrade Yellow Tinkerer Belt#k#l\r\n#L101##bUpgrade Green Tinkerer Belt#k#l\r\n#L120##rUpgrade Blue Tinkerer Belt#k#l\r\n#L121##rUpgrade Red Tinkerer Belt#k#l");

    } else if (status == 1) {         
        if (selection == 0) {
            if(cm.getPlayer().haveItem(1152120, 1) && cm.getPlayer().haveItem(4000444, 333) && cm.getPlayer().haveItem(4000447, 333) && cm.getPlayer().haveItem(4004001, 50) && cm.canHold(1152120, 1)) {  
	cm.gainItem(1152120, -1);
	cm.gainItem(4000444, -333);
	cm.gainItem(4000447, -333);
	cm.gainItem(4004001, -50);
	cm.gainItem(1152121, 1);
                cm.sendOk("Thank you! I've upgraded your #b#t1152120##k into a #g#t1152121##k");

            } else {
                cm.sendOk("In order to upgrade #b#t1152120##k You should have the following materials:\r\n1 #b#t1152120##k#i1152120# \r\n333 #b#t4000444##k#i4000444#\r\n333 #b#t4000447##k#i4000447#\r\n50 #b#t4004001##k#i4004001#.");
                cm.dispose(); 
            }
        } else if (selection == 100) {
            if(cm.getPlayer().haveItem(1152121, 1) && cm.getPlayer().haveItem(4000444, 333) && cm.getPlayer().haveItem(4000447, 333) && cm.getPlayer().haveItem(4004001, 50) && cm.canHold(1152120, 1)) {  
	cm.gainItem(1152121, -1);
	cm.gainItem(4000444, -333);
	cm.gainItem(4000447, -333);
	cm.gainItem(4004001, -50);
	cm.gainItem(1152122, 1);
                cm.sendOk("Thank you! I've upgraded your #b#t1152121##k into a #g#t1152122##k");

            } else {
                cm.sendOk("In order to upgrade #b#t1152121##k You should have the following materials:\r\n1 #b#t1152121##k#i1152121# \r\n333 #b#t4000444##k#i4000444#\r\n333 #b#t4000447##k#i4000447#\r\n50 #b#t4004001##k#i4004001#.");
                cm.dispose(); 
            }
       
 }

else if (selection == 122) {
            if(cm.getPlayer().haveItem(1152122, 1) && cm.getPlayer().haveItem(4250802, 3) && cm.canHold(1152123, 1)) {  
	cm.gainItem(1152122, -1);
	cm.gainItem(4250802, -3);
	cm.gainItem(1152123, 1);
                cm.sendOk("Thank you! I've upgraded your #b#t1152122##k into a #b#t1152123##k");

            } else {
                cm.sendOk("In order to upgrade #b#t1152122##k You should have the following materials:\r\n1 #b#t1152122##k#i1152122# \r\n3 #b#t4250802##k#i4250802#.");
                cm.dispose(); 
            }
       
 }


else if (selection == 123) {
            if(cm.getPlayer().haveItem(1152123, 1) && cm.getPlayer().haveItem(4251402, 5) && cm.canHold(1152124, 1)) {  
	cm.gainItem(1152123, -1);
	cm.gainItem(4250802, -5);
	cm.gainItem(1152124, 1);
                cm.sendOk("Thank you! I've upgraded your #b#t1152123##k into a #g#t1152124##k");

            } else {
                cm.sendOk("In order to upgrade #b#t1152123##k You should have the following materials:\r\n1 #b#t1152123##k#i1152123# \r\n5 #b#t4251402##k#i4251402#.");
                cm.dispose(); 
            }
       
 }

else if (selection == 120) {
            if(cm.getPlayer().haveItem(1132213, 1) && cm.getPlayer().haveItem(4250802, 3) && cm.canHold(1132214, 1)) {  
	cm.gainItem(1132213, -1);
	cm.gainItem(4250802, -3);
	cm.gainItem(1132214, 1);
                cm.sendOk("Thank you! I've upgraded your #b#t1132213##k into a #g#t1132214##k");

            } else {
                cm.sendOk("In order to upgrade #b#t1132213##k You should have the following materials:\r\n1 #b#t1132213##k#i1132213# \r\n3 #b#t4250802##k#i4250802#.");
                cm.dispose(); 
            }
       
 }


else if (selection == 121) {
            if(cm.getPlayer().haveItem(1132214, 1) && cm.getPlayer().haveItem(4251402, 5) && cm.canHold(1132215, 1)) {  
	cm.gainItem(1132214, -1);
	cm.gainItem(4250802, -5);
	cm.gainItem(1132215, 1);
                cm.sendOk("Thank you! I've upgraded your #b#t1132214##k into a #g#t1132215##k");

            } else {
                cm.sendOk("In order to upgrade #b#t1132214##k You should have the following materials:\r\n1 #b#t1132214##k#i1132214# \r\n5 #b#t4251402##k#i4251402#.");
                cm.dispose(); 
            }
       
 }

         else if (selection == 2) {
            if(cm.getPlayer().haveItem(1132211, 1) && cm.getPlayer().haveItem(4000444, 333) && cm.getPlayer().haveItem(4000447, 333) && cm.getPlayer().haveItem(4004001, 50) && cm.canHold(1152120, 1)) {  
	cm.gainItem(1132211, -1);
	cm.gainItem(4000444, -333);
	cm.gainItem(4000447, -333);
	cm.gainItem(4004001, -50);
	cm.gainItem(1132212, 1);
                cm.sendOk("Thank you! I've upgraded your #b#t1132211##k into a #g#t1132212##k");

            } else {
                cm.sendOk("In order to upgrade #b#t1132211##k You should have the following materials:\r\n1 #b#t1132211##k#i1132211# \r\n333 #b#t4000444##k#i4000444#\r\n333 #b#t4000447##k#i4000447#\r\n50 #b#t4004001##k#i4004001#.");
                cm.dispose(); 
            }
        } else if (selection == 101) {
            if(cm.getPlayer().haveItem(1132212, 1) && cm.getPlayer().haveItem(4000444, 333) && cm.getPlayer().haveItem(4000447, 333) && cm.getPlayer().haveItem(4004001, 50) && cm.canHold(1152120, 1)) {  
	cm.gainItem(1132212, -1);
	cm.gainItem(4000444, -333);
	cm.gainItem(4000447, -333);
	cm.gainItem(4004001, -50);
	cm.gainItem(1132213, 1);
                cm.sendOk("Thank you! I've upgraded your #b#t1132212##k into a #g#t1132213##k");

            } else {
                cm.sendOk("In order to upgrade #b#t1132212##k You should have the following materials:\r\n1 #b#t1132212##k#i1132212# \r\n333 #b#t4000444##k#i4000444#\r\n333 #b#t4000447##k#i4000447#\r\n50 #b#t4004001##k#i4004001#.");
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