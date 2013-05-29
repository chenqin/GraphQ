package edu.cs.dartmouth.gdbkernel.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.cs.dartmouth.qdb.kernel.D;
import edu.cs.dartmouth.qdb.kernel.Edge;
import edu.cs.dartmouth.qdb.kernel.Graph;
import edu.cs.dartmouth.qdb.kernel.Vertex;
import edu.cs.dartmouth.qdb.kernel.interfaces.IApply;
import edu.cs.dartmouth.qdb.kernel.interfaces.IGather;
import edu.cs.dartmouth.qdb.kernel.interfaces.IScatter;

public class GooglePageRankTest {
	static File input;
	static Ggraph g;
	Logger log;

	public GooglePageRankTest() {
		input = new File("src/edu/cs/dartmouth/gdbkernel/test/web-Google.txt");
		g = new Ggraph();
		log = Logger.getLogger(GooglePageRankTest.class.getName());
	}
	
	@Before
	public void setUp(){
		Assert.assertNotNull(input);
		LinkedList<String> records = new LinkedList<String>();
		
		try {
			FileReader reader = new FileReader(input);
			int read = reader.read();
			while(read > -1){
				StringBuilder sb = new StringBuilder();
				while('\r' != (char)read && '\n' != (char)read && read > -1){
					sb.append((char)read);
					read = reader.read();
				}
				records.add(sb.toString());
				read = reader.read();
			}
			
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info(String.valueOf(records.size()));
		
		for(String s : records) parse(s);
	}
	
	private void parse(String linestr){
		if(linestr.length() == 0 || linestr.charAt(0) == '#') return;
		String[] vertice = linestr.split("\t");
		String from = vertice[0];
		String to = vertice[1];
		
		g.addV(from);
		g.addV(to);
		g.addE(new Link(g.getSite(from),g.getSite(to)));
	}
	
	static class Ggraph extends Graph{
		
		private static final long serialVersionUID = 8848560555934539562L;
		
		public Ggraph(){
			
		}
		
		Site[] sitebin = new Site[8000000];
		
		public Vertex getSite(String str){
			return sitebin[Integer.valueOf(str)];
		}
		
		public void addV(String sitenum){
			if(sitebin[Integer.valueOf(sitenum)] == null){
				Site s = new Site(sitenum);
				sitebin[Integer.valueOf(sitenum)] = s;
				this.addV(s);
			}
		}
	};
	
	static class Site extends Vertex{
		int sitenum;
		float rank;
		public Site(String from){
			sitenum = Integer.valueOf(from);
			this.set(D.getNewTP(), new Float(1f));
		}
		public void setRank(float rank){
			this.rank = rank;
			this.set(D.getNewTP(), new Float(rank));
		}
		public float getRank(){
			return rank;
		}
	};
	
	static class Link extends Edge{
		public Link(Vertex from, Vertex to){
			super(from,to,DIRECTION.direct);
			this.set(D.getNewTP(), 1f);
		}
		
		public void setRatio(Float ratio){
			this.set(D.getNewTP(),ratio);
		}
		
		public Float getRatio(){
			return (Float) this.getValue();
		}
	};
	
	public static class PageRankGather implements IGather{

		@Override
		public D gather(Vertex self, Collection<Edge> edges,
				Collection<Vertex> othervertices) {
			D d = new D();
			d.setValue(new Float(0));
			
			float sum = 0;
			for(Edge e : edges){
				if(e.getConnectingVertex(self) != null) continue;
				Link l = (Link) e;
				sum += l.getRatio();
			}
			for(Edge e : edges){
				if(e.getConnectingVertex(self) != null) continue;
				Link l = (Link)e;
				l.setRatio(l.getRatio()/sum);
				d.setValue(new Float((Float)d.getValue()+((Float)(e.getStart().getValue()))*l.getRatio()));
			}
			return d;
		}

		@Override
		public D deltaGather(Vertex self, Map<Edge, Vertex> gmap) {
			// TODO Auto-generated method stub
			return null;
		}
		
	};
	
	public static class PageRankApply implements IApply{

		@Override
		public void apply(Vertex v, D delta) {
			Site s = (Site) v;
			s.setRank((Float)(s.getRank()*0.85f+0.15f*(Float)delta.getValue()));
		}
		
	};
	
	public static class PageRankScatter implements IScatter{

		@Override
		public void scater(Vertex u, D delta, Collection<Edge> edges,
				Collection<Vertex> otherVertices) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Edge deltascater(Vertex newu, Edge uv, Vertex v) {
			// TODO Auto-generated method stub
			return null;
		}
		
	};
	
	
	/**
	 * took me around 5G memory to construct the graph
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@Test
	public void loadGraph() throws InstantiationException, IllegalAccessException{
		log.info(String.valueOf(g.edges.size()));
		log.info(String.valueOf(g.vertices.size()));
		
		log.info("start simulation one iteration"+D.getNewTP().toLocaleString());
		
		while(true){
			D d = null;
			log.info("vertices size is " + String.valueOf(g.vertices.size()));
			for(Vertex s : g.vertices){
				d = g.Gather(s, PageRankGather.class);
				g.Apply(s, d, PageRankApply.class);
				g.Scatter(s, d, PageRankScatter.class);
			}
			
			log.info("one iteration done "+D.getNewTP().toLocaleString()+ " " +String.valueOf((Float)d.getValue()));
		}
	}
	
	@After
	public void tearDown(){
		
	}
	
	

}
