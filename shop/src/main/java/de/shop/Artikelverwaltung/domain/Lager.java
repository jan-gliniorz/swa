package de.shop.Artikelverwaltung.domain;

import static de.shop.Util.Constants.LONG_ANZ_ZIFFERN;
import static javax.persistence.TemporalType.TIMESTAMP;
import static de.shop.Util.Constants.KEINE_ID;
import java.io.Serializable;
import javax.persistence.*;

import de.shop.Auftragsverwaltung.domain.Auftrag;

import java.sql.Timestamp;
import java.util.*;

/**
 * The persistent class for the lager database table.
 * 
 */
@Entity
@Table(name = "Lager")
   @NamedQueries({
	@NamedQuery(name = Lager.FIND_Lager_BY_ID, 
			query = "SELECT a" +
					" FROM Lager a"
					+ " WHERE a.id = :" + Lager.PARAM_ID),
	@NamedQuery(name = Lager.FIND_Lager_BY_Bezeichnung, 
				query = "SELECT a" +
						" FROM Lager a"
						+ " WHERE a.bezeichnung = :" + Lager.PARAM_Bezeichnung)
   })

public class Lager implements Serializable {	
	
	private static final String PREFIX = "Lager.";

	public static final String FIND_Lager_BY_Bezeichnung=
		PREFIX +"findArtikelByBezeichnung";
	public static final String FIND_Lager_BY_ID =
		PREFIX + "findArtikelByArtikelid";
	public static final String PARAM_ID = "id";
	public static final String PARAM_Bezeichnung = "bezeichnung";

	private static final long serialVersionUID = -2184864191060846895L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "lager_ID", nullable = false, updatable = false, precision = LONG_ANZ_ZIFFERN)
	private Long id = KEINE_ID;

	@OneToMany(mappedBy = "lager")
	private List<Lagerposition> lagerpositionen;
	
	
	private String bezeichnung;

	@Column(name="erstellt_am")
	@Temporal(TIMESTAMP)
	private Date erstelltAm;

	@Column(name="geaendert_am")
	@Temporal(TIMESTAMP)
	private Date geaendertAm;

	public Lager() {
	}

	public Long getID() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBezeichung() {
		return this.bezeichnung;
	}

	public void setBezeichung(String bezeichung) {
		this.bezeichnung = bezeichung;
	}

	public Date getErstelltAm() {
		return erstelltAm == null ? null : (Date) erstelltAm.clone();
	}

	public void setErstelltAm(Date erstelltAm) {
		this.erstelltAm = erstelltAm == null ? null : (Date) erstelltAm.clone();
	}

	public Date getGeaendertAm() {
		return geaendertAm == null ? null : (Date) geaendertAm.clone();
	}

	public void setGeaendertAm(Date geaendertAm) {
		this.geaendertAm = geaendertAm == null ? null : (Date) geaendertAm.clone();
	}
	
	public List<Lagerposition> getLagerpositionen(){
		return Collections.unmodifiableList(lagerpositionen);
	}
	
	public void setLagerposition(List<Lagerposition> lagerpositionen){
		if(this.lagerpositionen == null){
			this.lagerpositionen = lagerpositionen;
			return;
		}
	
	
	this.lagerpositionen.clear();
	if (lagerpositionen != null){
		this.lagerpositionen.addAll(lagerpositionen);
	}
	}
	
	public Lager addLagerpositionen(Lagerposition lagerposition){
		if(lagerpositionen == null){
			lagerpositionen = new ArrayList<>();
		}
		lagerpositionen.add(lagerposition);
		return this;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bezeichnung == null) ? 0 : bezeichnung.hashCode());
		result = prime * result
				+ ((erstelltAm == null) ? 0 : erstelltAm.hashCode());
		result = prime * result
				+ ((geaendertAm == null) ? 0 : geaendertAm.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Lager other = (Lager) obj;
		if (bezeichnung == null) {
			if (other.bezeichnung != null)
				return false;
		} else if (!bezeichnung.equals(other.bezeichnung))
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
		if (id != other.id)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Lager [lager_ID=" + id + ", bezeichung=" + bezeichnung
				+ ", erstelltAm=" + erstelltAm + ", geaendertAm=" + geaendertAm
				+ "]";
	}
	
	

}