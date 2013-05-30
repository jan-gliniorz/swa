//package de.shop.Auftragsverwaltung.controller;
//
//import java.io.Serializable;
//
//import javax.enterprise.context.RequestScoped;
//import javax.faces.context.Flash;
//import javax.inject.Inject;
//import javax.inject.Named;
//
//import de.shop.Auftragsverwaltung.domain.Auftrag;
//import de.shop.Auftragsverwaltung.service.AuftragService;
//import de.shop.Util.Log;
//import de.shop.Util.Transactional;
//
///**
// * Dialogsteuerung fuer die Auftragsverwaltung
// */
//@Named("ac")
//@RequestScoped
//@Log
//public class AuftragController implements Serializable {
//	private static final long serialVersionUID = -8817180909526894740L;
//	
//	private static final String FLASH_AUFTRAG = "auftrag";
//	private static final String JSF_VIEW_AUFTRAG = "/auftragsverwaltung/viewAuftrag";
//	
//	@Inject
//	private AuftragService as;
//	
//	@Inject
//	private Flash flash;
//	
//	private Long auftragId;
//
//	@Override
//	public String toString() {
//		return "AuftragController [AuftragId=" + auftragId + "]";
//	}
//
//	public void setAuftragId(Long auftragId) {
//		this.auftragId = auftragId;
//	}
//
//	public Long getAuftragId() {
//		return auftragId;
//	}
//
//	/**
//	 * Action Methode, um einen Auftrag zu gegebener ID zu suchen
//	 * @return URL fuer Anzeige des gefundenen Auftrags; sonst null
//	 */
//	@Transactional
//	public String findAuftragById() {
//		final Auftrag auftrag = as.findAuftragById(auftragId);
//		if (auftrag == null) {
//			flash.remove(FLASH_AUFTRAG);
//			return null;
//		}
//		
//		flash.put(FLASH_AUFTRAG, auftrag);
//		return JSF_VIEW_AUFTRAG;
//	}
//}
