package de.shop.Artikelverwaltung.Service;

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
import de.shop.Artikelverwaltung.Service.ArtikelService.FetchType;
import de.shop.Artikelverwaltung.Service.ArtikelService.OrderType;
import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Artikelverwaltung.Service.ArtikelService;
import de.shop.Artikelverwaltung.domain.Lager;
import de.shop.Util.AbstractTest;

@RunWith(Arquillian.class)
public class LagerServiceTest extends AbstractTest {
	private static final Long LAGER_ID_VORHANDEN = Long.valueOf(350);
	private static final String LAGER_BEZEICHNUNG_VORHANDEN = "L0";
	private static final Long LAGER_ID_UPDATE = Long.valueOf(350);
	private static final Long LAGER_ID_NEU = Long.valueOf(360);
	private static final String LAGER_BEZEICHNUNG_NEU = "L10";
	
	@Inject
	LagerService ls;
		
	
	
	@Test
	public void findLagerById() {
		// Given
		final Long lagerId = LAGER_ID_VORHANDEN;
		final String lagerBez = LAGER_BEZEICHNUNG_VORHANDEN;
		
		
		// Then
		Lager testLager = ls.findLagerById(lagerId, LOCALE);
		
		// When
		assertThat(testLager, is(notNullValue()));
		assertThat(testLager.getId() == lagerId, is(true));
		assertThat(testLager.getBezeichnung() == lagerBez, is(true));
	}
	
	@Test
	public void createLager(){
		final Long lagerID = LAGER_ID_NEU;
		final String lagerbez = LAGER_BEZEICHNUNG_NEU;
		
		
		Lager neuesLager = new Lager();
		
		neuesLager.setId(lagerID);
		neuesLager.setBezeichnung(lagerbez);
		
		ls.createLager(neuesLager, LOCALE);
		
		neuesLager = ls.findLagerById(lagerID, LOCALE);
		
		assertThat(neuesLager.getId() == lagerID, is(true));
		assertThat(neuesLager.getBezeichnung() == lagerbez, is(true));
	}
	
	@Test
	public void deleteLager() {
		final Long lagerID = LAGER_ID_VORHANDEN;
		
		Lager neuesLager = ls.findLagerById(lagerID, LOCALE);
		ls.deleteLager(neuesLager);
		Lager geloeschtesLager = ls.findLagerById(lagerID, LOCALE);
		
		assertThat(geloeschtesLager, is(nullValue()));
	}
	
	@Test
	public void updateLager() {
		final Long lagerID = LAGER_ID_UPDATE;
		final String lagerbez = LAGER_BEZEICHNUNG_NEU;
		
		Lager lager = ls.findLagerById(lagerID, LOCALE);
		
		lager.setBezeichnung(lagerbez);
		ls.updateLager(lager, LOCALE);
		
		Lager lagerNachUpdate = ls.findLagerById(lagerID, LOCALE);
		
		assertThat(lagerNachUpdate.getBezeichnung() == lagerbez, is(true));
		
	}
	
}
