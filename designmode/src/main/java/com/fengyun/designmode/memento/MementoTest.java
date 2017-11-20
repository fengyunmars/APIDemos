package fengyun.designmode.memento;

public class MementoTest {
	
	public static void main(String[] args) {
		Original origi = new Original("egg");
		Storage storage = new Storage(origi.createMemento());
		System.out.println("初始化状态为：" + origi.getValue());
		origi.setValue("niu");
		System.out.println("修改后的状态为：" + origi.getValue());
		
		origi.restoreMemento(storage.getMemento());
		System.out.println("恢复后的状态为：" + origi.getValue());
		
		
				
	}
}
