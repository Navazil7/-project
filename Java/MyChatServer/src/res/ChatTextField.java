package res;

import java.awt.event.InputMethodEvent;
import java.text.AttributedCharacterIterator;

import javax.swing.JTextField;

public class ChatTextField extends JTextField{

	private int composedLen;
	private int maxLength;
	
	public ChatTextField(int length,int maxLength) {
		super(length);
		this.maxLength=maxLength;
		setDocument(new MyDocument(maxLength));
	}
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
