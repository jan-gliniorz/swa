package de.shop.Artikelverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.ARTIKEL_URI;
import static de.shop.util.TestConstants.LIEFERUNGEN_ID_PATH;
import static de.shop.util.TestConstants.LIEFERUNGEN_ID_PATH_PARAM;
import static de.shop.util.TestConstants.LIEFERUNGEN_PATH;
import static de.shop.util.TestConstants.LIEFERUNGSPOSITIONEN_ID_PATH;
import static de.shop.util.TestConstants.LIEFERUNGSPOSITIONEN_ID_PATH_PARAM;
import static de.shop.util.TestConstants.LIEFERUNGSPOSITION_ID_QUERY_PARAM;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.util.AbstractResourceTest;


@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class LieferungResourceTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final Long LIEFERUNG_ID_VORHANDEN = Long.valueOf(450);
	private static final Long LIEFERUNGSPOSITION_ID_VORHANDEN_1 = Long.valueOf(500);
	private static final Long ARTIKEL_ID_VORHANDEN_1 = Long.valueOf(304);
	private static final Long ARTIKEL_ID_VORHANDEN_3 = Long.valueOf(303);

	private static final Long LIEFERUNG_ID_DELETE = Long.valueOf(452);
	
	private static final Long LIEFERUNG_ID_UPDATE = Long.valueOf(451);
	private static final Date LIEFERUNGSDATUM_UPDATE = new GregorianCalendar(2011,9,28).getTime();
	
	private static final Date BESTELLDATUM_NEU = new GregorianCalendar(2013,4,15).getTime();
	private static final Date LIEFERUNGSDATUM_NEU = new GregorianCalendar(2013,4,19).getTime();
			
	private static final Long LIEFERUNG_ID_NICHT_VORHANDEN = Long.valueOf(475);
	
	private final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

	@Test
	public void validate() {
		assertThat(true, is(true));
	}
	

	@Test
	public void findLieferungById() {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long lieferungId = LIEFERUNG_ID_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(LIEFERUNGEN_ID_PATH_PARAM, lieferungId)
				                         .get(LIEFERUNGEN_ID_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
		
		try (final JsonReader jsonReader = getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			 final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("id").longValue(), is(lieferungId.longValue()));
			assertThat(jsonObject.getJsonArray("lieferungspositionen").size()>0, is(true));
		}

		LOGGER.finer("ENDE");
	}


	@Test
	public void findLieferungByIdNichtVorhanden() {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long lieferungId = LIEFERUNG_ID_NICHT_VORHANDEN;
		
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
				                         .pathParameter(LIEFERUNGEN_ID_PATH_PARAM, lieferungId)
                                         .get(LIEFERUNGEN_ID_PATH);

    	// Then
    	assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		LOGGER.finer("ENDE");
	}

	
	@Test
	public void createLieferung() {
		LOGGER.finer("BEGINN");
		
		// Given
		final DateFormat formatter = FORMATTER;
		final Long artikelId1 = ARTIKEL_ID_VORHANDEN_1;
		final Long artikelId2 = ARTIKEL_ID_VORHANDEN_3;

		final Date lieferungsdatum = LIEFERUNGSDATUM_NEU;
		final Date bestelldatum = BESTELLDATUM_NEU;
		
		final String username = USERNAME;
		final String password = PASSWORD;


		final JsonObject jsonObject = getJsonBuilderFactory().createObjectBuilder()
                .add("bestelldatum", formatter.format(bestelldatum))
                .add("lieferungsdatum", formatter.format(lieferungsdatum))
                .add("lieferungspositionen", getJsonBuilderFactory().createArrayBuilder()
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
				                         .post(LIEFERUNGEN_PATH);
		
		assertThat(response.getStatusCode(), is(HTTP_CREATED));
		final String location = response.getHeader(LOCATION);
		final int startPos = location.lastIndexOf('/');
		final String idStr = location.substring(startPos + 1);
		final Long id = Long.valueOf(idStr);
		assertThat(id.longValue() > 0, is(true));

		LOGGER.finer("ENDE");
	}
	
	
//	@Test
//	public void updateLieferung() {
//		LOGGER.finer("BEGINN");
//		
//		// Given
//		final DateFormat formatter = FORMATTER;
//		final Long lieferungId = LIEFERUNG_ID_UPDATE;
//		final Date neuesLieferungsdatum = LIEFERUNGSDATUM_UPDATE;
//		final String username = USERNAME;
//		final String password = PASSWORD;
//		
//		// When
//		Response response = given().header(ACCEPT, APPLICATION_JSON)
//				                   .pathParameter(LIEFERUNGEN_ID_PATH_PARAM, lieferungId)
//                                   .get(LIEFERUNGEN_ID_PATH);
//		
//		JsonObject jsonObject;
//		try (final JsonReader jsonReader =
//				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
//			jsonObject = jsonReader.readObject();
//		}
//    	assertThat(jsonObject.getJsonNumber("id").longValue(), is(lieferungId.longValue()));
//    	
//    	// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Lieferungsdatum bauen
//    	final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
//    	final Set<String> keys = jsonObject.keySet();
//    	for (String k : keys) {
//    		if ("lieferungsdatum".equals(k)) {
//    			job.add("lieferungsdatum",formatter.format(neuesLieferungsdatum));
//    		}
//    		else {
//    			job.add(k, jsonObject.get(k));
//    		}
//    	}
//    	jsonObject = job.build();
//    	
//		response = given().contentType(APPLICATION_JSON)
//				          .body(jsonObject.toString())
//                          .auth()
//                          .basic(username, password)
//                          .put(LIEFERUNGEN_PATH);
//		
//		// Then
//		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
//   	}
	
	@Test
	public void deleteLieferungMitBestellung() {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long lieferungId = LIEFERUNG_ID_DELETE;
		final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		
		// When
		final Response response = given().pathParameter(LIEFERUNGEN_ID_PATH_PARAM, lieferungId)
                                         .delete(LIEFERUNGEN_ID_PATH);
		
		// Then
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		LOGGER.finer("ENDE");
	}


//=================================Lieferungspositionen============================================================================

	@Test
	public void findLieferungspositionenById() {
		LOGGER.finer("BEGINN");
	
		// Given
		final Long lpId = LIEFERUNGSPOSITION_ID_VORHANDEN_1;
	
	
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
			                         .pathParameter(LIEFERUNGSPOSITIONEN_ID_PATH_PARAM, lpId)
			                         .get(LIEFERUNGSPOSITIONEN_ID_PATH);
	
		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
	
		try (final JsonReader jsonReader = getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonObject jsonObject = jsonReader.readObject();
			assertThat(jsonObject.getJsonNumber("id").longValue(), is(lpId.longValue()));
		}

		LOGGER.finer("ENDE");
	}
	
	@Test
	public void findLieferungspositionenByLieferungId() {
		LOGGER.finer("BEGINN");
	
		// Given
		final Long lieferungId = LIEFERUNG_ID_VORHANDEN;
	
	
		// When
		final Response response = given().header(ACCEPT, APPLICATION_JSON)
			                         .pathParameter(LIEFERUNGEN_ID_PATH_PARAM, lieferungId)
			                         .get(LIEFERUNGSPOSITION_ID_QUERY_PARAM);
	
		// Then
		assertThat(response.getStatusCode(), is(HTTP_OK));
	
		try (final JsonReader jsonReader = getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			final JsonArray jsonArray = jsonReader.readArray();
			assertThat(jsonArray.size()> 0, is(true));
		}

		LOGGER.finer("ENDE");
	}
}
