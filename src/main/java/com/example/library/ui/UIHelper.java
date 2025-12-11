package com.example.library.ui;

import com.example.library.domain.MediaItem;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Utility class for common UI operations across different frames.
 */
public final class UIHelper {
    
    private UIHelper() {
        // Utility class, prevent instantiation
    }
    
    /**
     * Adds a form field to a panel with a label and component.
     */
    public static void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }
    
    /**
     * Adds a detail field to a panel with a bold label and value.
     */
    public static void addDetailField(JPanel panel, GridBagConstraints gbc, int row, String label, String value, String fontName) {
        gbc.gridx = 0; 
        gbc.gridy = row; 
        gbc.weightx = 0.3; 
        gbc.gridwidth = 1;
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font(fontName, Font.BOLD, 12));
        panel.add(labelComp, gbc);
        
        gbc.gridx = 1; 
        gbc.weightx = 0.7;
        panel.add(new JLabel(value), gbc);
    }
    
    /**
     * Populates a table model with media items.
     */
    public static void populateItemsTable(DefaultTableModel tableModel, List<MediaItem> items) {
        tableModel.setRowCount(0);
        
        for (MediaItem item : items) {
            Object[] row = {
                    item.getItemId(),
                    item.getTitle(),
                    item.getAuthor(),
                    item.getType(),
                    item.getIsbn() != null ? item.getIsbn() : "",
                    item.getPublisher() != null ? item.getPublisher() : "",
                    item.getAvailableCopies() + "/" + item.getTotalCopies()
            };
            tableModel.addRow(row);
        }
    }
}
