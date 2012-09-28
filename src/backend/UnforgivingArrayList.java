package backend;

import java.util.ArrayList;
import java.util.Iterator;

public class UnforgivingArrayList<T> extends ArrayList<T> implements Iterable<T>	{
	ArrayList<T> removed, added;
	
	public UnforgivingArrayList(int timeTaken)	{
		removed = new ArrayList<T>();
		added = new ArrayList<T>();
	}
	
	public boolean add(T element)	{
		synchronized(this){
			added.add(element);
			return super.add(element);
		}
	}
	
	public T remove(int i)	{
		synchronized(this){
			T t = super.remove(i);
			removed.add(t);
			return t;
		}
	}
	
	public boolean remove(Object element)	{
		synchronized(this){
			boolean b = super.remove(element);
			if(b)
				removed.add((T)element);
			return b;
		}
	}
	
	public void clear()	{
		synchronized(this){
			removed.addAll(this);
			super.clear();
		}
	}
	
	public void clean()	{
		removed.clear();
		added.clear();
	}
	
	public boolean isDirty()	{
		return removed.size() + added.size() > 0;
	}
}