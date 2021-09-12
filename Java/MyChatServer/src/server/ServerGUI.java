package server;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.imageio.ImageTypeSpecifier;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.Segment;

import res.ChatTextField;

public class ServerGUI extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
	JLabel label=new JLabel("����㲥��Ϣ");
	JLabel label_userNum=new JLabel("�����û�0��"); 
	ChatTextField tf=new ChatTextField(30,15);
	//JTextField tf=new JTextField(30);
	JTextArea ta_sys=new JTextArea();
	JTextArea ta_msg=new JTextArea();
	JPanel panel1=new JPanel();
	JPanel panel2=new JPanel();//new FlowLayout(FlowLayout.LEFT)
	JPanel panelW=new JPanel(new FlowLayout(FlowLayout.LEFT));
	
	Integer a[]= {1,2,3,4,5,6,7,8,9,10,15,20};
	JComboBox<Integer> cb=new JComboBox<Integer>(a);
	JList<String> userList=new JList<String>();
	DefaultListModel<String> dlm = new DefaultListModel<String>();
	
	JButton btn_stopServer=new JButton("ֹͣ����");
	JButton btn_startServer=new JButton("��ʼ����");
	JButton btn_silence=new JButton("����");
	JButton btn_talk=new JButton("ȡ������");
	JButton btn_kickout=new JButton("�߳�������");
	JLabel lb_cb=new JLabel("�����������");
	
	JButton btn_send=new JButton("����");
	JScrollPane sp_ta1=new JScrollPane(ta_sys);
	JScrollPane sp_ta2=new JScrollPane(ta_msg);
	JScrollPane sp_userList=new JScrollPane(userList);
	
	
	Font font1=new Font("����",Font.BOLD,20);
	
	public ServerGUI(String title) {
		super(title);	
		//setLayout(new FlowLayout());
		setSize(1400,800);
		setResizable(false);
		
		panel1.add(lb_cb);
		panel1.add(cb);
		panel1.add(btn_startServer);
		panel1.add(btn_stopServer);
			
		Dimension preferredSize = new Dimension(500,70);
		panel2.setPreferredSize(preferredSize);
		panel2.add(label);
		panel2.add(tf);
		panel2.add(btn_send);
		panel2.add(label_userNum);
		
		panelW.setPreferredSize(new Dimension(350,70));
		panelW.add(sp_userList);
		panelW.add(btn_silence);
		panelW.add(btn_talk);
		panelW.add(btn_kickout);		

		userList.setFont(font1);
		lb_cb.setFont(font1);
		cb.setFont(font1);
		btn_stopServer.setFont(font1);
		btn_startServer.setFont(font1);
		btn_send.setFont(font1);   //��������	
		btn_kickout.setFont(font1);
		btn_silence.setFont(font1);
		btn_talk.setFont(font1);
		label.setFont(font1);
		//label_userNum.setFont(font1);
		tf.setFont(font1);
		sp_ta2.setPreferredSize(new Dimension(500,400));
		ta_msg.setFont(font1);
		ta_msg.setEditable(false); //���������Ϊֻ��
		
		sp_ta1.setPreferredSize(new Dimension(400,400));
		ta_sys.setFont(font1);
		ta_sys.setEditable(false); //���������Ϊֻ��
		
		sp_userList.setPreferredSize(new Dimension(345,600));
		
		add("North",panel1);
		add(BorderLayout.WEST,panelW);
		add(BorderLayout.CENTER,sp_ta1);
		add(BorderLayout.EAST,sp_ta2);	
		add("South",panel2);
		
		btn_stopServer.setEnabled(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
		
		userList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		tf.setDocument(new MyDocument(15));

//		tf.addKeyListener(new KeyAdapter(){
//			public void keyTyped(KeyEvent e) {				
//				//char c=e.getKeyChar();
//				if(tf.getText().length() > 15) {
//
//					e.consume();
//				}				
//			}
//		});
	}
}


