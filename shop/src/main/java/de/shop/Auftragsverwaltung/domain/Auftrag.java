package de.shop.Auftragsverwaltung.domain;

import static de.shop.Util.Constants.ERSTE_VERSION;
import static de.shop.Util.Constants.KEINE_ID;
import static de.shop.Util.Constants.LONG_ANZ_ZIFFERN;
import static de.shop.Util.Constants.MIN_ID;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlElement;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.NotEmpty;

import de.shop.Kundenverwaltung.domain.Kunde;
import de.shop.Util.IdGroup;


/**
 * The persistent class for the auftrag database table.
 * 
 */
@Entity
@Table(name = "auftrag")
@NamedQueries({
	@NamedQuery(name = Auftrag.FIND_AUFTRAG_ALL, 
			query = "SELECT a"
					+ " FROM Auftrag a"),
	@NamedQuery(name = Auftrag.FIND_AUFTRAG_BY_ID, 
			query = "SELECT a"
					+ " FROM Auftrag a"
					+ " WHERE a.id = :" + Auftrag.PARAM_ID),
	@NamedQuery(name = Auftrag.FIND_AUFTRAG_BY_KUNDE, 
				query = "SELECT a"
						+ " FROM Auftrag a"
						+ " WHERE a.kunde.id = :" + Auftrag.PARAM_KUNDEID)
})
public class Auftrag implements Serializable {	
	private static final long serialVersionUID = 2465349694241738534L;
	
	private static final String PREFIX = "Auftrag.";
	public static final String FIND_AUFTRAG_BY_ID = PREFIX + "findAuftragById";
	public static final String FIND_AUFTRAG_ALL = PREFIX + "findAuftragByAll";
	public static final String FIND_AUFTRAG_BY_KUNDE = PREFIX + "findAuftragByKunde";
	
	public static final String PARAM_KUNDEID = "kundeId";
	public static final String PARAM_ID = "id";

	@Id
	@GeneratedValue
	@Column(name = "auftrag_ID", nullable = false, updatable = false, precision = LONG_ANZ_ZIFFERN)
	@Min(value = MIN_ID, message = "{auftragsverwaltung.auftrag.id.min}", groups = IdGroup.class)
	private Long id = KEINE_ID;
	
	@OneToMany(fetch = EAGER, cascade = PERSIST)
	@JoinColumn(name = "auftrag_FID", nullable = false)
	@OrderColumn(name = "idx")
	@NotEmpty(message = "{auftragsverwaltung.auftrag.auftragspositionen.notEmpty}")
	@Valid
	private List<Auftragsposition> auftragspositionen;
	
	@OneToOne(mappedBy = "auftrag")
	@JsonIgnore
	private Rechnung rechnung;

	@Transient
	@XmlElement(name = "rechnung")
	private URI rechnungUri;

	@ManyToOne(optional = false)
	@JoinColumn(name = "kunde_FID", nullable = false, updatable = false)
	@JsonIgnore
	private Kunde kunde;
	
	@Transient
	@XmlElement(name = "kunde", required = true)
	private URI kundeUri;

	@Column(name = "erstellt_am")
	@Temporal(TIMESTAMP)
	private Date erstelltAm;

	@Column(name = "geaendert_am")
	@Temporal(TIMESTAMP)
	private Date geaendertAm;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;

	public Auftrag() {
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getErstelltAm() {
		return this.erstelltAm == null ? null : (Date) erstelltAm.clone();
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

	public Kunde getKunde() {
		return this.kunde;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}
	
	public URI getKundeUri() {
		return kundeUri;
	}

	public void setKundeUri(URI kundeUri) {
		this.kundeUri = kundeUri;
	}

	public List<Auftragsposition> getAuftragspositionen() {
		return Collections.unmodifiableList(auftragspositionen);
	}

	public void setAuftragspositionen(List<Auftragsposition> auftragspositionen) {
		if (this.auftragspositionen == null) {
			this.auftragspositionen = auftragspositionen;
			return;
		}
		
		this.auftragspositionen.clear();
		if (auftragspositionen != null) {
			this.auftragspositionen = auftragspositionen;
		}
	}
	
	public Auftrag addAuftragsposition(Auftragsposition position) {
		
		if (this.auftragspositionen == null) {
			this.auftragspositionen = new ArrayList<>();
		}
		
		this.auftragspositionen.add(position);
		return this;
	}
	
	public Rechnung getRechnung() {
		return rechnung;
	}

	public void setRechnung(Rechnung rechnung) {
		this.rechnung = rechnung;
	}
	
	public URI getRechnungUri() {
		return rechnungUri;
	}

	public void setRechnungUri(URI rechnungUri) {
		this.rechnungUri = rechnungUri;
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
				+ ((erstelltAm == null) ? 0 : erstelltAm.hashCode());
		result = prime * result
				+ ((geaendertAm == null) ? 0 : geaendertAm.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Auftrag other = (Auftrag) obj;
		if (erstelltAm == null) {
			if (other.erstelltAm != null) {
				return false;
			}
		} 
		else if (!erstelltAm.equals(other.erstelltAm)) {
			return false;
		}
		if (geaendertAm == null) {
			if (other.geaendertAm != null) {
				return false;
			}
		}
		else if (!geaendertAm.equals(other.geaendertAm)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		}
		else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("Auftrag [auftrag_ID=%s, rechnung=%s, erstelltAm=%s, geaendertAm=%s, kunde=%s]",
						id, rechnung, erstelltAm, geaendertAm, kunde);
	}	
}