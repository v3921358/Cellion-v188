var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 2) {
	    qm.sendNext("Hmmm? are you busy with something else? But I assure you this is the most urgent matter you'll get.. because there's a reward tied to it that you won't want to miss.");
	    qm.dispose();
	    return;
	}
	status--;
    }
    switch (status) {
	case 0:
	    qm.sendNext("It've been a long time! You look like you have gained a lot of levels since the last time we met! You must be training really hard, I say, which I do not find surprising considering you'r the hero and all. Lirin will find it quite pleasing. Hahahah.");
	    break;
	case 1:
	    qm.sendNextPrev("Okay, enough talk. I realized that limiting my information network to just victoria Island might not be enough, so I tried expanding by first investigating Ossyria. I started off with #bOrbis#k and immediately hit the jackpot.");
	    break;
	case 2:
	    qm.askAcceptDecline("It looks like something strange is taking place at Orbis of Ossyria. It's a little different from the days of the puppeteer, but something just doesn't seem right, and I believe it has to do with Black Wings. I say you should head over to Orbis.");
	    break;
	case 3:
	    qm.forceStartQuest();
	    qm.sendNext("#bLisa the Fairy#k at Orbis should know a thing or two. Go visit Lisa and ask around. #bThere's something strange going on at Orbis#k...");
	    break;
	case 4:
	    qm.sendPrev("We havn't had anything in this scale in a while. Don't you find this exciting? It seems like whenever you've solved a case like this, you always acquire a new skill that've been deciphered by Lirin. Maybe this might lead you to a new one");
	    break;
	case 5:
	    qm.dispose();
	    break;
    }
}

function end(mode, type, selection) {
    qm.dispose();
}