package de.shop.kundenverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.KUNDEN_ID_PATH_PARAM;
import static de.shop.util.TestConstants.KUNDEN_ID_PATH;
import static de.shop.util.TestConstants.KUNDEN_NACHNAME_QUERY_PARAM;
import static de.shop.util.TestConstants.KUNDEN_PATH;
import static de.shop.util.TestConstants.LAGERPOSITION_URI;
import static de.shop.util.TestConstants.LOCATION;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static java.util.logging.Level.FINER;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.util.AbstractResourceTest;

@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class KundeResourceTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final Long KUNDE_ID_VORHANDEN = Long.valueOf(10);
	private static final Long KUNDE_ID_NICHT_VORHANDEN = Long.valueOf(1500);
	private static final Long KUNDE_ID_UPDATE = Long.valueOf(19);
	private static final Long KUNDE_ID_DELETE = Long.valueOf(18);
	private static final Long KUNDE_ID_DELETE_MIT_BESTELLUNGEN = Long.valueOf(16);
	private static final Long KUNDE_ID_DELETE_FORBIDDEN = Long.valueOf(17);
	private static final String NACHNAME_VORHANDEN = "Loya";
	private static final String NACHNAME_NICHT_VORHANDEN = "Wrongname";
	private static final String NEUER_NACHNAME = "Newname";
	private static final String NEUER_NACHNAME_INVALID = "!";
	private static final String NEUER_VORNAME = "Newfirstname";
	private static final String NEUE_EMAIL = NEUER_NACHNAME + "@gmail.com";
	private static final String NEUE_EMAIL_INVALID = "falsch@falsch";
	private static final String NEUE_PLZ = "76133";
	private static final String NEUER_ORT = "Karlsruhe";
	private static final String NEUE_STRASSE = "Testweg";
	private static final String NEUE_HAUSNR = "1";
	private static final String NEUES_LAND = "Deutschland";

	@Test
	public void validate() {
		assertThat(true, is(true));
	}

	@Test
	public void findKundeById() {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long kundeId = KUNDE_ID_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                                         .get(KUNDEN_ID_PATH);

		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("kundenNr").longValue(), is(kundeId.longValue()));
		}
		
		LOGGER.finer("ENDE");
	}
	
	@Test
	public void findKundeByIdNichtVorhanden() {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long kundeId = KUNDE_ID_NICHT_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                                         .get(KUNDEN_ID_PATH);

    	// Then
    	assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.finer("ENDE");
	}

	@Test
	public void findKundenByNachnameVorhanden() {
		LOGGER.finer("BEGINN");
		
		// Given
		final String nachname = NACHNAME_VORHANDEN;

		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .queryParam(KUNDEN_NACHNAME_QUERY_PARAM, nachname)
                                         .get(KUNDEN_PATH);
		
		// Then
		try (final JsonReader jsonReader = 
						getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonArray jsonArray = jsonReader.readArray();
			assertThat(jsonArray.size() > 0, is(true));

			final List<JsonObject> jsonObjectList = jsonArray.getValuesAs(JsonObject.class);
			for (JsonObject jsonObject : jsonObjectList) {
				assertThat(jsonObject.getString("nachname"), is(nachname));
			}
		}
		LOGGER.finer("ENDE");
	}

	@Test
	public void findKundenByNachnameNichtVorhanden() {
		LOGGER.finer("BEGINN");
		
		// Given
		final String nachname = NACHNAME_NICHT_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .queryParam(KUNDEN_NACHNAME_QUERY_PARAM, nachname)
                                         .get(KUNDEN_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));

		LOGGER.finer("ENDE");
	}
	
	
	@Test
	public void createKunde() {
		LOGGER.finer("BEGINN");
		
		// Given
		final String nachname = NEUER_NACHNAME;
		final String vorname = NEUER_VORNAME;
		final String email = NEUE_EMAIL;
		final String plz = NEUE_PLZ;
		final String ort = NEUER_ORT;
		final String land = NEUES_LAND;
		final String strasse = NEUE_STRASSE;
		final String hausnr = NEUE_HAUSNR;
		final String username = USERNAME;
		final String password = PASSWORD;
		
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
		             		          .add("nachname", nachname)
		             		          .add("vorname", vorname)
		             		          .add("email", email)
		             		          .add("passwort", password)
		             		          .add("adresse", getJsonBuilderFactory().createObjectBuilder()
		                    		                  .add("plz", plz)
		                    		                  .add("ort", ort)
		                    		                  .add("strasse", strasse)
		                    		                  .add("hausNr", hausnr)
		                    		                  .add("land", land)
		                    		                  .build())
		                              .build();

		// When
		final Response response = given().contentType(APPLICATION_JSON)
				                         .body(jsonObject.toString())
				                         .auth()
                                         .basic(username, password)
                                         .post(KUNDEN_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		final String location = response.getHeader(LOCATION);
		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id.longValue() > 0, is(true));

		LOGGER.finer("ENDE");
	}

	@Test
	public void createFalschesPassword() {
		LOGGER.finer("BEGINN");
		
		// Given
		final String username = USERNAME;
		final String password = PASSWORD_FALSCH;
		final String nachname = NEUER_NACHNAME;
		
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
            		                  .add("nachname", nachname)
            		                  .build();
		
		// When
		final Response response = given().contentType(APPLICATION_JSON)
				                         .body(jsonObject.toString())
                                         .auth()
                                         .basic(username, password)
                                         .post(KUNDEN_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_UNAUTHORIZED));
		
		LOGGER.finer("ENDE");
	}
	
	@Test
	public void createkundeInvalid() {
		LOGGER.finer("BEGINN");
		
		// Given
		final String nachname = NEUER_NACHNAME_INVALID;
		final String vorname = NEUER_VORNAME;
		final String email = NEUE_EMAIL_INVALID;
		final String username = USERNAME;
		final String password = PASSWORD;

		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
   		                              .add("nachname", nachname)
   		                              .add("vorname", vorname)
   		                              .add("email", email)
   		                              .addNull("adresse")
   		                              .build();

		// When
		final Response response = given().contentType(APPLICATION_JSON)
				                         .body(jsonObject.toString())
                                         .auth()
                                         .basic(username, password)
                                         .post(KUNDEN_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
		assertThat(response.asString().isEmpty(), is(false));
		
		LOGGER.finer("ENDE");
	}
	
	@Test
	public void updateKunde() {
		LOGGER.log(FINER, "BEGINN updateTest");
		
		// Given
		final Long kundeId = KUNDE_ID_UPDATE;
		final String neuerNachname = NEUER_NACHNAME;
		final String username = USERNAME;
		final String password = PASSWORD;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                                   .get(KUNDEN_ID_PATH);
		
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}
    	assertThat(jsonObject.getJsonNumber("kundenNr").longValue(), is(kundeId.longValue()));
    	
    	// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
    	final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
    	final Set<String> keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("nachname".equals(k)) {
    			job.add("nachname", neuerNachname);
    		}
    		else {
    			job.add(k, jsonObject.get(k));
    		}
    	}
    	jsonObject = job.build();
    	
		response = given().contentType(APPLICATION_JSON)
				          .body(jsonObject.toString())
                          .auth()
                          .basic(username, password)
                          .put(KUNDEN_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));

   	}
	
	@Test
	public void deleteKunde() {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long kundeId = KUNDE_ID_DELETE;
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		
		// When
		final Response response = given().auth()
                                         .basic(username, password)
                                         .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                                         .delete(KUNDEN_ID_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		LOGGER.finer("ENDE");
	}
		
	@Test
	public void deleteKundeFehlendeBerechtigung() {
		LOGGER.finer("BEGINN");
		
		// Given
		final String username = USERNAME;
		final String password = PASSWORD;
		final Long kundeId = KUNDE_ID_DELETE_FORBIDDEN;
		
		// When
		final Response response = given().auth()
                                         .basic(username, password)
                                         .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
                                         .delete(KUNDEN_ID_PATH);
		
		// Then
		assertThat(response.getStatusCode(), anyOf(is(HTTP_FORBIDDEN), is(HTTP_NOT_FOUND)));
		
		LOGGER.finer("ENDE");
	}
	
}
