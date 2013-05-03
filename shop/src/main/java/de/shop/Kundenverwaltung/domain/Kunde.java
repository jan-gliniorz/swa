package de.shop.Kundenverwaltung.domain;

import static de.shop.Util.Constants.ERSTE_VERSION;
import static de.shop.Util.Constants.KEINE_ID;
import static de.shop.Util.Constants.LONG_ANZ_ZIFFERN;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.validator.constraints.Email;
import org.jboss.logging.Logger;

import de.shop.Auftragsverwaltung.domain.Auftrag;

/**
 * The persistent class for the kunde database table.
 * 
 */
@Entity
@Table(name = "kunde")
@NamedQueries({
@NamedQuery(name = Kunde.KUNDE_BY_NACHNAME,
			query = "FROM Kunde k WHERE k.nachname = :" + Kunde.PARAM_NACHNAME),
@NamedQuery(name = Kunde.KUNDE_BY_EMAIL,
			query = "FROM Kunde k WHERE k.email = :" + Kunde.PARAM_EMAIL),
@NamedQuery(name = Kunde.KUNDE_BY_PLZ,
			query = "FROM Kunde k WHERE k.adresse.plz = :" + Kunde.PARAM_PLZ),
@NamedQuery(name = Kunde.KUNDE_BY_KNR,
			query = "FROM Kunde k WHERE k.kundenNr = :" + Kunde.PARAM_KUNDENNUMMER),
@NamedQuery(name  = Kunde.KUNDEN_ALL,
            query = "SELECT k FROM Kunde k"),
@NamedQuery(name = Kunde.KUNDE_BY_NAME_AUFTRAEGE,
		query = "SELECT DISTINCT k"
					+ " FROM Kunde k"
					+ " LEFT JOIN FETCH k.auftraege"
					+ " WHERE k.nachname = :" + Kunde.PARAM_NACHNAME),
@NamedQuery(name = Kunde.KUNDE_BY_ID_AUFTRAEGE,
			query = "SELECT DISTINCT k"
					+ " FROM Kunde k"
					+ " LEFT JOIN FETCH k.auftraege"
					+ " WHERE k.kundenNr = :" + Kunde.PARAM_KUNDENNUMMER)
})
public class Kunde implements Serializable {
	
	private static final long serialVersionUID = 3925016425151715847L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final String PREFIX = "Kunde.";
	public static final String KUNDEN_ALL = PREFIX + "findKundenAll";
	public static final String KUNDE_BY_EMAIL = PREFIX + "findKundenByEmail";
	public static final String KUNDE_BY_NACHNAME = PREFIX + "findKundenByNachname";
	public static final String KUNDE_BY_PLZ = PREFIX + "findKundenByPlz";
	public static final String KUNDE_BY_KNR = PREFIX + "findKundenByKundennummer";
	public static final String KUNDE_BY_NAME_AUFTRAEGE = PREFIX + "findKundenByNachnameFetchAufraege";
	public static final String KUNDE_BY_ID_AUFTRAEGE = PREFIX + "findKundenByIdFetchAufraege";
	
	public static final String PARAM_PLZ = "plz";
	public static final String PARAM_NACHNAME = "nachname";
	public static final String PARAM_KUNDENNUMMER = "kundenNr";
	public static final String PARAM_EMAIL = "email";
	
	@Id
	@GeneratedValue
	@Column(nullable = false, updatable = false, precision = LONG_ANZ_ZIFFERN)
	private Long kundenNr = KEINE_ID;
	
	@Version
	@Basic(optional = false)
	private int version = ERSTE_VERSION;
	
	@Email
	@NotNull(message = "{kundenverwaltung.kunde.email.notNull}")
	private String email;

	@Column(name = "erstellt_am")
	@Temporal(TIMESTAMP)
	private Date erstelltAm;

	@Column(name = "geaendert_am")
	@Temporal(TIMESTAMP)
	private Date geaendertAm;

	@NotNull(message = "{kundenverwaltung.kunde.nachname.notNull}")
	@Pattern(regexp = "[A-Z���][a-z����]+", message = "{kundenverwaltung.kunde.nachname.pattern}")
	private String nachname;

	@NotNull(message = "{kundenverwaltung.kunde.passwort.notNull}")
	private String passwort;
	
	@Transient
	@JsonIgnore
	private String passwortWdh;
	
	@AssertTrue(groups = PasswordGroup.class, message = "{kundenverwaltung.kunde.passwort.notEqual}")
	@JsonIgnore
	public boolean isPasswordEqual() {
	if (passwort == null)
	return passwortWdh == null;
	return passwort.equals(passwortWdh);
	}

	@NotNull(message = "{kundenverwaltung.kunde.vorname.notNull}")
	private String vorname;
	
	//EAGER-Fetching
	@OneToOne(mappedBy = "kunde", cascade = PERSIST)
	@Valid
	@NotNull(message = "{kundenverwaltung.kunde.adresse.notNull}")
	private Adresse adresse;
	
//	@ElementCollection(fetch = EAGER)
//	@CollectionTable(name = "kunde_rolle",
//	                 joinColumns = @JoinColumn(name = "email", nullable = false),
//	                 uniqueConstraints =  @UniqueConstraint(columnNames = { "email", "rolle_FID" }))
//	@Column(table = "kunde_rolle", name = "rolle_FID", nullable = false)
//	private Set<RolleType> rollen;
	
	@PrePersist
	protected void prePersist() {
		erstelltAm = new Date();
		geaendertAm = new Date();
	}
	
	@PostPersist
	protected void postPersist() {
		LOGGER.debugf("Neuer Kunde mit Kundenummer=%d", kundenNr);
	}
	
	@PreUpdate
	protected void preUpdate() {
		geaendertAm = new Date();
	}
	
	@PostLoad
	protected void postLoad() {
		passwortWdh = passwort;
	}

	//LAZY-Fetching
	@OneToMany(mappedBy = "kunde")
	@JsonIgnore
	//@OrderColumn(name = "idx")
	private List<Auftrag> auftraege;
	
	@Transient
	private URI auftraegeUri;
	
	public List<Auftrag> getAuftraege() {
		return Collections.unmodifiableList(auftraege);
	}
	
	public URI getAuftraegeUri() {
		return auftraegeUri;
	}
	
	public void setAuftraege(List<Auftrag> auftraege) {
		if (this.auftraege == null) {
			this.auftraege = auftraege;
			return;
		}
		// Wiederverwendung der vorhandenen Collection
		auftraege.clear();
		if (auftraege != null) {
		this.auftraege.addAll(auftraege);
		}
	}
	
	public void setAuftraegeUri(URI auftraegeUri) {
		this.auftraegeUri = auftraegeUri;
	}
	
	public Kunde addAuftrag(Auftrag auftrag) {
		if (auftraege == null) {
			auftraege = new ArrayList<>();
		}
		auftraege.add(auftrag);
		return this;	
	}
	
	public void setValues(Kunde k) {
		nachname = k.nachname;
		vorname = k.vorname;
		email = k.email;
		passwort = k.passwort;
		passwortWdh = k.passwort;
		if(k.getAdresse() != null)
			adresse.setValues(k.getAdresse());
		else
			adresse = null;
	}
	
	public Adresse getAdresse() {
		return adresse;
	}

	public void setAdresse(Adresse adresse) {
		this.adresse = adresse;
	}
	
	public Kunde() {
	}

	public Long getKundenNr() {
		return this.kundenNr;
	}

	public void setKundenNr(Long kundenNr) {
		this.kundenNr = kundenNr;
	}
	
	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getNachname() {
		return this.nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getPasswort() {
		return this.passwort;
	}

	public void setPasswort(String passwort) {
		this.passwort = passwort;
	}
	
	public String getPasswortWdh() {
		return passwortWdh;
	}

	public void setPasswortWdh(String passwortWdh) {
		this.passwortWdh = passwortWdh;
	}

	public String getVorname() {
		return this.vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}
	
//	public Set<RolleType> getRollen() {
//		return rollen;
//	}
//
//	public void setRollen(Set<RolleType> rollen) {
//		this.rollen = rollen;
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adresse == null) ? 0 : adresse.hashCode());
		result = prime * result
				+ ((auftraege == null) ? 0 : auftraege.hashCode());
		result = prime * result
				+ ((auftraegeUri == null) ? 0 : auftraegeUri.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result
				+ ((erstelltAm == null) ? 0 : erstelltAm.hashCode());
		result = prime * result
				+ ((geaendertAm == null) ? 0 : geaendertAm.hashCode());
		result = prime * result
				+ ((kundenNr == null) ? 0 : kundenNr.hashCode());
		result = prime * result
				+ ((nachname == null) ? 0 : nachname.hashCode());
		result = prime * result
				+ ((passwort == null) ? 0 : passwort.hashCode());
		result = prime * result
				+ ((passwortWdh == null) ? 0 : passwortWdh.hashCode());
		result = prime * result + version;
		result = prime * result + ((vorname == null) ? 0 : vorname.hashCode());
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
		final Kunde other = (Kunde) obj;
		if (adresse == null) {
			if (other.adresse != null)
				return false;
		} 
		else if (!adresse.equals(other.adresse))
			return false;
		if (auftraege == null) {
			if (other.auftraege != null)
				return false;
		} 
		else if (!auftraege.equals(other.auftraege))
			return false;
		if (auftraegeUri == null) {
			if (other.auftraegeUri != null)
				return false;
		} 
		else if (!auftraegeUri.equals(other.auftraegeUri))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} 
		else if (!email.equals(other.email))
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
		if (kundenNr == null) {
			if (other.kundenNr != null)
				return false;
		} 
		else if (!kundenNr.equals(other.kundenNr))
			return false;
		if (nachname == null) {
			if (other.nachname != null)
				return false;
		} 
		else if (!nachname.equals(other.nachname))
			return false;
		if (passwort == null) {
			if (other.passwort != null)
				return false;
		}
		else if (!passwort.equals(other.passwort))
			return false;
		if (passwortWdh == null) {
			if (other.passwortWdh != null)
				return false;
		} 
		else if (!passwortWdh.equals(other.passwortWdh))
			return false;
		if (version != other.version)
			return false;
		if (vorname == null) {
			if (other.vorname != null)
				return false;
		} 
		else if (!vorname.equals(other.vorname))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Kunde [kundenNr=" + kundenNr + ", version=" + version + " vorname=" + vorname + ", nachname=" + nachname
				+ ", email=" + email + ", passwort=" + passwort + ", passwortWdh=" + passwortWdh
				+ " erstelltAm=" + erstelltAm + ", geaendertAm=" + geaendertAm	+ "]";
	}
	
}
