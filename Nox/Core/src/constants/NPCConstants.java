package constants;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lloyd Korn
 */
public class NPCConstants {

    public static final int PlayerNPC_REMOVECS = 9901000,
                            NPC_DivineBird = 2143002,
                            NPC_BigHeadward_Ruins = 2142003,
            
                            Tutorial_NPC = 9330603,
                            JobAdvance_NPC = 9000386,
                            VMatrixAdvance_NPC = 1057006,
                            QuickMove_NPC = 9001001,
            
                            EventShop_NPC = 9010001,
                            VoteShop_NPC = 9010002,
                            DonorShop_NPC = 9010003,
            
                            CashItemDrop_NPC = 9010017
    ;

    public static final String[] LIMITED_NPC = {
        "buroo", // Inkie
        "halloween09_GL",
        "chocoDay",
        "MonsterPark",
        "jobsangin", // Buff item seller
        "2012Fool1",};

    public static final boolean isPlayerNPC(final int nid) {
        return nid >= 9901000 && nid <= 9901920;
    }

    public static final List<Integer> possiblePlayerNPCLocation(final int mapid) {
        switch (mapid) {
            case 910000000: {
                final List<Integer> ret = new ArrayList();
                ret.add(PlayerNPC_REMOVECS);
                return ret;
            }
        }
        return null;
    }
}
