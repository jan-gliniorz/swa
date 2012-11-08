package de.shop.temp;

import java.io.Serializable;
import javax.persistence.*;

import java.sql.Timestamp;
import java.util.*;
import java.util.Collections;
import java.math.BigDecimal;


/**
 * The persistent class for the artikel database table.
 * 
 */
@Entity
@Table(name = "Artikel")
public class Artikel implements Serializable {
	
	@OneToMany
	@JoinTable(name = "lagerposition.aufgragsposition.lieferungsposition", joinColumns = {
	@JoinColumn(name = "artikel_FID", nullable = false),
	@JoinColumn(name = "artikel_FID", nullable = false),
	@JoinColumn(name = "artikel_FID", nullable = false)
	
	})
	
	
	private static final long serialVersionUID = 1L;
	
	
	private List<Lagerposition> lagerpositionen;
	
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
		
		
	public Artikel addLagerposition(Lagerposition lagerposition){
			
		if(lagerpositionen == null){
			lagerpositionen = new ArrayList<>();
		}
		
		lagerpositionen.add(lagerposition);
		return this;
	}
		
	
	

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int artikel_ID;
	
	
	

	@Lob
	private String beschreibung;

	private String bezeichung;

	private String bild;

	@Column(name="erstellt_am")
	private Timestamp erstelltAm;

	@Column(name="geaendert_am")
	private Timestamp geaendertAm;

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

	public BigDecimal getPreis() {
		return this.preis;
	}

	public void setPreis(BigDecimal preis) {
		this.preis = preis;
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

}