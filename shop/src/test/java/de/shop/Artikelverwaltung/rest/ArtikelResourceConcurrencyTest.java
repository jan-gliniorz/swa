package de.shop.Artikelverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH;
import static de.shop.util.TestConstants.ARTIKEL_ID_PATH_PARAM;
import static de.shop.util.TestConstants.ARTIKEL_PATH;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.runners.MethodSorters.NAME_ASCENDING;

import java.io.StringReader;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.json.JsonObject;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.util.AbstractResourceTest;
import de.shop.util.ConcurrentDelete;
import de.shop.util.ConcurrentUpdate;

@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class ArtikelResourceConcurrencyTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	private static final Long ARTIKEL_ID_UPDATE = Long.valueOf(302);
	private static final String NEUE_BEZEICHNUNG2 = "Test2";
	private static final String NEUE_BEZEICHNUNG1 = "TEST1";
	private static final Long ARTIKEL_ID_DELETE1 = Long.valueOf(312);
	private static final Long ARTIKEL_ID_DELETE2 = Long.valueOf(313);

//	@Test
//	public void updateUpdate() throws InterruptedException, ExecutionException {
//		LOGGER.finer("BEGINN");
//		
//		// Given
//		final Long artikelId = ARTIKEL_ID_UPDATE;
//		final String neueBezeichnung2 = NEUE_BEZEICHNUNG2;
//		final String neueBezeichnung1 = NEUE_BEZEICHNUNG1;
//		final String username = USERNAME;
//		final String password = PASSWORD;
//		
//		// When
//		Response response = given().header(ACCEPT, APPLICATION_JSON)
//				                   .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
//                                   .get(ARTIKEL_ID_PATH);
//		JsonObject jsonObject;
//		try (final JsonReader jsonReader =
//				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
//			jsonObject = jsonReader.readObject();
//		}
//
//		final JsonObject changedJsonObject = getJsonBuilderFactory().createObjectBuilder()
//				.add("id", artikelId)
//				.add("lagerpositionenUri", jsonObject.get("lagerpositionenUri"))
//				.add("bezeichnung", neueBezeichnung2)
//				.add("beschreibung", jsonObject.get("beschreibung"))
//				.add("bild", jsonObject.get("bild"))
//				.add("preis", jsonObject.get("preis"))
//				.build();
//
//    	final ConcurrentUpdate concurrentUpdate = new ConcurrentUpdate(changedJsonObject, ARTIKEL_PATH,
//    			                                                       username, password);
//    	final ExecutorService executorService = Executors.newSingleThreadExecutor();
//		final Future<Response> future = executorService.submit(concurrentUpdate);
//		response = future.get();   // Warten bis der "parallele" Thread fertig ist
//		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
//		
//		
//		final JsonObject jsonObjectnew = getJsonBuilderFactory().createObjectBuilder()
//				.add("id", artikelId)
//				.add("lagerpositionenUri", jsonObject.get("lagerpositionenUri"))
//				.add("bezeichnung", neueBezeichnung1)
//				.add("beschreibung", jsonObject.get("beschreibung"))
//				.add("bild", jsonObject.get("bild"))
//				.add("preis", jsonObject.get("preis"))
//				.build();
//    	// Fehlschlagendes Update
//		// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
////    	job = getJsonBuilderFactory().createObjectBuilder();
////    	keys = jsonObject.keySet();
////    	for (String k : keys) {
////    		if("lagerpositionen".equals(k) || "lagerposition".equals(k))
////    			continue;
////    		if ("bezeichnung".equals(k)) {
////    			job.add("bezeichnung", neueBezeichnung1);
////    		}
////    		else {
////    			job.add(k, jsonObject.get(k));
////    		}
////    	}
////    	jsonObject = job.build();
//		response = given().contentType(APPLICATION_JSON)
//				          .body(jsonObjectnew.toString())
//		                  .auth()
//		                  .basic(username, password)
//		                  .put(ARTIKEL_PATH);
//    	
//		// Then
//		assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
//		
//		LOGGER.finer("ENDE");
//	}
	
	@Test
	public void updateDelete() throws InterruptedException, ExecutionException {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long artikelId = ARTIKEL_ID_DELETE1;
    	final String neueBezeichnung1 = NEUE_BEZEICHNUNG1;
		final String username = USERNAME;
		final String password = PASSWORD;
		final String username2 = USERNAME_ADMIN;
		final String password2 = PASSWORD_ADMIN;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
                                   .get(ARTIKEL_ID_PATH);
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}

		// Konkurrierendes Delete
    	final ConcurrentDelete concurrentDelete = new ConcurrentDelete(ARTIKEL_PATH + '/' + artikelId,
    			                                                       username2, password2);
    	final ExecutorService executorService = Executors.newSingleThreadExecutor();
		final Future<Response> future = executorService.submit(concurrentDelete);
		response = future.get();   // Warten bis der "parallele" Thread fertig ist
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
		final JsonObject jsonObjectchanged = getJsonBuilderFactory().createObjectBuilder()
				.add("id", artikelId)
				.add("lagerpositionenUri", jsonObject.get("lagerpositionenUri"))
				.add("bezeichnung", neueBezeichnung1)
				.add("beschreibung", jsonObject.get("beschreibung"))
				.add("bild", jsonObject.get("bild"))
				.add("preis", jsonObject.get("preis"))
				.build();		
		
    	// Fehlschlagendes Update
//		final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
//    	final Set<String> keys = jsonObject.keySet();
//    	for (String k : keys) {
//    		if ("bezeichnung".equals(k)) {
//    			job.add("bezeichnung", neueBezeichnung1);
//    		}
//    		else {
//    			job.add(k, jsonObject.get(k));
//    		}
//    	}
    	response = given().contentType(APPLICATION_JSON)
    			          .body(jsonObjectchanged.toString())
                          .auth()
                          .basic(username, password)
                          .put(ARTIKEL_PATH);
		
		// Then
    	assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		
		LOGGER.finer("ENDE");
	}
	
	@Test
	public void deleteUpdate() throws InterruptedException, ExecutionException {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long artikelId = ARTIKEL_ID_DELETE2;
    	final String neueBezeichnung1 = NEUE_BEZEICHNUNG1;
    	final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		final String username2 = USERNAME;
		final String password2 = PASSWORD;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
                                   .get(ARTIKEL_ID_PATH);
		
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}
		
		// Konkurrierendes Update
//		final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
//    	final Set<String> keys = jsonObject.keySet();
//    	for (String k : keys) {
//    		if ("bezeichnung".equals(k)) {
//    			job.add("bezeichnung", neueBezeichnung1);
//    		}
//    		else {
//    			job.add(k, jsonObject.get(k));
//    		}
//    	}
		
		final JsonObject jsonObjectchanged = getJsonBuilderFactory().createObjectBuilder()
				.add("id", artikelId)
				.add("lagerpositionenUri", jsonObject.get("lagerpositionenUri"))
				.add("bezeichnung", neueBezeichnung1)
				.add("beschreibung", jsonObject.get("beschreibung"))
				.add("bild", jsonObject.get("bild"))
				.add("preis", jsonObject.get("preis"))
				.build();	
		
    	final ConcurrentUpdate concurrenUpdate = new ConcurrentUpdate(jsonObjectchanged, ARTIKEL_PATH,
    			                                                      username2, password2);
    	final ExecutorService executorService = Executors.newSingleThreadExecutor();
		final Future<Response> future = executorService.submit(concurrenUpdate);
		response = future.get();   // Warten bis der "parallele" Thread fertig ist
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
    	// Erfolgreiches Delete trotz konkurrierendem Update
		response = given().auth()
                          .basic(username, password)
                          .pathParameter(ARTIKEL_ID_PATH_PARAM, artikelId)
                          .delete(ARTIKEL_ID_PATH);
		
		// Then
    	assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
		LOGGER.finer("ENDE");
	}
}
