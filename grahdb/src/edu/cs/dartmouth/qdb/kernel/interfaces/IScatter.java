package edu.cs.dartmouth.qdb.kernel.interfaces;

import java.util.Collection;

import edu.cs.dartmouth.qdb.kernel.D;
import edu.cs.dartmouth.qdb.kernel.Edge;
import edu.cs.dartmouth.qdb.kernel.Vertex;

public interface IScatter {
	/**
	 * transmit what has been changed in vertex self to surrounding
	 * verteices instead of message a replica
	 * @param u vertex that scatter change
	 * @param delta the change
	 * @param edges edges that connect to vertex u, also provide ref to vertex in other end of edge
	 * @param otherVertices vertices that are not connected with edges
	 */
	public void scater(Vertex u, D delta, Collection<Edge> edges, Collection<Vertex> otherVertices);
	
	/**
	 * transmit what's change in vertex self to surrounding where connecitons 
	 * are huge and spare across partitions
	 * @param newu
	 * @param uv
	 * @param v
	 * @return
	 */
	public Edge deltascater(Vertex newu, Edge uv, Vertex v);
	
}
