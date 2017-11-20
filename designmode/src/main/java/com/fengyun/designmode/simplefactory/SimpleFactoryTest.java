package fengyun.designmode.simplefactory;

import fengyun.designmode.mode.Sender;

public class SimpleFactoryTest {
	
	public static void main(String[] args) {
		
		GeneralSendFactory factory = new GeneralSendFactory();
		Sender sender = factory.produce("sms");
		sender.send();
		
		MultiMethodSendFactory factory2 = new MultiMethodSendFactory();
		factory2.produceMail().send();
		
		MultiStaticMethodSendFactory.produceMail().send();
	}
}
