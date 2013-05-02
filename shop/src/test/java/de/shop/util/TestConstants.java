package de.shop.util;

public final class TestConstants {
	public static final String WEB_PROJEKT = "shop";
	
	// HTTP-Header
	public static final String ACCEPT = "Accept";
	public static final String LOCATION = "Location";
	
	// URLs und Pfade
	public static final String BASEURI;
	public static final int PORT;
	public static final String BASEPATH;
	
	static {
		BASEURI = System.getProperty("baseuri", "http://localhost");
		PORT = Integer.parseInt(System.getProperty("port", "8080"));
		BASEPATH = System.getProperty("basepath", "/shop/rest");
	}
	
	
	public static final String LIEFERUNGEN_PATH = "/lieferungen";
	public static final String LIEFERUNGEN_URI = BASEURI + ":" + PORT + BASEPATH + LIEFERUNGEN_PATH;
	public static final String LIEFERUNGEN_ID_PATH_PARAM = "lieferungId";
	public static final String LIEFERUNGEN_ID_PATH = LIEFERUNGEN_PATH + "/{" + LIEFERUNGEN_ID_PATH_PARAM + "}";
	
	public static final String LIEFERUNGSPOSITIONEN_PATH = "/lieferungspositionen";
	public static final String LIEFERUNGSPOSITIONEN_URI = BASEURI + ":" + PORT + BASEPATH + LIEFERUNGSPOSITIONEN_PATH;
	public static final String LIEFERUNGSPOSITIONEN_ID_PATH_PARAM = "lpId";
	public static final String LIEFERUNGSPOSITIONEN_ID_PATH = LIEFERUNGSPOSITIONEN_PATH + "/{" + LIEFERUNGSPOSITIONEN_ID_PATH_PARAM + "}";

	public static final String LIEFERUNGSPOSITION_ID_QUERY_PARAM = LIEFERUNGSPOSITIONEN_PATH + "?lieferungid={" + LIEFERUNGEN_ID_PATH_PARAM + "}";
	
	public static final String KUNDEN_PATH = "/kunden";
	public static final String KUNDEN_URI = BASEURI + ":" + PORT + BASEPATH + KUNDEN_PATH;
	public static final String KUNDEN_ID_PATH_PARAM = "kundenNr";
	public static final String KUNDEN_ID_PATH = KUNDEN_PATH + "/{" + KUNDEN_ID_PATH_PARAM + "}";
	public static final String KUNDEN_NACHNAME_QUERY_PARAM = "nachname";
	public static final String KUNDEN_ID_FILE_PATH = KUNDEN_ID_PATH + "/file";
	
	public static final String AUFTRAEGE_PATH = "/auftraege";
	public static final String AUFTRAEGE_ID_PATH_PARAM = "auftragId";
	public static final String AUFTRAEGE_ID_PATH = AUFTRAEGE_PATH + "/{" + AUFTRAEGE_ID_PATH_PARAM + "}";
	public static final String AUFTRAEGE_BY_KUNDE_ID_PATH_PARAM = "kundenr";
	public static final String AUFTRAEGE_BY_KUNDE_ID_PATH = AUFTRAEGE_PATH + "/byKunde/{" + AUFTRAEGE_BY_KUNDE_ID_PATH_PARAM + "}";
	
	public static final String LAGER_PATH = "/lager";
	public static final String LAGER_ID_PATH_PARAM = "lagerId";
	public static final String LAGER_ID_PATH = LAGER_PATH + "/{" + LAGER_ID_PATH_PARAM + "}";
	//public static final String BESTELLUNGEN_ID_KUNDE_PATH = BESTELLUNGEN_ID_PATH + "/kunde";
	
	public static final String ARTIKEL_PATH = "/artikel";
	public static final String ARTIKEL_URI = BASEURI + ":" + PORT + BASEPATH + ARTIKEL_PATH;
	public static final String ARTIKEL_ID_PATH_PARAM = "artikelId";
	public static final String ARTIKEL_ID_PATH = ARTIKEL_PATH + "/{" + ARTIKEL_ID_PATH_PARAM + "}";
	
	public static final String LAGERPOSITION_PATH = "/lagerpositionen";
	public static final String LAGERPOSITION_URI = BASEURI + ":" + PORT + BASEPATH + LAGERPOSITION_PATH;
	public static final String LAGERPOSITION_ID_PATH_PARAM = "lagerpositionId";
	public static final String LAGERPOSITION_ID_PATH = LAGERPOSITION_PATH + "/{" + LAGERPOSITION_ID_PATH_PARAM + "}";

	
	// Testklassen fuer Service- und Domain-Tests
	public static final Class<?>[] TEST_CLASSES = { };
	
	private TestConstants() {
	}
}
