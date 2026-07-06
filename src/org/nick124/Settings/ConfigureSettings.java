package org.nick124.Settings;

import javax.swing.JMenu;

import org.nick124.GUI;

import com.fasterxml.jackson.databind.node.ObjectNode;
import static org.nick124.GUI.*;
import static org.nick124.GenericConsts.*;

import java.awt.Component;

public class ConfigureSettings {

    public static void applyTheme() {
        if (isDarkTheme) {
            textArea.setBackground(DARK_BG);
            textArea.setForeground(DARK_FG);
            textArea.setCaretColor(DARK_FG);
            lineNumberGutter.setBackground(DARK_GUTTER_BG);
            lineNumberGutter.setForeground(DARK_GUTTER_FG);
            explorerScroll.setBackground(DARK_GUTTER_FG);
            explorerTree.setBackground(DARK_GUTTER_BG);
            if (treeCellRenderer != null) {
                    treeCellRenderer.setBackgroundNonSelectionColor(DARK_GUTTER_BG);
                    treeCellRenderer.setBackgroundSelectionColor(DARK_GUTTER_BG.darker());
                    treeCellRenderer.setTextNonSelectionColor(DARK_FG);
                    treeCellRenderer.setTextSelectionColor(DARK_GUTTER_BG);
                    treeCellRenderer.setBorderSelectionColor(DARK_GUTTER_BG);
            }
            for (Component menuComp : optionsMenu.getComponents()) {
            menuComp.setBackground(DARK_GUTTER_BG);
            menuComp.setForeground(DARK_GUTTER_FG);
            if (menuComp instanceof JMenu menu) {
                for (Component item : menu.getMenuComponents()) {
                    item.setBackground(DARK_GUTTER_BG);
                    item.setForeground(DARK_GUTTER_FG);
                }
            }
        }
        } else {
            textArea.setBackground(LIGHT_BG);
            textArea.setForeground(LIGHT_FG);
            textArea.setCaretColor(LIGHT_FG);
            lineNumberGutter.setBackground(LIGHT_GUTTER_BG);
            lineNumberGutter.setForeground(LIGHT_GUTTER_FG);
            explorerScroll.setBackground(LIGHT_GUTTER_FG);
            explorerTree.setBackground(LIGHT_GUTTER_BG);
        }
        frame.repaint();
    }

public static void applySettings(ObjectNode settings) {
        isDarkTheme = settings.get("Theme").asText().equals("DARK");
        currentFontSize = settings.get("Font Size").asInt();
        ConfigureSettings.applyTheme();
        GUI.updateFont();
        if (scrollPane.getRowHeader() != null && scrollPane.getRowHeader().getView() != null) {
            scrollPane.setRowHeaderView(null);
        } else {
            scrollPane.setRowHeaderView(lineNumberGutter);
        }
        scrollPane.revalidate();
        scrollPane.repaint();    
    }

}
