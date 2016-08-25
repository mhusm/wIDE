package ch.ethz.inf.globis.wide.ui.listener;

import ch.ethz.inf.globis.wide.io.query.WideQueryResponse;
import ch.ethz.inf.globis.wide.language.IWideLanguageHandler;
import ch.ethz.inf.globis.wide.logging.WideLogger;
import ch.ethz.inf.globis.wide.registry.WideLanguageRegistry;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseMotionListener;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * After having done a lookup, this class listens for the mouse position
 * and updates the position and content of the information popup.
 *
 * @author Fabian Stutz
 * @since 2016-21-05
 * @version 1.0
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

    /**
     * Registers a mouse listener on the editor, which updates the displayed popup
     * information depending on the mouse position.
     *
     * Calls {@Code PopupHelper.showLookupResult(...)} of related Language Handler.
     *
     * @param parentResult The {@Code WideQueryResponse} of the parent result
     *                     (the highest parent element for which we did the lookup)
     * @param currentResult The {@Code WideQueryResponse} of the currently pointed
     *                      element.
     * @param editor The current editor instance for which the listener should be
     *               registered
     */
    public void registerMouseEventListener(WideQueryResponse parentResult, WideQueryResponse currentResult, Editor editor) {
        this.editor = editor;
        this.currentResult = currentResult;

        this.listener = new EditorMouseMotionListener() {

            @Override
            public void mouseMoved(EditorMouseEvent editorMouseEvent) {
                // Move Caret with mouse cursor
                // Numbers are based on empirical data
                int fromTop = editorMouseEvent.getMouseEvent().getY() / editorMouseEvent.getEditor().getLineHeight();
                int fromLeft = 2 * editorMouseEvent.getMouseEvent().getX() / (editorMouseEvent.getEditor().getContentComponent().getFont().getSize() + 1);

                // Wrap into Visual position
                VisualPosition startPosition = new VisualPosition(fromTop, fromLeft);

                // Move the editor's caret to the calculated position
                editorMouseEvent.getEditor().getCaretModel().moveToVisualPosition(startPosition);

                if (!editorMouseEvent.getEditor().getCaretModel().getVisualPosition().leansRight) {
                    // if caret over word -> select word
                    editorMouseEvent.getEditor().getSelectionModel().selectWordAtCaret(true);
                    String selectedAttribute = editorMouseEvent.getEditor().getSelectionModel().getSelectedText();

                    // Find the respective psiElement
                    PsiFile psiFile = PsiDocumentManager.getInstance(editorMouseEvent.getEditor().getProject()).getPsiFile(editorMouseEvent.getEditor().getDocument());
                    PsiElement element = psiFile.findElementAt(editorMouseEvent.getEditor().getSelectionModel().getSelectionStart());

                    // Get the language-handler of the respective language
                    IWideLanguageHandler handler = WideLanguageRegistry.getInstance().getLanguageHandler(element.getClass());

                    // Handler is not null -> Language is supported
                    if (handler != null) {

                        // We have selected the parent element
                        if (parentResult.getKey().equals(selectedAttribute)) {
                            // tag selected -> show tag information
                            if (!parentResult.equals(currentResult)) {
                                handler.getPopupFactory().showLookupResults(parentResult, parentResult, editor);
                            }
                        } else {
                            // attribute selected -> show attribute information
                            for (WideQueryResponse result : parentResult.getSubResults()) {
                                if (result.getKey().equals(selectedAttribute) || result.getValue().equals(selectedAttribute)) {
                                    if (!result.equals(currentResult)) {
                                        handler.getPopupFactory().showLookupResults(parentResult, result, editor);
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
