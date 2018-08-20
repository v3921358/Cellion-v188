/*
 * Thunder stone - Cygnus Dream Key
 */

function action(mode, type, selection) {
    if (cm.haveItem(2430200,1) && cm.haveItem(4000663,1) && cm.haveItem(4000662,1) && cm.haveItem(4000661,1) && cm.haveItem(4000660,1)) {
	cm.gainItem(2430200,-1);
        cm.gainItem(4000663,-1);
        cm.gainItem(4000662,-1);
        cm.gainItem(4000661,-1);
        cm.gainItem(4000660,-1);
        
        cm.gainItem(4032923,1); // basic assumption that there'll be an ETC slot after taking 4 
    }
    cm.dispose();
}