package server.skills.effects.manager;

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
public class EffectManager {

    private static final HashMap<String, AbstractEffect> mEffectProviders = new HashMap<>();
    private static final List<Class<?>> liEffectClassess = new ArrayList<>();
    private static final List<Integer> liLoadedEffects = new ArrayList<>();
    static Reflections pReflection;

    public static void loadBuffEffectProviders() {
        mEffectProviders.clear();
        pReflection = new Reflections("server.skills.effects");
        Set<Class<?>> buffEffectClasses = pReflection.getTypesAnnotatedWith(Effect.class);
        for (Class effectClass : buffEffectClasses) {
            liEffectClassess.add(effectClass);
        }
    }

    public static void loadUserEffectsFromHandlingClasses() {
        for (Class<?> effectClass : liEffectClassess) {
            try {
                AbstractEffect cls = (AbstractEffect) effectClass.newInstance();
                for (int buffid : cls.GetUserEffects()) {
                    liLoadedEffects.add(buffid);
                }
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(EffectManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static final boolean IsUserEffect(int nSourceID) {
        for (int nSkillID : liLoadedEffects) {
            if (nSourceID == nSkillID) {
                return true;
            }
        }
        return false;
    }

    public static boolean SetEffect(MapleStatEffect pEffect, int nSourceID) {
        for (Class<?> effectClass : liEffectClassess) {
            int nClass = nSourceID / 10000;
            try {
                if (!AbstractEffect.class.isAssignableFrom(effectClass)) {
                    continue;
                }
                AbstractEffect pEffectProvider = (AbstractEffect) effectClass.newInstance();
                if (pEffectProvider.IsCorrectClass(nClass)) {
                    pEffectProvider.SetEffect(pEffect, nSourceID);
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
