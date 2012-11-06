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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + adresse_ID;
		result = prime * result
				+ ((erstelltAm == null) ? 0 : erstelltAm.hashCode());
		result = prime * result
				+ ((geaendertAm == null) ? 0 : geaendertAm.hashCode());
		result = prime * result + ((hausNr == null) ? 0 : hausNr.hashCode());
		result = prime * result + ((kunde == null) ? 0 : kunde.hashCode());
		result = prime * result + kunde_FID;
		result = prime * result + ((land == null) ? 0 : land.hashCode());
		result = prime * result + ((ort == null) ? 0 : ort.hashCode());
		result = prime * result + ((plz == null) ? 0 : plz.hashCode());
		result = prime * result + ((strasse == null) ? 0 : strasse.hashCode());
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
		Adresse other = (Adresse) obj;
		if (adresse_ID != other.adresse_ID)
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
		if (hausNr == null) {
			if (other.hausNr != null)
				return false;
		} else if (!hausNr.equals(other.hausNr))
			return false;
		if (kunde == null) {
			if (other.kunde != null)
				return false;
		} else if (!kunde.equals(other.kunde))
			return false;
		if (kunde_FID != other.kunde_FID)
			return false;
		if (land == null) {
			if (other.land != null)
				return false;
		} else if (!land.equals(other.land))
			return false;
		if (ort == null) {
			if (other.ort != null)
				return false;
		} else if (!ort.equals(other.ort))
			return false;
		if (plz == null) {
			if (other.plz != null)
				return false;
		} else if (!plz.equals(other.plz))
			return false;
		if (strasse == null) {
			if (other.strasse != null)
				return false;
		} else if (!strasse.equals(other.strasse))
			return false;
		return true;
	}

}