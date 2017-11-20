package fengyun.designmode.strategy;

public class StrategyTest {
	
	public static void main(String[] args) {
		
		String exp = "2+8";
		ICalculator cal = new Plus();
		int result = cal.calculate(exp);
		System.out.println(exp + "=" + result);
		
		String exp1 = "2-8";
		ICalculator cal1 = new Minus();
		int result1 = cal1.calculate(exp1);
		System.out.println(exp1 + "=" + result1);
		
		String exp2 = "2*8";
		ICalculator cal2 = new Multiply();
		int result2 = cal2.calculate(exp2);
		System.out.println(exp2 + "=" + result2);
				
	}
}
