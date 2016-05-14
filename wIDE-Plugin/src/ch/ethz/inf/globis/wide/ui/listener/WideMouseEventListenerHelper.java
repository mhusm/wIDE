package ch.ethz.inf.globis.wide.ui.listener;

import ch.ethz.inf.globis.wide.language.IWideLanguageHandler;
import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.lookup.io.WideQueryResponse;
import ch.ethz.inf.globis.wide.registry.WideLanguageRegistry;
import ch.ethz.inf.globis.wide.ui.components.popup.WidePopupHelper;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseMotionListener;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * Created by fabian on 17.04.16.
 */
public class WideMouseEventListenerHelper {

    private static final WideLogger LOGGER = new WideLogger(WideMouseEventListenerHelper.class.getName());

    private static final WideMouseEventListenerHelper INSTANCE = new WideMouseEventListenerHelper();
    private Editor editor;
    private WideQueryResponse parentResult;
    private WideQueryResponse currentResult;
    private EditorMouseMotionListener listener;

    public static WideMouseEventListenerHelper getInstance() {
        return INSTANCE;
    }

    private WideMouseEventListenerHelper() {

    }

    public void registerMouseEventListener(WideQueryResponse parentResult, WideQueryResponse currentResult, Editor editor) {
        this.editor = editor;
        this.currentResult = currentResult;

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
                    String selectedAttribute = editorMouseEvent.getEditor().getSelectionModel().getSelectedText();

                    PsiFile psiFile = PsiDocumentManager.getInstance(editorMouseEvent.getEditor().getProject()).getPsiFile(editorMouseEvent.getEditor().getDocument());
                    PsiElement element = psiFile.findElementAt(editorMouseEvent.getEditor().getSelectionModel().getSelectionStart());

                    IWideLanguageHandler handler = WideLanguageRegistry.getInstance().getLanguageHandler(element.getClass());

                    if (handler != null) {
                        if (parentResult.getKey().equals(selectedAttribute)) {
                            // tag selected -> show tag information
                            if (!parentResult.equals(currentResult)) {
                                handler.getPopupHelper().showLookupResults(parentResult, parentResult, editor);
                            }
                        } else {
                            // attribute selected -> show attribute information
                            for (WideQueryResponse result : parentResult.getSubResults()) {
                                if (result.getKey().equals(selectedAttribute) || result.getValue().equals(selectedAttribute)) {
                                    if (!result.equals(currentResult)) {
                                        handler.getPopupHelper().showLookupResults(parentResult, result, editor);
                                    }
                                }
                            }
                        }
                    } else {
                        // Too much output
                        //LOGGER.info("MouseEventListener was hovered over an element of a not supported Language.");
                    }
                }
            }

            @Override
            public void mouseDragged(EditorMouseEvent editorMouseEvent) {

            }
        };

        this.editor.addEditorMouseMotionListener(this.listener);
    }

    public void deregisterMouseEventListener() {
        // when popup is closed -> remove listener to get normal cursor behaviour
        if (this.editor != null && this.listener != null) {
            this.editor.removeEditorMouseMotionListener(this.listener);
            this.editor = null;
            this.listener = null;
            this.parentResult = null;
            this.currentResult = null;
        }
    }

}
