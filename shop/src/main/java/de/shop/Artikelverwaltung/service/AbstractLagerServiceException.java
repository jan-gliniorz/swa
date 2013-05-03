package de.shop.Artikelverwaltung.service;

import de.shop.Util.AbstractShopException;

public abstract class AbstractLagerServiceException extends AbstractShopException {
	private static final long serialVersionUID = 1L;

	public AbstractLagerServiceException(String msg) {
		super(msg);
	}
	
	public AbstractLagerServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
