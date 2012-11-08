package de.shop.Auftragsverwaltung.domain;

import de.shop.temp.*;

import java.io.Serializable;
import javax.persistence.*;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;
import static javax.persistence.TemporalType.TIMESTAMP;

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
public class Auftrag implements Serializable {	
	private static final long serialVersionUID = 2465349694241738534L;

	@Id
	@GeneratedValue
	@Column(nullable = false, updatable = false)
	private int auftrag_ID;
	
	@OneToMany
	@JoinColumn(name= "auftrag_FID", nullable = false)
	@OrderColumn(name = "idx")
	private List<Auftragsposition> auftragspositionen;
	
	@OneToOne(mappedBy = "auftrag") //TODO: wie wird hier als Ziel die Klasse Rechnung festgestellt? MappedBy ist das Feld auftrag in Rechnung....
	private Rechnung rechnung;

	@Column(name="erstellt_am")
	@Temporal(TIMESTAMP)
	private Date erstelltAm;

	@Column(name="geaendert_am")
	private Date geaendertAm;

	@ManyToOne(optional = false)
	@JoinColumn(name = "kunde_FID", nullable = false, insertable = false, updatable = false)
	private Kunde kunde;

	public Auftrag() {
	}

	public int getAuftrag_ID() {
		return this.auftrag_ID;
	}

	public void setAuftrag_ID(int auftrag_ID) {
		this.auftrag_ID = auftrag_ID;
	}

	public Date getErstelltAm() {
		return this.erstelltAm;
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
		if(this.auftragspositionen == null)
		{
			this.auftragspositionen = auftragspositionen;
			return;
		}
		
		this.auftragspositionen.clear();
		if(auftragspositionen != null)
		{
			this.auftragspositionen = auftragspositionen;
		}
	}
	
	public Auftrag addAuftragsposition(Auftragsposition position)
	{
		if(this.auftragspositionen == null)
		{
			this.auftragspositionen = new ArrayList<>(); //TODO: Warum hier keinen Typ angeben?
		}
		
		this.auftragspositionen.add(position);
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + auftrag_ID;
		result = prime * result
				+ ((erstelltAm == null) ? 0 : erstelltAm.hashCode());
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
		if (auftrag_ID != other.auftrag_ID) {
			return false;
		}
		if (erstelltAm == null) {
			if (other.erstelltAm != null) {
				return false;
			}
		} else if (!erstelltAm.equals(other.erstelltAm)) {
			return false;
		}
		return true;
	}
}