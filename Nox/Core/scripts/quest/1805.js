var status = -1;

function start(mode, type, selection) {
	if (mode == 1){
	    status++;
	 }else{
	    status--;
	qm.dispose();
	}
	if (status == 0) {
	    qm.sendYesNo("Evolution System instructional program complete. Activating warm-up program. Connect to Virtual World?");
	} else if (status == 1) {
		qm.sendSimple("Press #r#eSTART#n#k to enter.");
	} else if (status == 2) {
	    qm.sendUIWindow(100, 9075006);
        qm.dispose();
	}
}

function end(mode, type, selection) {
      qm.dispose();		
}       
  
  