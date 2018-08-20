package constants;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lloyd Korn
 */
public class ReactorConstants {

    public static final List<Integer> Viens = new ArrayList();
    public static final List<Integer> Herbs = new ArrayList();
    public static final int Silver_Vein = 200000,
            Magenta_Vein = 200001,
            Blue_Vien = 200002,
            Brown_Vien = 200003,
            Emerald_Vein = 200004,
            Gold_Vein = 200005,
            Aquamarine_Vein = 200006,
            Red_Vein = 200007,
            Black_Vein = 200008,
            Purple_Vein = 200009,
            Silver_Vein2 = 200010,
            Heartstone = 200011,
            Silver_Herb = 100000,
            Magenta_Herb = 100001,
            Blue_Herb = 100002,
            Brown_Herb = 100003,
            Emerald_Herb = 100004,
            Gold_Herb = 100005,
            Aquamarine_Herb = 100006,
            Red_Herb = 100007,
            Black_Herb = 100008,
            Purple_Herb = 100009,
            Silver_Herb2 = 100010,
            Gold_Flower = 100011;

    static {
        Viens.add(Silver_Vein);
        Viens.add(Magenta_Vein);
        Viens.add(Blue_Vien);
        Viens.add(Brown_Vien);
        Viens.add(Emerald_Vein);
        Viens.add(Gold_Vein);
        Viens.add(Aquamarine_Vein);
        Viens.add(Red_Vein);
        Viens.add(Black_Vein);
        Viens.add(Purple_Vein);
//	Viens.add(Silver_Vein2); // Tutorial
//	Viens.add(Heartstone);
        //
        Herbs.add(Silver_Herb);
        Herbs.add(Magenta_Herb);
        Herbs.add(Blue_Herb);
        Herbs.add(Brown_Herb);
        Herbs.add(Emerald_Herb);
        Herbs.add(Gold_Herb);
        Herbs.add(Aquamarine_Herb);
        Herbs.add(Red_Herb);
        Herbs.add(Black_Herb);
        Herbs.add(Purple_Herb);
//	Herbs.add(Silver_Herb2); // Tutorial
//	Herbs.add(Gold_Flower);
    }

    public static boolean IsHerbsOrViens(int id) {
        return id >= Silver_Herb && id <= Heartstone;
    }

    public static boolean IsHerbs(int id) {
        return id >= Silver_Herb && id <= Gold_Flower;
    }

    public static boolean IsViens(int id) {
        return id >= Silver_Vein && id <= Heartstone;
    }

    public static boolean IsTutorialProfession(int id) {
        return id == Silver_Vein2 || id == Silver_Herb2;
    }
}
