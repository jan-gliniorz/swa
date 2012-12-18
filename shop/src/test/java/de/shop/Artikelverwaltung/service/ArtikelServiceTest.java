package de.shop.Artikelverwaltung.service;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import de.shop.Artikelverwaltung.service.ArtikelService.FetchType;
import de.shop.Artikelverwaltung.service.ArtikelService.OrderType;
import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Artikelverwaltung.service.ArtikelService;
import de.shop.Artikelverwaltung.domain.Lager;
import de.shop.Util.AbstractTest;

@RunWith(Arquillian.class)
public class ArtikelServiceTest extends AbstractTest {
	private static final Long ARTIKEL_ID_VORHANDEN = Long.valueOf(301);
	private static final String ARTIKEL_BEZEICHNUNG_VORHANDEN = "emd";
	private static final short ARTIKEL_ANZAHL = 1;
	private static final Long ARTIKEL_ID_UPDATE = Long.valueOf(300);
	private static final Long ARTIKEL_ID_DELETE = Long.valueOf(312);
	private static final String ARTIKEL_BEZEICHNUNG_NEU = "Hose";
	private static final String ARTIKEL_BESCHREIBUNG_NEU = "Regenschirm";
	private static final BigDecimal ARTIKEL_PREIS_NEU = BigDecimal.valueOf(39.0);
	
	@Inject
	ArtikelService as;
		
	@Test
	public void findAlleArtikel(){
		//When
		List<Artikel> artikel = as.findArtikelAll(FetchType.NUR_Artikel, OrderType.ID);
		
		//Then
		assertThat(artikel.size()>5, is(true));
	}
	
	@Test
	public void findArtikelById() {
		// Given
		final Long artikelId = ARTIKEL_ID_VORHANDEN;
		final String artikelBez = ARTIKEL_BEZEICHNUNG_VORHANDEN;
		
		
		// Then
		Artikel testArtikel = as.findArtikelByID(artikelId, FetchType.NUR_Artikel, LOCALE);
		
		// When
		assertThat(testArtikel, is(notNullValue()));
		assertThat(testArtikel.getId() == artikelId, is(true));
		assertThat(testArtikel.getBezeichnung() == artikelBez, is(true));
	}
	
	
	@Test
	public void createArtikel(){
	
		final String artikelbez = ARTIKEL_BEZEICHNUNG_NEU;
		final String artikelbes = ARTIKEL_BESCHREIBUNG_NEU;
		final BigDecimal artikelpre = ARTIKEL_PREIS_NEU;
		
		Artikel neuerArtikel = new Artikel();
		
		
		neuerArtikel.setBeschreibung(artikelbes);
		neuerArtikel.setBezeichnung(artikelbez);
		neuerArtikel.setPreis(artikelpre);
		
		List<Artikel> artikelalt = as.findArtikelAll(FetchType.NUR_Artikel, OrderType.ID);		
		as.createArtikel(neuerArtikel, LOCALE);
		List<Artikel> artikelneu = as.findArtikelAll(FetchType.NUR_Artikel, OrderType.ID);
		
		assertThat(artikelneu.size(), is(artikelalt.size()+1));

	}
	
	@Test
	public void deleteArtikel() {
		final Long artikelID = ARTIKEL_ID_DELETE;
		
		Artikel neuerArtikel = as.findArtikelByID(artikelID, FetchType.NUR_Artikel, LOCALE);
		as.deleteArtikel(neuerArtikel);
		Artikel geloeschterArtikel = as.findArtikelByID(artikelID, FetchType.NUR_Artikel, LOCALE);
		
		assertThat(geloeschterArtikel, is(nullValue()));
	}
	
	@Test
	public void updateArtikel() {
		final Long artikelID = ARTIKEL_ID_UPDATE;
		final String artikelbez = ARTIKEL_BEZEICHNUNG_NEU;
		
		Artikel artikel = as.findArtikelByID(artikelID, FetchType.NUR_Artikel, LOCALE);
		
		artikel.setBezeichnung(artikelbez);
		as.updateArtikel(artikel, LOCALE);
		
		Artikel artikelNachUpdate = as.findArtikelByID(artikelID, FetchType.NUR_Artikel, LOCALE);
		
		assertThat(artikelNachUpdate.getBezeichnung() == artikelbez, is(true));
		
	}
	
}
