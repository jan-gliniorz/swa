package de.shop.temp;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the kunde database table.
 * 
 */
@Entity
@Table(name = "Kunde")
public class Kunde implements Serializable {
	@OneToOne(mappedBy = "kunde_FID")
	
	@OneToMany
	@JoinColumn(name = "kunde_FID", nullable = false)
	@OrderColumn(name = "idx")
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int kundenNr;

	private String email;

	@Column(name="erstellt_am")
	private Timestamp erstelltAm;

	@Column(name="geaendert_am")
	private Timestamp geaendertAm;

	private String nachname;

	private String passwort;

	private String vorname;
		
	//private List<Auftrag> auftraege;
	
	private Adresse adresse;
	
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

	public Timestamp getErstelltAm() {
		return this.erstelltAm;
	}

	public void setErstelltAm(Timestamp erstelltAm) {
		this.erstelltAm = erstelltAm;
	}

	public Timestamp getGeaendertAm() {
		return this.geaendertAm;
	}

	public void setGeaendertAm(Timestamp geaendertAm) {
		this.geaendertAm = geaendertAm;
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

}