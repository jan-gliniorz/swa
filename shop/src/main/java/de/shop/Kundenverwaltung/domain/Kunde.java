package de.shop.Kundenverwaltung.domain;

import static javax.persistence.TemporalType.TIMESTAMP;

import static de.shop.Util.Constants.KEINE_ID;
import static de.shop.Util.Constants.LONG_ANZ_ZIFFERN;

import java.io.Serializable;
import javax.persistence.*;
import javax.persistence.criteria.*;

import java.sql.Timestamp;
import java.util.*;

import de.shop.Auftragsverwaltung.domain.*;;

/**
 * The persistent class for the kunde database table.
 * 
 */
@Entity
@Table(name = "kunde")
@NamedQueries({
@NamedQuery(name = Kunde.KUNDE_BY_NACHNAME,
			query = "FROM Kunde k WHERE k.nachname = :" + Kunde.PARAM_NACHNAME),
@NamedQuery(name = Kunde.KUNDE_BY_PLZ,
			query = "FROM Kunde k WHERE k.adresse.plz = :" + Kunde.PARAM_PLZ),
@NamedQuery(name = Kunde.KUNDE_BY_KNR,
			query = "FROM Kunde k WHERE k.kundenNr = :" + Kunde.PARAM_KUNDENNUMMER),
@NamedQuery(name = Kunde.KUNDE_BY_NAME_AUFTRAEGE,
			query = "SELECT DISTINCT k"
					+ " FROM Kunde k"
					+ " LEFT JOIN FETCH k.auftraege"
					+ " WHERE k.nachname = :"+ Kunde.PARAM_NACHNAME)
})

public class Kunde implements Serializable {
	
	private static final long serialVersionUID = 3925016425151715847L;
	
	private static final String PREFIX = "Kunde.";
	public static final String KUNDE_BY_NACHNAME = PREFIX +"findKundenByNachname";
	public static final String KUNDE_BY_PLZ = PREFIX + "findKundenByPlz";
	public static final String KUNDE_BY_KNR = PREFIX + "findKundenByKundennummer";
	public static final String KUNDE_BY_NAME_AUFTRAEGE = PREFIX + "findKundenByNachnameFetchAufraege";
	
	public static final String PARAM_PLZ = "plz";
	public static final String PARAM_NACHNAME = "nachname";
	public static final String PARAM_KUNDENNUMMER = "kundenNr";
	
	
	@Id
	@GeneratedValue
	@Column(nullable = false, updatable = false, precision = LONG_ANZ_ZIFFERN)
	private Long kundenNr = KEINE_ID;

	private String email;

	@Column(name="erstellt_am")
	@Temporal(TIMESTAMP)
	private Date erstelltAm;

	@Column(name="geaendert_am")
	@Temporal(TIMESTAMP)
	private Date geaendertAm;

	private String nachname;

	private String passwort;

	private String vorname;
	
	//EAGER-Fetching
	@OneToOne(mappedBy = "kunde")
	private Adresse adresse;
	
	//LAZY-Fetching
	@OneToMany(mappedBy = "kunde")
	private List<Auftrag> auftraege;
	
	public List<Auftrag> getAuftraege(){
		return Collections.unmodifiableList(auftraege);
	}
	
	public void setAuftraege(List<Auftrag> _auftraege) {
		if (auftraege == null) {
		auftraege = _auftraege;
		return;
		}
		// Wiederverwendung der vorhandenen Collection
		auftraege.clear();
		if (_auftraege != null) {
		auftraege.addAll(_auftraege);
		}
	}
	
	public Kunde addAuftrag(Auftrag _auftrag) {
		if (auftraege == null) {
			auftraege = new ArrayList<>();
		}
		auftraege.add(_auftrag);
		return this;	
	}
	
	public Adresse getAdresse(){
		return adresse;
	}

	public void setAdresse(Adresse _adresse){
		adresse = _adresse;
	}
	
	public Kunde() {
	}

	public Long getKundenNr() {
		return this.kundenNr;
	}

	public void setKundenNr(Long kundenNr) {
		this.kundenNr = kundenNr;
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

	public String getVorname() {
		return this.vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adresse == null) ? 0 : adresse.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result
				+ ((erstelltAm == null) ? 0 : erstelltAm.hashCode());
		result = prime * result
				+ ((geaendertAm == null) ? 0 : geaendertAm.hashCode());
		result = prime * result
				+ ((nachname == null) ? 0 : nachname.hashCode());
		result = prime * result
				+ ((passwort == null) ? 0 : passwort.hashCode());
		result = prime * result + ((kundenNr == null) ? 0 : kundenNr.hashCode());
		result = prime * result + ((vorname == null) ? 0 : vorname.hashCode());
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
		Kunde other = (Kunde) obj;
		if (adresse == null) {
			if (other.adresse != null)
				return false;
		} else if (!adresse.equals(other.adresse))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (erstelltAm == null) {
			if (other.erstelltAm != null)
				return false;
		} else if (!erstelltAm.equals(other.erstelltAm))
			return false;
		if (geaendertAm == null) {
			if (other.geaendertAm != null)
				return false;
		} else if (!geaendertAm.equals(other.geaendertAm))
			return false;
		if (kundenNr != other.kundenNr)
			return false;
		if (nachname == null) {
			if (other.nachname != null)
				return false;
		} else if (!nachname.equals(other.nachname))
			return false;
		if (passwort == null) {
			if (other.passwort != null)
				return false;
		} else if (!passwort.equals(other.passwort))
			return false;
		if (vorname == null) {
			if (other.vorname != null)
				return false;
		} else if (!vorname.equals(other.vorname))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Kunde [kundenNr=" + kundenNr + ", vorname=" + vorname + ", nachname=" + nachname
				+ ", email=" + email + ", passwort=" + passwort
				+ " erstelltAm=" + erstelltAm + ", geaendertAm=" + geaendertAm	+ "]";
	}
	

}