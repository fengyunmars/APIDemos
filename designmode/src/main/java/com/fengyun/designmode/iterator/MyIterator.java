package fengyun.designmode.iterator;

public class MyIterator implements Iterator{
	
	private Collection collection;
	private int pos = -1;
	
	public MyIterator(Collection collection){
		this.collection = collection;
	}
	
	@Override
	public Object previous() {
		// TODO Auto-generated method stub
		if(pos > 0){
			pos--;
		}
		return collection.get(pos);
	}

	@Override
	public Object next() {
		// TODO Auto-generated method stub
		if(pos < collection.size() -1){
			pos++;
		}
		return collection.get(pos);
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		if(pos < collection.size() -1){
			return true;
		}else{
		return false;
		}
	}

	@Override
	public Object first() {
		// TODO Auto-generated method stub
		pos = 0;
		return collection.get(pos);
	}

}
