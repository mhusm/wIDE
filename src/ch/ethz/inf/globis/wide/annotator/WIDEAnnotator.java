package ch.ethz.inf.globis.wide.annotator;

import com.intellij.database.model.PsiObject;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.intellij.plugins.relaxNG.compact.psi.util.PsiFunction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WIDEAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull final PsiElement element, @NotNull AnnotationHolder holder) {
        System.out.println(element.getClass());

//        if (element instanceof LeafPsiElement) {
//            System.out.println(element.getNode().getChars());
//        }

        if (element instanceof PsiFunctionalExpression) {
            // doesn't work
            System.out.println(element.getNode().getChars());
        }

        if (element instanceof PsiLiteralExpression) {
            System.out.println("literal expression");
        }

        if (element instanceof PsiCall) {
            // doesn't work
            System.out.println("call");
        }

//        if (element instanceof PsiReference) {
//            element.getNode().getPsi().getChildren();
//            if (element.getNode().getTreeParent() == null) {
//                System.out.println("ROOT ELEMENT: " + element.getNode().getChars());
//            } else {
//               // System.out.println("ELEMENT " + element.getNode().getChars() + " IS CHILD OF " + element.getNode().getTreeParent().getChars());
//            }
//        }

//        if (element instanceof PsiReferenceExpression) {
//            System.out.println("EXPRESSION: " + element.getNode().getChars());
//        }
//
//        if(element instanceof PsiCodeBlock) {
//            System.out.println("codeblock");
//        }
//
//        if(element instanceof PsiClass) {
//            System.out.println("class");
//        }

        if(element instanceof PsiComment) {
            //works
            System.out.println("comment");
        }

        if(element instanceof PsiForStatement) {
            System.out.println("for statement");
        }

        if(element instanceof PsiKeyword) {
            System.out.println("keyword");
        }

        if(element instanceof PsiLiteral) {
            System.out.println("literal");
        }

        if(element instanceof PsiPlainText) {
            System.out.println("plaintext");
        }

//        if(element instanceof PsiObject) {
//            System.out.println("object");
//        }

        if(element instanceof PsiModifier) {
            System.out.println("modifier");
        }

//        if(element instanceof PsiAnnotation) {
//            System.out.println("annotation");
//        }
    }
}
