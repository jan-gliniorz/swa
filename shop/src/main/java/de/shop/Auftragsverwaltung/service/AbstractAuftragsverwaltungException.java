package de.shop.Auftragsverwaltung.service;

import de.shop.Util.AbstractShopException;

public abstract class AbstractAuftragsverwaltungException extends AbstractShopException {
	private static final long serialVersionUID = -626920099480136224L;

	public AbstractAuftragsverwaltungException(String msg) {
		super(msg);
	}
}
