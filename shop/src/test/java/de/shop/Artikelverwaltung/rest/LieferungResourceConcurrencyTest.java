package de.shop.Artikelverwaltung.rest;

import static com.jayway.restassured.RestAssured.given;
import static de.shop.util.TestConstants.ACCEPT;
import static de.shop.util.TestConstants.LIEFERUNGEN_ID_PATH_PARAM;
import static de.shop.util.TestConstants.LIEFERUNGEN_ID_PATH;
import static de.shop.util.TestConstants.LIEFERUNGEN_PATH;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.jayway.restassured.response.Response;

import de.shop.util.ConcurrentDelete;
import de.shop.util.AbstractResourceTest;
import de.shop.util.ConcurrentUpdate;

@RunWith(Arquillian.class)
@FixMethodOrder(NAME_ASCENDING)
public class LieferungResourceConcurrencyTest extends AbstractResourceTest {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	private static final Long LIEFERUNG_ID_UPDATE = Long.valueOf(455);
	private static final Date NEUES_LIEFERUNGSDATUM_1 = new GregorianCalendar(2011,9,28).getTime();
	private static final Date NEUES_LIEFERUNGSDATUM_2 = new GregorianCalendar(2011,9,29).getTime();
	private static final Long LIEFERUNG_ID_DELETE1 = Long.valueOf(453);
	private static final Long LIEFERUNG_ID_DELETE2 = Long.valueOf(454);
	
	private final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


	@Test
	public void updateUpdate() throws InterruptedException, ExecutionException {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long lieferungId = LIEFERUNG_ID_UPDATE;
    	final Date neuesLieferungsdatum1 = NEUES_LIEFERUNGSDATUM_1;
    	final Date neuesLieferungsdatum2 = NEUES_LIEFERUNGSDATUM_2;
		final String username = USERNAME;
		final String password = PASSWORD;
		final DateFormat formatter = FORMATTER;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(LIEFERUNGEN_ID_PATH_PARAM, lieferungId)
                                   .get(LIEFERUNGEN_ID_PATH);
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}

    	// Konkurrierendes Update
		// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
    	JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
    	Set<String> keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("lieferungdatum".equals(k)) {
    			job.add("lieferungsdatum",formatter.format(neuesLieferungsdatum2));
    		}
    		else {
    			job.add(k, jsonObject.get(k));
    		}
    	}
    	final JsonObject jsonObject2 = job.build();
    	final ConcurrentUpdate concurrentUpdate = new ConcurrentUpdate(jsonObject2, LIEFERUNGEN_PATH,
    			                                                       username, password);
    	final ExecutorService executorService = Executors.newSingleThreadExecutor();
		final Future<Response> future = executorService.submit(concurrentUpdate);
		response = future.get();   // Warten bis der "parallele" Thread fertig ist
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
    	// Fehlschlagendes Update
		// Aus den gelesenen JSON-Werten ein neues JSON-Objekt mit neuem Nachnamen bauen
    	job = getJsonBuilderFactory().createObjectBuilder();
    	keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("lieferungsdatum".equals(k)) {
    			job.add("lieferungsdatum", formatter.format(neuesLieferungsdatum1));
    		}
    		else {
    			job.add(k, jsonObject.get(k));
    		}
    	}
//    	jsonObject = job.build();
		response = given().contentType(APPLICATION_JSON)
				          .body(jsonObject.toString())
		                  .auth()
		                  .basic(username, password)
		                  .put(LIEFERUNGEN_PATH);
    	
		// Then
		assertThat(response.getStatusCode(), is(HTTP_CONFLICT));
		
		LOGGER.finer("ENDE");
	}
	

	@Test
	public void updateDelete() throws InterruptedException, ExecutionException {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long lieferungId = LIEFERUNG_ID_DELETE1;
    	final Date neuesLieferungsdatum = NEUES_LIEFERUNGSDATUM_1;
		final String username = USERNAME;
		final String password = PASSWORD;
		final String username2 = USERNAME_ADMIN;
		final String password2 = PASSWORD_ADMIN;
		final DateFormat formatter = FORMATTER;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(LIEFERUNGEN_ID_PATH_PARAM, lieferungId)
                                   .get(LIEFERUNGEN_ID_PATH);
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}

		// Konkurrierendes Delete
    	final ConcurrentDelete concurrentDelete = new ConcurrentDelete(LIEFERUNGEN_PATH + '/' + lieferungId,
    			                                                       username2, password2);
    	final ExecutorService executorService = Executors.newSingleThreadExecutor();
		final Future<Response> future = executorService.submit(concurrentDelete);
		response = future.get();   // Warten bis der "parallele" Thread fertig ist
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
    	// Fehlschlagendes Update
		final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
    	final Set<String> keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("lieferungsdatum".equals(k)) {
    			job.add("lieferungsdatum", formatter.format(neuesLieferungsdatum));
    		}
    		else {
    			job.add(k, jsonObject.get(k));
    		}
    	}
    	response = given().contentType(APPLICATION_JSON)
    			          .body(jsonObject.toString())
                          .auth()
                          .basic(username, password)
                          .put(LIEFERUNGEN_PATH);
		
		// Then
    	assertThat(response.getStatusCode(), is(HTTP_NOT_FOUND));
		
		LOGGER.finer("ENDE");
	}
	

	@Test
	public void deleteUpdate() throws InterruptedException, ExecutionException {
		LOGGER.finer("BEGINN");
		
		// Given
		final Long lieferungId = LIEFERUNG_ID_DELETE2;
    	final Date neuesLieferungsdatum = NEUES_LIEFERUNGSDATUM_1;
    	final String username = USERNAME_ADMIN;
		final String password = PASSWORD_ADMIN;
		final String username2 = USERNAME;
		final String password2 = PASSWORD;
		final DateFormat formatter = FORMATTER;
		
		// When
		Response response = given().header(ACCEPT, APPLICATION_JSON)
				                   .pathParameter(LIEFERUNGEN_ID_PATH_PARAM, lieferungId)
                                   .get(LIEFERUNGEN_ID_PATH);
		
		JsonObject jsonObject;
		try (final JsonReader jsonReader =
				              getJsonReaderFactory().createReader(new StringReader(response.asString()))) {
			jsonObject = jsonReader.readObject();
		}

		// Konkurrierendes Update
		final JsonObjectBuilder job = getJsonBuilderFactory().createObjectBuilder();
    	final Set<String> keys = jsonObject.keySet();
    	for (String k : keys) {
    		if ("lieferungsdatum".equals(k)) {
    			job.add("lieferungsdatum", formatter.format(neuesLieferungsdatum));
    		}
    		else {
    			job.add(k, jsonObject.get(k));
    		}
    	}
    	final ConcurrentUpdate concurrenUpdate = new ConcurrentUpdate(jsonObject, LIEFERUNGEN_PATH,
    			                                                      username2, password2);
    	final ExecutorService executorService = Executors.newSingleThreadExecutor();
		final Future<Response> future = executorService.submit(concurrenUpdate);
		response = future.get();   // Warten bis der "parallele" Thread fertig ist
		assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
    	// Erfolgreiches Delete trotz konkurrierendem Update
		response = given().auth()
                          .basic(username, password)
                          .pathParameter(LIEFERUNGEN_ID_PATH_PARAM, lieferungId)
                          .delete(LIEFERUNGEN_ID_PATH);
		
		// Then
    	assertThat(response.getStatusCode(), is(HTTP_NO_CONTENT));
		
		LOGGER.finer("ENDE");
	}
}
