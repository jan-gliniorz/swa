package de.shop.Auftragsverwaltung.domain;

import java.io.Serializable;
import javax.persistence.*;

import java.sql.Timestamp;
import java.util.*;


/**
 * The persistent class for the lieferung database table.
 * 
 */
@Entity
@Table(name ="lieferung")
public class Lieferung implements Serializable {
	
	@OneToMany
	@JoinColumn(name = "lieferung_FID", nullable = false)
	@OrderColumn(name = "erstelltAm")
	
	private List<Lieferungsposition> lieferungspositionen;
	
	public List<Lieferungsposition> getLieferungsposition(){
		return Collections.unmodifiableList(lieferungspositionen);
	}
	
	public void setLieferungsposition(List<Lieferungsposition> lieferungspositionen){

		if(this.lieferungspositionen == null){
			this.lieferungspositionen = lieferungspositionen;
			return;
		}
		
		this.lieferungspositionen.clear();
		
		if(lieferungspositionen != null){
			this.lieferungspositionen.addAll(lieferungspositionen);
		}
		
	}	
	
	public Lieferung addLieferungsposition(Lieferungsposition lieferungsposition){
		
		if(lieferungspositionen == null){
			lieferungspositionen = new ArrayList<>();
		}
		
		lieferungspositionen.add(lieferungsposition);
		return this;
	}
	
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int lieferung_ID;

	@Temporal(TemporalType.DATE)
	private Date bestelldatum;

	@Column(name="erstellt_am")
	private Timestamp erstelltAm;

	@Column(name="geaendert_am")
	private Timestamp geaendertAm;

	@Temporal(TemporalType.DATE)
	private Date lieferungsdatum;

	public Lieferung() {
	}

	public int getLieferung_ID() {
		return this.lieferung_ID;
	}

	public void setLieferung_ID(int lieferung_ID) {
		this.lieferung_ID = lieferung_ID;
	}

	public Date getBestelldatum() {
		return bestelldatum == null ? null : (Date) bestelldatum.clone();
	}

	public void setBestelldatum(Date bestelldatum) {
		this.bestelldatum = bestelldatum == null ? null : (Date) bestelldatum.clone();
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

	public Date getLieferungsdatum() {
		return lieferungsdatum == null ? null : (Date) lieferungsdatum.clone();
	}

	public void setLieferungsdatum(Date lieferungsdatum) {
		this.lieferungsdatum = lieferungsdatum == null ? null : (Date) lieferungsdatum.clone();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bestelldatum == null) ? 0 : bestelldatum.hashCode());
		result = prime * result
				+ ((erstelltAm == null) ? 0 : erstelltAm.hashCode());
		result = prime * result
				+ ((geaendertAm == null) ? 0 : geaendertAm.hashCode());
		result = prime * result + lieferung_ID;
		result = prime * result
				+ ((lieferungsdatum == null) ? 0 : lieferungsdatum.hashCode());
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
		
		Lieferung other = (Lieferung) obj;
		
		if (bestelldatum == null) {
			if (other.bestelldatum != null)
				return false;
		} else if (!bestelldatum.equals(other.bestelldatum))
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
		
		if (lieferungsdatum == null) {
			if (other.lieferungsdatum != null)
				return false;
		} else if (!lieferungsdatum.equals(other.lieferungsdatum))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "Lieferung [lieferung_ID=" + lieferung_ID + ", bestelldatum="
				+ bestelldatum + ", erstelltAm=" + erstelltAm
				+ ", geaendertAm=" + geaendertAm + ", lieferungsdatum="
				+ lieferungsdatum + "]";
	}
	
}