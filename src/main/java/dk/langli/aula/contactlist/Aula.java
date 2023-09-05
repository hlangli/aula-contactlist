package dk.langli.aula.contactlist;

import static dk.langli.bahco.Bahco.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Options;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Aula {
	public static final String AULA_API_VERSION = "v17";
	private static final String PROFILE_CONTEXT_URL = "https://www.aula.dk/api/${AULA_API_VERSION}/?method=profiles.getProfileContext";
	private static final String CONTACTLIST_URL = "https://www.aula.dk/api/${AULA_API_VERSION}/?method=profiles.getContactlist&groupId=${GROUP_ID}&filter=child&field=name&page=${PAGE}&order=asc";
	private final WebDriver driver;
	private ObjectMapper mapper;
	
	private ObjectMapper mapper() {
		if(mapper == null) {
			mapper = new ObjectMapper()
					.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		}
		return mapper;
	}
	
	public List<Contactlist> getGuardians(String groupId) {
		List<Contactlist> responses = list();
		AtomicInteger page = new AtomicInteger(0);
		Supplier<String> url = () -> subst(CONTACTLIST_URL, map(
				entry("AULA_API_VERSION", AULA_API_VERSION),
				entry("GROUP_ID", groupId),
				entry("PAGE", page.incrementAndGet())
		));
		Contactlist response = null;
		do {
			driver.get(url.get());
			Options options = driver.manage();
			options.timeouts().implicitlyWait(Duration.of(5, ChronoUnit.SECONDS));
			String responseJson = driver.findElement(By.tagName("body")).getText();
			response = wrap(() -> mapper().readValue(responseJson, Contactlist.class));
			responses.add(response);
		}
		while(response != null && response.getData().size() > 0);
		return responses;
	}

	public ProfileContext getProfileContext() {
		String url = subst(PROFILE_CONTEXT_URL, map(
				entry("AULA_API_VERSION", AULA_API_VERSION)
		));
		System.out.println("GET "+url);
		driver.get(url);
		Options options = driver.manage();
		options.timeouts().implicitlyWait(Duration.of(5, ChronoUnit.SECONDS));
		String responseJson = driver.findElement(By.tagName("body")).getText();
		return wrap(() -> mapper().readValue(responseJson, ProfileContext.class));
	}
}
