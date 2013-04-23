package de.shop.Artikelverwaltung.domain;

import static de.shop.Util.Constants.ERSTE_VERSION;
import static de.shop.Util.Constants.KEINE_ID;
import static de.shop.Util.Constants.LONG_ANZ_ZIFFERN;
import static de.shop.Util.Constants.MIN_ID;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnore;

import de.shop.Util.IdGroup;


/**
 * The persistent class for the lagerposition database table.
 * 
 */
@Entity
@Table(name = "Lagerposition")
	@NamedQueries({
		@NamedQuery(name = Lagerposition.FIND_LAGERPOSITION_BY_ID, 
			query = "SELECT a " 
					+ "FROM Lagerposition a "
					+ "WHERE a.id = :" + Lagerposition.PARAM_ID),
		@NamedQuery(name = Lagerposition.FIND_LAGERPOSITION_ALL,
		    query = "SELECT lp FROM Lagerposition lp"),
		@NamedQuery(name = Lagerposition.FIND_LAGERPOSITION_BY_ARTIKEL,
		    query = "SELECT lp FROM Lagerposition lp WHERE lp.artikel.id = :" + Lagerposition.PARAM_ARTIKEL_ID)//,
//		@NamedQuery(name = Lagerposition.FIND_LAGERPOSITION_BY_LAGER,
//			query = "select lagerposition "
//					+ "from Lagerposition as lagerposition " 
//					+ "where lagerposition.lager.id = :" + Lagerposition.PARAM_LAGER_ID)
	
  })

public class Lagerposition implements Serializable {	
	
	private static final String PREFIX = "Lagerposition.";

//	public static final String FIND_LAGERPOSITION_BY_LAGER =
//		PREFIX + "findLagerpositionByLagerId";
	public static final String FIND_LAGERPOSITION_BY_ID =
		PREFIX + "findLagerpositionById";
	public static final String PARAM_ID = "id";
	public static final String FIND_LAGERPOSITION_ALL = 
		PREFIX + "findLagerpositionAll";
	public static final String FIND_LAGERPOSITION_BY_ARTIKEL =
		PREFIX + "findLagerpositionByArtikelId";
	public static final String PARAM_ARTIKEL_ID = "artikelId";
	public static final String PARAM_LAGER_ID = "lagerId";
	
	private static final long serialVersionUID = 6937895919585767805L;

	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "lagerposition_ID", nullable = false, updatable = false, precision = LONG_ANZ_ZIFFERN)
	@Min(value = MIN_ID, message = "artikelverwaltung.lagerposition.id.min", groups = IdGroup.class)
	private Long id = KEINE_ID;
	
//	@ManyToOne
//	@JoinColumn(name = "lager_FID", nullable = false)
//	@OrderColumn(name = "erstellt_am")
//	@NotNull(message = "artikelverwaltung.lagerposition.lager.notNull")
//	@JsonIgnore
//	private Lager lager;
	
//	@Transient
//	private URI lagerUri;

	@ManyToOne
	@JoinColumn(name = "artikel_FID", nullable = false)
	@OrderColumn(name = "erstellt_am")
	@NotNull(message = "artikelverwaltung.lagerposition.artikel.notNull")
	@JsonIgnore
	private Artikel artikel;
	
	@Transient
	private URI artikelUri;

	@Min(value = 1, message = "artikelverwaltung.lagerposition.anzahl.min")
	private int anzahl;

	@Column(name = "erstellt_am")
	@Temporal(TIMESTAMP)
	private Date erstelltAm;

	@Column(name = "geaendert_am")
	@Temporal(TIMESTAMP)
	private Date geaendertAm;


	public Lagerposition() {
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
//	public Lager getLager() {
//		return lager;
//	}
//	
//	public void setLager(Lager lager) {
//		this.lager = lager;
//	}
//	
//	public URI getLagerUri() {
//		return lagerUri;
//	}
//
//	public void setLagerUri(URI lagerUri) {
//		this.lagerUri = lagerUri;
//	}

	public void setArtikel(Artikel artikel) {
		this.artikel = artikel;
	}
	
	public Artikel getArtikel() {
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
		//lager = l.lager;
		artikel = l.artikel;
		anzahl = l.anzahl;
	}
	
	
	@PrePersist
	private void prePersist() {
		erstelltAm = new Date();
		geaendertAm = new Date();
	}
	
	@PreUpdate
	private void preUpdate() {
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
		} 
		else if (!artikel.equals(other.artikel))
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