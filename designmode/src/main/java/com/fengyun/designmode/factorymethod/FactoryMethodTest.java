package fengyun.designmode.factorymethod;

import fengyun.designmode.mode.Sender;

public class FactoryMethodTest {
	
	public static void main(String[] args) {
		
		Provider provider = new MailFactory();
		Sender sender = provider.produce();
		sender.send();
	}
}
