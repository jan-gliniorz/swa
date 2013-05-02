package de.shop.Util;

public final class Constants {
	// JPA
	public static final Long KEINE_ID = null;
	public static final int LONG_ANZ_ZIFFERN = 20;
	public static final long MIN_ID = 1L;
	public static final int ERSTE_VERSION = 0;
	
	// REST
	public static final String ARTIKELVERWALTUNG_NS = "urn:shop:artikelverwaltung";
	public static final String AUFTRAGSVERWALTUNG_NS = "urn:shop:auftragsverwaltung";
	public static final String KUNDENVERWALTUNG_NS = "urn:shop:kundenverwaltung";
	
	// JAAS
	public static final String SECURITY_DOMAIN = "shop";
	public static final String KUNDE_ROLLE_TABELLE = "kunde_rolle";
	
	public static final String HASH_ALGORITHM = "SHA-256";
	public static final String HASH_ENCODING = "base64";
	public static final String HASH_CHARSET = "UTF-8";
	
	private Constants() {
	}
}
