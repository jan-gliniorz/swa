package de.shop.Auftragsverwaltung.domain;

import static de.shop.Util.Constants.LONG_ANZ_ZIFFERN;
import static de.shop.Util.Constants.KEINE_ID;

import java.io.Serializable;
import javax.persistence.*;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.Date;


/**
 * The persistent class for the rechnung database table.
 * 
 */
@Entity
@Table(name = "rechnung")
@NamedQueries({
	@NamedQuery(name = Rechnung.FIND_RECHNUNG_BY_ID, 
				query = "SELECT r"
						+ " FROM Rechnung r"
						+ " WHERE r.id = :" + Rechnung.PARAM_ID)
})
public class Rechnung implements Serializable {
	private static final long serialVersionUID = 4985112963755405161L;
	
	private static final String PREFIX = "Rechnung.";
	public static final String FIND_RECHNUNG_BY_ID = PREFIX + "findRechnungById";
	
	public static final String PARAM_ID = "id";

	@Id
	@GeneratedValue
	@Column(name = "rechnung_ID", nullable = false, updatable = false, precision = LONG_ANZ_ZIFFERN)
	private Long id = KEINE_ID;

	@OneToOne
	@JoinColumn(name = "auftrag_FID")
	private Auftrag auftrag;

	@Column(name="erstellt_am")
	@Temporal(TIMESTAMP)
	private Date erstelltAm;

	@Column(name="geaendert_am")
	@Temporal(TIMESTAMP)
	private Date geaendertAm;

	@Temporal(TemporalType.DATE)
	private Date rechnungsdatum;

	public Rechnung() {
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Auftrag getAuftrag() {
		return auftrag;
	}

	public void setAuftrag(Auftrag auftrag) {
		this.auftrag = auftrag;
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

	public Date getRechnungsdatum() {
		return this.rechnungsdatum == null ? null : (Date) this.rechnungsdatum.clone();
	}

	public void setRechnungsdatum(Date rechnungsdatum) {
		this.rechnungsdatum = rechnungsdatum == null ? null : (Date) rechnungsdatum.clone();
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
		result = prime * result
				+ ((rechnungsdatum == null) ? 0 : rechnungsdatum.hashCode());
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
		Rechnung other = (Rechnung) obj;
		if (erstelltAm == null) {
			if (other.erstelltAm != null) {
				return false;
			}
		} else if (!erstelltAm.equals(other.erstelltAm)) {
			return false;
		}
		if (geaendertAm == null) {
			if (other.geaendertAm != null) {
				return false;
			}
		} else if (!geaendertAm.equals(other.geaendertAm)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (rechnungsdatum == null) {
			if (other.rechnungsdatum != null) {
				return false;
			}
		} else if (!rechnungsdatum.equals(other.rechnungsdatum)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return String
				.format("Rechnung [rechnung_ID=%s, erstelltAm=%s, geaendertAm=%s, rechnungsdatum=%s]",
						id, erstelltAm, geaendertAm, rechnungsdatum);
	}	
}
	