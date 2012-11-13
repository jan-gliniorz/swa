package de.shop.Artikelverwaltung.domain;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * The persistent class for the lager database table.
 * 
 */
@Entity
@Table(name = "Lager")
public class Lager implements Serializable {	
	

	private static final long serialVersionUID = -2184864191060846895L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int lager_ID;

	@OneToMany(mappedBy = "lager")
	private List<Lagerposition> lagerpositionen;
	
	
	private String bezeichung;

	@Column(name="erstellt_am")
	@Temporal(TIMESTAMP)
	private Date erstelltAm;

	@Column(name="geaendert_am")
	@Temporal(TIMESTAMP)
	private Date geaendertAm;

	public Lager() {
	}

	public int getLager_ID() {
		return this.lager_ID;
	}

	public void setLager_ID(int lager_ID) {
		this.lager_ID = lager_ID;
	}

	public String getBezeichung() {
		return this.bezeichung;
	}

	public void setBezeichung(String bezeichung) {
		this.bezeichung = bezeichung;
	}

	public Date getErstelltAm() {
		return erstelltAm == null ? null : (Date) erstelltAm.clone();
	}

	public void setErstelltAm(Date erstelltAm) {
		this.erstelltAm = erstelltAm == null ? null : (Date) erstelltAm.clone();
	}

	public Date getGeaendertAm() {
		return geaendertAm == null ? null : (Date) geaendertAm.clone();
	}

	public void setGeaendertAm(Date geaendertAm) {
		this.geaendertAm = geaendertAm == null ? null : (Date) geaendertAm.clone();
	}
	
	public List<Lagerposition> getLagerpositionen(){
		return Collections.unmodifiableList(lagerpositionen);
	}
	
	public void setLagerposition(List<Lagerposition> lagerpositionen){
		if(this.lagerpositionen == null){
			this.lagerpositionen = lagerpositionen;
			return;
		}
	
	
	this.lagerpositionen.clear();
	if (lagerpositionen != null){
		this.lagerpositionen.addAll(lagerpositionen);
	}
	}
	
	public Lager addLagerpositionen(Lagerposition lagerposition){
		if(lagerpositionen == null){
			lagerpositionen = new ArrayList<>();
		}
		lagerpositionen.add(lagerposition);
		return this;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bezeichung == null) ? 0 : bezeichung.hashCode());
		result = prime * result
				+ ((erstelltAm == null) ? 0 : erstelltAm.hashCode());
		result = prime * result
				+ ((geaendertAm == null) ? 0 : geaendertAm.hashCode());
		result = prime * result + lager_ID;
		return result;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Lager other = (Lager) obj;
		if (bezeichung == null) {
			if (other.bezeichung != null)
				return false;
		} else if (!bezeichung.equals(other.bezeichung))
			return false;
		if (erstelltAm == null) {
			if (other.erstelltAm != null)
				return false;
		} else if (!erstelltAm.equals(other.erstelltAm))
			return false;
		if (geaendertAm == null) {
			if (other.geaendertAm != null)
				return false;
		} else if (!geaendertAm.equals(other.geaendertAm))
			return false;
		if (lager_ID != other.lager_ID)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Lager [lager_ID=" + lager_ID + ", bezeichung=" + bezeichung
				+ ", erstelltAm=" + erstelltAm + ", geaendertAm=" + geaendertAm
				+ "]";
	}
	
	

}