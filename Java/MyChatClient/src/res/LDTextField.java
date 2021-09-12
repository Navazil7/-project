package res;

import java.awt.event.InputMethodEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.AttributedCharacterIterator;


import javax.swing.JTextField;

import res.MyDocument;

public class LDTextField extends JTextField{
	
	private static final long serialVersionUID = 1L;
	
	boolean onlyInteger=false; //�Ƿ����������
	int maxLength;   //������볤��
	
	public LDTextField(boolean b,int maxlen,int len) {
		super(len);
		maxLength=maxlen;  
		setDocument(new MyDocument(maxLength));		//������󳤶ȵķ���
		onlyInteger=b;
		
		//���̼���
		addKeyListener(new KeyAdapter() { 
			public void keyTyped(KeyEvent e) {				
				char c=e.getKeyChar();
				
				if(c>127) {
					e.consume();
				}
				
				if( getText().length() < maxLength ) {
					if( onlyInteger && !Character.isDigit(c) ){  
						e.consume();
					}else if( !(Character.isLetter(c)||Character.isDigit(c)) ) {   //ֻ�������ֺ���ĸ����
						e.consume();
					}else if( c >= '\u4E00' && c <= '\u9FA5' ){   //��ֹ�������� (���ⶨ��Ч��
						e.consume();
					}
				}
				else {
					e.consume();
				}				

			}
		});
	}
	
	private int composedLen;
	@Override
	protected void processInputMethodEvent(InputMethodEvent e) {
		if (e.getID() == InputMethodEvent.INPUT_METHOD_TEXT_CHANGED) {
			if (e.getCommittedCharacterCount() == 0) {
				AttributedCharacterIterator aci = e.getText();
				if (getDocument().getLength() - composedLen >= maxLength) {
					e.consume();
					composedLen = 0;
				} else
					composedLen = aci != null ? aci.getEndIndex()
							- aci.getBeginIndex() : 0;
			} else
				composedLen = 0;
		}
		super.processInputMethodEvent(e);
	}
}
