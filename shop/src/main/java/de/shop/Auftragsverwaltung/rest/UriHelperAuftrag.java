package de.shop.Auftragsverwaltung.rest;


import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.Artikelverwaltung.domain.Artikel;
import de.shop.Artikelverwaltung.domain.Lager;
import de.shop.Artikelverwaltung.domain.Lagerposition;
import de.shop.Artikelverwaltung.rest.UriHelperArtikel;
import de.shop.Artikelverwaltung.service.ArtikelService;
import de.shop.Auftragsverwaltung.domain.Auftrag;
import de.shop.Auftragsverwaltung.domain.Auftragsposition;
import de.shop.Kundenverwaltung.domain.Kunde;
import de.shop.Kundenverwaltung.rest.UriHelperKunde;
import de.shop.Kundenverwaltung.service.KundeService;
import de.shop.Util.Log;
import de.shop.Util.NotFoundException;
import de.shop.Util.RestLocaleHelper;


@ApplicationScoped
@Log
public class UriHelperAuftrag {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	@Inject
	private UriHelperKunde uriHelperKunde;
	
	@Inject
	private UriHelperArtikel uriHelperArtikel;
	
	public void updateUriAuftrag(Auftrag auftrag, UriInfo uriInfo) {
		// URL fuer Kunde setzen
		final Kunde kunde = auftrag.getKunde();
		if (kunde != null) {
			
			final URI kundeUri = uriHelperKunde.getUriKunde(auftrag.getKunde(), uriInfo);
			auftrag.setKundeUri(kundeUri);
		}
		
		// URLs fuer Artikel in den Bestellpositionen setzen
		final List<Auftragsposition> auftragspositionen = auftrag.getAuftragspositionen();
		if (auftragspositionen != null && !auftragspositionen.isEmpty()) {
			for (Auftragsposition ap : auftragspositionen) {
				final URI artikelUri = uriHelperArtikel.getUriArtikel(ap.getArtikel(), uriInfo);
				ap.setArtikelUri(artikelUri);
			}
		}		
	}

	public URI getUriAuftrag(Auftrag auftrag, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(AuftragResource.class)
		                             .path(AuftragResource.class, "findAuftragById");
		final URI uri = ub.build(auftrag.getId());
		return uri;
	}
	
	public URI getUriAuftragByKunde(Kunde kunde, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(AuftragResource.class)
		                             .queryParam("kundenr", kunde.getKundenNr());
		final URI uri = ub.build();
		return uri;
	}
}
