// Mr. Unknown >> Aran - Catch the shadow warrior!

var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	status--;
    }
    if (status == 0) {
	cm.sendNext("I have been waiting for you, the hero's successor...");
    } else if (status == 1) {
	cm.sendNextPrev("#b(The hero's successor...? The Shadow Warrior must know a thing or two about the hero, but much like Mu Gong, I don't think he knows that I AM that hero.)");
    } else if (status == 2) {
	cm.sendNextPrev("The #bSeal Stone of Mu Lung is the seed that has been sowed by the heroes, but we the Black Wings will be the one that reaps from it. I am amazed to see you defeat Francisand Dagoth... but that would be the end of your winning streak.");
    } else if (status == 3) {
	cm.sendNextPrev("It's unfortunate that I am facing the hero's successor as an enemy, but there's nothing I can do. As a member of Black Wings, be prepared to die!");
    } else if (status == 4) {
	cm.removeNpc(925040100,1204020);
	cm.spawnMob(9300351,907,51);
	cm.dispose();
    }
}