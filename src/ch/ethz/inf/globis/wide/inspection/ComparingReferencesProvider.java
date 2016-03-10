package ch.ethz.inf.globis.wide.inspection;

import ch.ethz.inf.globis.wide.inspection.ComparingReferencesInspection;
import com.intellij.codeInspection.InspectionToolProvider;

/**
 * @author max
 */
public class ComparingReferencesProvider implements InspectionToolProvider {
    public Class[] getInspectionClasses() {
        return new Class[] { ComparingReferencesInspection.class};
    }
}