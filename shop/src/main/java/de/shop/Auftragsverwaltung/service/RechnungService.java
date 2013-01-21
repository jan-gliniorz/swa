package de.shop.Auftragsverwaltung.service;

import java.util.List;
import java.util.Locale;

import de.shop.Auftragsverwaltung.domain.Rechnung;

public interface RechnungService {
	List<Rechnung> findRechnungAll();
	/**
	 */
	Rechnung findRechnungById(Long id);

	/**
	 */
	Rechnung createRechnung(Rechnung rechnung, Locale locale);
}
