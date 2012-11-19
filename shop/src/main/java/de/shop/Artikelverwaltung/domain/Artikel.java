package de.shop.Artikelverwaltung.domain;

import static de.shop.Util.Constants.LONG_ANZ_ZIFFERN;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.io.Serializable;
import javax.persistence.*;

import de.shop.Auftragsverwaltung.domain.Auftrag;
import de.shop.Kundenverwaltung.domain.Kunde;
import static de.shop.Util.Constants.KEINE_ID;
import java.sql.Timestamp;
import java.util.*;
import java.math.BigDecimal;

/**
 * The persistent class for the artikel database table.
 * 
 */
@Entity
@Table(name = "Artikel")
@NamedQueries({
	  @NamedQuery(name = Artikel.FIND_Artikel_BY_Bezeichnung,
			      query = "FROM Artikel a WHERE a.bezeichnung = :"+ Artikel.PARAM_Bezeichnung),	
	  @NamedQuery(name = Artikel.FIND_Artikel_BY_Artikel_ID,
			  	  query = "FROM Artikel a WHERE a.id = :"+ Artikel.PARAM_ID)
	  
	})

public class Artikel implements Serializable {

	private static final String PREFIX = "Artikel.";

	public static final String FIND_Artikel_BY_Bezeichnung=
		PREFIX +"findArtikelByBezeichnung";
	public static final String FIND_Artikel_BY_Artikel_ID =
		PREFIX + "findArtikelByArtikelid";
	public static final String PARAM_ID = "id";
	public static final String PARAM_Bezeichnung = "bezeichnung";

	
	
	private static final long serialVersionUID = 4651646021686650992L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name = "artikel_ID", nullable = false, updatable = false, precision = LONG_ANZ_ZIFFERN)
	private Long id = KEINE_ID;


	@OneToMany(mappedBy = "artikel")
	private List<Lagerposition> lagerpositionen;
	
	@Lob
	private String beschreibung;

	private String bezeichnung;

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

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBeschreibung() {
		return this.beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public String getBezeichung() {
		return this.bezeichnung;
	}

	public void setBezeichung(String bezeichung) {
		this.bezeichnung = bezeichung;
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
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((beschreibung == null) ? 0 : beschreibung.hashCode());
		result = prime * result
				+ ((bezeichnung == null) ? 0 : bezeichnung.hashCode());
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
		if (id != other.id)
			return false;
		if (beschreibung == null) {
			if (other.beschreibung != null)
				return false;
		} else if (!beschreibung.equals(other.beschreibung))
			return false;
		if (bezeichnung == null) {
			if (other.bezeichnung != null)
				return false;
		} else if (!bezeichnung.equals(other.bezeichnung))
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
		return "Artikel [artikel_ID=" + id + ", beschreibung=" + beschreibung
				+ ", bezeichung=" + bezeichnung + ", bild=" + bild
				+ ", erstelltAm=" + erstelltAm + ", geaendertAm=" + geaendertAm
				+ ", preis=" + preis + "]";
	}

	
}