package de.shop.Artikelverwaltung.Service;

import de.shop.Util.AbstractShopException;

public abstract class LagerpositionServiceException extends AbstractShopException {
	private static final long serialVersionUID = 1L;

	public LagerpositionServiceException(String msg) {
		super(msg);
	}
	
	public LagerpositionServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
