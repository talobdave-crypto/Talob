package login;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
public class REGISTER extends JFrame {
   private JTextField txtUser, txtSec1, txtSec2, txtSec3;
   private JPasswordField txtPass;
   private JTextArea textArea;
   public REGISTER() {
       setTitle("Freddy Fazbear Register");
       setSize(450, 350);
       setLocationRelativeTo(null);
       setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
       JPanel contentPane = new JPanel();
       contentPane.setBackground(new Color(20, 20, 20));
       contentPane.setLayout(new BorderLayout());
       setContentPane(contentPane);
       JLabel lblTitle = new JLabel("REGISTER SECURITY SYSTEM", SwingConstants.CENTER);
       lblTitle.setForeground(new Color(255, 204, 0));
       lblTitle.setFont(new Font("Impact", Font.BOLD, 22));
       lblTitle.setBorder(new EmptyBorder(10, 10, 10, 10));
       contentPane.add(lblTitle, BorderLayout.NORTH);
       JPanel panel = new JPanel();
       panel.setLayout(null);
       panel.setBackground(new Color(50, 50, 50));
       contentPane.add(panel, BorderLayout.CENTER);
       JLabel lblUser = new JLabel("Username:");
       lblUser.setForeground(Color.WHITE);
       lblUser.setBounds(40, 20, 120, 20);
       panel.add(lblUser);
       txtUser = new JTextField();
       txtUser.setBounds(160, 20, 200, 25);
       txtUser.setBackground(Color.BLACK);
       txtUser.setForeground(new Color(0, 255, 0));
       panel.add(txtUser);
       JLabel lblPass = new JLabel("Password:");
       lblPass.setForeground(Color.WHITE);
       lblPass.setBounds(40, 60, 120, 20);
       panel.add(lblPass);
       txtPass = new JPasswordField();
       txtPass.setBounds(160, 60, 200, 25);
       txtPass.setBackground(Color.BLACK);
       txtPass.setForeground(new Color(0, 255, 0));
       panel.add(txtPass);
       JLabel lbl1 = new JLabel("Favorite color:");
       lbl1.setForeground(Color.WHITE);
       lbl1.setBounds(40, 100, 120, 20);
       panel.add(lbl1);
       txtSec1 = new JTextField();
       txtSec1.setBounds(160, 100, 200, 25);
       txtSec1.setBackground(Color.BLACK);
       txtSec1.setForeground(new Color(0, 255, 0));
       panel.add(txtSec1);
       JLabel lbl2 = new JLabel("Pet name:");
       lbl2.setForeground(Color.WHITE);
       lbl2.setBounds(40, 130, 120, 20);
       panel.add(lbl2);
       txtSec2 = new JTextField();
       txtSec2.setBounds(160, 130, 200, 25);
       txtSec2.setBackground(Color.BLACK);
       txtSec2.setForeground(new Color(0, 255, 0));
       panel.add(txtSec2);
       JLabel lbl3 = new JLabel("Birthplace:");
       lbl3.setForeground(Color.WHITE);
       lbl3.setBounds(40, 160, 120, 20);
       panel.add(lbl3);
       txtSec3 = new JTextField();
       txtSec3.setBounds(160, 160, 200, 25);
       txtSec3.setBackground(Color.BLACK);
       txtSec3.setForeground(new Color(0, 255, 0));
       panel.add(txtSec3);
       textArea = new JTextArea();
       textArea.setBounds(40, 196, 160, 30);
       textArea.setEditable(false);
       textArea.setBackground(Color.BLACK);
       textArea.setForeground(new Color(0, 255, 0));
       textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
       panel.add(textArea);
       JButton btnRegister = new JButton("REGISTER");
       btnRegister.setBounds(210, 196, 150, 30);
       btnRegister.setBackground(new Color(255, 204, 0));
       btnRegister.setForeground(Color.BLACK);
       btnRegister.setFocusPainted(false);
       panel.add(btnRegister);
       btnRegister.addActionListener(e -> {
           try {
               Connection conn = DriverManager.getConnection(
                       "jdbc:mysql://localhost:3306/login_system", "root", "");
               String query = "INSERT INTO users(username,password,sec1,sec2,sec3) VALUES(?,?,?,?,?)";
               PreparedStatement ps = conn.prepareStatement(query);
               ps.setString(1, txtUser.getText());
               ps.setString(2, LOGIN.hashPassword(new String(txtPass.getPassword())));
               ps.setString(3, txtSec1.getText());
               ps.setString(4, txtSec2.getText());
               ps.setString(5, txtSec3.getText());
               ps.executeUpdate();
               textArea.setText("Registered successfully!");
           } catch (Exception ex) {
               ex.printStackTrace();
               textArea.setText("Error registering!" + ex.getMessage());
           }
       });
   }
}

