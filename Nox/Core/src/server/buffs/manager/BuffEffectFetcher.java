package server.buffs.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.MapleStatEffect;
import org.reflections.Reflections;
import tools.LogHelper;

/**
 *
 * @author Novak
 */
public class BuffEffectFetcher {

    private static final HashMap<String, AbstractBuffClass> buffClasses = new HashMap<>();
    private static final List<Class<?>> loadedBuffProviders = new ArrayList<>();
    private static final List<Integer> loadedUserEffects = new ArrayList<>();
    static Reflections reflections;

    public static void loadBuffEffectProviders() {
        buffClasses.clear();
        reflections = new Reflections("server.buffs");
        Set<Class<?>> buffEffectClasses = reflections.getTypesAnnotatedWith(BuffEffectManager.class);
        for (Class effectClass : buffEffectClasses) {
            loadedBuffProviders.add(effectClass);
        }
    }

    public static void loadUserEffectsFromHandlingClasses() {
        for (Class<?> effectClass : loadedBuffProviders) {
            try {
                AbstractBuffClass cls = (AbstractBuffClass) effectClass.newInstance();
                for (int buffid : cls.getUserEffects()) {
                    loadedUserEffects.add(buffid);
                }
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(BuffEffectFetcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static final boolean isUserEffect(int skillid) {
        for (int buffid : loadedUserEffects) {
            if (skillid == buffid) {
                return true;
            }
        }
        return false;
    }

    public static boolean getEffectInfo(MapleStatEffect eff, int skillid) {
        for (Class<?> effectClass : loadedBuffProviders) {
            int jobid = skillid / 10000;
            try {
                if (!AbstractBuffClass.class.isAssignableFrom(effectClass)) {
                    continue;
                }
                AbstractBuffClass cls = (AbstractBuffClass) effectClass.newInstance();
                if (cls.containsJob(jobid)) {
                    if (!cls.containsSkill(skillid)) {
                        continue;
                    }
                    cls.handleEffect(eff, skillid);
                    return true;
                }
            } catch (InstantiationException | IllegalAccessException ex) {
                LogHelper.GENERAL_EXCEPTION.get().error("Buff handling class: " + effectClass.getName()
                        + "doesn't seem contain the annotated handleBuff method.");
            }
        }
        return false;
    }
}
