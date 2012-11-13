package de.shop.Auftragsverwaltung.domain;

import de.shop.Artikelverwaltung.domain.*;

import java.io.Serializable;
import javax.persistence.*;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigDecimal;


/**
 * The persistent class for the auftragsposition database table.
 * 
 */
@Entity
@Table(name = "auftragsposition")
public class Auftragsposition implements Serializable {
	private static final long serialVersionUID = 3112053858805093020L;

	@Id
	@GeneratedValue
	@Column(nullable = false, updatable = false)
	private int auftragsposition_ID;

	private int anzahl;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "artikel_FID", nullable = false)
	private Artikel artikel;

	private BigDecimal preis;

	public Auftragsposition() {
	}

	public int getAuftragsposition_ID() {
		return this.auftragsposition_ID;
	}

	public void setAuftragsposition_ID(int auftragsposition_ID) {
		this.auftragsposition_ID = auftragsposition_ID;
	}

	public int getAnzahl() {
		return this.anzahl;
	}

	public void setAnzahl(int anzahl) {
		this.anzahl = anzahl;
	}
	
	public Artikel getArtikel() {
		return artikel;
	}

	public void setArtikel(Artikel artikel) {
		this.artikel = artikel;
	}

	public BigDecimal getPreis() {
		return this.preis;
	}

	public void setPreis(BigDecimal preis) {
		this.preis = preis;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + anzahl;
		result = prime * result + auftragsposition_ID;
		result = prime * result + ((preis == null) ? 0 : preis.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Auftragsposition other = (Auftragsposition) obj;
		if (anzahl != other.anzahl) {
			return false;
		}
		if (auftragsposition_ID != other.auftragsposition_ID) {
			return false;
		}
		if (preis == null) {
			if (other.preis != null) {
				return false;
			}
		} else if (!preis.equals(other.preis)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("Auftragsposition [auftragsposition_ID=%s, anzahl=%s, artikel=%s, preis=%s]",
						auftragsposition_ID, anzahl, artikel, preis);
	}
	
	
}