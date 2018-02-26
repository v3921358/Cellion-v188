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
        cm.sendYesNo("A*bzzt bzzt* Monsters will be generated! Proceed?");//Deactivate Monster Generator?
    if(chat == 1)
        //topmsg Monster Generator activated. + spawn mobs
        cm.sendSimple("Evolution System shutting down.");//The Monster Generator is running.
    if(chat == 2){
        cm.dispose();
    }
 }