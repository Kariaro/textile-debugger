package textile.utils;

public class DirectList<T> {
	private transient Object[] list;
	private int count;
	
	public DirectList() {
		this.list = new Object[16];
	}
	
	public void add(T element) {
		final int len = list.length;
		
		if(count + 1 >= len) {
			Object[] array = new Object[len * (len >>> 2)];
			System.arraycopy(list, 0, array, 0, len);
			list = array;
		}
		
		list[count++] = element;
	}
	
	public int size() {
		return count;
	}
	
	public void resetIndex() {
		count = 0;
	}
	
	public T get(int index) {
		if(index >= list.length) return null;
		return (T)list[index];
	}
}
