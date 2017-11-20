package fengyun.designmode.simplefactory;

import fengyun.designmode.mode.MailSender;
import fengyun.designmode.mode.Sender;
import fengyun.designmode.mode.SmsSender;

public class MultiStaticMethodSendFactory {
	
	public static Sender produceMail(){
		
		return new MailSender();
	}
	
	public static Sender produceSms(){
		return new SmsSender();
	}
	
}
