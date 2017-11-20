package fengyun.designmode.visitor;


public class MyVisitor implements Visitor{

	@Override
	public void visit(Subject sub) {
		// TODO Auto-generated method stub
		System.out.println("visit the subject:" + sub.getSubject());
	}

}
