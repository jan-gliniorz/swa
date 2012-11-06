package de.shop.temp;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the lagerposition database table.
 * 
 */
@Entity
@Table(name ="Lagerposition")
public class Lagerposition implements Serializable {
	
	@ManyToOne(optional = false)
	@JoinTable(name = "blabla",joinColumns = {
	@JoinColumn(name = "lager_FID", nullable = false,
				insertable = false, updatable = false),
	
	@JoinColumn(name = "artikel_FID", nullable = false,
				insertable = false, updatable = false)}
	)
	
	private static final long serialVersionUID = 1L;

	private Lager lager;
	
	public Lager getLager(){
		return lager;
	}
	
	public void setLager(Lager lager){
		this.lager = lager;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int lagerposition_ID;

	private int anzahl;

	private int artikel_FID;

	@Column(name="erstellt_am")
	private Timestamp erstelltAm;

	@Column(name="geaendert_am")
	private Timestamp geaendertAm;

	private int lager_FID;

	public Lagerposition() {
	}

	public int getLagerposition_ID() {
		return this.lagerposition_ID;
	}

	public void setLagerposition_ID(int lagerposition_ID) {
		this.lagerposition_ID = lagerposition_ID;
	}

	public int getAnzahl() {
		return this.anzahl;
	}

	public void setAnzahl(int anzahl) {
		this.anzahl = anzahl;
	}

	public int getArtikel_FID() {
		return this.artikel_FID;
	}

	public void setArtikel_FID(int artikel_FID) {
		this.artikel_FID = artikel_FID;
	}

	public Timestamp getErstelltAm() {
		return this.erstelltAm;
	}

	public void setErstelltAm(Timestamp erstelltAm) {
		this.erstelltAm = erstelltAm;
	}

	public Timestamp getGeaendertAm() {
		return this.geaendertAm;
	}

	public void setGeaendertAm(Timestamp geaendertAm) {
		this.geaendertAm = geaendertAm;
	}

	public int getLager_FID() {
		return this.lager_FID;
	}

	public void setLager_FID(int lager_FID) {
		this.lager_FID = lager_FID;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + anzahl;
		result = prime * result + artikel_FID;
		result = prime * result
				+ ((erstelltAm == null) ? 0 : erstelltAm.hashCode());
		result = prime * result
				+ ((geaendertAm == null) ? 0 : geaendertAm.hashCode());
		result = prime * result + lager_FID;
		result = prime * result + lagerposition_ID;
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
		Lagerposition other = (Lagerposition) obj;
		if (anzahl != other.anzahl)
			return false;
		if (artikel_FID != other.artikel_FID)
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
		if (lager_FID != other.lager_FID)
			return false;
		if (lagerposition_ID != other.lagerposition_ID)
			return false;
		return true;
	}

}