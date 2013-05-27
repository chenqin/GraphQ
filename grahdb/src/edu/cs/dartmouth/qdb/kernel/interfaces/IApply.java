package edu.cs.dartmouth.qdb.kernel.interfaces;

import edu.cs.dartmouth.qdb.kernel.D;
import edu.cs.dartmouth.qdb.kernel.Vertex;

public interface IApply {
	/**
	 * apply delta to vertex self, this should be a atom operation
	 * where delta may lead to consistency problems
	 * @param v
	 * @param delta
	 */
	public void apply(Vertex v, D delta);
}
