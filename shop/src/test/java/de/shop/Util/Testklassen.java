package de.shop.Util;

import java.util.Arrays;
import java.util.List;

import de.shop.Kundenverwaltung.domain.AdresseTest;
import de.shop.Kundenverwaltung.domain.KundeTest;


public enum Testklassen {
	INSTANCE;
	
	// Testklassen aus *VERSCHIEDENEN* Packages auflisten (durch Komma getrennt):
	// so dass alle darin enthaltenen Klassen ins Web-Archiv mitverpackt werden
	//private List<Class<? extends AbstractTest>> classes = Arrays.asList(KundeTest.class, AdresseTest.class);
	private List<Class<? extends AbstractDomainTest>> classes = Arrays.asList(KundeTest.class, AdresseTest.class);

	public static Testklassen getInstance() {
		return INSTANCE;
	}
	
	public List<Class<? extends AbstractDomainTest>> getTestklassen() {
		return classes;
	}
}
