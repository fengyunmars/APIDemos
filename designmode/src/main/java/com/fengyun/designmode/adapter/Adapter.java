package fengyun.designmode.adapter;

public class Adapter extends Source implements Targetable{

	@Override
	public void method2() {
		// TODO Auto-generated method stub
		System.out.println("this is the targetable method!");
	}
	
	public static void main(String[] args) {
		System.out.println("aaa");
	}
}
