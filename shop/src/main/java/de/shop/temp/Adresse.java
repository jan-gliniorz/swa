package de.shop.temp;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the adresse database table.
 * 
 */
@Entity
@Table(name ="adresse")
public class Adresse implements Serializable {
	@ManyToOne(optional = false)
	@JoinColumn(name = "kunde_FID", nullable = false, insertable = false, updatable = false)
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int adresse_ID;

	@Column(name="erstellt_am")
	private Timestamp erstelltAm;

	@Column(name="geaendert_am")
	private Timestamp geaendertAm;

	private String hausNr;

	private int kunde_FID;

	private String land;

	private String ort;

	private String plz;

	private String strasse;
	
	private Kunde kunde;
	
	public Kunde getKunde() {
		return kunde;
	}
	
	public void setKunde(Kunde _kunde){
		kunde = _kunde;
	}
	
	public Adresse() {
	}

	public int getAdresse_ID() {
		return this.adresse_ID;
	}

	public void setAdresse_ID(int adresse_ID) {
		this.adresse_ID = adresse_ID;
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

	public String getHausNr() {
		return this.hausNr;
	}

	public void setHausNr(String hausNr) {
		this.hausNr = hausNr;
	}

	public int getKunde_FID() {
		return this.kunde_FID;
	}

	public void setKunde_FID(int kunde_FID) {
		this.kunde_FID = kunde_FID;
	}

	public String getLand() {
		return this.land;
	}

	public void setLand(String land) {
		this.land = land;
	}

	public String getOrt() {
		return this.ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getPlz() {
		return this.plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getStrasse() {
		return this.strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}

}