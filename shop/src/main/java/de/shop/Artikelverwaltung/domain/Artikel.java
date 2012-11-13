package de.shop.Artikelverwaltung.domain;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;
import java.math.BigDecimal;



/**
 * The persistent class for the artikel database table.
 * 
 */
@Entity
@Table(name = "Artikel")
public class Artikel implements Serializable {
	
	
	
	private static final long serialVersionUID = 4651646021686650992L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int artikel_ID;


	@OneToMany(mappedBy = "artikel")
	private List<Lagerposition> lagerpositionen;
	
	@Lob
	private String beschreibung;

	private String bezeichung;

	private String bild;

	@Column(name="erstellt_am")
	@Temporal(TIMESTAMP)
	private Date erstelltAm;
	

	@Column(name="geaendert_am")
	@Temporal(TIMESTAMP)
	private Date geaendertAm;

	private BigDecimal preis;

	public Artikel() {
	}

	public int getArtikel_ID() {
		return this.artikel_ID;
	}

	public void setArtikel_ID(int artikel_ID) {
		this.artikel_ID = artikel_ID;
	}

	public String getBeschreibung() {
		return this.beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public String getBezeichung() {
		return this.bezeichung;
	}

	public void setBezeichung(String bezeichung) {
		this.bezeichung = bezeichung;
	}

	public String getBild() {
		return this.bild;
	}

	public void setBild(String bild) {
		this.bild = bild;
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

	public BigDecimal getPreis() {
		return this.preis;
	}

	public void setPreis(BigDecimal preis) {
		this.preis = preis;
	}
	
	public Artikel addLagerposition(Lagerposition lagerposition){
		
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
		result = prime * result + artikel_ID;
		result = prime * result
				+ ((beschreibung == null) ? 0 : beschreibung.hashCode());
		result = prime * result
				+ ((bezeichung == null) ? 0 : bezeichung.hashCode());
		result = prime * result + ((bild == null) ? 0 : bild.hashCode());
		result = prime * result
				+ ((erstelltAm == null) ? 0 : erstelltAm.hashCode());
		result = prime * result
				+ ((geaendertAm == null) ? 0 : geaendertAm.hashCode());
		result = prime * result + ((preis == null) ? 0 : preis.hashCode());
		return result;
	}
	
	
	public List<Lagerposition> getLagerposition(){
		return Collections.unmodifiableList(lagerpositionen);
	}
	
	public void setLagerposition(List<Lagerposition> lagerpositionen){
		
			if(this.lagerpositionen == null){
				this.lagerpositionen = lagerpositionen;
				return;
			}
			
			this.lagerpositionen.clear();
			
			if(lagerpositionen != null){
				this.lagerpositionen.addAll(lagerpositionen);
			}
			
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
		Artikel other = (Artikel) obj;
		if (artikel_ID != other.artikel_ID)
			return false;
		if (beschreibung == null) {
			if (other.beschreibung != null)
				return false;
		} else if (!beschreibung.equals(other.beschreibung))
			return false;
		if (bezeichung == null) {
			if (other.bezeichung != null)
				return false;
		} else if (!bezeichung.equals(other.bezeichung))
			return false;
		if (bild == null) {
			if (other.bild != null)
				return false;
		} else if (!bild.equals(other.bild))
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
		if (preis == null) {
			if (other.preis != null)
				return false;
		} else if (!preis.equals(other.preis))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Artikel [artikel_ID=" + artikel_ID + ", lagerpositionen="
				+ lagerpositionen + ", beschreibung=" + beschreibung
				+ ", bezeichung=" + bezeichung + ", bild=" + bild
				+ ", erstelltAm=" + erstelltAm + ", geaendertAm=" + geaendertAm
				+ ", preis=" + preis + "]";
	}

	
}