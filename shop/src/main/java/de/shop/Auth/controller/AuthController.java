package de.shop.Auth.controller;

import static de.shop.Util.Constants.JSF_INDEX;
import static de.shop.Util.Constants.JSF_REDIRECT_SUFFIX;
import static de.shop.Util.Messages.MessagesType.AUTH;
import static de.shop.Util.Messages.MessagesType.SHOP;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;

import com.google.common.collect.Lists;

import de.shop.Auth.service.jboss.AuthService;
import de.shop.Auth.service.jboss.AuthService.RolleType;
import de.shop.Kundenverwaltung.domain.Kunde;
import de.shop.Kundenverwaltung.service.KundeService;
import de.shop.Util.Client;
import de.shop.Util.InternalError;
import de.shop.Util.Log;
import de.shop.Util.Messages;
import de.shop.Util.Transactional;


/**
 * Ein Managed Bean zur Abwicklung von Authentifizierung (Login und Logout) und erweiterbar f&uuml;r
 * Authorisierung (rollenbasierte Berechtigungen).
 */
@Named("auth")
@SessionScoped
@Log
public class AuthController implements Serializable {
	private static final long serialVersionUID = -8604525347843804815L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String MSG_KEY_LOGIN_ERROR = "login.error";
	private static final String CLIENT_ID_USERNAME = "loginFormHeader:username";
	private static final String MSG_KEY_UPDATE_ROLLEN_KEIN_USER = "updateRollen.keinUser";
	private static final String CLIENT_ID_USERNAME_INPUT = "rollenForm:usernameInput";
	private static final String JSF_UPDATE_ROLLEN = "/auth/updateRollen";
	
	private String username;
	private String password;
	
	private String usernameUpdateRollen;

	@Produces
	@SessionScoped
	@KundeLoggedIn
	private Kunde user;
	
	@Inject
	private KundeService ks;
	
	@Inject
	private AuthService authService;
	
	@Inject
	private Messages messages;
	
	@Inject
	private transient FacesContext ctx;
	
	@Inject
	@Client
	private Locale locale;
	
	@Inject
	private transient HttpServletRequest request;
	
	@Inject
	private transient HttpSession session;
	
	private List<RolleType> ausgewaehlteRollen;
	private List<RolleType> ausgewaehlteRollenOrig;
	private List<RolleType> verfuegbareRollen;

	@PostConstruct
	private void postConstruct() {
		LOGGER.debugf("CDI-faehiges Bean %s wurde erzeugt", this);
		user = null;
	}

	@PreDestroy
	private void preDestroy() {
		LOGGER.debugf("CDI-faehiges Bean %s wird geloescht", this);
	}

	@Override
	public String toString() {
		return "AuthController [username=" + username + ", password=" + password + ", user=" + user + "]";
	}
	
	public String getVorname() {
		return user.getVorname();
	}
	
	public String getNachname() {
		return user.getNachname();
	}
	
	public String getUserRolesToDisply() {
		String result = "";
		for (RolleType role : user.getRollen()) {
			if ("".equals(result))
				result = role.name();
			else
				result += ", " + role.name();
		}
		
		return result;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getUsernameUpdateRollen() {
		return usernameUpdateRollen;
	}

	public void setUsernameUpdateRollen(String usernameUpdateRollen) {
		this.usernameUpdateRollen = usernameUpdateRollen;
	}

	public List<RolleType> getAusgewaehlteRollen() {
		return ausgewaehlteRollen;
	}

	public void setAusgewaehlteRollen(List<RolleType> ausgewaehlteRollen) {
		this.ausgewaehlteRollen = ausgewaehlteRollen;
	}

	public List<RolleType> getVerfuegbareRollen() {
		return verfuegbareRollen;
	}

	public void setVerfuegbareRollen(List<RolleType> verfuegbareRollen) {
		this.verfuegbareRollen = verfuegbareRollen;
	}

	/**
	 * Einloggen eines registrierten Kunden mit Benutzername und Password.
	 */
	@Transactional
	public String login() {
		if (username == null || "".equals(username)) {
			reset();
			messages.error(SHOP, MSG_KEY_LOGIN_ERROR, CLIENT_ID_USERNAME);
			return null;   // Gleiche Seite nochmals aufrufen: mit den fehlerhaften Werten
		}
		
		try {
			request.login(username, password);
		}
		catch (ServletException e) {
			LOGGER.tracef("username=%s, password=%s", username, password);
			reset();
			messages.error(SHOP, MSG_KEY_LOGIN_ERROR, CLIENT_ID_USERNAME);
			return null;   // Gleiche Seite nochmals aufrufen: mit den fehlerhaften Werten
		}
		
		user = ks.findKundeByEmail(username, locale);
		if (user == null) {
			logout();
			throw new InternalError("Kein Kunde mit dem Loginnamen \"" + username + "\" gefunden");
		}
		
		// Gleiche JSF-Seite erneut aufrufen: Re-Render fuer das Navigationsmenue stattfindet
		final String path = ctx.getViewRoot().getViewId();
		return path;
	}
	
	/**
	 * Nachtraegliche Einloggen eines registrierten Kunden mit Benutzername und Password.
	 */
	@Transactional
	public void preserveLogin() {
		if (username != null && user != null) {
			return;
		}
		
		// Benutzername beim Login ermitteln
		username = request.getRemoteUser();

		user = ks.findKundeByEmail(username, locale);
		if (user == null) {
			// Darf nicht passieren, wenn unmittelbar zuvor das Login erfolgreich war
			logout();
			throw new InternalError("Kein Kunde mit dem Loginnamen \"" + username + "\" gefunden");
		}
	}


	/**
	 */
	private void reset() {
		username = null;
		password = null;
		user = null;
	}

	
	/**
	 * Ausloggen und L&ouml;schen der gesamten Session-Daten.
	 */
	public String logout() {
		try {
			request.logout();  // Der Loginname wird zurueckgesetzt
		}
		catch (ServletException e) {
			return null;
		}
		
		reset();
		session.invalidate();
		
		// redirect bewirkt neuen Request, der *NACH* der Session ist
		return JSF_INDEX + JSF_REDIRECT_SUFFIX;
	}

	/**
	 * &Uuml;berpr&uuml;fen, ob Login-Informationen vorhanden sind.
	 * @return true, falls man eingeloggt ist.
	 */
	public boolean isLoggedIn() {
		return user != null;
	}
	
	@Transactional
	public List<String> findUsernameListByUsernamePrefix(String usernamePrefix) {
		final List<String> usernameList = authService.findUsernameListByUsernamePrefix(usernamePrefix);
		return usernameList;
	}
	
	@Transactional
	public String findRollenByUsername() {
		// Gibt es den Usernamen ueberhaupt?
		final Kunde kunde = ks.findKundeByEmail(usernameUpdateRollen, locale);
		if (kunde == null) {
			//kundeNr = null;
			ausgewaehlteRollenOrig = null;
			ausgewaehlteRollen = null;
			
			messages.error(AUTH, MSG_KEY_UPDATE_ROLLEN_KEIN_USER, CLIENT_ID_USERNAME_INPUT);
			return null;
		}
		
		ausgewaehlteRollenOrig = Lists.newArrayList(kunde.getRollen());
		ausgewaehlteRollen = Lists.newArrayList(kunde.getRollen());
		//kundeNr = kunde.getKundenNr();
		LOGGER.tracef("Rollen von %s: %s", usernameUpdateRollen, ausgewaehlteRollen);

		if (verfuegbareRollen == null) {
			verfuegbareRollen = Arrays.asList(RolleType.values());
		}
		
		return null;
	}
	
	@Transactional
	public String updateRollen() {
		// Zusaetzliche Rollen?
		final List<RolleType> zusaetzlicheRollen = new ArrayList<>();
		for (RolleType rolle : ausgewaehlteRollen) {
			if (!ausgewaehlteRollenOrig.contains(rolle)) {
				zusaetzlicheRollen.add(rolle);
			}
		}
		authService.addRollen(usernameUpdateRollen, zusaetzlicheRollen);
		
		// Zu entfernende Rollen?
		final List<RolleType> zuEntfernendeRollen = new ArrayList<>();
		for (RolleType rolle : ausgewaehlteRollenOrig) {
			if (!ausgewaehlteRollen.contains(rolle)) {
				zuEntfernendeRollen.add(rolle);
			}
		}
		authService.removeRollen(usernameUpdateRollen, zuEntfernendeRollen);
		
		// zuruecksetzen
		usernameUpdateRollen = null;
		ausgewaehlteRollenOrig = null;
		ausgewaehlteRollen = null;
		//kundeNr = null;

		return JSF_UPDATE_ROLLEN + JSF_REDIRECT_SUFFIX;
	}
}
