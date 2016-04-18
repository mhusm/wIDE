package ch.ethz.inf.globis.wide.ui.editor;

import ch.ethz.inf.globis.wide.parsing.WideQueryResult;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseMotionListener;
import com.intellij.openapi.editor.event.SelectionEvent;
import com.intellij.openapi.editor.event.SelectionListener;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.ui.awt.RelativePoint;

import java.util.List;

/**
 * Created by fabian on 17.04.16.
 */
public class WideMouseEventListenerHelper {

    private static final WideMouseEventListenerHelper INSTANCE = new WideMouseEventListenerHelper();
    private Editor editor;
    private List<WideQueryResult> results;
    private EditorMouseMotionListener listener;

    public static WideMouseEventListenerHelper getInstance() {
        return INSTANCE;
    }

    private WideMouseEventListenerHelper() {

    }

    public void registerMouseEventListener(List<WideQueryResult> results, Editor editor) {
        this.editor = editor;
        this.results = results;

//        editor.getSelectionModel().addSelectionListener(new SelectionListener() {
//            @Override
//            public void selectionChanged(SelectionEvent selectionEvent) {
//                System.out.println("Start-offset: " + selectionEvent.getNewRange().getStartOffset());
//                System.out.println("End-offset: " + selectionEvent.getNewRange().getEndOffset());
//            }
//        });

        this.listener = new EditorMouseMotionListener() {
            @Override
            public void mouseMoved(EditorMouseEvent editorMouseEvent) {
                // Move Caret with mouse cursor
                int fromTop = editorMouseEvent.getMouseEvent().getY() / editorMouseEvent.getEditor().getLineHeight();
                int fromLeft = 2 * editorMouseEvent.getMouseEvent().getX() / (editorMouseEvent.getEditor().getContentComponent().getFont().getSize() + 1);

                // Wrap into Visual position
                VisualPosition startPosition = new VisualPosition(fromTop, fromLeft);

                editorMouseEvent.getEditor().getCaretModel().moveToVisualPosition(startPosition);
                if (!editorMouseEvent.getEditor().getCaretModel().getVisualPosition().leansRight) {
                    // if caret over word -> select word
                    editorMouseEvent.getEditor().getSelectionModel().selectWordAtCaret(true);
                }
            }

            @Override
            public void mouseDragged(EditorMouseEvent editorMouseEvent) {

            }
        };

        this.editor.addEditorMouseMotionListener(this.listener);
    }

    public void deregisterMouseEventListener() {
        if (this.editor != null && this.listener != null) {
            this.editor.removeEditorMouseMotionListener(this.listener);
            this.editor = null;
            this.listener = null;
            this.results = null;
        }
    }

}
