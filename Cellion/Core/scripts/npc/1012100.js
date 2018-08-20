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
		cm.sendYesNo("Would you like to become an archer?");
		job = 0
		}
		if (cm.getPlayer().getLevel() >= 30 && cm.getJob() == 300 ){
		cm.sendSimple("What do you want to become?#b\r\n#L0#Hunter#l\r\n#L1#Crossbowman#l#k");
		}
		if (cm.getPlayer().getLevel() >= 60 && (cm.getJob() == 310 || cm.getJob() == 320 )){
		cm.sendSimple("Would you like to job advance?");
		job = 1;
		}
		if (cm.getPlayer().getLevel() >= 100 && (cm.getJob() == 311 || cm.getJob() == 321 )){
		cm.sendSimple("Would you like to job advance?");
		job = 2;
		}
}	if (status == 1){
	switch (selection)
	{
		case 0:
		cm.getPlayer().changeJob(310); 
		cm.dispose();
		break;
		case 1:
		cm.getPlayer().changeJob(320); 
		cm.dispose();
		break;		
	}
	switch (job)
	{
		case 0:
		if(cm.getJob() == 0) {
			cm.getPlayer().changeJob(300);
			cm.gainItem(1452002, 1);
			cm.gainItem(2060000, 500);
			cm.gainItem(2060000, 500);
			cm.dispose();			
		}
		break;
		case 1:
		if(cm.getJob() == 310) {
			cm.getPlayer().changeJob(311);
			cm.dispose();			
		}
		if(cm.getJob() == 320) {
			cm.getPlayer().changeJob(321);
			cm.dispose();			
		}
		break;
				case 2:
		if(cm.getJob() == 311) {
			cm.getPlayer().changeJob(312);
			cm.dispose();			
		}
		if(cm.getJob() == 321) {
			cm.getPlayer().changeJob(322);
			cm.dispose();			
		}
		break;
	}
}
}