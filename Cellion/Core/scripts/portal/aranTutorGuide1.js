function enter(pi) { // tutor00
    if (pi.getInfoQuest(21002).equals("normal=o;arr0=o;arr1=o;mo1=o;mo2=o;mo3=o;mo4=o")) {
	pi.playerMessage(5, "Press C repeatedly for a combo attack.");
	pi.updateInfoQuest(21002, "normal=o;arr0=o;arr1=o;mo1=o;chain=o;mo2=o;mo3=o;mo4=o");
	pi.showWZUOLEffect("Effect/OnUserEff.img/guideEffect/aranTutorial/tutorialGuide2");
    }
}