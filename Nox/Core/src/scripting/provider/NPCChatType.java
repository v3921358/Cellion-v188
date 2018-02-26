package scripting.provider;

/**
 * Describes the type of NPC Conversation, and what it can do [back,forward, etc] Will make our job of checking easier [anticheat]
 *
 * @author
 */
public enum NPCChatType {

    OnAskImage(false, true, (byte) 0), // odin's hack, image, next, prev, nextprev, ok all have 0... until I clean this up later
    NEXT(false, true, (byte) 0),
    PREV(true, false, (byte) 0),
    NEXTPREV(true, true, (byte) 0),
    OK(false, false, (byte) 0),
    OnAskYesNo(true, true, (byte) 3),
    OnAskText(false, true, (byte) 4),
    OnAskNumber(false, true, (byte) 5),
    OnAskMenu(false, true, (byte) 6),
    OnInitialQuiz(false, true, (byte) 7), // TODO fix allowback and allowForward
    OnInitialSpeedQuiz(false, true, (byte) 8), // TODO fix allowback and allowForward
    OnICQuiz(false, true, (byte) 9), // TODO fix allowback and allowForward
    OnAskAvater(false, true, (byte) 10), // TODO fix allowback and allowForward
    OnAskAndroid(false, true, (byte) 11), // TODO fix allowback and allowForward
    OnAskPet(false, true, (byte) 13), // TODO fix allowback and allowForward
    OnAskPetAll(false, true, (byte) 14), // TODO fix allowback and allowForward
    OnAskPetEvolution(false, true, (byte) 15), // TODO fix allowback and allowForward
    //???
    OnAskAccept(false, true, (byte) 17),// --> basically OnAskYesNo but with a bool
    OnAskBoxText(false, true, (byte) 19), // TODO fix allowback and allowForward
    OnAskSlideMenu(false, true, (byte) 20), // TODO fix allowback and allowForward
    OnAskUserDirection(false, true, (byte) 21), // TODO fix allowback and allowForward
    OnPlayMoveClip(false, true, (byte) 22), // TODO fix allowback and allowForward=
    OnAskCenter(false, true, (byte) 23), // TODO fix allowback and allowForward
    OnAskSelectMenu(false, true, (byte) 26), // TODO fix allowback and allowForward
    OnAskAngelicBuster(false, true, (byte) 27), // TODO fix allowback and allowForward
    OnSayIllustration(false, true, (byte) 28), // TODO fix allowback and allowForward
    OnSayDualIllustration(false, true, (byte) 29), // TODO fix allowback and allowForward
    OnAskYesNoIllustration(false, true, (byte) 30), // TODO fix allowback and allowForward
    OnAskAcceptIllustration(false, true, (byte) 31), // TODO fix allowback and allowForward
    OnAskMenuIllustration(false, true, (byte) 32), // TODO fix allowback and allowForward
    OnAskYesNoDualIllustration(false, true, (byte) 33), // TODO fix allowback and allowForward
    OnAskAcceptDualIllustration(false, true, (byte) 34), // TODO fix allowback and allowForward
    OnAskMenuDualIllustration(false, true, (byte) 35), // TODO fix allowback and allowForward
    OnAskSSN2(false, true, (byte) 36), // TODO fix allowback and allowForward
    OnAskAvaterExZero(false, true, (byte) 37), // TODO fix allowback and allowForward
    OnMonologue(false, true, (byte) 40), // TODO fix allowback and allowForward
    OnAskWeaponBox(false, true, (byte) 41), // TODO fix allowback and allowForward
    OnAskBoxtTextBGImage(false, true, (byte) 42), // TODO fix allowback and allowForward
    OnAskUserSurvey(false, true, (byte) 43), // TODO fix allowback and allowForward
    OnSuccessCamera(false, true, (byte) 44), // TODO fix allowback and allowForward
    OnAskMixHair(false, true, (byte) 45), // TODO fix allowback and allowForward
    OnAskMixHairExZero(false, true, (byte) 46), // TODO fix allowback and allowForward
    OnAskCutsomMixHair(false, true, (byte) 47), // TODO fix allowback and allowForward
    OnAskCustomMixHairAndProb(false, true, (byte) 48), // TODO fix allowback and allowForward
    OnAskMixHairNew(false, true, (byte) 49), // TODO fix allowback and allowForward
    OnAskMixHairNewExZero(false, true, (byte) 50), // TODO fix allowback and allowForward
    OnNPCAction(false, true, (byte) 51), // TODO fix allowback and allowForward
    OnAskScreenShinningStarMsg(false, true, (byte) 52), // TODO fix allowback and allowForward
    OnInputUI(false, true, (byte) 53), // TODO fix allowback and allowForward
    OnAskNumberKeypad(false, true, (byte) 55), // TODO fix allowback and allowForward
    OnSpinOffGuitarRhytmGame(false, true, (byte) 56), // TODO fix allowback and allowForward
    OnAskGhostparkEnter(false, true, (byte) 57), // TODO fix allowback and allowForward
    OnCameraMsg(false, true, (byte) 58), // TODO fix allowback and allowForward
    OnSlidePuzzle(false, true, (byte) 59), // TODO fix allowback and allowForward
    OnDisguise(false, true, (byte) 60), // TODO fix allowback and allowForward
    OnRequireClientResponse(false, true, (byte) 61), // TODO fix allowback and allowForward

    NULL(false, false, (byte) -1);
    private final boolean allowBack, allowForward;
    private final byte intType;

    private NPCChatType(boolean allowBack, boolean allowForward, byte intType) {
        this.allowBack = allowBack;
        this.allowForward = allowForward;
        this.intType = intType;
    }

    public static final NPCChatType fromInt(int Str) {
        for (NPCChatType t : values()) {
            if (t.getType() == Str) {
                return t;
            }
        }
        return NULL;
    }

    public final byte getType() {
        return (byte) intType;
    }

    public final boolean allowBack() {
        return allowBack;
    }

    public final boolean allowForward() {
        return allowForward;
    }

    private boolean checkInner(final int mode) {
        // System.out.println(this.toString() + " allowForward  " + allowForward + " allowForward" + allowForward );
        if ((!allowBack && mode == 0) || (!allowForward && mode == 1)) {
            return false;
        }
        return true;
    }

    public final boolean check(NPCChatType client_input, final int mode) {
        if (intType == -1) { // Hack, not done
            client_input = this;
        }
        return this == client_input && checkInner(mode);
    }
}
