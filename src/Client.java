import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class Client extends JFrame implements ActionListener{

    private JButton logButton = new JButton("Connect");
    private JButton discButton = new JButton("Disconnect");
    private JButton checkCashButton = new JButton("Check cash");
    private JButton getMoneyButton = new JButton("Get money");

    private JTextField userLogin = new JTextField("");
    private JTextField userPassword = new JTextField("");
    private JTextField userInfo = new JTextField("");

    Connection clientConnect;

    public static void main(String[] args) {

        new Client();
    }

    public Client() {

        this.setTitle("Bank client");
        this.setBounds(100, 100, 400, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        logButton.addActionListener(this);
        discButton.addActionListener(this);
        checkCashButton.addActionListener(this);
        getMoneyButton.addActionListener(this);
        this.setLayout(new GridLayout(7,1));

        add(userLogin);
        add(userPassword);
        add(logButton);
        add(userInfo);
        add(discButton);
        add(checkCashButton);
        add(getMoneyButton);
        discButton.setVisible(false);
        checkCashButton.setVisible(false);
        getMoneyButton.setVisible(false);

        this.setVisible(true);
    }

    public void actionPerformed (ActionEvent e) {

        if (e.getSource() == logButton) {

            clientConnect = new Connection();
            String returnedString = clientConnect.
                    tryAuthorization(userLogin.getText(), userPassword.getText());

            if(returnedString.equals("Welcome")) {

                discButton.setVisible(true);
                checkCashButton.setVisible(true);
                getMoneyButton.setVisible(true);
                logButton.setVisible(false);
            }

            userInfo.setText(returnedString);
        } else if (e.getSource() == discButton) {

            discButton.setVisible(false);
            checkCashButton.setVisible(false);
            getMoneyButton.setVisible(false);
            logButton.setVisible(true);
            userInfo.setText(clientConnect.sendRequest("1"));
        } else if (e.getSource() == checkCashButton) {

            userInfo.setText(clientConnect.sendRequest("2"));
        } else if (e.getSource() == getMoneyButton) {

            String amount = JOptionPane.showInputDialog("How much money?");
            userInfo.setText(clientConnect.getMoneyFromCash(amount));
        }
    }

    class Connection {

        private DataInputStream in;
        private DataOutputStream out;
        private String inLine;
        private Socket socket;

        public Connection() {
            try {

                socket = new Socket("127.0.0.1", 6666);
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        public String tryAuthorization(String login,String password) {
            try {

                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(login + " " + password);
                out.flush();
                inLine = in.readUTF();
            } catch (IOException e) {

                e.printStackTrace();
            }

            return inLine;
        }

        public String sendRequest(String request) {
            try {

                out.writeUTF(request);
                out.flush();
                inLine = in.readUTF();
            } catch (IOException e) {

                e.printStackTrace();
            }

            return inLine;
        }

        public String getMoneyFromCash (String moneyAmount) {
            try {

                out.writeUTF("3");
                out.flush();
                in.readUTF();
                out.writeUTF(moneyAmount);
                out.flush();
                inLine = in.readUTF();
            } catch (IOException e) {

                e.printStackTrace();
            }

            return inLine;
        }
    }
}

