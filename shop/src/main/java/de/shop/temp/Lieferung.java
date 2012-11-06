package de.shop.temp;

import java.io.Serializable;
import javax.persistence.*;

import java.sql.Timestamp;
import java.util.*;
import java.util.Collections;
import java.math.BigDecimal;


/**
 * The persistent class for the lieferung database table.
 * 
 */
@Entity
@Table(name ="lieferung")
public class Lieferung implements Serializable {
	
	@OneToMany
	@JoinColumn(name = "lieferung_FID", nullable = false)
	
	
	private static final long serialVersionUID = 1L;

	
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
		return this.bestelldatum;
	}

	public void setBestelldatum(Date bestelldatum) {
		this.bestelldatum = bestelldatum;
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
		return this.lieferungsdatum;
	}

	public void setLieferungsdatum(Date lieferungsdatum) {
		this.lieferungsdatum = lieferungsdatum;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + lieferung_ID;
		result = prime * result
				+ ((erstelltAm == null) ? 0 : erstelltAm.hashCode());
		result = prime * result
				+ ((geaendertAm == null) ? 0 : geaendertAm.hashCode());
		result = prime * result + ((bestelldatum == null) ? 0 : bestelldatum.hashCode());
		result = prime * result + ((lieferungsdatum == null) ? 0 : lieferungsdatum.hashCode());
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
		
		Lieferung other = (Lieferung) obj;
		if (lieferung_ID != other.lieferung_ID)
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
		
		if (bestelldatum == null) {
			if (other.bestelldatum != null)
				return false;
		} else if (!bestelldatum.equals(other.bestelldatum))
			return false;
		
		if (lieferungsdatum == null) {
			if (other.lieferungsdatum != null)
				return false;
		} else if (!lieferungsdatum.equals(other.lieferungsdatum))
			return false;
		
		return true;
	}
}