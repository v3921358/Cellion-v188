package constants;

/**
 *
 * @author Itzik
 */
public enum QuickMove {

    MAP1(100000000),
    MAP2(101000000),
    MAP3(102000000),
    MAP4(103000000),
    MAP5(104000000),
    MAP6(105000000),
    MAP7(120000100),
    MAP8(200000000),
    MAP9(220000000),
    MAP10(221000000),
    MAP11(222000000),
    MAP12(230000000),
    MAP13(240000000),
    MAP14(250000000),
    MAP15(251000000),
    MAP16(260000000),
    MAP17(261000000),
    MAP18(310000000),
    MAP19(400000000);
    private final int map;

    private QuickMove(int map) {
        this.map = map;
    }

    public int getMap() {
        return map;
    }

    public int getNPCFlag() {
        return QuickMoveNPC.PVP.getValue()
                | QuickMoveNPC.MONSTER_PARK.getValue()
                | QuickMoveNPC.MIRROR.getValue()
                | QuickMoveNPC.MARKET.getValue()
                | QuickMoveNPC.CRAFTING.getValue()
                | QuickMoveNPC.WORLD_TRANSPORT.getValue()
                | QuickMoveNPC.ISLAND_TRANSPORT.getValue()
                | QuickMoveNPC.RANDOLF.getValue()
                | QuickMoveNPC.LUCIA.getValue()
                | QuickMoveNPC.CONOR.getValue()
                | QuickMoveNPC.PART_TIME.getValue()
                | QuickMoveNPC.HAIR_STYLE.getValue()
                | QuickMoveNPC.FACE_STYLE.getValue();
    }

    public enum QuickMoveNPC {

        PVP(0x1, 0, false, 9070004, 30, "Move to the Battle Mode zone #cBattle Square#, where you can fight against other users.\n#cLv. 30 or above can participate in Battle Square."),
        MIRROR(0x2, 2, false, 9010022, 10, "Use the #cDimensional Mirror# to move to a variety of party quests."),
        MONSTER_PARK(0x4, 1, false, 9071003, 20, "Move to the party zone \n#cMonster Park#, where you can fight against strong monsters with your party members.\n#cOnly Lv. 20 or above can participate in the Monster Park."),
        
        WORLD_TRANSPORT(0x8, 5, true, NPCConstants.QuickMove_NPC, 0, "Travel to different maps and access useful shortcuts."), //Warping npc, Lolo
        
        ISLAND_TRANSPORT(0x10, 6, false, 9000089, 0, "Take the #cTaxi# to move to major areas quickly."), //Taxi, Camel
        
        MARKET(0x20, 3, true, 9000087, 0, "Move to the #cFree Market#, where you can mingle with other users and access a variety of NPCs."),
        
        CRAFTING(0x40, 4, false, 9000088, 30, "Move to #cArdentmill#, the town of Professions.\n#cOnly Lv. 30 or above can move to Ardentmill"),
        RANDOLF(0x80, 7, false, 0, 10, ""),
        LUCIA(0x100, 8, false, 0, 10, ""),
        CONOR(0x200, 9, false, 0, 10, ""),
        PART_TIME(0x400, 10, false, 9010041, 30, "Receive Part-Time Job reward."),
        HAIR_STYLE(0x800, 13, false, 9000123, 0, "Change your characters appears for NX."),
        FACE_STYLE(0x1000, 14, false, 9201252, 0, "You can get plastic surgery from Nurse Pretty.");
       
        private final int value, type, id, level;
        private final String desc;
        private final boolean show;

        private QuickMoveNPC(int value, int type, boolean show, int id, int level, String desc) {
            this.value = value;
            this.type = type;
            this.show = show;
            this.id = id;
            this.level = level;
            this.desc = desc;
        }

        public final byte getValue() {
            return (byte) value;
        }

        public final boolean check(int flag) {
            return (flag & value) != 0;
        }

        public int getType() {
            return type;
        }

        public boolean show() {
            return show;
        }

        public int getId() {
            return id;
        }

        public int getLevel() {
            return level;
        }

        public String getDescription() {
            return desc;
        }
    }
}
