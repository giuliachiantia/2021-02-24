package it.polito.tdp.PremierLeague.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private PremierLeagueDAO dao;
	private Graph<Player, DefaultWeightedEdge> grafo;
	private Map<Integer, Player> idMap;
	private Simulatore sim;
	
	public Model() {
		this.dao= new PremierLeagueDAO();
		this.idMap=new HashMap<>();
		this.dao.listAllPlayers(idMap);
		
	}
	
	public List<Match> getAllMatches(){
		List<Match>lista= dao.listAllMatches();
		Collections.sort(lista, new Comparator<Match>() {

			@Override
			public int compare(Match o1, Match o2) {
				// TODO Auto-generated method stub
				return o1.getMatchID().compareTo(o2.getMatchID());
				//o se no order by nella query e via
			}
			
		});
		return lista;
	}
	public void creaGrafo(Match m) {
		this.grafo=new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		//aggiungo i vertici
		Graphs.addAllVertices(this.grafo, this.dao.getVertici(m, idMap));
		//aggiungo archi
		for(Adiacenza a:dao.getAdiacenze(m, idMap)) {
			if((a.getPeso()>=0)) {
				//p1 meglio di p2
				if(grafo.containsVertex(a.getP1()) && grafo.containsVertex(a.getP2())) {
				Graphs.addEdgeWithVertices(this.grafo, a.getP1(), a.getP2(), a.getPeso());
				}
			} else {
				if(grafo.containsVertex(a.getP1()) && grafo.containsVertex(a.getP2())) {
				//p2 meglio di p1
				Graphs.addEdgeWithVertices(this.grafo, a.getP2(), a.getP1(), (-1)*a.getPeso());
			}
			}
			
		}
		System.out.println("Grafo Creato");
		
		
	}
	public int nVertici() {
		return this.grafo.vertexSet().size();
	}

	public int nArchi() {
		return this.grafo.edgeSet().size();
	}
	public GiocatoreMigliore getMigliore() {
		if(grafo==null) {
			return null;
		}
		Player best=null;
		double maxDelta=0; //oppure Integer.MIN_VALUE
		for(Player p:this.grafo.vertexSet()) {
			//calcolo somma pesi archi uscenti
			double pesoUscente=0.0;
			for(DefaultWeightedEdge e:this.grafo.outgoingEdgesOf(p)) {
				pesoUscente+=this.grafo.getEdgeWeight(e);
			}
			//calcolo somma pesi archi entranti
			double pesoEntrante=0.0;
			for(DefaultWeightedEdge e:this.grafo.incomingEdgesOf(p)) {
				pesoUscente+=this.grafo.getEdgeWeight(e);
			}
			double delta=pesoUscente-pesoEntrante;
			if(delta>maxDelta) {
				best=p;
				maxDelta=delta;
			}
			
		}
		return new GiocatoreMigliore(best, maxDelta);
	}

	public Graph<Player, DefaultWeightedEdge>getGrafo() {
		// TODO Auto-generated method stub
		return this.grafo;
	}
	
	public Integer teamIDPlayerBest(GiocatoreMigliore b) {
		return dao.getTeamID(b);
	}
	
	public Statistiche simula(int num, Match m) {
		GiocatoreMigliore g= getMigliore();
		sim.init(num, g, m);
		sim.run();
		return sim.getStatistiche();
	}
	
	
}
