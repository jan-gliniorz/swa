package de.shop.Artikelverwaltung.domain;

import static javax.persistence.TemporalType.TIMESTAMP;
import java.util.*;
import java.io.Serializable;
import javax.persistence.*;

import static de.shop.Util.Constants.KEINE_ID;
import static de.shop.Util.Constants.LONG_ANZ_ZIFFERN;

import java.sql.Timestamp;


/**
 * The persistent class for the lagerposition database table.
 * 
 */
@Entity
@Table(name ="Lagerposition")
public class Lagerposition implements Serializable {
	
	private static final long serialVersionUID = 6937895919585767805L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "lagerposition_ID", nullable = false, updatable = false, precision = LONG_ANZ_ZIFFERN)
	private Long id = KEINE_ID;
	
	@ManyToOne
	@JoinColumn(name="lager_FID", nullable = false)
	@OrderColumn(name="erstellt_am")
	private Lager lager;
	
	@ManyToOne
	@JoinColumn(name="artikel_FID", nullable = false)
	@OrderColumn(name="erstellt_am")
	private Artikel artikel;

	private int anzahl;

	@Column(name="erstellt_am")
	@Temporal(TIMESTAMP)
	private Date erstelltAm;

	@Column(name="geaendert_am")
	@Temporal(TIMESTAMP)
	private Date geaendertAm;


	public Lagerposition() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Lager getLager(){
		return lager;
	}
	
	public void setLager(Lager lager){
		this.lager = lager;
	}

	public int getAnzahl() {
		return this.anzahl;
	}

	public void setAnzahl(int anzahl) {
		this.anzahl = anzahl;
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


	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artikel == null) ? 0 : artikel.hashCode());
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
		if (artikel == null) {
			if (other.artikel != null)
				return false;
		} else if (!artikel.equals(other.artikel))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Lagerposition [lagerposition_ID=" + id
				+ ", artikel=" + artikel + ", anzahl=" + anzahl
				+ ", erstelltAm=" + erstelltAm + ", geaendertAm=" + geaendertAm
				+ "]";
	}

	
	
}