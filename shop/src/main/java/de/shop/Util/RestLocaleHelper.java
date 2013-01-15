package de.shop.Util;

import java.util.List;
import java.util.Locale;

import javax.ws.rs.core.HttpHeaders;

public class RestLocaleHelper {	
	public static Locale getLocalFromHttpHeaders(HttpHeaders headers) {
		final List<Locale> locales = headers.getAcceptableLanguages();
		final Locale locale = locales.isEmpty() ? Locale.getDefault() : locales.get(0);
		
		return locale;
	}
}
