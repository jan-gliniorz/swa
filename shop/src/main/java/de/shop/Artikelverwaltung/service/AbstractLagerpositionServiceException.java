package de.shop.Artikelverwaltung.service;

import de.shop.Util.AbstractShopException;

public abstract class AbstractLagerpositionServiceException extends AbstractShopException {
	private static final long serialVersionUID = 1L;

	public AbstractLagerpositionServiceException(String msg) {
		super(msg);
	}
	
	public AbstractLagerpositionServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
