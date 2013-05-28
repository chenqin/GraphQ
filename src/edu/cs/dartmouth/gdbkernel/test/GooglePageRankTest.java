package edu.cs.dartmouth.gdbkernel.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.cs.dartmouth.qdb.kernel.Edge;
import edu.cs.dartmouth.qdb.kernel.Graph;
import edu.cs.dartmouth.qdb.kernel.Vertex;

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
		public Site(String from){
			this.val = Integer.valueOf(from);
		}
	};
	
	static class Link extends Edge{
		public Link(Vertex from, Vertex to){
			super(from,to,DIRECTION.direct);
		}
	};
	
	
	@Test
	public void loadGraph(){
		log.info(String.valueOf(g.edges.size()));
		log.info(String.valueOf(g.vertices.size()));
	}
	
	@After
	public void tearDown(){
		
	}
	
	

}
