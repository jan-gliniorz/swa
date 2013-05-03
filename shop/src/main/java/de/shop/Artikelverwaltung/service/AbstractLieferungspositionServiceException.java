package de.shop.Artikelverwaltung.service;

import de.shop.Util.AbstractShopException;

public abstract class AbstractLieferungspositionServiceException extends AbstractShopException {
	private static final long serialVersionUID = 1L;

	public AbstractLieferungspositionServiceException(String msg) {
		super(msg);
	}
	
	public AbstractLieferungspositionServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
