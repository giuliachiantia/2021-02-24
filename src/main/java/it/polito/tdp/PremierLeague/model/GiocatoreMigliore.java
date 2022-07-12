package it.polito.tdp.PremierLeague.model;

public class GiocatoreMigliore {
	
	Player p;
	Double delta;
	public Player getP() {
		return p;
	}
	public void setP(Player p) {
		this.p = p;
	}
	public Double getDelta() {
		return delta;
	}
	public void setDelta(Double delta) {
		this.delta = delta;
	}
	public GiocatoreMigliore(Player p, Double delta) {
		super();
		this.p = p;
		this.delta = delta;
	}
	
	@Override
	public String toString() {
		return "GiocatoreMigliore [p=" + p + ", delta=" + delta + "]";
	}
	

}
