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

public class WideCompatibilityAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {

    }

    private void annotateHtml(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {

    }

    private void annotateJS(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {

  }

    private void annotateCss(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {

    }
}
