/*
 * Suni - Money sucker NPC
 */

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    switch (status) {
	case 0:
	    cm.sendNext("Hello, I am Suni and I am #bREALLY#k desperate for #bmesos#k. Hey there, Maplers, will you help me out in donating what you can to me? \r\nI will show my gratitude for the meso you have donated.. sniff sniff..");
	    break;
	case 1:
	    cm.sendGetNumber("I see you have #b"+cm.getMeso()+"#k meso, how much would you like to donate?", 1, 1, 100000000);
	    break;
	case 2:
	    if (!cm.canHold(2022002) || !cm.canHold(3010130)) {
		cm.sendOk("Please make space on your SETUP and USE inventory.");
		cm.dispose();
		return;
	    }
	    var record = cm.getQuestRecord(150012);
	    var delay;

	    if (record.getInfoData() == null) {
		record.setInfoData("0");

		delay = true;
	    } else {
		//	    cm.playerMessage(((new Date().getTime()) - parseInt(record.getInfoData())));
		if (((new Date().getTime()) - parseInt(record.getInfoData()) < 3600000)) {
		    delay = false;
		} else {
		    delay = true;
		}
	    }
	    if (!delay) {
		cm.sendOk("I don't need any meso now, please check back in an hour.");
	    } else {
		var meso = selection;
		if (meso > 0 && cm.getMeso() > meso) {
		    var reward;

		    if (meso < 500000) { // 1 mill
			reward = new Array(
			    2022002, // Cider
			    2000000, // Red Pot
			    2000004, // Elixir
			    2000005, // Power Elixir
			    2020032, // Birthday Cake
			    2020020, // Anniversary Cake
			    2020031 // Coca Cola
			    );
		    } else if (meso < 5000000) { // < 5m
			reward = new Array(
			    2022002, // Cider
			    2000000, // Red Pot
			    2000004, // Elixir
			    2000005, // Power Elixir
			    2022121, // Gelt Chocolate
			    2020032, // Birthday Cake
			    2020020, // Anniversary Cake
			    2020031, // Coca Cola

			    2022002, // Cider
			    2000000, // Red Pot
			    2022002, // Cider
			    2000000, // Red Pot
			    
			    3010046, // Dragon Chair
			    3010047, // Dragon Chair
			    3012001, // Round the campfire
			    3010130 // Rose Chair
			    );
		    } else if (meso < 15000000) { // < 15m
			reward = new Array(
			    2022002, // Cider
			    2000000, // Red Pot
			    2000004, // Elixir
			    2000005, // Power Elixir
			    2022121, // Gelt Chocolate

			    3010046, // Dragon Chair
			    3010047, // Dragon Chair
			    3012001, // Round the campfire
			    3010130 // Rose Chair
			    )
		    } else {
			reward = new Array(
			    2022002, // Cider
			    2000000, // Red Pot
			    2000004, // Elixir
			    2000005, // Power Elixir
			    2022121, // Gelt Chocolate
			    
			    3010046, // Dragon Chair
			    3010047, // Dragon Chair
			    3012001, // Round the campfire
			    3010130, // Rose Chair
			    3010111// Tiger Skin Chair
			    )
		    }
		    record.setInfoData(new Date().getTime());
		    cm.gainMeso(-meso);
		    cm.gainItem(reward[Math.floor(Math.random() * reward.length)], 1);
		    cm.sendOk("I've recieved all the meso I need! Thank-you maplers! Hope to see you again soon.");
		} else {
		    cm.sendOk("You are lacking of meso to donate.");
		}
	    }
	    cm.dispose();
	    break;
    }
}