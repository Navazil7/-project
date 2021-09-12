package client;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import res.LDPasswordField;
import res.LDTextField;

public class ClientRegister extends Frame implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	
	JPanel panel=new JPanel();	
	LDTextField tf_name=new LDTextField(false,11,20);
	LDTextField tf_account=new LDTextField(false, 11,20);
	LDPasswordField tf_password=new LDPasswordField(11);
	LDPasswordField tf_password2=new LDPasswordField(11);
	JButton btn_register=new JButton("ȷ��");
	//JButton btn_exit=new JButton("�˳�");
	JLabel lb_name=new JLabel("    �ǳ�");
	JLabel lb_account=new JLabel("    �˺�");
	JLabel lb_password=new JLabel("    ����");
	JLabel lb_password2=new JLabel("ȷ������");
	JLabel lb_msg=new JLabel("");
	Font font1=new Font("����",Font.BOLD,20);
	
	Client client;
	
	public  ClientRegister (Client client) {
		super("ע��");
		
		this.client=client;
		this.client.setEnabled(false);
		setSize(380,400);
		setResizable(false);
		
		//����������������Ļ�м�
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension screenSize=kit.getScreenSize();
		setLocation((screenSize.width-getWidth())/2, (screenSize.height-getHeight())/2);
		
		tf_name.setFont(font1);;
		tf_account.setFont(font1);
		tf_password.setFont(font1);
		tf_password2.setFont(font1);
		btn_register.setFont(font1);
		//btn_exit.setFont(font1);
		lb_name.setFont(font1);
		lb_account.setFont(font1);
		lb_password.setFont(font1);
		lb_password2.setFont(font1);
		lb_msg.setFont(font1);

		lb_msg.setPreferredSize(new Dimension(300,50));
		
		panel.add(lb_name);
		panel.add(tf_name);
		panel.add(lb_account);
		panel.add(tf_account);
		panel.add(lb_password);
		panel.add(tf_password);
		panel.add(lb_password2);
		panel.add(tf_password2);
		panel.add(lb_msg);
		panel.add(btn_register);
		//panel.add(btn_exit);	
		add("Center",panel);
		
		btn_register.addActionListener(this);
		//btn_exit.addActionListener(this);
		
		//setDefaultCloseOperation(DISPOSE_ON_CLOSE);
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
		//���������Ƿ�Ϊ��
		if(tf_name.getText().equals("")
				||tf_account.getText().equals("")
				||String.valueOf(tf_password.getPassword()).equals("")
				||String.valueOf(tf_password2.getPassword()).equals("") )
			JOptionPane.showConfirmDialog(null, "���벻��Ϊ��", "ȷ��", JOptionPane.CLOSED_OPTION);
		
		//�����ȷ�����벻һ��
		else if(!String.valueOf(tf_password.getPassword()).equals(String.valueOf(tf_password2.getPassword())))
			JOptionPane.showConfirmDialog(null, "�������벻һ��", "ȷ��", JOptionPane.CLOSED_OPTION);
		//ע��ɹ�
		else {
			client.sendMsg("%%REGISTER#"+tf_name.getText()+
					"#"+tf_account.getText()+
					"#"+String.valueOf(tf_password.getPassword()));
			
		}
			
		
	}

	public void receiver(String msg1) {
		// TODO �Զ����ɵķ������
		if(msg1.equals("%%FAILREGISTER#NAME")) {
			lb_msg.setText("�û����Ѵ��ڣ�");
		}else if(msg1.equals("%%FAILREGISTER#ACCOUNT")) {
			lb_msg.setText("�˺��Ѵ��ڣ�");
		}else if(msg1.equals("%%SUCCESSREGISTER#")){
			JOptionPane.showConfirmDialog(null, "ע��ɹ���", "Oh yeah", JOptionPane.CLOSED_OPTION);
			client.setEnabled(true);
			this.dispose();
		}
	}
}
