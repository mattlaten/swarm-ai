package backend;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * An UnforgivingArrayList is an ArrayList that remembers that you've modified it.
 * To check whether the list has been modified, use the isDirty() function. The give
 * it a fresh start use the clean() function.
 *
 * @param <T>
 */
public class UnforgivingArrayList<T> extends ArrayList<T> implements Iterable<T>	{
	/**
	 * The elements that have been add/removed since the last clean
	 */
	ArrayList<T> removed, added;
	private boolean dirty = false;
	
	/**
	 * Constructor
	 * @param timeTaken The that this array list wa captured (can be 0)
	 */
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
	
	/**
	 * Clean this list, so that it won't be dirty anymore
	 */
	public void clean()	{
		dirty = false;
		removed.clear();
		added.clear();
	}
	
	/**
	 * Mark this list as dirty even if no elements have been added/removed
	 */
	public void stuffChanged()	{
		dirty = true;
	}
	
	/**
	 * Returns whether or not this list is dirty
	 * @return Whether or not this list is dirty
	 */
	public boolean isDirty()	{
		return dirty || removed.size() + added.size() > 0;
	}
}