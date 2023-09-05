package dk.langli.aula.contactlist;

import static java.time.temporal.ChronoUnit.*;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@Getter
@AllArgsConstructor
public class UniLogin {
	private final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	private final String username;
	private final String password;
	
	public void acceptDriver(Consumer<WebDriver> c) {
		applyDriver(d -> {
			c.accept(d);
			return null;
		});
	}

	public <V> V applyDriver(Function<WebDriver, V> f) {
		V retval = null;
		WebDriver driver = this.driver.get();
		if(driver == null) {
			WebDriverManager.chromedriver().browserVersionDetectionCommand("google-chrome --version | cut -d ' ' -f 3");
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("--headless");
			driver = new ChromeDriver(chromeOptions);
			try {
				Options options = driver.manage();
				driver.get("http://www.aula.dk");
				options.timeouts().implicitlyWait(Duration.of(5, SECONDS));
				driver.findElement(By.className("uni-login")).click();
				options.timeouts().implicitlyWait(Duration.of(5, SECONDS));
				driver.findElement(By.cssSelector("button[value='uni_idp']")).click();
				options.timeouts().implicitlyWait(Duration.of(5, SECONDS));
				driver.findElement(By.id("username")).sendKeys(username);
				driver.findElement(By.className("button-primary")).click();
				options.timeouts().implicitlyWait(Duration.of(5, SECONDS));
				driver.findElement(By.name("password")).sendKeys(password);
				driver.findElement(By.className("button-primary")).click();
				WebDriverWait wait = new WebDriverWait(driver, Duration.of(10, SECONDS));
				wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".aula-content")));
				this.driver.set(driver);
				log.trace("Connection to Aula established");
				retval = f.apply(driver);
			}
			finally {
				this.driver.remove();
				log.trace("Closing Webdriver");
				driver.close();
				driver.quit();
				log.trace("Webdriver closed");
			}
		}
		else {
			retval = f.apply(driver);
		}
		return retval;
	}
}
