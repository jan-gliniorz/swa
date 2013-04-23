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
	
	public static final String KUNDEN_PATH = "/kunden";
	public static final String KUNDEN_URI = BASEURI + ":" + PORT + BASEPATH + KUNDEN_PATH;
	public static final String KUNDEN_ID_PATH_PARAM = "kundeId";
	public static final String KUNDEN_ID_PATH = KUNDEN_PATH + "/{" + KUNDEN_ID_PATH_PARAM + "}";
	public static final String KUNDEN_NACHNAME_QUERY_PARAM = "nachname";
	public static final String KUNDEN_ID_FILE_PATH = KUNDEN_ID_PATH + "/file";
	
	public static final String AUFTRAEGE_PATH = "/auftraege";
	public static final String AUFTRAEGE_ID_PATH_PARAM = "auftragId";
	public static final String AUFTRAEGE_ID_PATH = AUFTRAEGE_PATH + "/{" + AUFTRAEGE_ID_PATH_PARAM + "}";
	public static final String AUFTRAEGE_BY_KUNDE_ID_PATH_PARAM = "kundenr";
	public static final String AUFTRAEGE_BY_KUNDE_ID_PATH = AUFTRAEGE_PATH + "/byKunde/{" + AUFTRAEGE_BY_KUNDE_ID_PATH_PARAM + "}";
	
	public static final String ARTIKEL_PATH = "/artikel";
	public static final String ARTIKEL_URI = BASEURI + ":" + PORT + BASEPATH + ARTIKEL_PATH;
	
	// Testklassen fuer Service- und Domain-Tests
	public static final Class<?>[] TEST_CLASSES = { };
	
	private TestConstants() {
	}
}
