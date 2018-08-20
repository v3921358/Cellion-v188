package scripting.provider;

import tools.LogHelper;

import javax.script.*;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class AbstractScriptManager {

    private static final ReadWriteLock mutex = new ReentrantReadWriteLock();
    private static final Map<ScriptType, Map<String, String>> SCRIPT_CACHE = new HashMap<>();
    private final static ScriptEngineFactory sef = new ScriptEngineManager().getEngineByName("javascript").getFactory();

    static {
        for (ScriptType s : ScriptType.values()) {
            SCRIPT_CACHE.put(s, new HashMap<>());
        }
    }

    protected static Invocable getInvocable(String path, boolean cacheScript, ScriptType stype) {
        path = String.format("scripts/%s", path);
        if (cacheScript) {
            final Map<String, String> scriptdata = SCRIPT_CACHE.get(stype); // doesnt need to lock here

            String script = scriptdata.get(path);
            if (script != null) {
                try {
                    return getInvocableInternal(script, path, cacheScript, stype);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println(String.format("Error executing script. [%s]", path));
                }
                return null;
            }
        }
        File scriptFile = new File(path);

        if (!scriptFile.exists()) {
            return null;
        }
        char[] chars = new char[(int) scriptFile.length()];
        try (final FileReader fr = new FileReader(scriptFile)) {
            fr.read(chars, 0, chars.length);

            final String script = new String(chars);

            try {
                return getInvocableInternal(script, path, cacheScript, stype);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(String.format("Error executing script. [%s]", path));
            }
        } catch (Exception e) {
            LogHelper.INVOCABLE.get().info("Error executing script. Path: " + path + ":\n{}", e);
        }
        return null;
    }

    private static Invocable getInvocableInternal(String script, String path, boolean CacheScript, ScriptType stype) throws Exception {
        final ScriptEngine engine2 = sef.getScriptEngine();
        final CompiledScript compiled = ((Compilable) engine2).compile(script);
        compiled.eval();
        if (CacheScript) {
            mutex.writeLock().lock();
            try {
                final Map<String, String> scriptdata = SCRIPT_CACHE.get(stype);
                scriptdata.put(path, script);
            } finally {
                mutex.writeLock().unlock();
            }
        }
        return (Invocable) engine2;
    }

    public static void reloadCachedScript(ScriptType stype) {
        mutex.writeLock().lock();
        try {
            final Map<String, String> scriptdata = SCRIPT_CACHE.get(stype);
            scriptdata.clear();
        } finally {
            mutex.writeLock().unlock();
        }
    }
}
