package de.shop.Artikelverwaltung.rest;
import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.ARTIKEL_URI;
import static de.shop.util.TestConstants.LAGERPOSITION_ID_PATH;
import static de.shop.util.TestConstants.LAGERPOSITION_ID_PATH_PARAM;
import static de.shop.util.TestConstants.LAGERPOSITION_PATH;
import static de.shop.util.TestConstants.LOCATION;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;

import javax.json.JsonObject;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.util.AbstractResourceTest;



@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class LagerpositionResourceTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final Long LAGERPOS_ID_VORHANDEN = Long.valueOf(402);
	private static final Long LAGERPOS_ID_NICHT_VORHANDEN = Long.valueOf(660);
	private static final Long NEUE_ANZAHL = Long.valueOf(6);
	private static final Long LAGERPOS_ID_UPDATE = Long.valueOf(400);
	private static final Long UPDATE_ANZAHL = Long.valueOf(6);
	private static final Long LAGERPOSITION_ID_DELETE = Long.valueOf(404);
	private static final Long ARTIKEL_ID_VORHANDEN = Long.valueOf(301);
	private static final Long NEUER_ARTIKEL = Long.valueOf(302);

	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Test
	public void findLagerposById() {
		LOGGER.debugf("BEGINN", this);
	
		//Given
		final Long lagerposId = LAGERPOS_ID_VORHANDEN;
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		
		//When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
										 .pathParameter(LAGERPOSITION_ID_PATH_PARAM, lagerposId)
										 .auth()
										 .basic(username, password)
										 .get(LAGERPOSITION_ID_PATH);
		
		//Then
		assertThat(response.getStatusCode(), is(HTTP_OK)); //ist Link OK 
		
		//stimmt JSON-Datensatz mit gesuchter ID Überein
		try (final JsonReader jsonReader =  
											getJsonReaderFactory()
											.createReader(new StringReader(response.asString()))) {
			final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("id").longValue(), is(lagerposId.longValue()));
		}
		
		LOGGER.debugf("ENDE", this);
	}
	
	@Test
	public void findLagerpositionByIdNichtVorhanden() {
		
		LOGGER.debugf("Beginn", this);
		
		//Given
		final Long lagerposId = LAGERPOS_ID_NICHT_VORHANDEN;
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		
		//WHEN
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
										 .pathParam(LAGERPOSITION_ID_PATH_PARAM, lagerposId)
										 .auth()
										 .basic(username, password)
										 .get(LAGERPOSITION_ID_PATH);
		//Then
		assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		
		LOGGER.debugf("Ende", this);		
	}
		
	@Test
	public void createLagerposition() {
		LOGGER.debugf("BEGINN", this);
		
		// Given
		final Long anzahl = NEUE_ANZAHL;	
		final Long artikelId = ARTIKEL_ID_VORHANDEN;
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
		 		             		  .add("artikelUri", ARTIKEL_URI + "/" + artikelId)
									  .add("anzahl", anzahl)
		                              .build();

		// When
		final Response response = given().contentType(APPLICATION_JSON)
				                         .body(jsonObject.toString())
				                         .auth()
                                         .basic(username, password)
                                         .post(LAGERPOSITION_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		final String location = response.getHeader(LOCATION);
		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id.longValue() > 0, is(true));

		LOGGER.debugf("ENDE", this);
	}

	@Test
	public void updateLagerposition() {
		LOGGER.debugf("BEGINN", this);
		
		// Given
		final Long lagerposId = LAGERPOS_ID_UPDATE;
		final Long neueAnzahl = UPDATE_ANZAHL;	
		final Long artikel = NEUER_ARTIKEL;
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(LAGERPOSITION_ID_PATH_PARAM, lagerposId)
				                   .auth()
				                   .basic(username, password)
                                   .get(LAGERPOSITION_ID_PATH);
		
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}
    	assertThat(jsonObject.getJsonNumber("id").longValue(), is(lagerposId.longValue()));
    	
        final JsonObject changedJsonObject = getJsonBuilderFactory().createObjectBuilder()
        									.add("id", lagerposId)
        									.add("artikelUri", ARTIKEL_URI + "/" + artikel)
        		  					  		.add("anzahl", neueAnzahl)
        		  					  		.build();
    	
		response = given().contentType(APPLICATION_JSON)
				          .body(changedJsonObject.toString())
				          .auth()
                          .basic(username, password)
                          .put(LAGERPOSITION_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
   	}

	@Test
	public void deleteLagerposition() {
		LOGGER.debugf("BEGINN", this);
		
		// Given
		final Long lagerposId = LAGERPOSITION_ID_DELETE;
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		
		// When
		final Response response = given().pathParameter(LAGERPOSITION_ID_PATH_PARAM, lagerposId)
										 .auth()
										 .basic(username, password)
                                         .delete(LAGERPOSITION_ID_PATH);
                                         
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		LOGGER.debugf("ENDE", this);
	}
}

////	@Test
////	public void deleteKundeMitBestellung() {
////		LOGGER.finer("BEGINN");
////		
////		// Given
////		final Long kundeId = KUNDE_ID_DELETE_MIT_BESTELLUNGEN;
////		final String username = USERNAME_ADMIN;
////		final String password = PASSWORD_ADMIN;
////		
////		// When
////		final Response response = given().auth()
////                                         .basic(username, password)
////                                         .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
////                                         .delete(KUNDEN_ID_PATH);
////		
////		// Then
////		assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
////		final String errorMsg = response.asString();
////		assertThat(errorMsg, startsWith("Kunde mit ID=" + kundeId + " kann nicht geloescht werden:"));
////		assertThat(errorMsg, endsWith("Bestellung(en)"));
////
////		LOGGER.finer("ENDE");
////	}
//
////	@Test
////	public void uploadInvalidMimeType() throws IOException {
////		LOGGER.finer("BEGINN");
////		
////		// Given
////		final Long kundeId = KUNDE_ID_UPLOAD;
////		final String fileName = FILENAME_UPLOAD_INVALID_MIMETYPE;
////		final String username = USERNAME;
////		final String password = PASSWORD;
////		
////		// Datei als byte[] einlesen
////		byte[] bytes;
////		try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
////			Files.copy(Paths.get(fileName), stream);
////			bytes = stream.toByteArray();
////		}
////		
////		// byte[] als Inhalt eines JSON-Datensatzes mit Base64-Codierung
////		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
////	                                  .add("bytes", DatatypeConverter.printBase64Binary(bytes))
////	                                  .build();
////		
////		// When
////		final Response response = given().contentType(APPLICATION_JSON)
////				                         .body(jsonObject.toString())
////				                         .auth()
////				                         .basic(username, password)
////				                         .pathParameter(KUNDEN_ID_PATH_PARAM, kundeId)
////				                         .post(KUNDEN_ID_FILE_PATH);
////		
////		assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
////		assertThat(response.asString(), is(NoMimeTypeException.MESSAGE));
////	}
//}

