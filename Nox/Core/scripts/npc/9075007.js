var chat = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1 /*End Chat*/ || mode == 0 && chat == 0 /*Due to no chat -1*/) {
        cm.dispose();
        return;
    }
    mode == 1 ? chat++ : chat--;
    if (chat == 0)
        cm.sendYesNo("Are you sure you want to exit the Evolution System?");
    if(chat == 1)
        cm.sendSimple("Evolution System shutting down.");
    if(chat == 2){
        cm.warp(957000000);
        cm.dispose();
    }
 }