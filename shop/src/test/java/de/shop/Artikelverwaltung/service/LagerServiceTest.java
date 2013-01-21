package de.shop.Artikelverwaltung.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import de.shop.Artikelverwaltung.domain.Lager;
import de.shop.Artikelverwaltung.service.LagerService.FetchType;
import de.shop.Artikelverwaltung.service.LagerService.OrderType;
import de.shop.Util.AbstractTest;

@RunWith(Arquillian.class)
public class LagerServiceTest extends AbstractTest {
	private static final Long LAGER_ID_VORHANDEN = Long.valueOf(350);
	private static final Long LAGER_ID_LOESCHEN = Long.valueOf(351);
	private static final Long LAGER_ID_UPDATE = Long.valueOf(352);
	private static final Long LAGER_ID_NEU = Long.valueOf(360);
	private static final String LAGER_BEZEICHNUNG_NEU = "L10";
	
	@Inject
	private LagerService ls;
	
	@Test
	public void deleteLager() {
		final Long lagerID = LAGER_ID_LOESCHEN;
		
		Lager neuesLager = ls.findLagerById(lagerID, LOCALE);
		ls.deleteLager(neuesLager);
		Lager geloeschtesLager = ls.findLagerById(lagerID, LOCALE);
		
		assertThat(geloeschtesLager, is(nullValue()));
	}	

	@Test
	public void findLagerById() {
		final Long lagerid = LAGER_ID_VORHANDEN;
		Lager lager = ls.findLagerById(lagerid, LOCALE);
		
		assertThat(lager, is(notNullValue()));
		assertThat(lager.getId() == lagerid, is(true));
	}
	
	@Test
	public void createLager() {
		final String lagerBez = LAGER_BEZEICHNUNG_NEU;
		final Long lagerId = LAGER_ID_NEU;
		
		Lager lager = new Lager();
		
		lager.setBezeichnung(lagerBez);
		lager.setId(lagerId);
		
		List<Lager> lageralt = ls.findLagerAll(FetchType.NUR_Lager, OrderType.ID);
		ls.createLager(lager, LOCALE);
		List<Lager> lagerneu = ls.findLagerAll(FetchType.NUR_Lager, OrderType.ID);
		
		assertThat(lagerneu.size(), is(lageralt.size() + 1));		
	}
	
	@Test
	public void updateLager() {
		final Long lagerid = LAGER_ID_UPDATE;
		final String lagerbez = LAGER_BEZEICHNUNG_NEU;
		
		Lager lageralt = ls.findLagerById(lagerid, LOCALE);
		
		lageralt.setBezeichnung(lagerbez);
		
		ls.updateLager(lageralt, LOCALE);
		
		Lager lagerneu = ls.findLagerById(lagerid, LOCALE);
		
		assertThat(lagerneu.getBezeichnung() == lagerbez, is(true));
	
		
	}
}

