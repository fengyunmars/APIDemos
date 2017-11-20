package fengyun.designmode.adapter;

public class AdapterTest {
	
	public static void main(String[] args) {
		Targetable target = new Adapter();
		target.method1();
		target.method2();
		
		Source source = new Source();
		Targetable target2 = new Wrapper(source);
		target2.method1();
		target2.method2();
		
		Sourceable source1 = new SourceSub1();
		Sourceable source2 = new SourceSub2();
		source1.method1();
		source1.method2();
		source2.method1();
		source2.method2();
	}
}
