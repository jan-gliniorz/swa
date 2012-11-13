package de.shop.Kundenverwaltung.domain;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import javax.persistence.*;

import java.sql.Timestamp;
import java.util.*;

import de.shop.Auftragsverwaltung.domain.*;;

/**
 * The persistent class for the kunde database table.
 * 
 */
@Entity
@Table(name = "kunde")
public class Kunde implements Serializable {
	private static final long serialVersionUID = 3925016425151715847L;
	
	@Id
	@GeneratedValue
	@Column(nullable = false, updatable = false)
	private int kundenNr;

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
	
	@OneToOne(mappedBy = "kunde")
	private Adresse adresse;
	
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

	public int getKundenNr() {
		return this.kundenNr;
	}

	public void setKundenNr(int kundenNr) {
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
		result = prime * result + kundenNr;
		result = prime * result
				+ ((nachname == null) ? 0 : nachname.hashCode());
		result = prime * result
				+ ((passwort == null) ? 0 : passwort.hashCode());
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
		return "Kunde [kundenNr=" + kundenNr + ", email=" + email
				+ ", erstelltAm=" + erstelltAm + ", geaendertAm=" + geaendertAm
				+ ", nachname=" + nachname + ", passwort=" + passwort
				+ ", vorname=" + vorname + "]";
	}
	

}