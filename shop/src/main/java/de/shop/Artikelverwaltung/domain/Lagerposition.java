package de.shop.Artikelverwaltung.domain;

import static javax.persistence.TemporalType.TIMESTAMP;
import java.util.*;
import java.io.Serializable;
import java.net.URI;

import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.shop.Util.IdGroup;
import static de.shop.Util.Constants.KEINE_ID;
import static de.shop.Util.Constants.LONG_ANZ_ZIFFERN;
import static de.shop.Util.Constants.MIN_ID;

import java.sql.Timestamp;


/**
 * The persistent class for the lagerposition database table.
 * 
 */
@Entity
@Table(name ="Lagerposition")
@NamedQueries({
	@NamedQuery(name = Lagerposition.FIND_Lagerposition_BY_ID, 
			query = "SELECT a" +
					" FROM Lagerposition a"
					+ " WHERE a.id = :" + Lagerposition.PARAM_ID),
	@NamedQuery(name = Lagerposition.FIND_Lagerposition_ALL,
				    query = "SELECT lp FROM Lagerposition lp"),
	@NamedQuery(name = Lagerposition.FIND_LAGERPOSITION_BY_Artikel,
				    query = "SELECT lp FROM Lagerposition lp WHERE lp.artikel.id = :" + Lagerposition.PARAM_ARTIKEL_ID),
	@NamedQuery(name = Lagerposition.FIND_LAGERPOSITION_BY_LAGER,
					query = "select lagerposition "
							+ "from Lagerposition as lagerposition " 
							+ "where lagerposition.lager.id = :" + Lagerposition.PARAM_LAGER_ID)
	
  })
@XmlRootElement
public class Lagerposition implements Serializable {	
	
	private static final String PREFIX = "Lagerposition.";


	public static final String FIND_LAGERPOSITION_BY_LAGER =
		PREFIX + "findLagerpositionByLagerId";
	public static final String FIND_Lagerposition_BY_ID =
		PREFIX + "findLagerpositionById";
	public static final String PARAM_ID = "id";
	public static final String FIND_Lagerposition_ALL = 
		PREFIX + "findLagerpositionAll";
	public static final String FIND_LAGERPOSITION_BY_Artikel =
		PREFIX + "findLagerpositionByArtikelId";
	public static final String PARAM_ARTIKEL_ID = "artikelId";
	public static final String PARAM_LAGER_ID = "lagerId";


	
	private static final long serialVersionUID = 6937895919585767805L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "lagerposition_ID", nullable = false, updatable = false, precision = LONG_ANZ_ZIFFERN)
	@Min(value = MIN_ID, message = "artikelverwaltung.lagerposition.id.min", groups = IdGroup.class)
	@XmlAttribute
	private Long id = KEINE_ID;
	
	@ManyToOne
	@JoinColumn(name="lager_FID", nullable = false)
	@OrderColumn(name="erstellt_am")
	@NotNull(message = "artikelverwaltung.lagerposition.lager.notNull")
	@XmlTransient
	private Lager lager;
	
	@Transient
	@XmlElement(name = "lager", required = true)
	private URI lagerUri;

	@ManyToOne
	@JoinColumn(name="artikel_FID", nullable = false)
	@OrderColumn(name="erstellt_am")
	@NotNull(message = "artikelverwaltung.lagerposition.artikel.notNull")
	@XmlTransient
	private Artikel artikel;
	
	@Transient
	@XmlElement(name = "artikel", required = true)
	private URI artikelUri;

	@Min(value = 1, message = "artikelverwaltung.lagerposition.anzahl.min")
	@XmlElement
	private int anzahl;

	@Column(name="erstellt_am")
	@Temporal(TIMESTAMP)
	@XmlElement
	private Date erstelltAm;

	@Column(name="geaendert_am")
	@Temporal(TIMESTAMP)
	@XmlElement
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
	
	public URI getLagerUri() {
		return lagerUri;
	}

	public void setLagerUri(URI lagerUri) {
		this.lagerUri = lagerUri;
	}

	public void setArtikel(Artikel artikel){
		this.artikel = artikel;
	}
	
	public Artikel getArtikel(){
		return artikel;
	}
	
	public int getAnzahl() {
		return this.anzahl;
	}

	public void setAnzahl(int anzahl) {
		this.anzahl = anzahl;
	}
	
	public URI getArtikelUri() {
		return artikelUri;
	}

	public void setArtikelUri(URI artikelUri) {
		this.artikelUri = artikelUri;
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

	public void setValues(Lagerposition l) {
		lager = l.lager;
		artikel = l.artikel;
		anzahl = l.anzahl;
	}
	
	
	@PrePersist
	private void prePersist()
	{
		erstelltAm = new Date();
		geaendertAm = new Date();
	}
	
	@PreUpdate
	private void preUpdate()
	{
		geaendertAm = new Date();
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