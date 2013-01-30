package de.shop.Util;

import java.util.List;
import java.util.Locale;

import javax.ws.rs.core.HttpHeaders;

public final class RestLocaleHelper {	
	public static Locale getLocalFromHttpHeaders(HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		return locale;
	}
	
	private RestLocaleHelper() {
	}
}

//public enum RestLocaleHelper {
//	INSTANCE;
//	
//	public Locale getLocalFromHttpHeaders(HttpHeaders headers) {
//		final List<Locale> locales = headers.getAcceptableLanguages();
//		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
//		
//		return locale;
//	}
//	
//	public RestLocaleHelper getInstance() {
//		return INSTANCE;
//	}
//
//}

