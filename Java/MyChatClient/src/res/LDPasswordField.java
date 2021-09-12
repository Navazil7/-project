package res;

import java.awt.event.InputMethodEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.AttributedCharacterIterator;

import javax.swing.JPasswordField;

import res.MyDocument;

public class LDPasswordField extends JPasswordField{
	
	int maxLength;
	
	public LDPasswordField(int l) {
		super(20);
		maxLength=l;
		setDocument(new MyDocument(maxLength));	
		addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {				
				char c=e.getKeyChar();
				if(c>127) {
					e.consume();
				}
				if( getPassword().length >= maxLength || ! (Character.isDigit(c)||Character.isLetter(c)) ) {  //只允许数字和字母输入		
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
