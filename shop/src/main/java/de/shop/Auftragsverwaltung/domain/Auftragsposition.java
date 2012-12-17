package de.shop.Auftragsverwaltung.domain;

import de.shop.Artikelverwaltung.domain.*;
import de.shop.Util.IdGroup;
import static de.shop.Util.Constants.KEINE_ID;
import static de.shop.Util.Constants.LONG_ANZ_ZIFFERN;
import static de.shop.Util.Constants.MIN_ID;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.TemporalType.TIMESTAMP;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import java.math.BigDecimal;
import java.net.URI;


/**
 * The persistent class for the auftragsposition database table.
 * 
 */
@Entity
@Table(name = "auftragsposition")
@NamedQueries({
	@NamedQuery(name = Auftragsposition.FIND_AUFTRAGSPOSITION_BY_ID, 
			query = "SELECT a"
					+ " FROM Auftragsposition a"
					+ " WHERE a.id = :" + Auftrag.PARAM_ID)
})
@XmlRootElement
public class Auftragsposition implements Serializable {
	private static final long serialVersionUID = 3112053858805093020L;
	
	private static final String PREFIX = "Auftragsposition.";
	public static final String FIND_AUFTRAGSPOSITION_BY_ID = PREFIX + "findAuftragspositionById";
	
	public static final String PARAM_ID = "id";	
	private static final int ANZAHL_MIN = 1;

	@Id
	@GeneratedValue
	@Column(name = "auftragsposition_ID", nullable = false, updatable = false, precision = LONG_ANZ_ZIFFERN)
	@Min(value = MIN_ID, message = "{auftragsverwaltung.auftragsposition.id.min}", groups = IdGroup.class)
	@XmlAttribute
	private Long id = KEINE_ID;

	@Min(value = ANZAHL_MIN, message = "{artikelverwaltung.auftragsposition.anzahl.min}")
	@XmlElement
	private int anzahl;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "artikel_FID", nullable = false)
	@NotNull(message = "{auftragsverwaltung.auftragsposition.artikel.notNull}")
	@XmlTransient
	private Artikel artikel;
	
	@Transient
	@XmlElement(name = "artikel", required=true)
	private URI artikelUri;

	@XmlElement
	private BigDecimal preis;

	public Auftragsposition() {
	}
	
	public Auftragsposition(Artikel artikel, int anzahl) {
		this.artikel = artikel;
		this.anzahl = anzahl;
//		this.preis = artikel.getPreis() * this.anzahl; //TODO: Preis berechnen
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
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
						id, anzahl, artikel, preis);
	}	
}