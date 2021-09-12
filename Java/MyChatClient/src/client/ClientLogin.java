package client;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import res.LDPasswordField;
import res.LDTextField;

public class ClientLogin extends JFrame implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	
	JPanel panel=new JPanel();	
	LDTextField tf_account=new LDTextField(false,11,20);
	LDPasswordField tf_password=new LDPasswordField(11);
	JButton btn_login=new JButton("��¼");
	//JButton btn_exit=new JButton("�˳�");
	JLabel lb_account=new JLabel("    �˺�");
	JLabel lb_password=new JLabel("    ����");
	JLabel lb_msg=new JLabel("     ");
	Font font1=new Font("����",Font.BOLD,20);
	
	Client client;
	
	public  ClientLogin (Client client) {
		super("��¼");
		
		this.client=client;
		this.client.setEnabled(false);
		setSize(380,400);
		setResizable(false);
		
		//����������������Ļ�м�
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension screenSize=kit.getScreenSize();
		setLocation((screenSize.width-getWidth())/2, (screenSize.height-getHeight())/2);
				
		tf_account.setFont(font1);
		tf_password.setFont(font1);
		btn_login.setFont(font1);
		lb_account.setFont(font1);
		lb_password.setFont(font1);
		lb_msg.setFont(font1);

		lb_msg.setPreferredSize(new Dimension(300,50));	
		
		panel.add(lb_account);
		panel.add(tf_account);
		panel.add(lb_password);
		panel.add(tf_password);
		panel.add(lb_msg);
		panel.add(btn_login);
		//panel.add(btn_exit);	
		add("Center",panel);
		
		btn_login.addActionListener(this);
		//btn_exit.addActionListener(this);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
		addWindowListener(new WindowAdapter() { 
	        public void windowClosing(WindowEvent e) {	 
	        	client.setEnabled(true);
			    dispose();			        	  	        
	        }
	   });
		
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==btn_login || e.getSource()==tf_password ) {
			//���������Ƿ�Ϊ��
			if(tf_account.getText().equals("")||String.valueOf(tf_password.getPassword()).equals(""))
				JOptionPane.showConfirmDialog(null, "�˻����벻��Ϊ��", "ȷ��", JOptionPane.CLOSED_OPTION);
			else
				client.sendMsg("%%LOGIN#"+tf_account.getText()+"#"+String.valueOf(tf_password.getPassword()));
		}

	}

	//��ȡClient�ഫ���Ĳ���
	public void receiver(String msg1) {
		if(msg1.equals("%%FAILLOGIN#")) {
			lb_msg.setText("��¼ʧ��");
		}
		
		else if(msg1.equals("%%SUCCESSLOGIN#")) {
			client.setEnabled(true);
			client.mySetEnabled(client.panel2);
			client.cancelEnabled(client.panel1);
			client.btn_logout.setEnabled(true);
			client.isLogin=true;
			this.dispose();
		}
		else if(msg1.equals("%%FULLUSER#")) {
			JOptionPane.showConfirmDialog(null, "������������", "ȷ��", JOptionPane.CLOSED_OPTION);
		}
	}

	
}
