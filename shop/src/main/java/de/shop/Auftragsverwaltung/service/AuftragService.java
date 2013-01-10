package de.shop.Auftragsverwaltung.service;

import java.util.List;
import java.util.Locale;

import de.shop.Auftragsverwaltung.domain.Auftrag;
import de.shop.Kundenverwaltung.domain.Kunde;

public interface AuftragService {
	List<Auftrag> findAuftragAll();
	/**
	 */
	Auftrag findAuftragById(Long id);

	/**
	 */
	//Kunde findKundeById(Long id);

	/**
	 */
	List<Auftrag> findAuftragByKundeId(Long kundeId);

	/**
	 */
	Auftrag createAuftrag(Auftrag auftrag, Kunde kunde, Locale locale);
}
