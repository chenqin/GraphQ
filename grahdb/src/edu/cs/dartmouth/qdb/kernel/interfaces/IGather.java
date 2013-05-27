package edu.cs.dartmouth.qdb.kernel.interfaces;

import java.util.Collection;
import java.util.Map;

import edu.cs.dartmouth.qdb.kernel.D;
import edu.cs.dartmouth.qdb.kernel.Edge;
import edu.cs.dartmouth.qdb.kernel.Vertex;

public interface IGather {
	
	/**
	 * gather information from connected edges and vertices as well as other vertices
	 * @param self vertex itself
	 * @param edges connected edges to vertex self and other end
	 * @param othervertices other vertices
	 * @return
	 */
	public D gather(final Vertex self,Collection<Edge> edges, Collection<Vertex> othervertices);
	
	/**
	 * allow delta calculation of affect factor over certain part of 
	 * adjacent edges where connections are huge and across partitions
	 * vertices
	 * @param self
	 * @param es
	 * @param vs
	 * @return
	 */
	public D deltaGather(final Vertex self, final Map<Edge, Vertex> gmap);
}