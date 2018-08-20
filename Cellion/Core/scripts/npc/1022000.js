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
		cm.sendOk("Warriors can wield the widest array of weapons in the game, including everything from swords and axes to spears and blunt weapons, as well as shields, and can specialize in their favorites as they level up. Warriors hail from the town of Perion, located in the northern highlands of Victoria Island.");
		/*if (cm.getPlayer().getLevel() >= 10 && cm.getJob() == 0 ){
		cm.sendYesNo("Would you like to become a warrior?");
		job = 0
		}
		if (cm.getPlayer().getLevel() >= 30 && cm.getJob() == 100 ){
		cm.sendSimple("What do you want to become?#b\r\n#L0#Hero#l\r\n#L1#Page#l\r\n#L2#Dark knight#l#k");
		}
		if (cm.getPlayer().getLevel() >= 60 && (cm.getJob() == 110 || cm.getJob() == 120 || cm.getJob() == 130 )){
		cm.sendSimple("Would you like to job advance?");
		job = 1;
		}
		if (cm.getPlayer().getLevel() >= 100 && (cm.getJob() == 111 || cm.getJob() == 121 || cm.getJob() == 131 )){
		cm.sendSimple("Would you like to job advance?");
		job = 2;
		}*/
}	if (status == 1){
	switch (selection)
	{
		case 0:
		cm.getPlayer().changeJob(110); 
		cm.dispose();
		break;
		case 1:
		cm.getPlayer().changeJob(120); 
		cm.dispose();
		break;
		case 2:
		cm.getPlayer().changeJob(130); 
		cm.dispose();
		break;		
	}
	switch (job)
	{
		case 0:
		if(cm.getJob() == 0) {
			cm.getPlayer().changeJob(100);
			cm.gainItem(1302007, 1);
			cm.dispose();			
		}
		break;
		case 1:
		if(cm.getJob() == 110) {
			cm.getPlayer().changeJob(111);
			cm.dispose();			
		}
		if(cm.getJob() == 120) {
			cm.getPlayer().changeJob(121);
			cm.dispose();			
		}
		if(cm.getJob() == 130) {
			cm.getPlayer().changeJob(131);
			cm.dispose();			
		}
		break;
				case 2:
		if(cm.getJob() == 111) {
			cm.getPlayer().changeJob(112);
			cm.dispose();			
		}
		if(cm.getJob() == 121) {
			cm.getPlayer().changeJob(122);
			cm.dispose();			
		}
		if(cm.getJob() == 131) {
			cm.getPlayer().changeJob(132);
			cm.dispose();			
		}
		break;
	}
}
}