package ch.ethz.inf.globis.wide.completion;

import ch.ethz.inf.globis.wide.lookup.WideSuggestionHandler;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.*;
import com.intellij.codeInsight.lookup.impl.LookupActionHandler;
import com.intellij.codeInsight.lookup.impl.LookupImpl;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import org.apache.xerces.impl.dtd.XMLSimpleType;
import org.jdesktop.swingx.util.Utilities;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.processing.Completion;
import java.awt.image.LookupTable;
import java.nio.file.attribute.UserPrincipalLookupService;

/**
 * Created by fabian on 11.05.16.
 */
public class WideCompletionContributor extends CompletionContributor {
    public WideCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {

//                        PsiFile psiFile = PsiDocumentManager.getInstance(parameters.getEditor().getProject()).getPsiFile(parameters.getEditor().getDocument());
//                        PsiElement startElement = psiFile.findElementAt(parameters.getOffset()).getPrevSibling();
//
//                        // Query suggestions
//                        resultSet.addAllElements(WideSuggestionHandler.differentiateElements(parameters.getEditor(), startElement, startElement.getText()));
//
//                        System.out.println("make suggestions");

                        // Don't let the IDE make suggestions

//                        LookupElement element = LookupElementBuilder.create(new Object());
//                        resultSet.stopHere();
                    }
                }
        );
    }
}
