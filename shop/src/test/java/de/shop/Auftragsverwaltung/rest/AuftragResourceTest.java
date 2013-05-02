package de.shop.Auftragsverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.ARTIKEL_URI;
import static de.shop.util.TestConstants.AUFTRAEGE_BY_KUNDE_ID_PATH;
import static de.shop.util.TestConstants.AUFTRAEGE_BY_KUNDE_ID_PATH_PARAM;
import static de.shop.util.TestConstants.AUFTRAEGE_ID_PATH;
import static de.shop.util.TestConstants.AUFTRAEGE_ID_PATH_PARAM;
import static de.shop.util.TestConstants.AUFTRAEGE_PATH;
import static de.shop.util.TestConstants.KUNDEN_URI;
import static de.shop.util.TestConstants.LOCATION;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.logging.Logger;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.util.AbstractResourceTest;


@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class AuftragResourceTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final Long AUFTRAG_ID_VORHANDEN = Long.valueOf(150);
	private static final Long KUNDE_ID_VORHANDEN = Long.valueOf(10);
	private static final Long ARTIKEL_ID_VORHANDEN_1 = Long.valueOf(300);
	private static final Long ARTIKEL_ID_VORHANDEN_2 = Long.valueOf(301);
	
	@Test
	public void test() {
		assertThat(true, is(true));
	}
	
	@Test
	public void findAuftragAll() {
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
										.get(AUFTRAEGE_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		try (JsonReader jsonReader = getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			JsonArray jsonArray = jsonReader.readArray();
			assertThat(jsonArray.size()>0, is(true));
		}
	}

	
	@Test
	public void findAuftragById() {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long bestellungId = AUFTRAG_ID_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(AUFTRAEGE_ID_PATH_PARAM, bestellungId)
				                         .get(AUFTRAEGE_ID_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("id").longValue(), is(bestellungId.longValue()));
			assertThat(jsonObject.getString("kundeUri"), is(notNullValue()));
		}

		LOGGER.finer("ENDE");
	}
	
	@Test
	public void findAuftragByKundennummer() {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long kundeMitBestellung = KUNDE_ID_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
											.pathParameter(AUFTRAEGE_BY_KUNDE_ID_PATH_PARAM, kundeMitBestellung)
											.get(AUFTRAEGE_BY_KUNDE_ID_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		try (final JsonReader jsonReader = getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonArray jsonObjects = jsonReader.readArray();
			assertThat(jsonObjects.size()  > 0, is(true));
			for(int i=0; i < jsonObjects.size(); i++) {
				JsonObject jsonObject = jsonObjects.getJsonObject(i);
				assertThat(jsonObject.getString("kundeUri"), endsWith("/kunden/" + kundeMitBestellung));
			}
			
		}
		
		LOGGER.fine("ENDE");
	}

	@Test
	public void createAuftrag() {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long kundeId = KUNDE_ID_VORHANDEN;
		final Long artikelId1 = ARTIKEL_ID_VORHANDEN_1;
		final Long artikelId2 = ARTIKEL_ID_VORHANDEN_2;
		final String username = USERNAME;
		final String password = PASSWORD;
		
		// Neues, client-seitiges Bestellungsobjekt als JSON-Datensatz
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
				                      .add("kundeUri", KUNDEN_URI + "/" + kundeId)
				                      .add("auftragspositionen", getJsonBuilderFactory().createArrayBuilder()
				            		                            .add(getJsonBuilderFactory().createObjectBuilder()
				            		                                 .add("artikelUri", ARTIKEL_URI + "/" + artikelId1)
				            		                                 .add("anzahl", 1))
				            		                            .add(getJsonBuilderFactory().createObjectBuilder()
				            		                                 .add("artikelUri", ARTIKEL_URI + "/" + artikelId2)
				            		                                 .add("anzahl", 2)))
				                      .build();

		// When
		final Response response = given().contentType(APPLICATION_JSON)
				                         .body(jsonObject.toString())
				                         .auth()
				                         .basic(username, password)
				                         .post(AUFTRAEGE_PATH);
		
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		final String location = response.getHeader(LOCATION);
		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id.longValue() > 0, is(true));

		LOGGER.finer("ENDE");
	}
	
	@Test
	public void createAuftragWrongPassword() {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long kundeId = KUNDE_ID_VORHANDEN;
		final Long artikelId1 = ARTIKEL_ID_VORHANDEN_1;
		final Long artikelId2 = ARTIKEL_ID_VORHANDEN_2;
		final String username = USERNAME;
		final String password = PASSWORD_FALSCH;
		
		// Neues, client-seitiges Bestellungsobjekt als JSON-Datensatz
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
				                      .add("kundeUri", KUNDEN_URI + "/" + kundeId)
				                      .add("auftragspositionen", getJsonBuilderFactory().createArrayBuilder()
				            		                            .add(getJsonBuilderFactory().createObjectBuilder()
				            		                                 .add("artikelUri", ARTIKEL_URI + "/" + artikelId1)
				            		                                 .add("anzahl", 1))
				            		                            .add(getJsonBuilderFactory().createObjectBuilder()
				            		                                 .add("artikelUri", ARTIKEL_URI + "/" + artikelId2)
				            		                                 .add("anzahl", 2)))
				                      .build();

		// When
		final Response response = given().contentType(APPLICATION_JSON)
				                         .body(jsonObject.toString())
				                         .auth()
				                         .basic(username, password)
				                         .post(AUFTRAEGE_PATH);
		
		assertThat(response.getStatusCode(), is(HTTP_UNAUTHORIZED));

		LOGGER.finer("ENDE");
	}
}
