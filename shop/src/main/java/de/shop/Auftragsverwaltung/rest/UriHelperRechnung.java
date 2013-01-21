package de.shop.Auftragsverwaltung.rest;


import java.net.URI;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.shop.Auftragsverwaltung.domain.Auftrag;
import de.shop.Auftragsverwaltung.domain.Rechnung;
import de.shop.Util.Log;


@ApplicationScoped
@Log
public class UriHelperRechnung {
	
	@Inject
	private UriHelperAuftrag uriHelperAuftrag;
	
	public void updateUriRechnung(Rechnung rechnung, UriInfo uriInfo) {
		final Auftrag auftrag = rechnung.getAuftrag();
		if (auftrag != null) {
			final URI auftragUri = uriHelperAuftrag.getUriAuftrag(auftrag, uriInfo);
			rechnung.setAuftragUri(auftragUri);
		}
	}

	public URI getUriRechnung(Rechnung rechnung, UriInfo uriInfo) {
		final UriBuilder ub = uriInfo.getBaseUriBuilder()
		                             .path(RechnungResource.class)
		                             .path(RechnungResource.class, "findRechnungById");
		final URI uri = ub.build(rechnung.getId());
		return uri;
	}
}
