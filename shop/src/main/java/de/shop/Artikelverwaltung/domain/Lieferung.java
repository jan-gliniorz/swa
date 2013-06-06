package de.shop.Artikelverwaltung.domain;

import static de.shop.Util.Constants.KEINE_ID;
import static de.shop.Util.Constants.LONG_ANZ_ZIFFERN;
import static de.shop.Util.Constants.MIN_ID;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.logging.Logger;

import de.shop.Util.IdGroup;


/**
 * The persistent class for the lieferung database table.
 * 
 */
@Entity
@Table(name = "lieferung")
@NamedQueries({
	@NamedQuery(name = Lieferung.LIEFERUNG_BY_ID, 
			query = "SELECT li"
					+ " FROM Lieferung li"
					+ " WHERE li.id = :" + Lieferung.PARAM_ID),
	@NamedQuery(name = Lieferung.LIEFERUNG_BY_ID_LIEFERUNGSPOSITIONEN,
			query = "SELECT DISTINCT li"
					+ " FROM Lieferung li"
					+ " JOIN li.lieferungspositionen lp"
//					+ " JOIN FETCH lp.artikel"
					+ " WHERE li.id = :" + Lieferung.PARAM_ID),
	@NamedQuery(name = Lieferung.LIEFERUNG_BY_BESTELLDATUM,
			query = "SELECT li"
					+ " FROM Lieferung li"
					+ " WHERE li.bestelldatum = :" + Lieferung.PARAM_BESTELLDATUM),
	@NamedQuery(name  = Lieferung.FIND_LIEFERUNG_BY_ID_PREFIX,
			        query = "SELECT   li"
			                + " FROM  Lieferung li"
			                + " WHERE CONCAT('', li.id) LIKE :" + Lieferung.PARAM_ID
			                + " ORDER BY li.id"),
	@NamedQuery(name = Lieferung.LIEFERUNGEN_ALL,
    		query = "SELECT li FROM Lieferung li")
})
public class Lieferung implements Serializable {
	
	private static final long serialVersionUID = -4645734623734341L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final String PREFIX = "Lieferung.";
	public static final String LIEFERUNG_BY_ID = PREFIX + "findLieferungById";
	public static final String LIEFERUNG_BY_ID_LIEFERUNGSPOSITIONEN = PREFIX 
							   + "findLieferungByIdFetchLieferungspositionen";
	public static final String FIND_LIEFERUNG_BY_ID_PREFIX = PREFIX + "findLieferungByIDPrefix";
	public static final String LIEFERUNG_BY_BESTELLDATUM = PREFIX + "findLieferungByBestelldatum";
	public static final String LIEFERUNGEN_ALL = PREFIX + "findLieferungenAll";
	
	public static final String PARAM_ID = "id";
	public static final String PARAM_BESTELLDATUM = "bestelldatum";
	
	@OneToMany (fetch = EAGER, cascade = PERSIST)
	@JoinColumn(name = "lieferung_FID", nullable = false)
	@OrderColumn(name = "idx")
	@NotEmpty(message = "{artikelverwaltung.lieferung.lieferungspositionen.notEmpty}")
	@NotNull
	@Valid
	private List<Lieferungsposition> lieferungspositionen;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "lieferung_ID", nullable = false, updatable = false, precision = LONG_ANZ_ZIFFERN)
	@Min(value = MIN_ID, message = "{artikelverwaltung.lieferung.id.min}", groups = IdGroup.class)
	private Long id = KEINE_ID;

	@Temporal(TemporalType.DATE)
	@NotNull(message = "{artikelverwaltung.lieferung.bestelldatum.notNull}")
	private Date bestelldatum;

	@Temporal(TemporalType.DATE)
	private Date lieferungsdatum;
	
	@Column(name = "erstellt_am")
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date erstelltAm;

	@Column(name = "geaendert_am")
	@Temporal(TIMESTAMP)
	@JsonIgnore
	private Date geaendertAm;

	@Version
	@Basic(optional = false)
	private int version = 0;
	
	public Lieferung() {
	}
	
	public List<Lieferungsposition> getLieferungspositionen() {
		if (lieferungspositionen == null)
			return null;
		return Collections.unmodifiableList(lieferungspositionen);
	}
	
	public void setLieferungspositionen(List<Lieferungsposition> lieferungspositionen) {
		if (this.lieferungspositionen == null) {
			this.lieferungspositionen = lieferungspositionen;
			return;
		}
		
		this.lieferungspositionen.clear();
		if (lieferungspositionen != null) {
			this.lieferungspositionen = lieferungspositionen;
		}
		
	}	
	
	public Lieferung addLieferungsposition(Lieferungsposition lieferungsposition) {
		
		if (lieferungspositionen == null) {
			lieferungspositionen = new ArrayList<>();
		}
		
		lieferungspositionen.add(lieferungsposition);
		return this;
	}
	
	public Lieferung removeLieferungsposition(Lieferungsposition lieferungsposition) {
		lieferungspositionen.remove(lieferungsposition);
		return this;
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getBestelldatum() {
		return bestelldatum == null ? null : (Date) bestelldatum.clone();
	}

	public void setBestelldatum(Date bestelldatum) {
		this.bestelldatum = bestelldatum == null ? null : (Date) bestelldatum.clone();
	}
	
	public Date getLieferungsdatum() {
		return lieferungsdatum == null ? null : (Date) lieferungsdatum.clone();
	}

	public void setLieferungsdatum(Date lieferungsdatum) {
		this.lieferungsdatum = lieferungsdatum == null ? null : (Date) lieferungsdatum.clone();
	}
	
	public Date getErstelltAm() {
		return this.erstelltAm == null ? null : (Date) this.erstelltAm.clone();
	}

	public void setErstelltAm(Date erstelltAm) {
		this.erstelltAm = erstelltAm == null ? null : (Date) erstelltAm.clone();
	}

	public Date getGeaendertAm() {
		return this.geaendertAm == null ? null : (Date) this.geaendertAm.clone();
	}

	public void setGeaendertAm(Date geaendertAm) {
		this.geaendertAm = geaendertAm == null ? null : (Date) geaendertAm.clone();
	}

	public void setValues(Lieferung lieferung) {
		lieferungsdatum = lieferung.lieferungsdatum;
		bestelldatum = lieferung.bestelldatum; 
	}
	
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	@PostPersist
	protected void postPersist() {
		LOGGER.debugf("Neue Lieferung mit ID=%d", id);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bestelldatum == null) ? 0 : bestelldatum.hashCode());
		result = prime * result
				+ ((erstelltAm == null) ? 0 : erstelltAm.hashCode());
		result = prime * result
				+ ((geaendertAm == null) ? 0 : geaendertAm.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((lieferungsdatum == null) ? 0 : lieferungsdatum.hashCode());
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
		
		final Lieferung other = (Lieferung) obj;
		
		if (bestelldatum == null) {
			if (other.bestelldatum != null)
				return false;
		} 
		else if (!bestelldatum.equals(other.bestelldatum))
			return false;
		
		if (erstelltAm == null) {
			if (other.erstelltAm != null)
				return false;
		} 
		else if (!erstelltAm.equals(other.erstelltAm))
			return false;
		
		if (geaendertAm == null) {
			if (other.geaendertAm != null)
				return false;
		} 
		else if (!geaendertAm.equals(other.geaendertAm))
			return false;
		
		if (lieferungsdatum == null) {
			if (other.lieferungsdatum != null)
				return false;
		} 
		else if (!lieferungsdatum.equals(other.lieferungsdatum))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "Lieferung [id=" + id + ", bestelldatum="
				+ bestelldatum + ", erstelltAm=" + erstelltAm
				+ ", geaendertAm=" + geaendertAm + ", lieferungsdatum="
				+ lieferungsdatum + "]";
	}
}
