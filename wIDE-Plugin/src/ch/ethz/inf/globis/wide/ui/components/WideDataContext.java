package ch.ethz.inf.globis.wide.ui.components;

import com.intellij.openapi.actionSystem.DataContext;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

/**
 * Created by fabian on 09.05.16.
 */
public class WideDataContext implements DataContext {

    private HashMap<String, Object> m_map = new HashMap();

    public void setData(String key, Object value) {
        m_map.put(key, value);
    }
    @Nullable
    @Override
    public Object getData(@NonNls String s) {
        return m_map.get(s);
    }
}
