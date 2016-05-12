package ch.ethz.inf.globis.wide.ui.listener;

import ch.ethz.inf.globis.wide.lookup.io.WideQueryResponse;
import ch.ethz.inf.globis.wide.ui.popup.WidePopupHelper;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseMotionListener;

import java.util.List;

/**
 * Created by fabian on 17.04.16.
 */
public class WideMouseEventListenerHelper {

    private static final WideMouseEventListenerHelper INSTANCE = new WideMouseEventListenerHelper();
    private Editor editor;
    private List<WideQueryResponse> results;
    private WideQueryResponse currentResult;
    private EditorMouseMotionListener listener;

    public static WideMouseEventListenerHelper getInstance() {
        return INSTANCE;
    }

    private WideMouseEventListenerHelper() {

    }

    public void registerMouseEventListener(List<WideQueryResponse> results, WideQueryResponse currentResult, Editor editor) {
        this.editor = editor;
        this.results = results;
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


                    if (results.get(0).getKey().equals(selectedAttribute)) {
                        // tag selected -> show tag information
                        if (!results.get(0).equals(currentResult)) {
                            WidePopupHelper.getInstance().showHtmlTagLookupResults(results, editor);
                        }
                    } else {
                        // attribute selected -> show attribute information
                        for (WideQueryResponse result : results.get(0).getSubResults()) {
                            if (result.getKey().equals(selectedAttribute) || result.getValue().equals(selectedAttribute)) {
                                if (!result.equals(currentResult)) {
                                    WidePopupHelper.getInstance().showHtmlAttributeLookupResult(results, result, editor);
                                }
                            }
                        }
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
            this.results = null;
            this.currentResult = null;
        }
    }

}
