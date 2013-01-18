package de.shop.Artikelverwaltung.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.shop.Artikelverwaltung.domain.Lager;
import de.shop.Util.AbstractTest;

@RunWith(Arquillian.class)
public class LagerServiceTest extends AbstractTest {
	private static final Long LAGER_ID_VORHANDEN = Long.valueOf(350);
	private static final String LAGER_BEZEICHNUNG_VORHANDEN = "L0";
	private static final Long LAGER_ID_UPDATE = Long.valueOf(352);
	private static final Long LAGER_ID_NEU = Long.valueOf(360);
	private static final String LAGER_BEZEICHNUNG_NEU = "L10";
	
	@Inject
	LagerService ls;
	
	@Test
	public void deleteLager() {
		final Long lagerID = LAGER_ID_VORHANDEN;
		
		Lager neuesLager = ls.findLagerById(lagerID, LOCALE);
		ls.deleteLager(neuesLager);
		Lager geloeschtesLager = ls.findLagerById(lagerID, LOCALE);
		
		assertThat(geloeschtesLager, is(nullValue()));
	}	
}
