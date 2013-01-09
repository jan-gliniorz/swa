package de.shop.Artikelverwaltung.domain;

import de.shop.Util.IdGroup;
import static de.shop.Util.Constants.KEINE_ID;
import static de.shop.Util.Constants.LONG_ANZ_ZIFFERN;
import static de.shop.Util.Constants.MIN_ID;

import java.io.Serializable;
import java.net.URI;

import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.TemporalType.TIMESTAMP;

/**
 * The persistent class for the lieferungsposition database table.
 * 
 */
@Entity
@Table(name ="lieferungsposition")
@NamedQueries({
	@NamedQuery(name = Lieferungsposition.LIEFERUNGSPOSITION_BY_ID, 
			query = "SELECT lp " +
					"FROM Lieferungsposition lp "+
					"WHERE lp.id = :"+Lieferungsposition.PARAM_ID),
	@NamedQuery(name = Lieferungsposition.LIEFERUNGSPOSITION_BY_ID_ARTIKEL,
			query = "SELECT DISTINCT lp" +
					" FROM Lieferungsposition lp" +
					" JOIN lp.artikel"+
					" WHERE lp.id = :" + Lieferungsposition.PARAM_ID),
})

public class Lieferungsposition implements Serializable {
	
	private static final long serialVersionUID = -895845732573641L;
	
	private static final String PREFIX = "Lieferungsposition.";
	public static final String LIEFERUNGSPOSITION_BY_ID = PREFIX + "findLieferungspositionById";
	public static final String LIEFERUNGSPOSITION_BY_ID_ARTIKEL = PREFIX  + "findLieferungspositionByIdArtikel";
	
	public static final String PARAM_ID = "id";
	
	@ManyToOne(fetch=FetchType.LAZY, optional = false)
	@JoinColumn(name="artikel_FID", nullable = false)
	@NotNull(message = "{artikelverwaltung.lieferungsposition.artikel.notNull}")
	@XmlTransient
	private Artikel artikel;
	
	@Transient
	@XmlElement(name = "artikel", required = true)
	private URI artikelUri;	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column (name= "lieferungsposition_ID", nullable = false, updatable = false, precision = LONG_ANZ_ZIFFERN)	
	@Min(value = MIN_ID, message = "{artikelverwaltung.lieferungsposition.id.min}", groups = IdGroup.class)
	@XmlAttribute
	private Long id=KEINE_ID ;
	
	@NotNull(message = "{artikelverwaltung.lieferungsposition.anzahl.notNull}")
	@XmlElement
	private int anzahl;

	public Lieferungsposition() {
	}
	
	public Artikel getArtikel(){
		return artikel;
	}
	
	public void setArtikel(Artikel artikel){
		this.artikel = artikel;
	}
	
	public URI getArtikelUri() {
		return artikelUri;
	}

	public void setArtikelUri(URI artikelUri) {
		this.artikelUri = artikelUri;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + anzahl;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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