package app;

import exception.*;
import model.*;
import model.Member.MembershipType;
import service.LibraryService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class BookBridgeFrame extends JFrame {
    private LibraryService service = new LibraryService();

    // Table models
    private DefaultTableModel bookModel = new DefaultTableModel(new String[]{"ISBN", "Title", "Status"}, 0);
    private DefaultTableModel memberModel = new DefaultTableModel(new String[]{"Member ID", "Name", "Type"}, 0);
    private DefaultTableModel loanModel = new DefaultTableModel(new String[]{"Loan ID", "Member", "ISBN", "Due Date", "Status", "Fine (PHP)"}, 0);
    private DefaultTableModel resModel = new DefaultTableModel(new String[]{"Res ID", "Member", "ISBN", "Status"}, 0);

    // Status bar components
    private JLabel lblStatus = new JLabel("Ready.");
    private JProgressBar progressBar = new JProgressBar();
    private JButton btnRefresh = new JButton("Refresh");
    private JTextArea txtReport = new JTextArea();

    public BookBridgeFrame() {
        setTitle("BookBridge Library System");
        setSize(850, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Catalog", createCatalogTab());
        tabs.addTab("Members", createMembersTab());
        tabs.addTab("Circulation", createCirculationTab());
        tabs.addTab("Reservations", createReservationsTab());
        tabs.addTab("Reports", createReportsTab());

        // Bottom status bar
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        JPanel rightStatus = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightStatus.add(progressBar);
        rightStatus.add(btnRefresh);
        bottomPanel.add(lblStatus, BorderLayout.CENTER);
        bottomPanel.add(rightStatus, BorderLayout.EAST);

        btnRefresh.addActionListener(e -> loadDataInBackground(""));

        add(tabs, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loadDataInBackground("");
    }

    private JPanel createCatalogTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JTextField txtIsbn = new JTextField(10);
        JTextField txtTitle = new JTextField(14);
        JButton btnAdd = new JButton("Add Book");
        JTextField txtSearch = new JTextField(10);
        JButton btnSearch = new JButton("Search");

        top.add(new JLabel("ISBN:")); top.add(txtIsbn);
        top.add(new JLabel("Title:")); top.add(txtTitle);
        top.add(btnAdd);
        top.add(new JLabel("Search:")); top.add(txtSearch);
        top.add(btnSearch);

        btnAdd.addActionListener(e -> {
            try {
                service.addBook(txtIsbn.getText(), txtTitle.getText());
                txtIsbn.setText("");
                txtTitle.setText("");
                lblStatus.setText("Book added.");
                loadDataInBackground("");
            } catch (Exception ex) {
                showError(ex);
            }
        });

        btnSearch.addActionListener(e -> loadDataInBackground(txtSearch.getText()));

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(new JTable(bookModel)), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMembersTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JTextField txtId = new JTextField(10);
        JTextField txtName = new JTextField(14);
        JComboBox<MembershipType> cmbType = new JComboBox<>(MembershipType.values());
        JButton btnRegister = new JButton("Register Member");

        top.add(new JLabel("ID:")); top.add(txtId);
        top.add(new JLabel("Name:")); top.add(txtName);
        top.add(new JLabel("Type:")); top.add(cmbType);
        top.add(btnRegister);

        btnRegister.addActionListener(e -> {
            try {
                MembershipType type = (MembershipType) cmbType.getSelectedItem();
                service.registerMember(txtId.getText(), txtName.getText(), type);
                txtId.setText("");
                txtName.setText("");
                lblStatus.setText("Member registered.");
                loadDataInBackground("");
            } catch (Exception ex) {
                showError(ex);
            }
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(new JTable(memberModel)), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCirculationTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JTextField txtMember = new JTextField(8);
        JTextField txtIsbn = new JTextField(10);
        JTextField txtDays = new JTextField("7", 4);
        JTextField txtLate = new JTextField("0", 4);
        JButton btnIssue = new JButton("Issue Loan");
        JButton btnReturn = new JButton("Return Book");

        top.add(new JLabel("Member ID:")); top.add(txtMember);
        top.add(new JLabel("ISBN:")); top.add(txtIsbn);
        top.add(new JLabel("Days:")); top.add(txtDays);
        top.add(new JLabel("Late:")); top.add(txtLate);
        top.add(btnIssue);
        top.add(btnReturn);

        JTable tblLoans = new JTable(loanModel);

        btnIssue.addActionListener(e -> {
            try {
                int days = Integer.parseInt(txtDays.getText().trim());
                int late = Integer.parseInt(txtLate.getText().trim());
                LoanRecord loan = service.issueLoan(txtMember.getText(), txtIsbn.getText(), days, late);
                lblStatus.setText("Loan issued: " + loan.getLoanId());
                loadDataInBackground("");
            } catch (Exception ex) {
                showError(ex);
            }
        });

        btnReturn.addActionListener(e -> {
            int row = tblLoans.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a loan from the table first.");
                return;
            }
            String loanId = loanModel.getValueAt(row, 0).toString();
            int choice = JOptionPane.showConfirmDialog(this, "Return loan " + loanId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                try {
                    service.returnBook(loanId);
                    lblStatus.setText("Book returned.");
                    loadDataInBackground("");
                } catch (Exception ex) {
                    showError(ex);
                }
            }
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblLoans), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createReservationsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JTextField txtMember = new JTextField(8);
        JTextField txtIsbn = new JTextField(10);
        JButton btnReserve = new JButton("Reserve");
        JButton btnCancel = new JButton("Cancel");
        JButton btnProcess = new JButton("Process Next");

        top.add(new JLabel("Member ID:")); top.add(txtMember);
        top.add(new JLabel("ISBN:")); top.add(txtIsbn);
        top.add(btnReserve);
        top.add(btnCancel);
        top.add(btnProcess);

        JTable tblRes = new JTable(resModel);

        btnReserve.addActionListener(e -> {
            try {
                Reservation r = service.reserveBook(txtMember.getText(), txtIsbn.getText());
                lblStatus.setText("Reserved: " + r.getReservationId());
                loadDataInBackground("");
            } catch (Exception ex) {
                showError(ex);
            }
        });

        btnCancel.addActionListener(e -> {
            int row = tblRes.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a reservation first.");
                return;
            }
            String resId = resModel.getValueAt(row, 0).toString();
            int choice = JOptionPane.showConfirmDialog(this, "Cancel reservation " + resId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                try {
                    service.cancelReservation(resId);
                    lblStatus.setText("Reservation cancelled.");
                    loadDataInBackground("");
                } catch (Exception ex) {
                    showError(ex);
                }
            }
        });

        btnProcess.addActionListener(e -> {
            try {
                Optional<Reservation> next = service.processNextReservation();
                if (next.isPresent()) {
                    lblStatus.setText("Fulfilled: " + next.get().getReservationId());
                } else {
                    lblStatus.setText("No pending reservations.");
                }
                loadDataInBackground("");
            } catch (Exception ex) {
                showError(ex);
            }
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblRes), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createReportsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JButton btnGenerate = new JButton("Generate Report");
        txtReport.setEditable(false);

        btnGenerate.addActionListener(e -> {
            btnGenerate.setEnabled(false);
            progressBar.setIndeterminate(true);
            lblStatus.setText("Generating report...");

            new SwingWorker<LibraryService.CirculationSummary, Void>() {
                @Override
                protected LibraryService.CirculationSummary doInBackground() throws Exception {
                    Thread.sleep(500); // keeps UI responsive during background work
                    return service.generateSummary();
                }

                @Override
                protected void done() {
                    btnGenerate.setEnabled(true);
                    progressBar.setIndeterminate(false);
                    lblStatus.setText("Report ready.");
                    try {
                        LibraryService.CirculationSummary s = get();
                        txtReport.setText(
                            "--- BookBridge Circulation Report ---\n" +
                            "Available Books: " + s.availableBooks() + "\n" +
                            "Checked-Out / Reserved: " + s.checkedOutBooks() + "\n" +
                            "Active Loans by Type: " + s.activeLoansByType() + "\n" +
                            "Total Estimated Fines: PHP " + String.format("%.2f", s.totalEstimatedFines()) + "\n" +
                            "Reservations Processed: " + s.processedReservationsSessionCount()
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

    // Background refresh so the UI never freezes
    private void loadDataInBackground(String searchKeyword) {
        progressBar.setIndeterminate(true);
        btnRefresh.setEnabled(false);
        lblStatus.setText("Loading data...");

        new SwingWorker<Void, Void>() {
            List<LibraryBook> books;
            List<Member> members;
            List<LoanRecord> loans;
            List<Reservation> reservations;
            Exception error;

            @Override
            protected Void doInBackground() {
                try {
                    books = service.searchBooks(searchKeyword);
                    members = service.listMembers();
                    loans = service.listLoans();
                    reservations = service.listReservations();
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

                bookModel.setRowCount(0);
                for (LibraryBook b : books) {
                    bookModel.addRow(new Object[]{b.getIsbn(), b.getTitle(), b.getStatus()});
                }

                memberModel.setRowCount(0);
                for (Member m : members) {
                    memberModel.addRow(new Object[]{m.getMemberId(), m.getName(), m.getType()});
                }

                loanModel.setRowCount(0);
                for (LoanRecord l : loans) {
                    loanModel.addRow(new Object[]{
                        l.getLoanId(), l.getBorrower().getMemberId(), l.getBook().getIsbn(),
                        l.getDueDate(), l.getStatus(), String.format("%.2f", l.getEstimatedFine())
                    });
                }

                resModel.setRowCount(0);
                for (Reservation r : reservations) {
                    resModel.addRow(new Object[]{
                        r.getReservationId(), r.getMember().getMemberId(), r.getBook().getIsbn(), r.getStatus()
                    });
                }

                lblStatus.setText("Data loaded.");
            }
        }.execute();
    }

    private void showError(Exception ex) {
        String title = "Error";
        String msg = ex.getMessage();

        if (ex instanceof NumberFormatException || ex instanceof IllegalArgumentException || ex instanceof IllegalStateException) {
            title = "Invalid Input";
        } else if (ex instanceof DuplicateEntityException || ex instanceof ItemUnavailableException || ex instanceof LoanLimitExceededException) {
            title = "Library Rule Error";
        } else if (ex instanceof NoSuchElementException) {
            title = "Not Found";
        } else if (ex instanceof SQLException) {
            title = "Database Error";
            msg = "Database connection failed. Please check if XAMPP MySQL is running.";
        }

        lblStatus.setText(title + ": " + msg);
        JOptionPane.showMessageDialog(this, msg, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
       
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new BookBridgeFrame().setVisible(true));
    }
}