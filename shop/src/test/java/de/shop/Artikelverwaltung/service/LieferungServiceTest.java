package de.shop.Artikelverwaltung.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Artikelverwaltung.domain.Lieferung;
import de.shop.Artikelverwaltung.domain.Lieferungsposition;
import de.shop.Artikelverwaltung.service.LieferungService.FetchType;
import de.shop.Util.AbstractTest;


@RunWith(Arquillian.class)
public class LieferungServiceTest extends AbstractTest {

	private static final Long ID_VORHANDEN = Long.valueOf(451);
	private static final Long ID_UNGUELTIG = Long.valueOf(888);
	
	private static final int TAG_NEU = 31;
	private static final int MONAT_NEU = Calendar.NOVEMBER;
	private static final int JAHR_NEU = 2012;
	private static final Date LIEFERUNGSDATUM_NEU = new GregorianCalendar(JAHR_NEU, MONAT_NEU, TAG_NEU).getTime();
	private static final Date BESTELLDATUM_NEU = new GregorianCalendar(JAHR_NEU, MONAT_NEU, TAG_NEU).getTime();
	
	private static final int TAG_NVORH = 2;
	private static final int MONAT_NVORH = Calendar.FEBRUARY;
	private static final int JAHR_NVORH = 1999;
	private static final Date BESTELLDATUM_NICHT_VORHANDEN = 
							  new GregorianCalendar(JAHR_NVORH, MONAT_NVORH, TAG_NVORH).getTime();
	
	private static final int TAG_VORH = 26;
	private static final int MONAT_VORH = Calendar.SEPTEMBER;
	private static final int JAHR_VORH = 2011;
	private static final Date BESTELLDATUM_VORHANDEN = new GregorianCalendar(JAHR_VORH, MONAT_VORH, TAG_VORH).getTime();
	
	private static final Long ARTIKEL_ID_VORHANDEN = Long.valueOf(305);
	private static final String BEZEICHNUNG_VORHANDEN = "Regenjacke";
	private static final String BESCHREIBUNG_VORHANDEN = "Regenjacke";
	private static final BigDecimal PREIS_VORHANDEN = BigDecimal.valueOf(50);
	private static final String BILD_VORHANDEN = "x";
	
	private static final int ANZAHL_NEU = 3;
	
	@Inject
	private LieferungService ls;
	private ArtikelService as;
	
	/**
	 */
	@Test
	public void findLieferungMitBestelldatumVorhanden() {
		// Given
		final Date bestelldatum = BESTELLDATUM_VORHANDEN;

		// When
		final Collection<Lieferung> lieferungen = ls.findLieferungByBestelldatum(bestelldatum, LOCALE);
		
		// Then
		assertThat(lieferungen, is(notNullValue()));
		assertThat(lieferungen.isEmpty(), is(false));

		for (Lieferung li : lieferungen) {
			assertThat(li.getBestelldatum(), is(bestelldatum));
		}
	}

	/**
	 */
	@Test
	public void findLieferungMitBestelldatumNichtVorhanden() {
		// Given
		final Date bestelldatum = BESTELLDATUM_NICHT_VORHANDEN;

		// When
		final List<Lieferung> lieferungen = ls.findLieferungByBestelldatum(bestelldatum, LOCALE);
		
		// Then
		assertThat(lieferungen.isEmpty(), is(true));
	}
	
	/**
	 */
	@Test
	public void findLieferungMitIdNichtVorhanden() {
		// Given
		final Long id = ID_UNGUELTIG;

		// When
		final Lieferung lieferung = ls.findLieferungById(id, FetchType.NUR_LIEFERUNG, LOCALE);
		
		// Then
		assertThat(lieferung, is(nullValue()));
	}

	/**
	 */
	@Test
	public void createLieferungOhneBestelldatum() {
		
		// Given
		//Attribute neueLieferung
		final Date lieferungsdatum = LIEFERUNGSDATUM_NEU;
				
				
		//Attribute neueLieferungsposition
		final int anzahl = ANZAHL_NEU;


		//Attribute vorhandenerArtikel
		final String bezeichnung = BEZEICHNUNG_VORHANDEN;
		final String beschreibung = BESCHREIBUNG_VORHANDEN;
		final BigDecimal preis = PREIS_VORHANDEN;
		final String bild = BILD_VORHANDEN;
			
		
		Lieferung neueLieferung = new Lieferung();
		neueLieferung.setBestelldatum(null);
		neueLieferung.setLieferungsdatum(lieferungsdatum);
				
		final Artikel vorhandenerArtikel = new Artikel();
		vorhandenerArtikel.setBezeichnung(bezeichnung);
		vorhandenerArtikel.setBeschreibung(beschreibung);
		vorhandenerArtikel.setBild(bild);
		vorhandenerArtikel.setPreis(preis);
				
		final Lieferungsposition neueLieferungsposition = new Lieferungsposition();
		neueLieferungsposition.setAnzahl(anzahl);
		neueLieferungsposition.setArtikel(vorhandenerArtikel);
		neueLieferung.addLieferungsposition(neueLieferungsposition);

		// Then
		thrown.expect(LieferungValidationException.class);
		thrown.expectMessage("Ungueltige Lieferung:");
		ls.createLieferung(neueLieferung, LOCALE);
	}
	/**
	 */
	@Test
	public void createLieferung() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                       SystemException, NotSupportedException {
		// Given
		//Attribute neueLieferung
		final Date bestelldatum = BESTELLDATUM_NEU;
		final Date lieferungsdatum = LIEFERUNGSDATUM_NEU;
		
		final int anzahl = ANZAHL_NEU;
		
		//Attribute vorhandenerArtikel
		final String bezeichnung = BEZEICHNUNG_VORHANDEN;
		final String beschreibung = BESCHREIBUNG_VORHANDEN;
		final BigDecimal preis = PREIS_VORHANDEN;
		final String bild = BILD_VORHANDEN;

		// When
		
		//Collection mit allen Lieferungen vorm Create
		final Collection<Lieferung> lieferungenVorher = ls.findLieferungenAll(FetchType.NUR_LIEFERUNG, null);
		final UserTransaction trans = getUserTransaction();
		trans.commit();

		//Objekt lieferung anlegen und Attribute setten
		Lieferung lieferung = new Lieferung();
		lieferung.setBestelldatum(bestelldatum);
		lieferung.setLieferungsdatum(lieferungsdatum);
		
//		Artikel vorhandenerArtikel = new Artikel();
//		vorhandenerArtikel.setBezeichnung(bezeichnung+"kkk");
//		vorhandenerArtikel.setBeschreibung(beschreibung+"lll");
//		vorhandenerArtikel.setBild(bild);
//		vorhandenerArtikel.setPreis(preis);
//		trans.begin();
//		vorhandenerArtikel = as.createArtikel(vorhandenerArtikel, LOCALE);
//		trans.commit();
		
//		Objekt vorhandenerArtikel anhand einer vorgegebenen Artikelnummer erstellen
		trans.begin();
		Artikel vorhandenerArtikel = as.findArtikelByID(
										Long.valueOf(ARTIKEL_ID_VORHANDEN), 
										de.shop.Artikelverwaltung.service.ArtikelService.FetchType.NUR_Artikel, 
										LOCALE);
		trans.commit();	
		
		//Objekt neueLieferungsposition erstellen und Attribute setten
		final Lieferungsposition neueLieferungsposition = new Lieferungsposition();
		neueLieferungsposition.setAnzahl(anzahl);
		
		//Objekt vorhandenerArtikel in neueLieferungsposition einfügen
		neueLieferungsposition.setArtikel(vorhandenerArtikel);
		
		//Objekt neueLieferungsposition zum Objekt lieferung hinzufügen
		lieferung.addLieferungsposition(neueLieferungsposition);
	
		//Objekt neueLieferung erstellen und ???
		trans.begin();
		Lieferung neueLieferung = ls.createLieferung(lieferung, LOCALE);
		trans.commit();

		//Collection mit allen Lieferungen nach dem Create
		trans.begin();
		final Collection<Lieferung> lieferungenNachher = ls.findLieferungenAll(FetchType.NUR_LIEFERUNG, null);
		trans.commit();
		
		assertThat(lieferungenVorher.size() + 1, is(lieferungenNachher.size()));
		for (Lieferung li : lieferungenVorher) {
			assertTrue(li.getId() < neueLieferung.getId());
			assertTrue(li.getErstelltAm().getTime() < neueLieferung.getErstelltAm().getTime());
		}
		
		trans.begin();
		neueLieferung = ls.findLieferungById(neueLieferung.getId(), FetchType.NUR_LIEFERUNG, LOCALE);
		trans.commit();
		
		assertThat(neueLieferung.getBestelldatum(), is(bestelldatum));
	}
	
	/**
	 */
	@Test
	public void createLieferungOhnePosition() {
		// Given
		final Date bestelldatum = BESTELLDATUM_NEU;
		final Date lieferungsdatum = LIEFERUNGSDATUM_NEU;

		// When
		final Lieferung neueLieferung = new Lieferung();
		neueLieferung.setBestelldatum(bestelldatum);
		neueLieferung.setLieferungsdatum(lieferungsdatum);
		neueLieferung.setLieferungspositionen(null);
		
		// Then
		thrown.expect(LieferungValidationException.class);
		thrown.expectMessage("Ungueltige Lieferung:");
		ls.createLieferung(neueLieferung, LOCALE);
	}
	
	/**
	 */
	@Test
	public void deleteLieferung() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                 SystemException, NotSupportedException {
		// Given
		final Long id = ID_VORHANDEN;

		final Collection<Lieferung> lieferungVorher = ls.findLieferungenAll(FetchType.NUR_LIEFERUNG, null);
		final UserTransaction trans = getUserTransaction();
		trans.commit();
	
		// When
		trans.begin();
		final Lieferung lieferung = ls.findLieferungById(id, FetchType.MIT_POSITIONEN, LOCALE);
		trans.commit();
		trans.begin();
		ls.deleteLieferung(lieferung);
		trans.commit();
	
		// Then
		trans.begin();
		final Collection<Lieferung> lieferungNachher = ls.findLieferungenAll(FetchType.NUR_LIEFERUNG, null);
		trans.commit();
		assertThat(lieferungVorher.size() - 1, is(lieferungNachher.size()));
	}
}
