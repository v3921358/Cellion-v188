var names = Array("Dawn", "Blaze", "Wind", "Night", "Thunder");
var mid = Array("271030201", "271030202", "271030203", "271030204", "271030205");

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {                
            text = "Help us!!! #r#h0##k, The Cygnus Empress must be killed. Which area do you want to go first and gain your items. Select a destination.\r\n"; 
            for (var i = 0; i < names.length; text += "#L"+i+"##b"+names[i]+"#k\r\n#l", i++); 
            cm.sendSimple(text);
        } else if (status == 1) {
            cm.sendNext("Moving to "+names[selection]+". Press Escape to cancel.");
            map = mid[selection];
        } else if (status == 2) {
            cm.warp(map, 0);
            cm.dispose();
        }
    }
}  