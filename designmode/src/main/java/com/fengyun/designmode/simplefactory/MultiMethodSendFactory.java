package fengyun.designmode.simplefactory;

import fengyun.designmode.mode.MailSender;
import fengyun.designmode.mode.Sender;
import fengyun.designmode.mode.SmsSender;

public class MultiMethodSendFactory {
	
	public Sender produceMail(){
		
		return new MailSender();
	}
	
	public Sender produceSms(){
		return new SmsSender();
	}
	
}
