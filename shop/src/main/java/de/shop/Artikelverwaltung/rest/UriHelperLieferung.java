package de.shop.Artikelverwaltung.rest;

import java.net.URI;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.Artikelverwaltung.domain.Lieferung;
import de.shop.Artikelverwaltung.domain.Lieferungsposition;
import de.shop.Artikelverwaltung.rest.UriHelperLieferungsposition;

import de.shop.Auftragsverwaltung.domain.Auftragsposition;
import de.shop.Auftragsverwaltung.domain.Auftrag;

import de.shop.Kundenverwaltung.domain.Kunde;
import de.shop.Kundenverwaltung.rest.UriHelperKunde;

import de.shop.Util.Log;


@ApplicationScoped
@Log
public class UriHelperLieferung {

	@Inject
	private UriHelperLieferungsposition uriHelperLieferungsposition;
	
	public void updateUriLieferung(Lieferung lieferung, UriInfo uriInfo) {

		final List<Lieferungsposition> lieferungspositionen = lieferung.getLieferungsposition();
		
		if (lieferungspositionen != null && !lieferungspositionen.isEmpty()) {

			
			final URI lieferungspositionUri = uriHelperLieferungsposition.getUriLieferungspositionenByLieferungId(lieferung, uriInfo);
			lieferung.setLieferungspositionUri(lieferungspositionUri);
		}				
	}
	
	
	public URI getUriLieferung(Lieferung lieferung, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(LieferungResource.class)
		                             .path(LieferungResource.class, "findLieferungById");
		final URI uri = ub.build(lieferung.getId());
		return uri;
	}
}
