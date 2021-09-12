package client;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import res.ChatTextField;
import res.LDTextField;

//import com.sun.javafx.stage.WindowCloseRequestHandler;

public class ClientGUI extends JFrame{

	private static final long serialVersionUID = 1L;
	
	JPanel panel1=new JPanel(new FlowLayout(FlowLayout.LEFT));//
	JPanel panel2=new JPanel();
	
	JLabel label_ip=new JLabel("IP��ַ:");
	JLabel label_port=new JLabel("  �˿�:");
	JLabel label=new JLabel("����������Ϣ");
	JLabel label_userNum=new JLabel("------------");
	JLabel label_cb=new JLabel("���͸�");
	
	JComboBox<String> cb=new JComboBox<String>();	
	
	LDTextField tf_ip1=new LDTextField(true,3,3);
	//TextField tf_ip1=new TextField(3);
	LDTextField tf_ip2=new LDTextField(true,3,3);
	LDTextField tf_ip3=new LDTextField(true,3,3);
	LDTextField tf_ip4=new LDTextField(true,3,3);
	JTextField tf_port=new JTextField(4);
	ChatTextField tf=new ChatTextField(20,15);
	JTextArea ta=new JTextArea();

	JButton btn_connect=new JButton("����");
	JButton btn_logout=new JButton("ע��");
	JButton btn_login=new JButton("��¼");
	JButton btn_register=new JButton("ע��");
	JButton btn_send=new JButton("����");
	JScrollPane sp_ta=new JScrollPane(ta);
	
	Font font1=new Font("����",Font.BOLD,20);
	Font font2=new Font("����",Font.BOLD,17);
	
	public ClientGUI(String title) {
		super(title);	
		setSize(470,620);
		setResizable(false);
		
		//����������������Ļ�м�
		Toolkit kit=Toolkit.getDefaultToolkit();
		Dimension screenSize=kit.getScreenSize();
		setLocation((screenSize.width-getWidth())/2, (screenSize.height-getHeight())/2);
		
		panel1.add(label_ip);
//		try {
//			MaskFormatter mf1 = new MaskFormatter("###-###-###-###");
//			mf1.setPlaceholderCharacter('_');
//			JFormattedTextField ftf2 = new JFormattedTextField(mf1);
//			ftf2.setFont(font2);
//			panel1.add(ftf2);
//		} catch (ParseException e1) {			
//			e1.printStackTrace();
//		}				
		panel1.add(tf_ip1);
		panel1.add(tf_ip2);
		panel1.add(tf_ip3);
		panel1.add(tf_ip4);
		panel1.add(label_port);
		panel1.add(tf_port);
		panel1.add(btn_connect);
		panel1.add(new JLabel("          "));
		
		panel1.add(btn_logout);
		panel1.add(btn_login);
		panel1.add(btn_register);
				
		panel1.setPreferredSize(new Dimension(500,85));
		panel2.setPreferredSize(new Dimension(500,85));
		panel2.add(label);
		panel2.add(tf);
		panel2.add(btn_send);
		panel2.add(label_cb);
		panel2.add(cb);
		panel2.add(label_userNum);
//		Dimension preferredSize = new Dimension(300,100);//���óߴ�
//		bt_send.setPreferredSize(preferredSize);
		
		label_cb.setFont(font2);
		cb.setFont(font2);
		label_ip.setFont(font2);
		label_port.setFont(font2);
		tf_ip1.setFont(font2);
		tf_ip2.setFont(font2);
		tf_ip3.setFont(font2);
		tf_ip4.setFont(font2);		
		tf_port.setFont(font2);
		btn_connect.setFont(font1);
		tf_ip1.setText("127");
		tf_ip2.setText("0");
		tf_ip3.setText("0");
		tf_ip4.setText("1");
		tf_port.setText("8080");
		
		btn_logout.setFont(font1);
		btn_login.setFont(font1);
		btn_register.setFont(font1);
		btn_send.setFont(font1);   //��������	
		
		label.setFont(font1);
		label_userNum.setFont(font1);

		tf.setFont(font1);
		ta.setFont(font1);
		ta.setEditable(false); //���������Ϊֻ��
		
		add("North",panel1);
		add("South",panel2);
		add("Center",sp_ta);
		
		cb.addItem("������");
		btn_logout.setEnabled(false);
		btn_login.setEnabled(false);
		btn_register.setEnabled(false);
		cancelEnabled(panel2);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
		
		//ip��ַ�����ļ��̼����¼�ע��
		tf_ip1.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				// TODO �Զ����ɵķ������
				if(tf_ip1.getText().length()==2 && Character.isDigit(e.getKeyChar())) {
					tf_ip2.requestFocus(true);
				}
				//ta.append(tf_ip1.getCaretPosition()+"\n");//
				if(e.getKeyCode()==KeyEvent.VK_LEFT && (tf_ip1.getCaretPosition()==tf_ip1.getText().length() ) ) {
					tf_ip2.requestFocus(true);
				}
//				if(e.getKeyCode()==KeyEvent.VK_LEFT) {
//					tf_ip4.setText(tf_ip1.getCaretPosition()+"");
//				}
			}
		});		
		tf_ip2.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				// TODO �Զ����ɵķ������
				if(tf_ip2.getText().length()==2 && Character.isDigit(e.getKeyChar())) {
					tf_ip3.requestFocus(true);
				}
//				else if(tf_ip2.getText().length()==0 
//						&& e.getKeyChar()==KeyEvent.VK_BACK_SPACE ) {
//					tf_ip1.requestFocus(true);
//				}
				
				if(e.getKeyCode()==KeyEvent.VK_LEFT && (tf_ip2.getCaretPosition()==tf_ip2.getText().length() ) ) {
					tf_ip3.requestFocus(true);
				}
				else if(e.getKeyCode()==KeyEvent.VK_RIGHT && (tf_ip2.getCaretPosition()==0 )) {
					tf_ip1.requestFocus(true);
				}
			}
		});
		tf_ip3.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				// TODO �Զ����ɵķ������
				if(tf_ip3.getText().length()==2 && Character.isDigit(e.getKeyChar())) {
					tf_ip4.requestFocus(true);
				}
//				else if(tf_ip3.getText().length()==0 
//						&& e.getKeyChar()==KeyEvent.VK_BACK_SPACE  ) {
//					tf_ip2.requestFocus(true);
//				}
				
				if(e.getKeyCode()==KeyEvent.VK_LEFT && (tf_ip3.getCaretPosition()==tf_ip3.getText().length() ) ) {
					tf_ip4.requestFocus(true);
				}else if(e.getKeyCode()==KeyEvent.VK_RIGHT && (tf_ip3.getCaretPosition()==0 )) {
					tf_ip2.requestFocus(true);
				}
			}
		});
		tf_ip4.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				// TODO �Զ����ɵķ������
//				if(tf_ip4.getText().length()==0 && e.getKeyChar()==KeyEvent.VK_BACK_SPACE ) {
//					tf_ip3.requestFocus(true);
//				}
				
				if(e.getKeyCode()==KeyEvent.VK_LEFT && (tf_ip4.getCaretPosition()==tf_ip4.getText().length() ) ) {
					tf_port.requestFocus(true);
				}else if(e.getKeyCode()==KeyEvent.VK_RIGHT && (tf_ip4.getCaretPosition()==0 )) {
					tf_ip3.requestFocus(true);
				}
			}
		});
	}
		
	//ͳһ����JPanel����ϵĿؼ��Ƿ����
	public void mySetEnabled(JPanel p) {
		Component[] components=p.getComponents();
		for(Component i:components) {
			i.setEnabled(true);
		}
	}
	
	public void cancelEnabled(JPanel p) {
		Component[] components=p.getComponents();
		for(Component i:components) {
			i.setEnabled(false);
		}
	}


}
