package edu.cs.dartmouth.qdb.kernel;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import edu.cs.dartmouth.qdb.kernel.interfaces.IApply;
import edu.cs.dartmouth.qdb.kernel.interfaces.IGather;
import edu.cs.dartmouth.qdb.kernel.interfaces.IScatter;

public class Graph implements Serializable{
	
	private static final long serialVersionUID = -983186075335916922L;
	
	public Set<Vertex> vertices = null;
	public Set<Edge> edges = null;
	
	public Graph(){
		vertices = new TreeSet<Vertex>();
		edges = new TreeSet<Edge>();
	}
	
	public void addV(Vertex node){
		vertices.add(node);
	}
	
	public void addE(Edge e){
		edges.add(e);
	}
	
	public void removeV(Vertex node){
		vertices.remove(node);
		
		for(Edge e : node.edges){
			e.unlink();
		}
	}
	
	public void removeE(Edge e){
		edges.remove(e);
		e.unlink();
	}
	
	/**
	 * output all Vertices regards to its type
	 * @param start
	 * @return
	 */
	public Collection<Vertex> BFSearch(Vertex start){
		if(start == null) return null;
		Collection<Vertex> output = new TreeSet<Vertex>();
		Vertex currentnode = start;
		Queue<Vertex> que = new LinkedList<Vertex>();
		que.add(currentnode);
		output.add(currentnode);
		
		while(!que.isEmpty()){
			currentnode = que.remove();
			Collection<Edge> edgesavailabe = currentnode.findEdge(Edge.class);
			for(Edge e: edgesavailabe){
				Vertex endnode = e.findEndNode(Vertex.class);
				if(endnode != null && output.add(endnode)){
					que.add(endnode);
				} 
			}
		}
		return output;
	}
	
	public Collection<Vertex> DFSearch(Vertex start){
		if(start == null) return null;
		Stack<Vertex> stack = new Stack<Vertex>();
		Collection<Vertex> output = new TreeSet<Vertex>();
		stack.push(start);
		output.add(start);
		while(!stack.isEmpty()){
			Collection<Edge> adedges = stack.peek().findEdge(Edge.class);
			int count = adedges.size();
			for(Edge e: adedges){
				if(e.findEndNode(Vertex.class) != null && 
						output.add(e.findEndNode(Vertex.class))){
					stack.add(e.findEndNode(Vertex.class));
					break;
				}else{
					count--;
				}
			}
			if(count == 0) stack.pop();
		}
		return output;
	}
	
	/**
	 * calculate delta given gatherclass
	 * @param gatherclass
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public D Gather(Vertex self,Class<? extends IGather> gatherclass) throws InstantiationException, IllegalAccessException{
		IGather gatherinst = gatherclass.newInstance();
		
		return gatherinst.gather(self, self.findEdge(Edge.class),null);
	}
	
	public void Apply(Vertex self, D delta, Class<? extends IApply> applyclass) throws InstantiationException, 
																	IllegalAccessException{
		IApply aa = applyclass.newInstance();
		aa.apply(self,delta);
	}
	
	public void Scatter(Vertex self,D delta, Class<? extends IScatter> scaterclass) throws InstantiationException, IllegalAccessException{
		IScatter as = scaterclass.newInstance();
		as.scater(self,delta, null, null);
	}
}