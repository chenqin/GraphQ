package edu.cs.dartmouth.qdb.kernel;

import java.sql.Timestamp;
import java.util.Date;
import java.util.TreeSet;

/**
 * serve as base class of edge
 * the semenatic of edge is defined by its children class name
 * 
 * @author chenqin
 *
 */
public abstract class Edge extends D{
	protected Vertex start;
	protected Vertex end;
	protected DIRECTION dp;
	protected Date now = new Date();
	protected Timestamp tp = new Timestamp(now.getTime());
	
	public static enum DIRECTION{
		direct,
		bidirect
	};
	public Edge(){
		
	}
	
	public Vertex getStart(){
		return start;
	}
	public Vertex getEnd(){
		return end;
	}
	
	public Vertex getConnectingVertex(Vertex u){
		if(u == this.start && u != this.end) return this.end;
		else if(this.dp == DIRECTION.bidirect){
			if(u == this.start)
				return this.end;
			else 
				return this.start;
		}
		return null;
	}
	
	public Edge(Vertex start, Vertex end, DIRECTION dp){
		this.start = start;
		this.end = end;
		this.dp = dp;
		if(start.edges == null) start.edges = new TreeSet<Edge>();
		start.edges.add(this);
		if(end.edges == null) end.edges = new TreeSet<Edge>();
		end.edges.add(this);
	}
	
	public void setStart(Vertex start, DIRECTION dp){
		if(this.start != null) new Exception("please remove start first");
		else{
			this.start = start;
			this.dp = dp;
		}
	}
	
	public void setEnd(Vertex end, DIRECTION dp){
		if(this.end != null) new Exception("please remove end first");
		else{
			this.end = end;
			this.dp = dp;
		}
	}
	
	public void link() throws Exception{
		start.addEdge(this);
		end.addEdge(this);
	}
	
	public void unlink(){
		start.removeEdge(this);
		end.removeEdge(this);
		
		//further reuse this resource
		this.start = null;
		this.end = null;
		this.dp = DIRECTION.bidirect;
	}
	
	public Vertex findEndNode(Class<Vertex> nodeclass){
		if(nodeclass.equals(end.getClass()) || nodeclass.equals(Vertex.class)) 
			return end;
		return null;
	}
}
