/**
 * 
 */
package edu.cs.dartmouth.gdbkernel.test;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.cs.dartmouth.qdb.kernel.D;
import edu.cs.dartmouth.qdb.kernel.Edge;
import edu.cs.dartmouth.qdb.kernel.Graph;
import edu.cs.dartmouth.qdb.kernel.Vertex;
import edu.cs.dartmouth.qdb.kernel.interfaces.IApply;
import edu.cs.dartmouth.qdb.kernel.interfaces.IGather;
import edu.cs.dartmouth.qdb.kernel.interfaces.IScatter;

/**
 * @author chenqin
 *
 */
public class GraphSimpleTest {
	private static Graph g;
	private static People alice, bob;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		g = new Graph();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	public static class People extends Vertex{
		public float factor = 2f;
	};
	
	public static class Book extends Vertex{
		public float factor = 1f;
	};
	
	public static class Like extends Edge{
		public float factor = 0.1f;
		
		public void setLike(People p, Book b){
			this.start = p;
			this.end = b;
			this.dp = DIRECTION.bidirect;
		    try {
				p.addEdge(this);
				b.addEdge(this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	};
	
	public static class SimilarTopic extends Edge{
		
		public SimilarTopic(Book b1, Book b2){
			super();
			this.start = b1;
			this.end = b2;
			this.dp = DIRECTION.bidirect;
		}
	};

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		alice = new People();
		bob = new People();
		
		Book b1 = new Book();
		Book b2 = new Book();
		
		Like l1 = new Like();
		Like l2 = new Like();
		Like l3 = new Like();
		
		SimilarTopic st = new SimilarTopic(b1,b2);
		
		l1.setLike(alice, b1);
		l2.setLike(alice, b2);
		l3.setLike(bob, b2);
		
		
		g.addE(l1);
		g.addE(l2);
		g.addE(l3);
		g.addE(st);
		
		g.addV(alice);
		g.addV(bob);
		g.addV(b1);
		g.addV(b2);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		g.edges.clear();
		g.vertices.clear();
	}

	@Test
	public void testDFSearch() {
		Assert.assertEquals(g.DFSearch(alice).size(), 3);
	}
	
	static class Accumu extends D{
		public float factor = 0;
	};
	
	public static class PageRankGather implements IGather{
		@Override
		public D gather(Vertex self, Collection<Edge> edges,Collection<Vertex> others) {
			Accumu ac = new GraphSimpleTest.Accumu();
			if(self.isDead() || edges.isEmpty()) return ac;
			Iterator<Edge> ite = edges.iterator();
			float totalfactor = 0;
			while(ite.hasNext()){
				Edge ent = ite.next();
				Like e = (Like)ent;
				Book v = (Book) ent.getConnectingVertex(self);
				if(e.isDead() == false && v.isDead() == false){
					ac.factor += v.factor;
					totalfactor += v.factor;
				}
			}
			ac.factor /= totalfactor;
			return ac;
		}
		
		@Deprecated
		@Override
		public D deltaGather(Vertex self, Map<Edge, Vertex> gmap) {
			// TODO Auto-generated method stub
			return null;
		}

		
	};
	
	public static class PageRankApply implements IApply{
		@Override
		public void apply(Vertex v, D delta) {
			if(v instanceof People && delta instanceof Accumu){
				((People) v).factor = (float) (((People) v).factor*0.85
						+ ((Accumu)delta).factor*0.15);
			}
		}
		
	};
	
	public static class PageRankScater implements IScatter{

		@Override
		public void scater(Vertex u, D delta, Collection<Edge> edges, Collection<Vertex> otherVertices) {
			//TODO: this should be a message passing to rest of graph
		}

		@Override
		public Edge deltascater(Vertex newu, Edge uv, Vertex v) {
			// TODO Auto-generated method stub
			return null;
		}
		
	};
	
	@Test
	public void testGAS(){
		try {
			int count = 100;
			while(count-- > 0){
				D d = g.Gather(alice,PageRankGather.class);
				g.Apply(alice,d, PageRankApply.class);
				g.Scatter(alice, d, PageRankScater.class);
				
				d = g.Gather(bob, PageRankGather.class);
				g.Apply(bob, d,  PageRankApply.class);
				g.Scatter(bob, d, PageRankScater.class);
				
				//as bipartiate graph this should be hold
				Assert.assertEquals(alice.factor,bob.factor);
			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
