package com.example.library.ui;

import com.example.library.domain.Loan;
import com.example.library.domain.MediaItem;
import com.example.library.domain.User;
import com.example.library.service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Admin interface for the Library Management System.
 * Provides functionality for adding media items, searching, and viewing overdue loans.
 */
public class AdminFrame extends JFrame {
    
    private final User currentUser;
    private final LibraryService libraryService;
    private final PaymentService paymentService;
    private final AuthService authService;
    
    private JTabbedPane tabbedPane;
    
    public AdminFrame(User currentUser, AuthService authService, LibraryService libraryService, PaymentService paymentService) {
        this.currentUser = currentUser;
        this.authService = authService;
        this.libraryService = libraryService;
        this.paymentService = paymentService;
        
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Library Management System - Admin Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Add tabs
        tabbedPane.addTab("Add Media Item", createAddMediaItemPanel());
        tabbedPane.addTab("Search Items", createSearchItemsPanel());
        tabbedPane.addTab("User Loans", createUserLoansPanel());
        tabbedPane.addTab("Overdue Loans", createOverdueLoansPanel());
        tabbedPane.addTab("Fines Overview", createFinesOverviewPanel());
        tabbedPane.addTab("Profile", createProfilePanel());
        
        // Add tabbed pane to frame
        add(tabbedPane, BorderLayout.CENTER);
        
        // Add header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername() + " (Admin)");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private JPanel createAddMediaItemPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Form fields
        JTextField titleField = new JTextField(30);
        JTextField authorField = new JTextField(30);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"BOOK", "CD"});
        JTextField isbnField = new JTextField(30);
        JTextField publisherField = new JTextField(30);
        JTextField publicationDateField = new JTextField(30);
        JTextField totalCopiesField = new JTextField(30);
        JTextField lateFeesField = new JTextField(30);
        
        // Add fields to form
        int row = 0;
        addFormField(formPanel, gbc, row++, "Title*:", titleField);
        addFormField(formPanel, gbc, row++, "Author*:", authorField);
        addFormField(formPanel, gbc, row++, "Type*:", typeCombo);
        addFormField(formPanel, gbc, row++, "ISBN:", isbnField);
        addFormField(formPanel, gbc, row++, "Publisher:", publisherField);
        addFormField(formPanel, gbc, row++, "Publication Date (YYYY-MM-DD):", publicationDateField);
        addFormField(formPanel, gbc, row++, "Total Copies*:", totalCopiesField);
        addFormField(formPanel, gbc, row++, "Late Fee Per Day*:", lateFeesField);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton saveButton = new JButton("Save Media Item");
        JButton clearButton = new JButton("Clear");
        
        saveButton.addActionListener(e -> {
            saveMediaItem(titleField, authorField, typeCombo, isbnField, publisherField,
                    publicationDateField, totalCopiesField, lateFeesField);
        });
        
        clearButton.addActionListener(e -> {
            titleField.setText("");
            authorField.setText("");
            typeCombo.setSelectedIndex(0);
            isbnField.setText("");
            publisherField.setText("");
            publicationDateField.setText("");
            totalCopiesField.setText("");
            lateFeesField.setText("");
        });
        
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }
    
    private void saveMediaItem(JTextField titleField, JTextField authorField, JComboBox<String> typeCombo,
                                JTextField isbnField, JTextField publisherField, JTextField publicationDateField,
                                JTextField totalCopiesField, JTextField lateFeesField) {
        try {
            // Validate required fields
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            String totalCopiesStr = totalCopiesField.getText().trim();
            String lateFeesStr = lateFeesField.getText().trim();
            
            if (title.isEmpty() || author.isEmpty() || totalCopiesStr.isEmpty() || lateFeesStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields (*)",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Parse values
            int totalCopies = Integer.parseInt(totalCopiesStr);
            BigDecimal lateFees = new BigDecimal(lateFeesStr);
            
            if (totalCopies <= 0) {
                JOptionPane.showMessageDialog(this, "Total copies must be greater than 0",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (lateFees.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Late fees cannot be negative",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create media item
            MediaItem item = new MediaItem();
            item.setTitle(title);
            item.setAuthor(author);
            item.setType(type);
            item.setIsbn(isbnField.getText().trim());
            item.setPublisher(publisherField.getText().trim());
            
            // Parse publication date if provided
            String pubDateStr = publicationDateField.getText().trim();
            if (!pubDateStr.isEmpty()) {
                try {
                    LocalDate pubDate = LocalDate.parse(pubDateStr);
                    item.setPublicationDate(pubDate);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD",
                            "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            item.setTotalCopies(totalCopies);
            item.setAvailableCopies(totalCopies);
            item.setLateFeesPerDay(lateFees);
            
            // Save to database
            MediaItem saved = libraryService.addMediaItem(item);
            
            JOptionPane.showMessageDialog(this,
                    "Media item added successfully!\nItem ID: " + saved.getItemId(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Clear form
            titleField.setText("");
            authorField.setText("");
            typeCombo.setSelectedIndex(0);
            isbnField.setText("");
            publisherField.setText("");
            publicationDateField.setText("");
            totalCopiesField.setText("");
            lateFeesField.setText("");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number format: " + ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding media item: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createSearchItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        JTextField searchField = new JTextField(30);
        searchPanel.add(searchField);
        JButton searchButton = new JButton("Search");
        searchPanel.add(searchButton);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Title", "Author", "Type", "ISBN", "Publisher", "Available/Total"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Search action
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            searchItems(tableModel, keyword);
        });
        
        // Allow Enter key to trigger search
        searchField.addActionListener(e -> searchButton.doClick());
        
        // Load all items initially
        searchItems(tableModel, "");
        
        return panel;
    }
    
    private void searchItems(DefaultTableModel tableModel, String keyword) {
        try {
            List<MediaItem> items = libraryService.searchItems(keyword);
            
            // Clear table
            tableModel.setRowCount(0);
            
            // Add items to table
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
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error searching items: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createOverdueLoansPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Refresh");
        headerPanel.add(refreshButton);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Loan ID", "User ID", "Item ID", "Loan Date", "Due Date", "Days Overdue", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Refresh action
        refreshButton.addActionListener(e -> loadOverdueLoans(tableModel));
        
        // Load overdue loans initially
        loadOverdueLoans(tableModel);
        
        return panel;
    }
    
    private void loadOverdueLoans(DefaultTableModel tableModel) {
        try {
            LocalDate today = LocalDate.now();
            List<Loan> overdueLoans = libraryService.getOverdueLoans(today);
            
            // Clear table
            tableModel.setRowCount(0);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            // Add loans to table
            for (Loan loan : overdueLoans) {
                long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(loan.getDueDate(), today);
                Object[] row = {
                        loan.getLoanId(),
                        loan.getUserId(),
                        loan.getItemId(),
                        loan.getLoanDate().format(formatter),
                        loan.getDueDate().format(formatter),
                        daysOverdue,
                        loan.getStatus()
                };
                tableModel.addRow(row);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading overdue loans: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createUserLoansPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("User ID:"));
        JTextField userIdField = new JTextField(10);
        inputPanel.add(userIdField);
        JButton loadButton = new JButton("Load Loans");
        inputPanel.add(loadButton);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Loan ID", "Item ID", "Loan Date", "Due Date", "Return Date", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load action
        loadButton.addActionListener(e -> {
            String userIdStr = userIdField.getText().trim();
            if (userIdStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a User ID",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                int userId = Integer.parseInt(userIdStr);
                loadUserLoansForAdmin(tableModel, userId);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "User ID must be a number",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        return panel;
    }
    
    private void loadUserLoansForAdmin(DefaultTableModel tableModel, int userId) {
        try {
            List<Loan> loans = libraryService.getUserLoans(userId);
            
            // Clear table
            tableModel.setRowCount(0);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            // Add loans to table
            for (Loan loan : loans) {
                Object[] row = {
                        loan.getLoanId(),
                        loan.getItemId(),
                        loan.getLoanDate().format(formatter),
                        loan.getDueDate().format(formatter),
                        loan.getReturnDate() != null ? loan.getReturnDate().format(formatter) : "",
                        loan.getStatus()
                };
                tableModel.addRow(row);
            }
            
            if (loans.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No loans found for user ID: " + userId,
                        "Info", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading user loans: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createFinesOverviewPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("User ID:"));
        JTextField userIdField = new JTextField(10);
        inputPanel.add(userIdField);
        JButton loadButton = new JButton("Load Fines");
        inputPanel.add(loadButton);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Fine ID", "Loan ID", "Amount (NIS)", "Issued Date", "Status", "Paid Date"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Total panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel totalLabel = new JLabel("Total Unpaid: 0.00 NIS");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalPanel.add(totalLabel);
        panel.add(totalPanel, BorderLayout.SOUTH);
        
        // Load action
        loadButton.addActionListener(e -> {
            String userIdStr = userIdField.getText().trim();
            if (userIdStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a User ID",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                int userId = Integer.parseInt(userIdStr);
                loadFinesForAdmin(tableModel, totalLabel, userId);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "User ID must be a number",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        return panel;
    }
    
    private void loadFinesForAdmin(DefaultTableModel tableModel, JLabel totalLabel, int userId) {
        try {
            List<com.example.library.domain.Fine> fines = paymentService.getUnpaidFines(userId);
            BigDecimal total = paymentService.getTotalUnpaid(userId);
            
            // Clear table
            tableModel.setRowCount(0);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            // Add fines to table
            for (com.example.library.domain.Fine fine : fines) {
                Object[] row = {
                        fine.getFineId(),
                        fine.getLoanId(),
                        String.format("%.2f", fine.getAmount()),
                        fine.getIssuedDate().format(formatter),
                        fine.getStatus(),
                        fine.getPaidDate() != null ? fine.getPaidDate().format(formatter) : ""
                };
                tableModel.addRow(row);
            }
            
            totalLabel.setText(String.format("Total Unpaid: %.2f NIS", total));
            
            if (fines.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No unpaid fines found for user ID: " + userId,
                        "Info", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading fines: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Profile info panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Administrator Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        infoPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy++;
        
        // User ID
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        infoPanel.add(new JLabel("User ID:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        infoPanel.add(new JLabel(String.valueOf(currentUser.getUserId())), gbc);
        
        gbc.gridy++;
        
        // Username
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        infoPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        infoPanel.add(new JLabel(currentUser.getUsername()), gbc);
        
        gbc.gridy++;
        
        // Email
        gbc.gridx = 0;
        infoPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(currentUser.getEmail()), gbc);
        
        gbc.gridy++;
        
        // Role
        gbc.gridx = 0;
        infoPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        JLabel roleLabel = new JLabel(currentUser.getRole());
        roleLabel.setForeground(Color.BLUE);
        roleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        infoPanel.add(roleLabel, gbc);
        
        gbc.gridy++;
        
        // Created At
        gbc.gridx = 0;
        infoPanel.add(new JLabel("Member Since:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(currentUser.getCreatedAt().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))), gbc);
        
        panel.add(infoPanel, BorderLayout.NORTH);
        
        // Logout button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(150, 35));
        logoutButton.addActionListener(e -> logout());
        buttonPanel.add(logoutButton);
        
        panel.add(buttonPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
                "Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            // Reopen login frame
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame(authService, libraryService, paymentService);
                loginFrame.setVisible(true);
            });
        }
    }
}
