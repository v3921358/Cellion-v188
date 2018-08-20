var status = -1;

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 10) {
	    cm.sendNext("Has fear blinded you? If you are truly a Dragon Master, this is a fate that you cannot escape... If you desire to see the past again, #bforfeit the quest#k and speak with me.");
	    cm.dispose();
	    return;
	}
	status--;
    }
    switch (status) {
	case 0:
	    if (cm.getQuestStatus(22591) == 2) {
		cm.warp(914100021,0);
		cm.dispose();
		return;
	    }
            cm.setAllowConversationCancel(false); // dont allow this to be cancelled by the client
            
	    cm.sendNext("Do you now understand?");
	    break;
	case 1:
	    cm.sendNextPrev("Yes. I saw and heard many things. So you are #p1013000#", -1, true);
	    break;
	case 2:
	    cm.sendNextPrev("Do not speak those words. I am as good as dead now... Speaking of it would only cause sadness...");
	    break;
	case 3:
	    cm.sendNextPrev("What happened to you?", -1, true);
	    break;
	case 4:
	    cm.sendNextPrev("It was the last curse of the Black Mage. To save Freud, I received the curse in his place...and the curse encased me in ice. The ice has almost completely melted, but my body has become one with this Island. If I were to move, the organisms on this Island would die...");
	    break;
	case 5:
	    cm.sendNextPrev("But...!", -1, true);
	    break;
	case 6:
	    cm.sendNextPrev("It matters little what happens to me now. But the infiltrator... Freud, who was able to survive in my place, put a magic spell on this island. Anyone connected with the Black Mage could not step foot on it. The only one who could awaken me is an Onyx Dragon Master.");
	    break;
	case 7:
	    cm.sendNextPrev("I...I see. But how did #p1013203# know?" , -1, true);
	    break;
	case 8:
	    cm.sendNextPrev("That is not important. What is important is that he used you.");
	    break;
	case 9:
	    cm.sendNextPrev("Used...me?!", -1, true);
	    break;
	case 10:
	    cm.forceCompleteQuest(22591);
	    cm.sendYesNo("He knew that I was here, but as a follower of the Black Mage, he could not enter or awaken me. That is why he used you to break the magic protecting the cave.");
	    break;
	case 11:
	    cm.sendNext("If they are indeed followers of the Black Mage... If at the end of that brutal fight, they indeed are seeking to revive the Black Mage... Please stop them. That is all that I ask.");
	    break;
	case 12:
	    cm.sendOk("(Master of my child, may the blessings of heaven be with you.)", -1, true);
	    break;
	case 13:
	    cm.warp(914100021,0);
	    cm.dispose();
	    break;
    }
//   cm.forceCompleteQuest(22591);
//   cm.dispose();
}