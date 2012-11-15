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
	
	@ManyToOne
	@JoinColumn(name="artikel_FID", nullable = false, insertable = false, updatable = false)
	private Artikel artikel;
	
	public Artikel getArtikel(){
		return artikel;
	}
	
	public void setArtikel(Artikel artikel){
		this.artikel = artikel;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	
	@Column (name= "lieferungsposition_ID")	private int id ;

	private int anzahl;

	public Lieferungsposition() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAnzahl() {
		return this.anzahl;
	}

	public void setAnzahl(int anzahl) {
		this.anzahl = anzahl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + anzahl;
		result = prime * result
				+ ((lieferung == null) ? 0 : lieferung.hashCode());
		result = prime * result + id;
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
		
		if (id != other.id)
			return false;
		
		return true;
	}

	@Override
	public String toString() {
		return "Lieferungsposition [artikel=" + artikel
				+ ", id=" + id
				+ ", anzahl=" + anzahl + "]";
	}

	
}