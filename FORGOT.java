package login;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
public class FORGOT extends JFrame {
   private JTextField txtUser, txtSec1, txtSec2, txtSec3;
   private JPasswordField txtNewPass;
   private JTextArea textArea;
   public FORGOT() {
       setTitle("Forgot Password");
       setSize(450, 350);
       setLocationRelativeTo(null);
       setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
       JPanel contentPane = new JPanel();
       contentPane.setBackground(new Color(20,20,20));
       contentPane.setLayout(new BorderLayout());
       setContentPane(contentPane);
       JLabel title = new JLabel("PASSWORD RECOVERY", SwingConstants.CENTER);
       title.setForeground(new Color(255,204,0));
       title.setFont(new Font("Impact", Font.BOLD, 22));
       title.setBorder(new EmptyBorder(10,10,10,10));
       contentPane.add(title, BorderLayout.NORTH);
       JPanel panel = new JPanel();
       panel.setLayout(null);
       panel.setBackground(new Color(50,50,50));
       contentPane.add(panel, BorderLayout.CENTER);
       JLabel lblUser = new JLabel("Username:");
       lblUser.setForeground(Color.WHITE);
       lblUser.setBounds(40, 11, 120, 20);
       panel.add(lblUser);
       txtUser = new JTextField();
       txtUser.setBounds(160, 9, 175, 25);
       txtUser.setBackground(Color.BLACK);
       txtUser.setForeground(new Color(0,255,0));
       panel.add(txtUser);
       JLabel lbl1 = new JLabel("Favorite color:");
       lbl1.setForeground(Color.WHITE);
       lbl1.setBounds(40, 42, 120, 20);
       panel.add(lbl1);
       txtSec1 = new JTextField();
       txtSec1.setBounds(160, 42, 175, 25);
       txtSec1.setBackground(Color.BLACK);
       txtSec1.setForeground(new Color(0,255,0));
       panel.add(txtSec1);
       JLabel lbl2 = new JLabel("Pet name:");
       lbl2.setForeground(Color.WHITE);
       lbl2.setBounds(40, 73, 120, 20);
       panel.add(lbl2);
       txtSec2 = new JTextField();
       txtSec2.setBounds(160, 73, 175, 25);
       txtSec2.setBackground(Color.BLACK);
       txtSec2.setForeground(new Color(0,255,0));
       panel.add(txtSec2);
       JLabel lbl3 = new JLabel("Birthplace:");
       lbl3.setForeground(Color.WHITE);
       lbl3.setBounds(40, 104, 120, 20);
       panel.add(lbl3);
       txtSec3 = new JTextField();
       txtSec3.setBounds(160, 104, 175, 25);
       txtSec3.setBackground(Color.BLACK);
       txtSec3.setForeground(new Color(0,255,0));
       panel.add(txtSec3);
       JLabel lblNew = new JLabel("New Password:");
       lblNew.setForeground(Color.WHITE);
       lblNew.setBounds(40, 135, 120, 20);
       panel.add(lblNew);
       txtNewPass = new JPasswordField();
       txtNewPass.setBounds(160, 135, 175, 25);
       txtNewPass.setBackground(Color.BLACK);
       txtNewPass.setForeground(new Color(0,255,0));
       panel.add(txtNewPass);
       textArea = new JTextArea();
       textArea.setBounds(40, 207, 194, 30);
       textArea.setEditable(false);
       textArea.setBackground(Color.BLACK);
       textArea.setForeground(new Color(0,255,0));
       textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
       panel.add(textArea);
       JButton btnReset = new JButton("RESET PASSWORD");
       btnReset.setBounds(40, 166, 237, 30);
       btnReset.setBackground(Color.RED);
       btnReset.setForeground(Color.WHITE);
       btnReset.setFocusPainted(false);
       panel.add(btnReset);
       btnReset.addActionListener(e -> {
           try {
               Connection conn = DriverManager.getConnection(
                       "jdbc:mysql://localhost:3306/login_system", "root", "");
               String query = "SELECT * FROM users WHERE username=?";
               PreparedStatement ps = conn.prepareStatement(query);
               ps.setString(1, txtUser.getText());
               ResultSet rs = ps.executeQuery();
               if (rs.next()) {
                   if (txtSec1.getText().equalsIgnoreCase(rs.getString("sec1")) &&
                       txtSec2.getText().equalsIgnoreCase(rs.getString("sec2")) &&
                       txtSec3.getText().equalsIgnoreCase(rs.getString("sec3"))) {
                       String update = "UPDATE users SET password=? WHERE username=?";
                       PreparedStatement ups = conn.prepareStatement(update);
                       ups.setString(1, LOGIN.hashPassword(new String(txtNewPass.getPassword())));
                       ups.setString(2, txtUser.getText());
                       ups.executeUpdate();
                       textArea.setText("Password reset successful!");
                   } else {
                       textArea.setText("Incorrect answers!");
                   }
               } else {
                   textArea.setText("Username not found!");
               }
           } catch (Exception ex) {
               ex.printStackTrace();
               textArea.setText("Error: " + ex.getMessage());
           }
       });
   }
}

