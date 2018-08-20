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
		cm.sendYesNo("Would you like to become a magician?");
		job = 0
		}
		if (cm.getPlayer().getLevel() >= 30 && cm.getJob() == 200 ){
		cm.sendSimple("What do you want to become?#b\r\n#L0#fire/poison mage#l\r\n#L1#ice/lightning mage#l\r\n#L2#Cleric#l#k");
		}
		if (cm.getPlayer().getLevel() >= 60 && (cm.getJob() == 210 || cm.getJob() == 220 || cm.getJob() == 230 )){
		cm.sendSimple("Would you like to job advance?");
		job = 1;
		}
		if (cm.getPlayer().getLevel() >= 100 && (cm.getJob() == 211 || cm.getJob() == 221 || cm.getJob() == 231 )){
		cm.sendSimple("Would you like to job advance?");
		job = 2;
		}
}	if (status == 1){
	switch (selection)
	{
		case 0:
		cm.getPlayer().changeJob(210); 
		cm.dispose();
		break;
		case 1:
		cm.getPlayer().changeJob(220); 
		cm.dispose();
		break;
		case 2:
		cm.getPlayer().changeJob(230); 
		cm.dispose();
		break;		
	}
	switch (job)
	{
		case 0:
		if(cm.getJob() == 0) {
			cm.getPlayer().changeJob(200);
			cm.gainItem(1382000, 1);
			cm.dispose();			
		}
		break;
		case 1:
		if(cm.getJob() == 210) {
			cm.getPlayer().changeJob(211);
			cm.dispose();			
		}
		if(cm.getJob() == 220) {
			cm.getPlayer().changeJob(221);
			cm.dispose();			
		}
		if(cm.getJob() == 230) {
			cm.getPlayer().changeJob(231);
			cm.dispose();			
		}
		break;
				case 2:
		if(cm.getJob() == 211) {
			cm.getPlayer().changeJob(212);
			cm.dispose();			
		}
		if(cm.getJob() == 221) {
			cm.getPlayer().changeJob(222);
			cm.dispose();			
		}
		if(cm.getJob() == 231) {
			cm.getPlayer().changeJob(232);
			cm.dispose();			
		}
		break;
	}
}
}