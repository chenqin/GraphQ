package edu.cs.dartmouth.qdb.kernel;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import edu.cs.dartmouth.qdb.kernel.Edge.DIRECTION;

public abstract class Vertex extends D{
	protected Set<Edge> edges = null;
	
	public Vertex(){
		
	}
	
	protected boolean hasEdge(){
		return (edges == null) || (edges.size() == 0);
	}
	
	/**
	 * add edge to vertice to speed lookup
	 * edge should be defined first here then add to vertice edge set
	 * @param e
	 * @throws Exception
	 */
	public void addEdge(Edge e) throws Exception{
		_initEdges();
		if(checkEdge(e) == false) new Exception("edge direction or start/end mismatch");
		if(edges.add(e) == false) new Exception("edge already added");
	}
	
	protected void _initEdges(){
		if(edges == null) edges = new TreeSet<Edge>();
	}
	protected boolean checkEdge(Edge e){
		if(e.start == this || e.end == this) return true;
		return false;
	}
	
	/**
	 * remove edge from node , reset edge vertice information 
 	 * and direction information 
	 * @param e
	 */
	protected void removeEdge(Edge e){
		if(edges == null) return;
		if(checkEdge(e)){ 
			edges.remove(e);
			if(e.start == this) e.start = null;
			if(e.end == this) e.end = null;
			e.dp = DIRECTION.bidirect;
		}
	}
	
	protected Collection<Edge> findEdge(Class<Edge> edgeclass){
		
		Collection<Edge> output = new LinkedList<Edge>();
		if(this.edges == null){
			
		}else for(Edge e : this.edges){
			if((edgeclass.equals(Edge.class) || edgeclass.equals(e.getClass())) 
					&& (e.start == this || e.dp == DIRECTION.bidirect)){
				output.add(e);
			}
		}
		return output;
	}
}
