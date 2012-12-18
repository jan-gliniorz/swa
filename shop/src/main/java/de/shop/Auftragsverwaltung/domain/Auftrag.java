package de.shop.Auftragsverwaltung.domain;

import de.shop.Kundenverwaltung.domain.*;
import de.shop.Util.IdGroup;
import static de.shop.Util.Constants.KEINE_ID;
import static de.shop.Util.Constants.LONG_ANZ_ZIFFERN;
import static de.shop.Util.Constants.MIN_ID;

import java.io.Serializable;
import java.net.URI;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.hibernate.validator.constraints.*;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.TemporalType.TIMESTAMP;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the auftrag database table.
 * 
 */
@Entity
@Table(name = "auftrag")
@NamedQueries({
	@NamedQuery(name = Auftrag.FIND_AUFTRAG_BY_ID, 
			query = "SELECT a"
					+ " FROM Auftrag a"
					+ " WHERE a.id = :" + Auftrag.PARAM_ID),
	@NamedQuery(name = Auftrag.FIND_AUFTRAG_BY_KUNDE, 
				query = "SELECT a"
						+ " FROM Auftrag a"
						+ " WHERE a.kunde.id = :" + Auftrag.PARAM_KUNDEID)
})
@XmlRootElement
public class Auftrag implements Serializable {	
	private static final long serialVersionUID = 2465349694241738534L;
	
	private static final String PREFIX = "Auftrag.";
	public static final String FIND_AUFTRAG_BY_ID = PREFIX + "findAuftragById";
	public static final String FIND_AUFTRAG_BY_KUNDE = PREFIX + "findAuftragByKunde";
	
	public static final String PARAM_KUNDEID = "kundeId";
	public static final String PARAM_ID = "id";

	@Id
	@GeneratedValue
	@Column(name = "auftrag_ID", nullable = false, updatable = false, precision = LONG_ANZ_ZIFFERN)
	@Min(value = MIN_ID, message = "{auftragsverwaltung.auftrag.id.min}", groups = IdGroup.class)
	@XmlAttribute
	private Long id = KEINE_ID;
	
	@OneToMany(fetch = EAGER)
	@JoinColumn(name = "auftrag_FID", nullable = false)
	@OrderColumn(name = "idx")
	@NotEmpty(message = "{auftragsverwaltung.auftrag.auftragspositionen.notEmpty}")
	@Valid
	@XmlElementWrapper(name = "bestellpositionen", required = true)
	@XmlElement(name = "bestellposition", required = true)
	private List<Auftragsposition> auftragspositionen;
	
	@OneToOne(mappedBy = "auftrag")
	@XmlTransient
	private Rechnung rechnung;
	
	@Transient
	@XmlElement(name = "rechnung")
	private URI rechnungUri;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "kunde_FID", nullable = false, insertable = false, updatable = false)
	@XmlTransient
	private Kunde kunde;
	
	@Transient
	@XmlElement(name = "kunde", required = true)
	private URI kundeUri;

	@Column(name = "erstellt_am")
	@Temporal(TIMESTAMP)
	@XmlElement
	private Date erstelltAm;

	@Column(name = "geaendert_am")
	@Temporal(TIMESTAMP)
	@XmlElement
	private Date geaendertAm;

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
			this.auftragspositionen = new ArrayList<>(); //TODO: Warum hier keinen Typ angeben?
		}
		
		this.auftragspositionen.add(position);
		return this;
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