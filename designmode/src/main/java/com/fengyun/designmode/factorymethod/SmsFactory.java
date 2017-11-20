package fengyun.designmode.factorymethod;

import fengyun.designmode.mode.Sender;
import fengyun.designmode.mode.SmsSender;

public class SmsFactory implements Provider{

	@Override
	public Sender produce() {
		// TODO Auto-generated method stub
		return new SmsSender();
	}

}
