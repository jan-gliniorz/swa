package de.shop.Artikelverwaltung.Service;

import de.shop.Util.AbstractShopException;

public abstract class LagerServiceException extends AbstractShopException {
	private static final long serialVersionUID = 1L;

	public LagerServiceException(String msg) {
		super(msg);
	}
	
	public LagerServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
