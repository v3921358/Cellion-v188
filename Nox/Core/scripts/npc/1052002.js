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
		cm.sendYesNo("Would you like to become an thief?");
		job = 0
		}
		if (cm.getPlayer().getLevel() >= 30 && cm.getJob() == 400 ){
		cm.sendSimple("What do you want to become?#b\r\n#L0#Assassin#l\r\n#L1#Bandit#l#k");
		}
		if (cm.getPlayer().getLevel() >= 60 && (cm.getJob() == 410 || cm.getJob() == 420 )){
		cm.sendSimple("Would you like to job advance?");
		job = 1;
		}
		if (cm.getPlayer().getLevel() >= 100 && (cm.getJob() == 411 || cm.getJob() == 421 )){
		cm.sendSimple("Would you like to job advance?");
		job = 2;
		}
}	if (status == 1){
	switch (selection)
	{
		case 0:
		cm.getPlayer().changeJob(410); 
		cm.dispose();
		break;
		case 1:
		cm.getPlayer().changeJob(420); 
		cm.dispose();
		break;		
	}
	switch (job)
	{
		case 0:
		if(cm.getJob() == 0) {
			cm.getPlayer().changeJob(400);
			cm.dispose();			
		}
		break;
		case 1:
		if(cm.getJob() == 410) {
			cm.getPlayer().changeJob(411);
			cm.dispose();			
		}
		if(cm.getJob() == 420) {
			cm.getPlayer().changeJob(421);
			cm.dispose();			
		}
		break;
				case 2:
		if(cm.getJob() == 411) {
			cm.getPlayer().changeJob(412);
			cm.dispose();			
		}
		if(cm.getJob() == 421) {
			cm.getPlayer().changeJob(422);
			cm.dispose();			
		}
		break;
	}
}
//cm.dispose();	
}