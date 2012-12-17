package de.shop.Artikelverwaltung.service;

//import static org.hamcrest.CoreMatchers.both;
//import static org.hamcrest.CoreMatchers.either;
//
//import static javax.persistence.TemporalType.DATE;
//import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.CoreMatchers.notNullValue;
//import static org.hamcrest.CoreMatchers.nullValue;
//import static org.junit.Assert.assertThat;
//import static org.junit.Assert.assertTrue;
//
//import java.lang.invoke.MethodHandle;
//import java.lang.invoke.MethodHandles;
//import java.math.BigDecimal;
//import java.util.Calendar;
//import java.util.Collection;
//import java.util.Date;
//import java.util.GregorianCalendar;
//import java.util.List;
//
//import javax.inject.Inject;
//import javax.transaction.HeuristicMixedException;
//import javax.transaction.HeuristicRollbackException;
//import javax.transaction.NotSupportedException;
//import javax.transaction.RollbackException;
//import javax.transaction.SystemException;
//import javax.transaction.UserTransaction;
//
//import org.jboss.arquillian.junit.Arquillian;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.openqa.selenium.android.library.Logger;
//
//import de.shop.Artikelverwaltung.domain.Artikel;
//import de.shop.Artikelverwaltung.domain.Lieferung;
//import de.shop.Artikelverwaltung.domain.Lieferungsposition;
//import de.shop.Artikelverwaltung.service.LieferungService.FetchType;
//import de.shop.Kundenverwaltung.domain.Kunde;
//import de.shop.Util.AbstractTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.both;
import static org.junit.matchers.JUnitMatchers.either;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;
import javax.mail.Session;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.Artikelverwaltung.domain.*;
import de.shop.Artikelverwaltung.service.*;
import de.shop.Artikelverwaltung.service.LieferungService.FetchType;
import de.shop.Util.AbstractTest;

@RunWith(Arquillian.class)
public class LieferungServiceTest extends AbstractTest {

	private static final Long ID_NEU = Long.valueOf(475);
	private static final Long ID_VORHANDEN = Long.valueOf(451);
	private static final Long ID_UNGUELTIG = Long.valueOf(888);
	private static final Long ID_NICHT_VORHANDEN = Long.valueOf(1000);
	
	private static final Date LIEFERUNGSDATUM_NEU = new GregorianCalendar(2012,Calendar.NOVEMBER,30).getTime();
		
	private static final Date BESTELLDATUM_NEU = new GregorianCalendar(2012,Calendar.DECEMBER,31).getTime();
	private static final Date BESTELLDATUM_NICHT_VORHANDEN = new GregorianCalendar(1956,Calendar.FEBRUARY, 2).getTime();
	private static final Date BESTELLDATUM_VORHANDEN = new GregorianCalendar(2011, Calendar.SEPTEMBER, 26).getTime();
	private static final Date BESTELLDATUM_UNGUELTIG = new GregorianCalendar(2013, Calendar.FEBRUARY, 31).getTime();
	
	private static final String BEZEICHNUNG_VORHANDEN = "Regenjacke";
	private static final String BESCHREIBUNG_VORHANDEN = "Regenjacke";
	private static final BigDecimal PREIS_VORHANDEN = BigDecimal.valueOf(50);
	private static final String BILD_VORHANDEN = "x";
	
	
	private static final int ANZAHL_NEU = 3;

	
	
	@Inject
	private LieferungService ls;
	
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
//	@Test
//	public void findLieferungMitBestelldatumUngueltig() {
//		// Given
//		final Date bestelldatum = BESTELLDATUM_UNGUELTIG;
//
//		// When / Then
//		thrown.expect(LieferungInvalidBestelldatumException.class);
//		ls.findLieferungByBestelldatum(bestelldatum, LOCALE);
//		
//	}

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
	public void createLieferung() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                       SystemException, NotSupportedException {
		// Given
		//Attribute neueLieferung
		final long id = ID_NEU;
		final Date bestelldatum = BESTELLDATUM_NEU;
		final Date lieferungsdatum = LIEFERUNGSDATUM_NEU;
		
		
		//Attribute neueLieferungsposition
		final int anzahl = ANZAHL_NEU;


		//Attribute vorhandenerArtikel
		final String bezeichnung = BEZEICHNUNG_VORHANDEN;
		final String beschreibung = BESCHREIBUNG_VORHANDEN;
		final BigDecimal preis = PREIS_VORHANDEN;
		final String bild = BILD_VORHANDEN;
		

		// When
		final Collection<Lieferung> lieferungenVorher = ls.findLieferungenAll(FetchType.NUR_LIEFERUNG, null);
		final UserTransaction trans = getUserTransaction();
		trans.commit();

		Lieferung lieferung = new Lieferung();
		lieferung.setBestelldatum(bestelldatum);
		lieferung.setLieferungsdatum(lieferungsdatum);
		
		final Artikel vorhandenerArtikel = new Artikel();
		vorhandenerArtikel.setBezeichnung(bezeichnung);
		vorhandenerArtikel.setBeschreibung(beschreibung);
		vorhandenerArtikel.setBild(bild);
		vorhandenerArtikel.setPreis(preis);
		
		final Lieferungsposition neueLieferungsposition = new Lieferungsposition();
		neueLieferungsposition.setAnzahl(anzahl);
		neueLieferungsposition.setArtikel(vorhandenerArtikel);
		lieferung.addLieferungsposition(neueLieferungsposition);
	
		trans.begin();
		Lieferung neueLieferung = ls.createLieferung(lieferung, LOCALE);
		trans.commit();

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
	public void createLieferungOhnePosition() {
		// Given
		final Date bestelldatum = BESTELLDATUM_NEU;
		final Date lieferungsdatum = LIEFERUNGSDATUM_NEU;

		// When
		final Lieferung neueLieferung = new Lieferung();
		neueLieferung.setBestelldatum(bestelldatum);
		neueLieferung.setLieferungsdatum(lieferungsdatum);
		neueLieferung.setLieferungsposition(null);
		
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
	
	/**
	 */
	/*@Test
	public void neuesLieferungsdatumFuerLieferung() throws RollbackException, HeuristicMixedException, HeuristicRollbackException,
	                                        SystemException, NotSupportedException {
		// Given
		final Long id = ID_VORHANDEN;

		// When
		Lieferung lieferung = ls.findLieferungById(id, FetchType.NUR_LIEFERUNG, LOCALE);
		final UserTransaction trans = getUserTransaction();
		trans.commit();
		
		final Date altesLieferungsdatum = lieferung.getLieferungsdatum();
		final Date neuesLieferungsdatum = LIEFERUNGSDATUM_NEU;
		
		//lieferung.setLieferungsdatum(neuesLieferungsdatum);
	
		
		trans.begin();
		lieferung = ls.updateLieferung(lieferung, LOCALE);
		trans.commit();
		
		// Then
		assertThat(lieferung.getLieferungsdatum(), is(neuesLieferungsdatum));
		trans.begin();
		lieferung = ls.findLieferungById(id, FetchType.NUR_LIEFERUNG, LOCALE);
		trans.commit();
		assertThat(lieferung.getLieferungsdatum(), is(neuesLieferungsdatum));
	}*/
}
