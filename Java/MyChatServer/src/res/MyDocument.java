package res;

import java.awt.event.InputMethodEvent;
import java.text.AttributedCharacterIterator;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class MyDocument extends PlainDocument{

	private int maxLength=10;
	public MyDocument(int maxlen) {
		// TODO 自动生成的构造函数存根
		super();
		maxLength=maxlen;
	}
	
	@Override
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		if(a==null) {
			int allowCount = maxLength-getLength();
			if(allowCount > 0) {
				if(allowCount < str.length() )
					str = str.substring(0,allowCount);
			}else{
				return;
			}
		}
		super.insertString(offs, str, a);
		
	}
	
	
}
