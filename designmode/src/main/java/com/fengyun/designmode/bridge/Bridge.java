package fengyun.designmode.bridge;

public abstract class Bridge {
	
	public Sourceable source;
	public void method(){
		source.method();
	}
	public Sourceable getSource(){
		return source;
	}
	public void setSource(Sourceable source){
		this.source = source;
	}
}
