package fengyun.designmode.factorymethod;

import fengyun.designmode.mode.Sender;

public interface Provider {
	
	public Sender produce();
}
