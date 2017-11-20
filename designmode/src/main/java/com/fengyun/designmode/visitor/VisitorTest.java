package fengyun.designmode.visitor;

public class VisitorTest {
	
	public static void main(String[] args) {
		
		Visitor visitor = new MyVisitor();
		Subject sb = new MySubject();
		
		sb.accept(visitor);
	}
}
