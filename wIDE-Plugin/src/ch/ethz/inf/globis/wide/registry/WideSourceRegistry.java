package ch.ethz.inf.globis.wide.registry;

import ch.ethz.inf.globis.wide.io.query.AbstractWideSourceResult;
import ch.ethz.inf.globis.wide.sources.caniuse.WideCaniuseResult;
import ch.ethz.inf.globis.wide.sources.mdn.WideMDNResult;
import ch.ethz.inf.globis.wide.logging.WideLogger;
import org.codehaus.jettison.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by fabian on 12.05.16.
 */
public class WideSourceRegistry {
    private static final WideLogger LOGGER = new WideLogger(WideSourceRegistry.class.getName());

    private Map<String, Class<? extends AbstractWideSourceResult>> sources = new HashMap();
    private final static  WideSourceRegistry INSTANCE = new WideSourceRegistry();

    private WideSourceRegistry() {
        sources.put("mdn", WideMDNResult.class);
        sources.put("caniuse", WideCaniuseResult.class);
    }

    public static WideSourceRegistry getInstance() {
        return INSTANCE;
    }

    public Set<String> getSources() {
        return sources.keySet();
    }

    public AbstractWideSourceResult instantiateResult(String source, JSONObject obj) {
        try {
            Constructor<?> constructor = sources.get(source).getConstructor(JSONObject.class);
            if (constructor != null) {
                AbstractWideSourceResult result = (AbstractWideSourceResult) constructor.newInstance(obj);
                return result;
            }
        } catch (NoSuchMethodException e) {
            LOGGER.warning("WideQueryResult for " + source + " could not be instantiated.");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            LOGGER.warning("WideQueryResult for " + source + " could not be instantiated.");
            e.printStackTrace();
        } catch (InstantiationException e) {
            LOGGER.warning("WideQueryResult for " + source + " could not be instantiated.");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            LOGGER.warning("WideQueryResult for " + source + " could not be instantiated.");
            e.printStackTrace();
        }

        return null;
    }
}
