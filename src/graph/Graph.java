package graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Graph<T> {
	
	Set<Node> nodes = new HashSet<Node>();

	public Node addNode(T var) {
		Node n = new Node(var);
		for(Node node : nodes) {
			if(node.equals(n))
				return node;
		}
		nodes.add(n);
		return n;
	}
	
	public void addEdge(Node a, Node b) {
		a.addEdge(new Edge(a, b));
	}
	
	public void addConnections(T a, Set<T> b) {
		Node node = addNode(a);
		for(T element : b) {
			addEdge(node, addNode(element));
		}
	}
	
	public void setConnections(T a, Set<T> b) {
		Node node = addNode(a);
		node.getEdges().clear();
		for(T element : b)
			addEdge(node, addNode(element));
	}
	
	public void remove(T n) {
		Node node = null;
		for(Iterator<Node> it = nodes.iterator(); it.hasNext();) {
			node = it.next();
			if(node.getData().equals(n)) {
				it.remove();
				return;
			}
		}
	}
	
	public Set<Node> getNodes() {
		return nodes;
	}
	
	public boolean isCyclic() {
		DFS();
		for(Node n : nodes) {
			for(Edge e : n.edges)
				if(e.getB().start <= e.getA().start && e.getA().finish <= e.getB().finish)
					return true;
		}
		return false;
	}
	
	private int DFS(Node n, int time) {
		n.start = time;
		for(Edge e : n.edges) {
			if(e.getB().start == 0)
				time = DFS(e.getB(), time+1);
		}
		n.finish = ++time;
		return time;
	}
	
	public void DFS() {
		for(Node n : nodes)
			n.start = n.finish = 0;
		int time = 0;
		for(Node n : nodes)
			if(n.start==0)
				time = DFS(n, time+1);
	}
	
	public class Edge {
		Node a, b;
		Edge(Node a, Node b) {
			this.a = a;
			this.b = b;
		}
		
		public Node getA() {
			return a;
		}
		
		public Node getB() {
			return b;
		}
	}
	
	public class Node {
		T var;
		Set<Edge> edges;
		public int start, finish;
		
		Node(T var) {
			this.var = var;
			edges = new HashSet<>(4);
		}
		
		public T getData() {
			return var;
		}
		
		public void addEdge(Edge e) {
			edges.add(e);
		}
		
		public Set<Edge> getEdges() {
			return edges;
		}
		
		@Override
		public String toString() {
			return var.toString();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object e) {
			if(e instanceof Graph.Node)
				return ((Graph<T>.Node) e).getData().equals(var);
			return false;
		}
		
		@Override
		public int hashCode() {
			return var.hashCode();
		}
	}
}