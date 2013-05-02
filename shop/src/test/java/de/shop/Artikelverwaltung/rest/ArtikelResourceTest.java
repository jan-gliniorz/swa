package de.shop.Artikelverwaltung.rest;
import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH_PARAM;
import static de.shop.util.TestConstants.ARTIKEL_PATH;
import static de.shop.util.TestConstants.LAGERPOSITION_URI;
import static de.shop.util.TestConstants.LOCATION;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;

import javax.json.JsonObject;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.util.AbstractResourceTest;



@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class ArtikelResourceTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final Long ARTIKEL_ID_VORHANDEN = Long.valueOf(300);
	private static final Long ARTIKEL_ID_NICHT_VORHANDEN = Long.valueOf(660);
	//private static final Long ARTIKEL_ID_DELETE = Long.valueOf(312);
	private static final String NEU_BEZEICHNUNG = "Regenjacke";
	private static final String NEUE_BESCHREIBUNG = "Jacke";
	private static final BigDecimal NEU_PREIS = BigDecimal.valueOf(23);
	private static final Long ARTIKEL_ID_UPDATE = Long.valueOf(302);
	private static final String UPDATE_BEZEICHNUNG = "Hose";
	private static final String UPDATE_BESCHREIBUNG = "Chino";
	private static final BigDecimal UPDATE_PREIS = BigDecimal.valueOf(44);
	private static final Long UPDATE_ARTIKEL_LP = Long.valueOf(402);
	private static final String UPDATE_BILD = "X";
	
	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	
	@Ignore
   	@Test
	public void notYetImplemented() {
		fail();
	}
	
	@Test
	public void findArtikelById(){
		LOGGER.debugf("Beginn", this);
		
		final Long artikelId = ARTIKEL_ID_VORHANDEN;
		
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
										 .pathParam(ARTIKEL_ID_PATH_PARAM, artikelId)
										 .get(ARTIKEL_ID_PATH);
		
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		try(final JsonReader jsonReader = 
				 			getJsonReaderFactory().createReader(new StringReader(response.asString()))){
			final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("id").longValue(), is(artikelId.longValue()));
		}
		LOGGER.debugf("ENDE", this);
	}
	
	@Test
	public void findArtikelByIdNichtVorhanden() {
			LOGGER.debugf("BEGINN", this);
			
			final Long artikelId = ARTIKEL_ID_NICHT_VORHANDEN;
			
			final Response response = given().header(ACCEPT, APPLICATION_JSON)
											 .pathParam(ARTIKEL_ID_PATH_PARAM, artikelId)
											 .get(ARTIKEL_ID_PATH);
			
			assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
			LOGGER.debugf("BEGINN", this);
	}
	
	@Test
	public void createArtikel() {
		LOGGER.debugf("BEGINN", this);
		
		final String bezeichnung = NEU_BEZEICHNUNG;
		final String beschreibung = NEUE_BESCHREIBUNG;
		final BigDecimal preis = NEU_PREIS;
		final String username = USERNAME;
		final String password = PASSWORD;
		
		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
				                                             .add("bezeichnung", bezeichnung)
															 .add("beschreibung", beschreibung)
															 .add("preis", preis)
															 .build();
																		   
	    final Response response = given().contentType(APPLICATION_JSON)
	    								 .body(jsonObject.toString())
	    								 .auth()
                                         .basic(username, password)
	    								 .post(ARTIKEL_PATH);
										
																		   
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		final String location = response.getHeader(LOCATION);
		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id.longValue() > 0, is(true));

		LOGGER.debugf("ENDE", this);
		
		
	}

	@Test
	public void updateArtikel() {
		LOGGER.debugf("BEGINN", this);
		
		// Given
		final Long artikelId = ARTIKEL_ID_UPDATE;
		final String neueBezeichnung = UPDATE_BEZEICHNUNG;
		final String neueBeschreibung = UPDATE_BESCHREIBUNG;
		final BigDecimal neuerPreis = UPDATE_PREIS;
		final Long lagerpos = UPDATE_ARTIKEL_LP;
		final String neuesBild = UPDATE_BILD;
		final String username = USERNAME;
		final String password = PASSWORD;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
				                   .auth()
                                   .basic(username, password)
                                   .get(ARTIKEL_ID_PATH);
		
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}
    	assertThat(jsonObject.getJsonNumber("id").longValue(), is(artikelId.longValue()));
    	
    	// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
        JsonObject changedJsonObject = getJsonBuilderFactory().createObjectBuilder()
        								.add("id", artikelId)
        								.add("lagerpositionenUri", LAGERPOSITION_URI + "/" + lagerpos)
        								.add("bezeichnung", neueBezeichnung)
        								.add("beschreibung", neueBeschreibung)
        								.add("bild", neuesBild)
			  							.add("preis", neuerPreis)
        		  					  	.build();
    	
		response = given().contentType(APPLICATION_JSON)
				          .body(changedJsonObject.toString())
                          .put(ARTIKEL_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
   	}
	
//	@Test
//	public void deleteArtikel() {
//		LOGGER.debugf("BEGINN", this);
//		
//		// Given
//		final Long artikelId = ARTIKEL_ID_DELETE;
//		
//		// When
//		final Response response = given().pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
//                                         .delete(ARTIKEL_ID_PATH);
//                                         
//		
//		// Then
//		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
//		LOGGER.debugf("ENDE", this);
//	}
}