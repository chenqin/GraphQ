package edu.cs.dartmouth.qdb.kernel;

import java.sql.Timestamp;
import java.util.Date;

/**
 * This serves as basic mutable object in distributed graph system
 * @author chenqin
 *
 */
public class D implements Comparable<Object>, Cloneable{
	protected Date now = new Date();
	protected Timestamp tp;
	
	/**
	 * eact value of Object instance, null means dead object
	 */
	protected Object val;
	
	public D(){
		tp = new Timestamp(now.getTime());
	}
	
	public static Timestamp getNewTP(){
			return new Timestamp((new Date().getTime()));
	}

	@Override
	public int compareTo(Object o) {
		return this.hashCode() - o.hashCode();
	}
	
	public void setValue(Object newval){
		tp = new Timestamp(now.getTime());
		val = newval;
	}
	
	public final Object getValue(){
		return val;
	}
	
	protected void set(Timestamp t, Object newval){
		synchronized(val){
			if(t.after(tp)) val = newval;
			tp = t;
		}
	}
	
	protected final Timestamp time(){
		return tp;
	}
	
	protected final boolean upTick(Timestamp t){
		if(t.after(tp)) {
			tp = t;
			return true;
		}
		return false;
	}
	
	protected final Timestamp newTick(){
		Date newnow = new Date();
		return new Timestamp(newnow.getTime());
	}
	
	/**
	 * at moment t, O is dead, try to compare if this is latest status 
	 * and try to set val null
	 * @param t
	 */
	protected final void kill(Timestamp t){
		this.set(t, null);
	}
	
	public final boolean isDead(){
		return (val == null);
	}
}
