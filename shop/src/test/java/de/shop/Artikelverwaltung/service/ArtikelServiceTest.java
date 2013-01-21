package de.shop.Artikelverwaltung.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Artikelverwaltung.service.ArtikelService.FetchType;
import de.shop.Artikelverwaltung.service.ArtikelService.OrderType;
import de.shop.Util.AbstractTest;

@RunWith(Arquillian.class)
public class ArtikelServiceTest extends AbstractTest {
	private static final Long ARTIKEL_LISTE_GROESSE = Long.valueOf(5);
	private static final Long ARTIKEL_ID_VORHANDEN = Long.valueOf(301);
	private static final Long ARTIKEL_ID_LISTE1 = Long.valueOf(301);
	private static final Long ARTIKEL_ID_LISTE2 = Long.valueOf(302);
	private static final List<Long> ARTIKEL_IDS_VORHANDEN = new ArrayList<Long>() { {
		add(ARTIKEL_ID_LISTE1);
		add(ARTIKEL_ID_LISTE2);
		} };
	private static final Long ARTIKEL_ID_UPDATE = Long.valueOf(300);
	private static final Long ARTIKEL_ID_DELETE = Long.valueOf(312);
	private static final String ARTIKEL_BEZEICHNUNG_NEU = "Hose";
	private static final String ARTIKEL_BESCHREIBUNG_NEU = "Regenschirm";
	private static final BigDecimal ARTIKEL_PREIS_NEU = BigDecimal.valueOf(39.0);
	
	@Inject
	private ArtikelService as;
		
	@Test
	public void findAlleArtikel() {
		
		//When
		List<Artikel> artikel = as.findArtikelAll(FetchType.NUR_Artikel, OrderType.ID);
		
		//Then
		assertThat(artikel.size() > ARTIKEL_LISTE_GROESSE, is(true));
	}
	
	@Test
	public void findArtikelById() {
		// Given
		final Long artikelId = ARTIKEL_ID_VORHANDEN;
		
		// Then
		Artikel testArtikel = as.findArtikelByID(artikelId, FetchType.NUR_Artikel, LOCALE);
		
		// When
		assertThat(testArtikel, is(notNullValue()));
		assertThat(testArtikel.getId() == artikelId, is(true));
	}
	
	@Test
	public void findArtikelByIds() {
		// Given
		final List<Long> artikelIds = ARTIKEL_IDS_VORHANDEN;
				
		// Then
		List<Artikel> testArtikel = as.findArtikelByIDs(artikelIds, FetchType.NUR_Artikel, LOCALE);
		
		// When
		assertThat(testArtikel, is(notNullValue()));
		assertThat(ARTIKEL_IDS_VORHANDEN.contains(testArtikel.get(0).getId()), is(true));
		assertThat(ARTIKEL_IDS_VORHANDEN.contains(testArtikel.get(1).getId()), is(true));
	}
	
	
	@Test
	public void createArtikel() {
	
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
		
		assertThat(artikelneu.size(), is(artikelalt.size() + 1));
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
