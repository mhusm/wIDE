package ch.ethz.inf.globis.wide.lookup;

import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.parsing.css.WideCssHandler;
import ch.ethz.inf.globis.wide.parsing.html.WideHtmlHandler;
import ch.ethz.inf.globis.wide.parsing.javascript.WideJavascriptHandler;
import ch.ethz.inf.globis.wide.ui.popup.WidePopupHelper;
import ch.ethz.inf.globis.wide.ui.window.WideCSSWindowFactory;
import ch.ethz.inf.globis.wide.ui.window.WideHtmlWindowFactory;
import ch.ethz.inf.globis.wide.ui.window.WideJSWindowFactory;
import ch.ethz.inf.globis.wide.ui.window.WideWindowFactory;
import com.intellij.lang.javascript.psi.JSElement;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.css.CssElement;
import com.intellij.psi.xml.XmlElement;

import java.util.List;

/**
 * Created by fabian on 20.04.16.
 */
public interface IWideLookupHandler {

    void differentiateLanguages(Editor editor, PsiFile psiFile, PsiElement startElement, PsiElement endElement);

    void handleHtml(Editor editor, PsiFile psiFile, PsiElement startElement, PsiElement endElement);

    void handleJS(Editor editor, PsiFile psiFile, PsiElement startElement, PsiElement endElement);

    void handleCSS(Editor editor, PsiFile psiFile, PsiElement startElement, PsiElement endElement);

    void handleError(Editor editor, String error);
}
