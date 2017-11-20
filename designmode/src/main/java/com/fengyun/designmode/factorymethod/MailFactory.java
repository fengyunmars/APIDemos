package fengyun.designmode.factorymethod;

import fengyun.designmode.mode.MailSender;
import fengyun.designmode.mode.Sender;

public class MailFactory implements Provider{

	@Override
	public Sender produce() {
		// TODO Auto-generated method stub
		return new MailSender();
	}

}
