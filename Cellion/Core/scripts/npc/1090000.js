/* Athena Pierce
	Bowman Job Advancement
	Victoria Road : Bowman Instructional School (100000201)

	Custom Quest 100000, 100002
*/

var status = 0;
var job;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 0 && status == 2) {
	cm.sendOk("Make up your mind and visit me again.");
	cm.dispose();
	return;
    }
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
		if (cm.getPlayer().getLevel() >= 10 && cm.getJob() == 0 ){
		cm.sendYesNo("Would you like to become a Pirate?");
		job = 0
		}
		if (cm.getPlayer().getLevel() >= 30 && cm.getJob() == 500 ){
		cm.sendSimple("What do you want to become?#b\r\n#L0#Brawler#l\r\n#L1#Gunslinger#l#k");
		}
		if (cm.getPlayer().getLevel() >= 60 && (cm.getJob() == 510 || cm.getJob() == 520 )){
		cm.sendSimple("Would you like to job advance?");
		job = 1;
		}
		if (cm.getPlayer().getLevel() >= 100 && (cm.getJob() == 511 || cm.getJob() == 521 )){
		cm.sendSimple("Would you like to job advance?");
		job = 2;
		}
}	if (status == 1){
	switch (selection)
	{
		case 0:
		cm.getPlayer().changeJob(510); 
		cm.dispose();
		break;
		case 1:
		cm.getPlayer().changeJob(520); 
		cm.dispose();
		break;		
	}
	switch (job)
	{
		case 0:
		if(cm.getJob() == 0) {
			cm.getPlayer().changeJob(500);
			cm.gainItem(1492000, 1);
			cm.gainItem(1482000, 1);
			cm.gainItem(2330000, 500);
			cm.gainItem(2330000, 500);
			cm.dispose();			
		}
		break;
		case 1:
		if(cm.getJob() == 510) {
			cm.getPlayer().changeJob(511);
			cm.dispose();			
		}
		if(cm.getJob() == 520) {
			cm.getPlayer().changeJob(521);
			cm.dispose();			
		}
		break;
				case 2:
		if(cm.getJob() == 511) {
			cm.getPlayer().changeJob(512);
			cm.dispose();			
		}
		if(cm.getJob() == 521) {
			cm.getPlayer().changeJob(522);
			cm.dispose();			
		}
		break;
	}
}
}