package ch.ethz.inf.globis.wide.ui.components.editor;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.impl.EditorComponentImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by fabian on 16.04.16.
 */
public class WideExampleEditorFactory {
    public static Editor createExampleEditor(String exampleCode, @Nullable EditorHighlighter highlighter) {
        com.intellij.openapi.editor.Document document = EditorFactory.getInstance().createDocument(exampleCode);
        final EditorImpl editor = (EditorImpl) EditorFactory.getInstance().createEditor(document);
        editor.getSettings().setLineNumbersShown(true);
        editor.setVerticalScrollbarVisible(false);
        editor.setCaretEnabled(false);
        editor.setCaretVisible(false);
        editor.getSettings().setUseSoftWraps(true);
        editor.getSettings().setAdditionalLinesCount(1);

        if (highlighter != null) {
            editor.setHighlighter(highlighter);
        }

        // translate scrolling to JScrollView
        MouseWheelListener listener = new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                EditorComponentImpl editorComponent = (EditorComponentImpl) e.getSource();
                JScrollPane scrollPane = (JScrollPane) editorComponent.getEditor().getComponent().getParent().getParent().getParent();
                scrollPane.getBounds();
                Point oldPosition = scrollPane.getViewport().getViewPosition();
                oldPosition.translate(0, e.getUnitsToScroll() * e.getScrollAmount());

                // Prevent scrolling out of the content
                if (oldPosition.getY() + scrollPane.getHeight() <= editorComponent.getEditor().getComponent().getParent().getHeight()
                        && oldPosition.getY() >= 0) {
                    scrollPane.getViewport().setViewPosition(oldPosition);
                }

            }
        };

        editor.getContentComponent().addMouseWheelListener(listener);

        editor.getComponent().setBorder(new EmptyBorder(0, 10, 20, 10));
        editor.getComponent().setBackground(Color.white);
        return editor;
    }
}
