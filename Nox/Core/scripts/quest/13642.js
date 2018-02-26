var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else 
        if (status == 0) {
            qm.dispose();
        status--;
    }
    if (status == 0) {
        var lol = qm.itemQuantity(2431551);
		qm.sendSimple("Welcome to the Valentine's Day Gift Shop. Use your Valentine's Day Candy to pick up some much needed items! \r\n  #r(Available Candy: "+qm.itemQuantity(2431551)+") \r\n #b#b#L1##v1022172:# #t1022172# #r(Required Point(s): 20000)#k#l#b \r\n #L2##v1032182:# #t1032182# #r(Required Point(s): 20000)#k#l#b \r\n #L3##v2470002:# #t2470002# #r(Required Point(s): 7000)#k#l#b \r\n #L4##v2530002:# #t2530002# #r(Required Point(s): 10000)#k#l#b \r\n #L5##v3080000:# #t3080000# #r(Required Point(s): 10000)#k#l#b \r\n #L6##v3080001:# #t3080001# #r(Required Point(s): 10000)#k#l#b \r\n #L7##v2049406:# #t2049406# #r(Required Point(s): 7000)#k#l#b \r\n #L8##v2049303:# #t2049303# #r(Required Point(s): 7000)#k#l#b \r\n #L9##v2048204:# #t2048204# #r(Required Point(s): 7000)#k#l#b \r\n #L10##v2049117:# #t2049117# #r(Required Point(s): 5000)#k#l#b \r\n #L11##v2431935:# #t2431935# #r(Required Point(s): 40000)#k#l#b \r\n #L12##v2431936:# #t2431936# #r(Required Point(s): 60000)#k#l");
	} else if (status == 1) {
        qm.sendYesNo("Do you really want the #b#e#t2431936##k#n?");
    } else if (status == 2) {	  	 
        qm.sendOk("#rYou don't have enough points#k. Go earn some more, and come back when you have enough.");    
    } else if (status == 3) {	
		qm.dispose();
	}
}