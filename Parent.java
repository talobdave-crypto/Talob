package login;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;

public class Parent extends JFrame {
    private JPanel contentPane;
    private JTextArea textArea;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private double total = 0;
    private JCheckBox studentDiscount;
    private JRadioButton dineIn, takeOut;
    private JRadioButton cash, card;
    private String username;
    private String role;
    private JLabel lblTotal;
    private int selectedProductId = -1;

    public Parent(String username, String role) {
        this.username = username;
        this.role = role;
        setTitle("Freddy Fazbear Security System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 700, 550);

        contentPane = new JPanel();
        contentPane.setLayout(null);
        contentPane.setBackground(new Color(20, 20, 20));
        setContentPane(contentPane);

        JLabel lblTitle = new JLabel("FREDDY FAZBEAR'S PIZZA", SwingConstants.CENTER);
        lblTitle.setForeground(new Color(255, 204, 0));
        lblTitle.setFont(new Font("Impact", Font.BOLD, 22));
        lblTitle.setBounds(0, 5, 520, 30);
        contentPane.add(lblTitle);

        JLabel lblWelcome = new JLabel("Employee: " + username);
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setBounds(150, 40, 200, 20);
        contentPane.add(lblWelcome);

        JLabel lblTime = new JLabel();
        lblTime.setForeground(Color.LIGHT_GRAY);
        lblTime.setBounds(300, 40, 200, 20);
        contentPane.add(lblTime);

        new Timer(1000, e -> {
            lblTime.setText(java.time.LocalTime.now().withNano(0).toString());
        }).start();

        lblTime.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblTime.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null,
                        java.time.LocalDate.now().toString(),
                        "Current Date",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // ── Sidebar ──────────────────────────────────────────────────────────
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(40, 0, 0));
        sidebar.setBounds(0, 70, 140, 270);
        sidebar.setLayout(null);
        contentPane.add(sidebar);

        JButton btnOrders    = createMenuButton("Orders");
        JButton btnProducts  = createMenuButton("Products");
        JButton btnCustomers = createMenuButton("Customers");
        JButton btnReports   = createMenuButton("Reports");
        JButton btnLogout    = createMenuButton("Logout");

        btnOrders.setBounds(10, 20, 120, 30);
        btnProducts.setBounds(10, 60, 120, 30);
        btnCustomers.setBounds(10, 100, 120, 30);
        btnReports.setBounds(10, 140, 120, 30);
        btnLogout.setBounds(10, 210, 120, 30);

        sidebar.add(btnOrders);
        sidebar.add(btnProducts);
        sidebar.add(btnCustomers);
        sidebar.add(btnReports);
        sidebar.add(btnLogout);

        if (!role.equals("admin")) {
            btnProducts.setEnabled(false);
            btnCustomers.setEnabled(false);
            btnReports.setEnabled(false);
        }

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LOGIN().setVisible(true);
                dispose();
            }
        });

        // ── Main card panel ───────────────────────────────────────────────────
        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);
        mainPanel.setBounds(150, 70, 500, 420);
        contentPane.add(mainPanel);

        // ══════════════════════════════════════════════════════════════════════
        // PRODUCTS PANEL  (FIXED)
        // ══════════════════════════════════════════════════════════════════════
        JPanel productsPanel = new JPanel();
        productsPanel.setLayout(null);
        productsPanel.setBackground(new Color(30, 30, 30));

        // Row 1 – Name + Price
        JLabel lblName = new JLabel("Name:");
        lblName.setForeground(Color.WHITE);
        lblName.setBounds(10, 15, 55, 20);
        productsPanel.add(lblName);

        JTextField nameField = new JTextField();
        nameField.setBounds(65, 10, 175, 28);
        productsPanel.add(nameField);

        JLabel lblPrice = new JLabel("Price:");
        lblPrice.setForeground(Color.WHITE);
        lblPrice.setBounds(255, 15, 50, 20);
        productsPanel.add(lblPrice);

        JTextField priceField = new JTextField();
        priceField.setBounds(305, 10, 120, 28);
        productsPanel.add(priceField);

        // Row 2 – Stock
        JLabel lblStock = new JLabel("Stock:");
        lblStock.setForeground(Color.WHITE);
        lblStock.setBounds(10, 50, 55, 20);
        productsPanel.add(lblStock);

        JTextField stockField = new JTextField();
        stockField.setBounds(65, 45, 175, 28);
        productsPanel.add(stockField);

        // Row 3 – Search  (clearly below stock row, no overlap)
        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setForeground(Color.WHITE);
        lblSearch.setBounds(10, 88, 55, 20);
        productsPanel.add(lblSearch);

        JTextField searchField = new JTextField();
        searchField.setBounds(65, 84, 365, 26);
        productsPanel.add(searchField);

        // Table
        String[]           productColumns = {"ID", "Name", "Price", "Stock"};
        DefaultTableModel  productModel   = new DefaultTableModel(productColumns, 0);
        JTable             productTable   = new JTable(productModel);

        loadProducts(productModel);

        JScrollPane scroll = new JScrollPane(productTable);
        scroll.setBounds(10, 118, 460, 185);
        productsPanel.add(scroll);

        // Live search / filter
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String search = searchField.getText();
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(productModel);
                productTable.setRowSorter(sorter);
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + search));
            }
        });

        // Buttons row
        JButton btnProductAdd    = createActionButton("ADD");
        JButton btnProductUpdate = createActionButton("EDIT PRODUCT");
        JButton btnProductDelete = createActionButton("DELETE");
        JButton btnProductClear  = createActionButton("CLEAR");

        btnProductAdd.setBounds(10,  315, 90,  30);
        btnProductUpdate.setBounds(110, 315, 120, 30);
        btnProductDelete.setBounds(240, 315, 100, 30);
        btnProductClear.setBounds(350, 315, 100, 30);

        productsPanel.add(btnProductAdd);
        productsPanel.add(btnProductUpdate);
        productsPanel.add(btnProductDelete);
        productsPanel.add(btnProductClear);

        // Product CRUD listeners
        btnProductAdd.addActionListener(e -> {
            try {
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/login_system", "root", "");
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO products(name,price,stock) VALUES(?,?,?)");
                ps.setString(1, nameField.getText());
                ps.setDouble(2, Double.parseDouble(priceField.getText()));
                ps.setInt(3, Integer.parseInt(stockField.getText()));
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Product added!");
                loadProducts(productModel);
                nameField.setText(""); priceField.setText(""); stockField.setText("");
                selectedProductId = -1;
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        btnProductUpdate.addActionListener(e -> {
            String adminPass = JOptionPane.showInputDialog(null, "Enter admin password:");
            if (adminPass == null || !adminPass.equals("141139")) {
                JOptionPane.showMessageDialog(null, "Wrong password!");
                return;
            }
            if (selectedProductId == -1) {
                JOptionPane.showMessageDialog(null, "Select product first!");
                return;
            }
            try {
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/login_system", "root", "");
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE products SET name=?, price=?, stock=? WHERE id=?");
                ps.setString(1, nameField.getText());
                ps.setDouble(2, Double.parseDouble(priceField.getText()));
                ps.setInt(3, Integer.parseInt(stockField.getText()));
                ps.setInt(4, selectedProductId);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Product updated!");
                loadProducts(productModel);
                nameField.setText(""); priceField.setText(""); stockField.setText("");
                selectedProductId = -1;
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        btnProductDelete.addActionListener(e -> {
            String adminPass = JOptionPane.showInputDialog(null, "Enter admin password:");
            if (adminPass == null || !adminPass.equals("admin123")) {
                JOptionPane.showMessageDialog(null, "Wrong password!");
                return;
            }
            if (selectedProductId == -1) {
                JOptionPane.showMessageDialog(null, "Select product first!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(null, "Delete this product?",
                    "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Connection conn = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/login_system", "root", "");
                    PreparedStatement ps = conn.prepareStatement(
                            "DELETE FROM products WHERE id=?");
                    ps.setInt(1, selectedProductId);
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Product deleted!");
                    loadProducts(productModel);
                    nameField.setText(""); priceField.setText(""); stockField.setText("");
                    selectedProductId = -1;
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });

        btnProductClear.addActionListener(e -> {
            nameField.setText(""); priceField.setText(""); stockField.setText("");
            selectedProductId = -1;
        });

        productTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = productTable.getSelectedRow();
                if (row == -1) return;
                // convert view row → model row (important when sorter is active)
                int modelRow = productTable.convertRowIndexToModel(row);
                selectedProductId = Integer.parseInt(
                        productModel.getValueAt(modelRow, 0).toString());
                nameField.setText(productModel.getValueAt(modelRow, 1).toString());
                priceField.setText(productModel.getValueAt(modelRow, 2).toString());
                stockField.setText(productModel.getValueAt(modelRow, 3).toString());
            }
        });

        // ══════════════════════════════════════════════════════════════════════
        // ORDERS PANEL  –  two-column layout
        //   LEFT  (x=0..255)  : product/customer selectors, cart table, controls
        //   RIGHT (x=258..480): receipt textarea + checkout/clear buttons
        // Panel height available = 420px  (mainPanel is 420 tall)
        // ══════════════════════════════════════════════════════════════════════
        JPanel ordersPanel = new JPanel();
        ordersPanel.setLayout(null);
        ordersPanel.setBackground(new Color(30, 30, 30));

        // ── LEFT COLUMN ───────────────────────────────────────────────────────
        // Row 1: Product selector  (y=8)
        JLabel lblProduct = new JLabel("Product:");
        lblProduct.setForeground(Color.WHITE);
        lblProduct.setBounds(5, 12, 62, 20);
        ordersPanel.add(lblProduct);

        JComboBox<String> productBox = new JComboBox<>();
        productBox.setBounds(68, 8, 145, 26);
        ordersPanel.add(productBox);

        JLabel lblQty = new JLabel("Qty:");
        lblQty.setForeground(Color.WHITE);
        lblQty.setBounds(218, 12, 30, 20);
        ordersPanel.add(lblQty);

        JTextField qtyField = new JTextField("1");
        qtyField.setBounds(248, 8, 40, 26);
        ordersPanel.add(qtyField);

        // Row 2: Customer selector  (y=40)
        JLabel lblCustomer = new JLabel("Customer:");
        lblCustomer.setForeground(Color.WHITE);
        lblCustomer.setBounds(5, 44, 68, 20);
        ordersPanel.add(lblCustomer);

        JComboBox<String> customerBox = new JComboBox<>();
        customerBox.setBounds(74, 40, 139, 26);
        ordersPanel.add(customerBox);

        JButton btnCartAdd = createActionButton("ADD TO CART");
        btnCartAdd.setBounds(218, 40, 100, 26);
        ordersPanel.add(btnCartAdd);

        // Populate combo boxes
        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/login_system", "root", "");
            ResultSet productRS = conn.prepareStatement(
                    "SELECT name FROM products").executeQuery();
            while (productRS.next()) productBox.addItem(productRS.getString("name"));

            ResultSet customerRS = conn.prepareStatement(
                    "SELECT name FROM customers").executeQuery();
            while (customerRS.next()) customerBox.addItem(customerRS.getString("name"));
        } catch (Exception e1) { e1.printStackTrace(); }

        // Cart table  (y=72, height=130)
        String[] columns = {"Product", "Qty", "Price", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable cartTable = new JTable(model);

        JScrollPane tableScroll = new JScrollPane(cartTable);
        tableScroll.setBounds(5, 72, 245, 130);
        ordersPanel.add(tableScroll);

        // Total label  (y=208)
        lblTotal = new JLabel("Total: ₱0.00");
        lblTotal.setForeground(new Color(255, 204, 0));
        lblTotal.setFont(new Font("Arial", Font.BOLD, 13));
        lblTotal.setBounds(5, 208, 245, 22);
        ordersPanel.add(lblTotal);

        // Cart action buttons  (y=235)
        JButton btnRemove = createActionButton("REMOVE");
        btnRemove.setBounds(5,   235, 78, 26);
        ordersPanel.add(btnRemove);

        JButton btnEdit = createActionButton("EDIT QTY");
        btnEdit.setBounds(88,  235, 78, 26);
        ordersPanel.add(btnEdit);

        JButton btnPlace = createActionButton("CONFIRM");
        btnPlace.setBounds(171, 235, 79, 26);
        ordersPanel.add(btnPlace);

        // Divider label  (y=268)
        JLabel lblOpts = new JLabel("─── Order Options ───");
        lblOpts.setForeground(new Color(180, 180, 180));
        lblOpts.setFont(new Font("Arial", Font.PLAIN, 11));
        lblOpts.setBounds(5, 268, 245, 18);
        ordersPanel.add(lblOpts);

        // Student discount  (y=290)
        studentDiscount = new JCheckBox("Student Discount  (20% off)");
        studentDiscount.setForeground(Color.WHITE);
        studentDiscount.setBackground(new Color(30, 30, 30));
        studentDiscount.setBounds(5, 290, 245, 22);
        ordersPanel.add(studentDiscount);

        // Dine-in / Take-out  (y=316)
        dineIn  = new JRadioButton("Dine-in");
        takeOut = new JRadioButton("Take-out");
        styleRadio(dineIn);  styleRadio(takeOut);
        dineIn.setBounds(5,   316, 90, 22);
        takeOut.setBounds(100, 316, 90, 22);
        ButtonGroup orderType = new ButtonGroup();
        orderType.add(dineIn); orderType.add(takeOut);
        dineIn.setSelected(true);
        ordersPanel.add(dineIn); ordersPanel.add(takeOut);

        // Cash / Card  (y=340)
        cash = new JRadioButton("Cash");
        card = new JRadioButton("Card");
        styleRadio(cash); styleRadio(card);
        cash.setBounds(5,   340, 90, 22);
        card.setBounds(100, 340, 90, 22);
        ButtonGroup payment = new ButtonGroup();
        payment.add(cash); payment.add(card);
        cash.setSelected(true);
        ordersPanel.add(cash); ordersPanel.add(card);

        // ── RIGHT COLUMN ──────────────────────────────────────────────────────
        // Thin separator line
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setBounds(253, 5, 2, 405);
        sep.setForeground(new Color(80, 80, 80));
        ordersPanel.add(sep);

        // Receipt label
        JLabel lblReceipt = new JLabel("RECEIPT");
        lblReceipt.setForeground(new Color(255, 204, 0));
        lblReceipt.setFont(new Font("Impact", Font.BOLD, 14));
        lblReceipt.setBounds(258, 5, 235, 20);
        ordersPanel.add(lblReceipt);

        // Receipt textarea  (y=28, height=340)
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(new Color(0, 255, 0));
        textArea.setCaretColor(new Color(0, 255, 0));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(258, 28, 232, 300);
        ordersPanel.add(scrollPane);

        // Checkout + Clear buttons  (y=334)
        JButton btnCheckout = createActionButton("CHECKOUT");
        btnCheckout.setBounds(258, 334, 112, 30);
        ordersPanel.add(btnCheckout);

        JButton btnClear = createActionButton("CLEAR ALL");
        btnClear.setBounds(378, 334, 112, 30);
        ordersPanel.add(btnClear);

        // ADD TO CART
        btnCartAdd.addActionListener(e -> {
            try {
                int qty = Integer.parseInt(qtyField.getText());
                if (qty <= 0) {
                    JOptionPane.showMessageDialog(null, "Quantity must be greater than 0!");
                    return;
                }
                String name = productBox.getSelectedItem().toString();
                int price = 0;
                try {
                    Connection conn = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/login_system", "root", "");
                    PreparedStatement ps = conn.prepareStatement(
                            "SELECT price, stock FROM products WHERE name=?");
                    ps.setString(1, name);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        price = rs.getInt("price");
                        int stock = rs.getInt("stock");
                        if (qty > stock) {
                            JOptionPane.showMessageDialog(null, "Not enough stock available!");
                            return;
                        }
                    }
                } catch (Exception ex) { ex.printStackTrace(); }

                boolean duplicate = false;
                for (int i = 0; i < model.getRowCount(); i++) {
                    if (((String) model.getValueAt(i, 0)).equals(name)) {
                        int newQty = (int) model.getValueAt(i, 1) + qty;
                        model.setValueAt(newQty, i, 1);
                        model.setValueAt((double)(newQty * price), i, 3);
                        duplicate = true;
                        break;
                    }
                }
                if (!duplicate) {
                    model.addRow(new Object[]{name, qty, price, (double)(qty * price)});
                }
                updateTotal(model, lblTotal);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Invalid quantity!");
            }
        });

        // REMOVE
        btnRemove.addActionListener(e -> {
            int row = cartTable.getSelectedRow();
            if (row != -1) { model.removeRow(row); updateTotal(model, lblTotal); }
        });

        // EDIT QTY
        btnEdit.addActionListener(e -> {
            int row = cartTable.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(null, "Select an item first!"); return; }
            String input = JOptionPane.showInputDialog(null, "Enter new quantity:");
            try {
                int newQty = Integer.parseInt(input);
                if (newQty <= 0) { JOptionPane.showMessageDialog(null, "Invalid quantity!"); return; }
                double price    = Double.parseDouble(model.getValueAt(row, 2).toString());
                model.setValueAt(newQty, row, 1);
                model.setValueAt(newQty * price, row, 3);
                updateTotal(model, lblTotal);
            } catch (Exception ex) { JOptionPane.showMessageDialog(null, "Invalid input!"); }
        });

        // CONFIRM ORDER
        btnPlace.addActionListener(e -> {
            if (customerBox.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(null, "Select customer first!"); return;
            }
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "Cart is empty!"); return;
            }
            try {
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/login_system", "root", "");
                double finalTotal   = studentDiscount.isSelected() ? total * 0.8 : total;
                String orderTypeTxt = dineIn.isSelected() ? "Dine-in" : "Take-out";
                String paymentTxt   = cash.isSelected()   ? "Cash"    : "Card";

                int customerId = 0;
                PreparedStatement customerPS = conn.prepareStatement(
                        "SELECT id FROM customers WHERE name=?");
                customerPS.setString(1, customerBox.getSelectedItem().toString());
                ResultSet customerRS = customerPS.executeQuery();
                if (customerRS.next()) customerId = customerRS.getInt("id");

                PreparedStatement orderPS = conn.prepareStatement(
                        "INSERT INTO orders(username,customer_id,total,order_type,payment_method) VALUES(?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                orderPS.setString(1, username);
                orderPS.setInt(2, customerId);
                orderPS.setDouble(3, finalTotal);
                orderPS.setString(4, orderTypeTxt);
                orderPS.setString(5, paymentTxt);
                orderPS.executeUpdate();

                int orderID = 0;
                ResultSet keys = orderPS.getGeneratedKeys();
                if (keys.next()) orderID = keys.getInt(1);

                for (int i = 0; i < model.getRowCount(); i++) {
                    String product  = model.getValueAt(i, 0).toString();
                    int    qty      = (int) model.getValueAt(i, 1);
                    double price    = Double.parseDouble(model.getValueAt(i, 2).toString());
                    double subtotal = Double.parseDouble(model.getValueAt(i, 3).toString());

                    PreparedStatement detailPS = conn.prepareStatement(
                            "INSERT INTO order_details(order_id,product_name,quantity,price,subtotal) VALUES(?,?,?,?,?)");
                    detailPS.setInt(1, orderID); detailPS.setString(2, product);
                    detailPS.setInt(3, qty);     detailPS.setDouble(4, price);
                    detailPS.setDouble(5, subtotal);
                    detailPS.executeUpdate();

                    PreparedStatement stockPS = conn.prepareStatement(
                            "UPDATE products SET stock = stock - ? WHERE name = ?");
                    stockPS.setInt(1, qty); stockPS.setString(2, product);
                    stockPS.executeUpdate();
                }

                // Receipt
                textArea.setText("");
                textArea.append("================================\n");
                textArea.append("     FREDDY FAZBEAR PIZZA\n");
                textArea.append("================================\n\n");
                textArea.append("Cashier: "  + username + "\n");
                textArea.append("Customer: " + customerBox.getSelectedItem() + "\n");
                textArea.append("Order ID: " + orderID + "\n\n");
                for (int i = 0; i < model.getRowCount(); i++) {
                    textArea.append(
                            model.getValueAt(i, 0) + " x" +
                            model.getValueAt(i, 1) + "  =  " +
                            model.getValueAt(i, 3) + "\n");
                }
                textArea.append("\n--------------------------------\n");
                textArea.append("Order Type: " + orderTypeTxt + "\n");
                textArea.append("Payment: "    + paymentTxt   + "\n");
                if (studentDiscount.isSelected()) textArea.append("Student Discount Applied\n");
                textArea.append("--------------------------------\n");
                textArea.append("TOTAL: " + finalTotal + "\n\n");
                textArea.append("  THANK YOU! COME AGAIN!\n");
                textArea.append("================================\n");

                model.setRowCount(0);
                total = 0;
                lblTotal.setText("Total: ₱0.00");

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error saving order!");
            }
        });

        // CHECKOUT
        btnCheckout.addActionListener(e -> {
            if (total == 0) { JOptionPane.showMessageDialog(null, "No order!"); return; }
            double finalTotal = studentDiscount.isSelected() ? total * 0.8 : total;
            textArea.append("\n--- CHECKOUT ---\n");
            textArea.append("TOTAL: " + finalTotal + "\n");
        });

        // CLEAR
        btnClear.addActionListener(e -> {
            textArea.setText("");
            total = 0;
            lblTotal.setText("Total: ₱0.00");
            model.setRowCount(0);
        });

        // ══════════════════════════════════════════════════════════════════════
        // HOME PANEL
        // ══════════════════════════════════════════════════════════════════════
        JPanel homePanel = new JPanel();
        homePanel.setBackground(Color.BLACK);
        homePanel.setLayout(null);

        JLabel welcome = new JLabel("Welcome, " + username);
        welcome.setBounds(10, 11, 330, 64);
        welcome.setForeground(Color.YELLOW);
        welcome.setFont(new Font("Impact", Font.BOLD, 25));
        homePanel.add(welcome);

        mainPanel.add(homePanel,    "Home");
        mainPanel.add(ordersPanel,  "Orders");
        mainPanel.add(productsPanel,"Products");

        // ══════════════════════════════════════════════════════════════════════
        // CUSTOMERS PANEL
        // ══════════════════════════════════════════════════════════════════════
        JPanel customersPanel = new JPanel();
        customersPanel.setLayout(null);
        customersPanel.setBackground(new Color(30, 30, 30));

        JLabel lblCName = new JLabel("Name:");
        lblCName.setForeground(Color.WHITE);
        lblCName.setBounds(10, 15, 80, 20);
        customersPanel.add(lblCName);

        JTextField customerNameField = new JTextField();
        customerNameField.setBounds(90, 10, 180, 30);
        customersPanel.add(customerNameField);

        JLabel lblCContact = new JLabel("Contact:");
        lblCContact.setForeground(Color.WHITE);
        lblCContact.setBounds(280, 15, 70, 20);
        customersPanel.add(lblCContact);

        JTextField customerContactField = new JTextField();
        customerContactField.setBounds(350, 10, 120, 30);
        customersPanel.add(customerContactField);

        JLabel lblCAddress = new JLabel("Address:");
        lblCAddress.setForeground(Color.WHITE);
        lblCAddress.setBounds(10, 55, 80, 20);
        customersPanel.add(lblCAddress);

        JTextField customerAddressField = new JTextField();
        customerAddressField.setBounds(90, 50, 380, 30);
        customersPanel.add(customerAddressField);

        String[]          customerColumns = {"ID", "Name", "Contact", "Address"};
        DefaultTableModel customerModel   = new DefaultTableModel(customerColumns, 0);
        JTable            customerTable   = new JTable(customerModel);

        JScrollPane customerScroll = new JScrollPane(customerTable);
        customerScroll.setBounds(10, 95, 460, 185);
        customersPanel.add(customerScroll);

        JButton btnCustomerAdd    = createActionButton("ADD");
        JButton btnCustomerUpdate = createActionButton("EDIT CUSTOMER");
        JButton btnCustomerDelete = createActionButton("DELETE");
        JButton btnCustomerClear  = createActionButton("CLEAR");

        btnCustomerAdd.setBounds(10,  295, 100, 30);
        btnCustomerUpdate.setBounds(120, 295, 120, 30);
        btnCustomerDelete.setBounds(250, 295, 100, 30);
        btnCustomerClear.setBounds(360, 295, 100, 30);

        customersPanel.add(btnCustomerAdd);
        customersPanel.add(btnCustomerUpdate);
        customersPanel.add(btnCustomerDelete);
        customersPanel.add(btnCustomerClear);

        final int[] selectedCustomerId = {-1};

        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/login_system", "root", "");
            ResultSet rs = conn.prepareStatement("SELECT * FROM customers").executeQuery();
            while (rs.next()) {
                customerModel.addRow(new Object[]{
                        rs.getInt("id"), rs.getString("name"),
                        rs.getString("contact"), rs.getString("address")});
            }
        } catch (Exception ex) { ex.printStackTrace(); }

        btnCustomerAdd.addActionListener(e -> {
            if (customerNameField.getText().isEmpty() ||
                customerContactField.getText().isEmpty() ||
                customerAddressField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Complete all fields!"); return;
            }
            try {
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/login_system", "root", "");
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO customers(name,contact,address) VALUES(?,?,?)");
                ps.setString(1, customerNameField.getText());
                ps.setString(2, customerContactField.getText());
                ps.setString(3, customerAddressField.getText());
                ps.executeUpdate();

                // Fetch the generated ID
                ResultSet keys = ps.getGeneratedKeys();
                int newId = keys.next() ? keys.getInt(1) : 0;
                customerModel.addRow(new Object[]{
                        newId,
                        customerNameField.getText(),
                        customerContactField.getText(),
                        customerAddressField.getText()});
                JOptionPane.showMessageDialog(null, "Customer added!");
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        customerTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = customerTable.getSelectedRow();
                if (row == -1) return;
                selectedCustomerId[0] = Integer.parseInt(
                        customerModel.getValueAt(row, 0).toString());
                customerNameField.setText(customerModel.getValueAt(row, 1).toString());
                customerContactField.setText(customerModel.getValueAt(row, 2).toString());
                customerAddressField.setText(customerModel.getValueAt(row, 3).toString());
            }
        });

        btnCustomerUpdate.addActionListener(e -> {
            if (selectedCustomerId[0] == -1) {
                JOptionPane.showMessageDialog(null, "Select customer first!"); return;
            }
            try {
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/login_system", "root", "");
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE customers SET name=?, contact=?, address=? WHERE id=?");
                ps.setString(1, customerNameField.getText());
                ps.setString(2, customerContactField.getText());
                ps.setString(3, customerAddressField.getText());
                ps.setInt(4, selectedCustomerId[0]);
                ps.executeUpdate();
                JOptionPane.showMessageDialog(null, "Customer updated!");
                int row = customerTable.getSelectedRow();
                customerModel.setValueAt(customerNameField.getText(),    row, 1);
                customerModel.setValueAt(customerContactField.getText(), row, 2);
                customerModel.setValueAt(customerAddressField.getText(), row, 3);
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        btnCustomerDelete.addActionListener(e -> {
            if (selectedCustomerId[0] == -1) {
                JOptionPane.showMessageDialog(null, "Select customer first!"); return;
            }
            try {
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/login_system", "root", "");
                PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM customers WHERE id=?");
                ps.setInt(1, selectedCustomerId[0]);
                ps.executeUpdate();
                customerModel.removeRow(customerTable.getSelectedRow());
                JOptionPane.showMessageDialog(null, "Customer deleted!");
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        btnCustomerClear.addActionListener(e -> {
            customerNameField.setText("");
            customerContactField.setText("");
            customerAddressField.setText("");
            selectedCustomerId[0] = -1;
        });

        mainPanel.add(customersPanel, "Customers");

        // ══════════════════════════════════════════════════════════════════════
        // REPORTS PANEL
        // ══════════════════════════════════════════════════════════════════════
        JPanel reportsPanel = new JPanel();
        reportsPanel.setLayout(null);
        reportsPanel.setBackground(new Color(30, 30, 30));

        String[]          reportColumns = {"Order ID", "Username", "Total"};
        DefaultTableModel reportModel   = new DefaultTableModel(reportColumns, 0);
        JTable            reportTable   = new JTable(reportModel);

        JScrollPane reportScroll = new JScrollPane(reportTable);
        reportScroll.setBounds(10, 50, 460, 200);
        reportsPanel.add(reportScroll);

        JButton btnGenerate = createActionButton("GENERATE");
        btnGenerate.setBounds(10, 10, 140, 30);
        reportsPanel.add(btnGenerate);

        JButton btnExport = createActionButton("EXPORT CSV");
        btnExport.setBounds(160, 10, 140, 30);
        reportsPanel.add(btnExport);

        JLabel lblSales = new JLabel("Total Sales: 0");
        lblSales.setForeground(Color.WHITE);
        lblSales.setBounds(10, 260, 300, 30);
        reportsPanel.add(lblSales);

        btnGenerate.addActionListener(e -> {
            reportModel.setRowCount(0);
            double sales = 0;
            try {
                Connection conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/login_system", "root", "");
                ResultSet rs = conn.prepareStatement("SELECT * FROM orders").executeQuery();
                while (rs.next()) {
                    double t = rs.getDouble("total");
                    sales += t;
                    reportModel.addRow(new Object[]{
                            rs.getInt("id"), rs.getString("username"), t});
                }
                lblSales.setText("Total Sales: " + sales);
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        btnExport.addActionListener(e -> {
            try {
                java.io.FileWriter writer = new java.io.FileWriter("sales_report.csv");
                writer.write("Order ID,Username,Total\n");
                for (int i = 0; i < reportModel.getRowCount(); i++) {
                    writer.write(reportModel.getValueAt(i, 0) + "," +
                                 reportModel.getValueAt(i, 1) + "," +
                                 reportModel.getValueAt(i, 2) + "\n");
                }
                writer.close();
                JOptionPane.showMessageDialog(null, "CSV Exported!");
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        mainPanel.add(reportsPanel, "Reports");

        // ── Sidebar navigation ────────────────────────────────────────────────
        btnOrders.addActionListener(e -> {
            resetMenuColors(btnOrders, btnProducts, btnCustomers, btnReports);
            btnOrders.setBackground(Color.RED);
            cardLayout.show(mainPanel, "Orders");
        });
        btnProducts.addActionListener(e -> {
            resetMenuColors(btnOrders, btnProducts, btnCustomers, btnReports);
            btnProducts.setBackground(Color.RED);
            cardLayout.show(mainPanel, "Products");
        });
        btnCustomers.addActionListener(e -> {
            resetMenuColors(btnOrders, btnProducts, btnCustomers, btnReports);
            btnCustomers.setBackground(Color.RED);
            cardLayout.show(mainPanel, "Customers");
        });
        btnReports.addActionListener(e -> {
            resetMenuColors(btnOrders, btnProducts, btnCustomers, btnReports);
            btnReports.setBackground(Color.RED);
            cardLayout.show(mainPanel, "Reports");
        });

        cardLayout.show(mainPanel, "Home");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void updateTotal(DefaultTableModel model, JLabel lblTotal) {
        total = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            total += Double.parseDouble(model.getValueAt(i, 3).toString());
        }
        lblTotal.setText(String.format("Total: ₱%.2f", total));
    }

    private void resetMenuColors(JButton... buttons) {
        for (JButton btn : buttons) btn.setBackground(new Color(80, 0, 0));
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(80, 0, 0));
        btn.setForeground(Color.WHITE);
        return btn;
    }

    private JButton createActionButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.RED);
        btn.setForeground(Color.WHITE);
        return btn;
    }

    private void styleRadio(JRadioButton rb) {
        rb.setForeground(Color.WHITE);
        rb.setBackground(new Color(30, 30, 30));
    }

    private void loadProducts(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/login_system", "root", "");
            ResultSet rs = conn.prepareStatement("SELECT * FROM products").executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"), rs.getString("name"),
                        rs.getDouble("price"), rs.getInt("stock")});
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private JPanel createDarkPanel(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
}
