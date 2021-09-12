package res;

import java.awt.event.InputMethodEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.AttributedCharacterIterator;


import javax.swing.JTextField;

import res.MyDocument;

public class LDTextField extends JTextField{
	
	private static final long serialVersionUID = 1L;
	
	boolean onlyInteger=false; //是否仅输入数字
	int maxLength;   //最大输入长度
	
	public LDTextField(boolean b,int maxlen,int len) {
		super(len);
		maxLength=maxlen;  
		setDocument(new MyDocument(maxLength));		//限制最大长度的方法
		onlyInteger=b;
		
		//键盘监听
		addKeyListener(new KeyAdapter() { 
			public void keyTyped(KeyEvent e) {				
				char c=e.getKeyChar();
				
				if(c>127) {
					e.consume();
				}
				
				if( getText().length() < maxLength ) {
					if( onlyInteger && !Character.isDigit(c) ){  
						e.consume();
					}else if( !(Character.isLetter(c)||Character.isDigit(c)) ) {   //只允许数字和字母输入
						e.consume();
					}else if( c >= '\u4E00' && c <= '\u9FA5' ){   //禁止输入中文 (经测定无效）
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
