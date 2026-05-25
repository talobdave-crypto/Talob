package login;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.sql.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.security.MessageDigest;
public class LOGIN extends JFrame {
	
 private JPanel contentPane;
 private JTextField textField;
 private JPasswordField passwordField;
 private JTextArea textArea;
 private JLabel lblUserError;
 private JLabel lblPassError;
 private int attempts = 0;
 public static void main(String[] args) {
     EventQueue.invokeLater(() -> {
         try {
             LOGIN frame = new LOGIN();
             frame.setVisible(true);
         } catch (Exception e) {
             e.printStackTrace();
         }
     });
 }
 public LOGIN() {
     setTitle("Freddy Fazbear's Pizzeria Security System");
     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     setSize(450, 350);
     setLocationRelativeTo(null);
     contentPane = new JPanel();
     contentPane.setBackground(new Color(20, 20, 20));
     contentPane.setLayout(new BorderLayout());
     setContentPane(contentPane);
     JLabel lblTitle = new JLabel("FREDDY FAZBEAR'S PIZZERIA", SwingConstants.CENTER);
     lblTitle.setForeground(new Color(255, 204, 0));
     lblTitle.setFont(new Font("Impact", Font.BOLD, 22));
     lblTitle.setBorder(new EmptyBorder(10, 10, 10, 10));
     contentPane.add(lblTitle, BorderLayout.NORTH);
   
     JPanel panel = new JPanel();
     panel.setLayout(null);
     panel.setBackground(new Color(50, 50, 50));
     panel.setPreferredSize(new Dimension(450, 300));
     contentPane.add(panel, BorderLayout.CENTER);
   
     JLabel lblUsername = new JLabel("Username:");
     lblUsername.setForeground(Color.WHITE);
     lblUsername.setBounds(60, 20, 100, 20);
     panel.add(lblUsername);
     textField = new JTextField();
     textField.setBounds(150, 20, 200, 25);
     textField.setBackground(Color.BLACK);
     textField.setForeground(new Color(0, 255, 0));
     panel.add(textField);
     lblUserError = new JLabel("");
     lblUserError.setForeground(Color.RED);
     lblUserError.setBounds(150, 45, 250, 15);
     panel.add(lblUserError);
   
     JLabel lblPassword = new JLabel("Password:");
     lblPassword.setForeground(Color.WHITE);
     lblPassword.setBounds(60, 60, 100, 20);
     panel.add(lblPassword);
     passwordField = new JPasswordField();
     passwordField.setBounds(150, 60, 200, 25);
     passwordField.setBackground(Color.BLACK);
     passwordField.setForeground(new Color(0, 255, 0));
     panel.add(passwordField);
     lblPassError = new JLabel("");
     lblPassError.setForeground(Color.RED);
     lblPassError.setBounds(150, 85, 250, 15);
     panel.add(lblPassError);
   
     JCheckBox showPass = new JCheckBox("Show Password");
     showPass.setBounds(150, 100, 150, 20);
     showPass.setBackground(new Color(50, 50, 50));
     showPass.setForeground(Color.WHITE);
     panel.add(showPass);
     showPass.addActionListener(e -> {
         if (showPass.isSelected()) passwordField.setEchoChar((char) 0);
         else passwordField.setEchoChar('*');
     });
     textArea = new JTextArea();
     textArea.setBounds(60, 130, 290, 70);
     textArea.setEditable(false);
     textArea.setBackground(Color.BLACK);
     textArea.setForeground(new Color(0, 255, 0));
     textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
     panel.add(textArea);
   
     final JButton btnLogin = new JButton("LOGIN");
     btnLogin.setBounds(60, 210, 90, 30);
     btnLogin.setBackground(Color.RED);
     btnLogin.setForeground(Color.WHITE);
     btnLogin.setFocusPainted(false);
     panel.add(btnLogin);
   
     JButton btnClear = new JButton("CLEAR");
     btnClear.setBounds(280, 210, 90, 30);
     btnClear.setBackground(Color.RED);
     btnClear.setForeground(Color.WHITE);
     btnClear.setFocusPainted(false);
     panel.add(btnClear);
   
     JButton btnForgot = new JButton("FORGOT");
     btnForgot.setBounds(160, 250, 110, 25);
     btnForgot.setBackground(new Color(80, 0, 0));
     btnForgot.setForeground(Color.WHITE);
     btnForgot.setFocusPainted(false);
     panel.add(btnForgot);
   
     JButton btnRegister = new JButton("REGISTER");
     btnRegister.setBounds(160, 210, 110, 31);
     btnRegister.setBackground(new Color(255, 204, 0));
     btnRegister.setForeground(Color.BLACK);
     btnRegister.setFocusPainted(false);
     panel.add(btnRegister);
     btnClear.addActionListener(e -> clearAllFields());
     btnLogin.addActionListener(e -> {
   
  	   String username = textField.getText().trim();
  	   String password = new String(passwordField.getPassword());
  	   lblUserError.setText("");
  	   lblPassError.setText("");
  	   if(username.isEmpty()) {
  	       lblUserError.setText("Username is required!");
  	       textField.requestFocus();
  	       return;
  	   }
  	   if(password.isEmpty()) {
  	       lblPassError.setText("Password is required!");
  	       passwordField.requestFocus();
  	       return;
  	   }
  	 
  	try (
  		    Connection conn = DriverManager.getConnection(
  		            "jdbc:mysql://localhost:3306/login_system",
  		            "root",
  		            ""
  		    );
  		    PreparedStatement ps = conn.prepareStatement(
  		    		"SELECT password, role FROM users WHERE username=?"
  		    )
  		) {
  		    ps.setString(1, username);
  		    ResultSet rs = ps.executeQuery();
             if (rs.next()) {
                 String storedHash = rs.getString("password");
                 String inputHash = hashPassword(password);
                 if (MessageDigest.isEqual(storedHash.getBytes(), inputHash.getBytes())) {
                     textArea.setText("LOGIN SUCCESSFUL!");
                     clearAllFields();
                     String role = rs.getString("role");
                     new Parent(username, role).setVisible(true);
                     dispose();
                 } else {
              	    attempts++;
              	    textArea.setText("Invalid password! Attempt: " + attempts);
              	    if(attempts >= 3) {
              	        JOptionPane.showMessageDialog(null,
              	                "Too many failed attempts!");
              	        btnLogin.setEnabled(false);
              	    }
              	}
             } else {
                 textArea.setText("User not found!");
             }
         } catch (Exception ex) {
             ex.printStackTrace();
         }
     });
     btnForgot.addActionListener(e -> {
         new FORGOT().setVisible(true);
         dispose();
     });
   
     btnRegister.addActionListener(e -> {
         new REGISTER().setVisible(true);
         dispose();
     });
 }
 private void clearAllFields() {
     textField.setText("");
     passwordField.setText("");
     textArea.setText("");
     lblUserError.setText("");
     lblPassError.setText("");
     textField.requestFocusInWindow();
 }
 public static String hashPassword(String password) {
     try {
         MessageDigest md = MessageDigest.getInstance("SHA-256");
         byte[] hash = md.digest(password.getBytes("UTF-8"));
         StringBuilder hex = new StringBuilder();
         for (byte b : hash) {
             hex.append(String.format("%02x", b));
         }
         return hex.toString();
     } catch (Exception e) {
         throw new RuntimeException(e);
     }
 }
}

