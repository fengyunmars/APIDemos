package fengyun.designmode.state;

public class StateTest {
	
	public static void main(String[] args) {
		
		State state = new State();
		Context context = new Context(state);
		
		state.setValue("state1");
		context.method();
		
		state.setValue("state2");
		context.method();
	}
}
