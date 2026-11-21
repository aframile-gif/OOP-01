package app;

import exception.*;
import model.ServiceRequest;
import model.ServiceRequest.RequestCategory;
import model.ServiceRequest.TicketStatus;
import model.Student;
import service.CampusService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CampusConnectFrame extends JFrame {
    private CampusService service = new CampusService();

    // Table models
    private DefaultTableModel studentModel = new DefaultTableModel(new String[]{"Student ID", "Name"}, 0);
    private DefaultTableModel ticketModel = new DefaultTableModel(
            new String[]{"Ticket ID", "Student ID", "Name", "Category", "Description", "Priority", "Est. Days", "Status"}, 0);

    // Status bar & report components
    private JLabel lblStatus = new JLabel("Ready.");
    private JProgressBar progressBar = new JProgressBar();
    private JButton btnRefresh = new JButton("Refresh");
    private JTextArea txtReport = new JTextArea();

    public CampusConnectFrame() {
        setTitle("CampusConnect Student Service System");
        setSize(900, 540);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Service Tickets", createTicketsTab());
        tabs.addTab("Students", createStudentsTab());
        tabs.addTab("Reports", createReportsTab());

        // Bottom status bar
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        JPanel rightStatus = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightStatus.add(progressBar);
        rightStatus.add(btnRefresh);
        bottomPanel.add(lblStatus, BorderLayout.CENTER);
        bottomPanel.add(rightStatus, BorderLayout.EAST);

        btnRefresh.addActionListener(e -> loadDataInBackground("", "ALL"));

        add(tabs, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loadDataInBackground("", "ALL");
    }

    private JPanel createTicketsTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        //  Submit Ticket + Search/Filter
        JPanel northPanel = new JPanel(new GridLayout(2, 1));

        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtStudId = new JTextField(7);
        JComboBox<RequestCategory> cmbCat = new JComboBox<>(RequestCategory.values());
        JTextField txtDesc = new JTextField(14);
        JTextField txtPri = new JTextField("3", 3);
        JButton btnAdd = new JButton("Submit Ticket");

        addRow.add(new JLabel("Student ID:")); addRow.add(txtStudId);
        addRow.add(new JLabel("Category:")); addRow.add(cmbCat);
        addRow.add(new JLabel("Desc:")); addRow.add(txtDesc);
        addRow.add(new JLabel("Pri (1-5):")); addRow.add(txtPri);
        addRow.add(btnAdd);

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtSearch = new JTextField(10);
        JComboBox<String> cmbFilter = new JComboBox<>(new String[]{"ALL", "ENROLLMENT", "DOCUMENT", "PAYMENT", "ADVISING"});
        JButton btnSearch = new JButton("Search / Filter");
        JComboBox<TicketStatus> cmbNewStatus = new JComboBox<>(TicketStatus.values());
        JButton btnUpdate = new JButton("Update Status");
        JButton btnDelete = new JButton("Delete");
        JButton btnProcessNext = new JButton("Process Next");

        actionRow.add(new JLabel("Search:")); actionRow.add(txtSearch);
        actionRow.add(cmbFilter); actionRow.add(btnSearch);
        actionRow.add(new JLabel(" | Status:")); actionRow.add(cmbNewStatus);
        actionRow.add(btnUpdate); actionRow.add(btnDelete); actionRow.add(btnProcessNext);

        northPanel.add(addRow);
        northPanel.add(actionRow);

        JTable tblTickets = new JTable(ticketModel);

        // Listeners (thin handlers — zero SQL!)
        btnAdd.addActionListener(e -> {
            try {
                int pri = Integer.parseInt(txtPri.getText().trim());
                RequestCategory cat = (RequestCategory) cmbCat.getSelectedItem();
                ServiceRequest req = service.submitRequest(txtStudId.getText(), cat, txtDesc.getText(), pri);
                txtDesc.setText("");
                lblStatus.setText("Ticket created: " + req.getTicketId() + " (" + req.estimateDays() + " day/s)");
                loadDataInBackground("", "ALL");
            } catch (Exception ex) {
                showError(ex);
            }
        });

        btnSearch.addActionListener(e -> {
            String filter = cmbFilter.getSelectedItem().toString();
            loadDataInBackground(txtSearch.getText(), filter);
        });

        btnUpdate.addActionListener(e -> {
            int row = tblTickets.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a ticket row first.");
                return;
            }
            String ticketId = ticketModel.getValueAt(row, 0).toString();
            TicketStatus newStatus = (TicketStatus) cmbNewStatus.getSelectedItem();
            try {
                service.updateTicketStatus(ticketId, newStatus);
                lblStatus.setText("Updated " + ticketId + " to " + newStatus);
                loadDataInBackground(txtSearch.getText(), cmbFilter.getSelectedItem().toString());
            } catch (Exception ex) {
                showError(ex);
            }
        });

        btnDelete.addActionListener(e -> {
            int row = tblTickets.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a ticket row to delete.");
                return;
            }
            String ticketId = ticketModel.getValueAt(row, 0).toString();
            int choice = JOptionPane.showConfirmDialog(this, "Delete ticket " + ticketId + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                try {
                    service.deleteTicket(ticketId);
                    lblStatus.setText("Deleted ticket " + ticketId);
                    loadDataInBackground(txtSearch.getText(), cmbFilter.getSelectedItem().toString());
                } catch (Exception ex) {
                    showError(ex);
                }
            }
        });

        btnProcessNext.addActionListener(e -> {
            try {
                Optional<ServiceRequest> next = service.processNextRequest();
                if (next.isPresent()) {
                    lblStatus.setText("Processed next OPEN ticket: " + next.get().getTicketId());
                } else {
                    lblStatus.setText("No OPEN tickets left in queue.");
                }
                loadDataInBackground(txtSearch.getText(), cmbFilter.getSelectedItem().toString());
            } catch (Exception ex) {
                showError(ex);
            }
        });

        panel.add(northPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblTickets), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStudentsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JTextField txtId = new JTextField(10);
        JTextField txtName = new JTextField(16);
        JButton btnRegister = new JButton("Register Student");

        top.add(new JLabel("Student ID:")); top.add(txtId);
        top.add(new JLabel("Name:")); top.add(txtName);
        top.add(btnRegister);

        btnRegister.addActionListener(e -> {
            try {
                service.registerStudent(txtId.getText(), txtName.getText());
                txtId.setText("");
                txtName.setText("");
                lblStatus.setText("Student registered.");
                loadDataInBackground("", "ALL");
            } catch (Exception ex) {
                showError(ex);
            }
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(new JTable(studentModel)), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createReportsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton btnGenerate = new JButton("Generate Service Report");
        txtReport.setEditable(false);

        btnGenerate.addActionListener(e -> {
            btnGenerate.setEnabled(false);
            progressBar.setIndeterminate(true);
            lblStatus.setText("Generating summary report...");

            new SwingWorker<CampusService.ServiceSummary, Void>() {
                @Override
                protected CampusService.ServiceSummary doInBackground() throws Exception {
                    Thread.sleep(500); 
                    return service.generateSummary();
                }

                @Override
                protected void done() {
                    btnGenerate.setEnabled(true);
                    progressBar.setIndeterminate(false);
                    lblStatus.setText("Report ready.");
                    try {
                        CampusService.ServiceSummary s = get();
                        txtReport.setText(
                            "--- CampusConnect Service Summary ---\n" +
                            "Open Tickets: " + s.openTickets() + "\n" +
                            "In-Progress Tickets: " + s.inProgressTickets() + "\n" +
                            "Resolved Tickets: " + s.resolvedTickets() + "\n" +
                            "Count by Category: " + s.countByCategory() + "\n" +
                            "Average Est. Completion: " + String.format("%.2f day(s)", s.averageEstimatedDays()) + "\n" +
                            "Queue Tickets Processed (Session): " + s.processedInSession()
                        );
                    } catch (Exception ex) {
                        showError(ex);
                    }
                }
            }.execute();
        });

        panel.add(btnGenerate, BorderLayout.NORTH);
        panel.add(new JScrollPane(txtReport), BorderLayout.CENTER);
        return panel;
    }

    // Background refresh so the EDT stays responsive
    private void loadDataInBackground(String keyword, String categoryFilter) {
        progressBar.setIndeterminate(true);
        btnRefresh.setEnabled(false);
        lblStatus.setText("Loading records...");

        new SwingWorker<Void, Void>() {
            List<Student> students;
            List<ServiceRequest> tickets;
            Exception error;

            @Override
            protected Void doInBackground() {
                try {
                    students = service.listStudents();
                    tickets = service.searchTickets(keyword, categoryFilter);
                } catch (Exception ex) {
                    error = ex;
                }
                return null;
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                btnRefresh.setEnabled(true);

                if (error != null) {
                    showError(error);
                    return;
                }

                studentModel.setRowCount(0);
                for (Student s : students) {
                    studentModel.addRow(new Object[]{s.getId(), s.getName()});
                }

                ticketModel.setRowCount(0);
                for (ServiceRequest r : tickets) {
                    ticketModel.addRow(new Object[]{
                        r.getTicketId(), r.getStudent().getId(), r.getStudent().getName(),
                        r.getCategory(), r.getDescription(), r.getPriority(),
                        r.estimateDays(), r.getStatus()
                    });
                }
                lblStatus.setText("Data loaded (" + tickets.size() + " ticket/s).");
            }
        }.execute();
    }

    private void showError(Exception ex) {
        String title = "Error";
        String msg = ex.getMessage();

        if (ex instanceof NumberFormatException || ex instanceof IllegalArgumentException) {
            title = "Invalid Input";
        } else if (ex instanceof DuplicateTicketException || ex instanceof InvalidPriorityException) {
            title = "Business Rule Error";
        } else if (ex instanceof TicketNotFoundException) {
            title = "Not Found";
        } else if (ex instanceof SQLException) {
            title = "Database Error";
            msg = "Database operation failed. Please check if XAMPP MySQL is running.";
        }

        lblStatus.setText(title + ": " + msg);
        JOptionPane.showMessageDialog(this, msg, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new CampusConnectFrame().setVisible(true));
    }
}