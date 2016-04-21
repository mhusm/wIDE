package ch.ethz.inf.globis.wide.ui.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.javascript.psi.JSCallExpression;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.css.CssElement;
import com.intellij.psi.xml.XmlElement;
import org.jetbrains.annotations.NotNull;

public class WideAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        if (element.getParent() instanceof XmlElement) {
            annotateHtml(element, holder);
        } else if (element instanceof JSCallExpression) {
            annotateJS(((JSCallExpression) element).getMethodExpression().getLastChild(), holder);
        } else if (element instanceof CssElement) {
            annotateCss(element, holder);
        }
    }

    private void annotateHtml(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {

    }

    private void annotateJS(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
//        TextRange range = new TextRange(element.getTextRange().getStartOffset(),
//                element.getTextRange().getEndOffset());
//        Annotation annotation = holder.createErrorAnnotation(((JSCallExpression) element).getMethodExpression().getLastChild(), "blibla");
    }

    private void annotateCss(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {

    }
}
