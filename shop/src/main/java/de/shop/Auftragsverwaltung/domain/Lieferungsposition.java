package de.shop.Auftragsverwaltung.domain;

import de.shop.Artikelverwaltung.domain.*;

import java.io.Serializable;
import javax.persistence.*;
import java.util.*;

/**
 * The persistent class for the lieferungsposition database table.
 * 
 */
@Entity
@Table(name ="lieferungsposition")
public class Lieferungsposition implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne(optional = false)
	@JoinColumn(name = "lieferung_FID", nullable = false,
				insertable = false, updatable = false)

	private Lieferung lieferung;
	
	public Lieferung getLieferung(){
		return lieferung;
	}
	
	public void setLieferung(Lieferung lieferung){
		this.lieferung = lieferung;
	}
	
	@JoinColumn(name = "artikel_FID", nullable = false,
			insertable = false, updatable = false)
		
	private Artikel artikel;
	
	public Artikel getArtikel(){
		return artikel;
	}
	
	public void setArtikel(Artikel artikel){
		this.artikel = artikel;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int lieferungsposition_ID;

	private int anzahl;

	private int artikel_FID;

	private int lieferung_FID;

	public Lieferungsposition() {
	}

	public int getLieferungsposition_ID() {
		return this.lieferungsposition_ID;
	}

	public void setLieferungsposition_ID(int lieferungsposition_ID) {
		this.lieferungsposition_ID = lieferungsposition_ID;
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

	public int getLieferung_FID() {
		return this.lieferung_FID;
	}

	public void setLieferung_FID(int lieferung_FID) {
		this.lieferung_FID = lieferung_FID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + anzahl;
		result = prime * result
				+ ((lieferung == null) ? 0 : lieferung.hashCode());
		result = prime * result + lieferungsposition_ID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		Lieferungsposition other = (Lieferungsposition) obj;
		
		if (anzahl != other.anzahl)
			return false;
		
		if (lieferung == null) {
			if (other.lieferung != null)
				return false;
		} else if (!lieferung.equals(other.lieferung))
			return false;
		
		if (lieferungsposition_ID != other.lieferungsposition_ID)
			return false;
		
		return true;
	}

	@Override
	public String toString() {
		return "Lieferungsposition [artikel=" + artikel
				+ ", lieferungsposition_ID=" + lieferungsposition_ID
				+ ", anzahl=" + anzahl + "]";
	}

	
}