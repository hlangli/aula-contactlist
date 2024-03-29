package dk.langli.aula.contactlist;

import static dk.langli.bahco.Bahco.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class Main {
	private static final String NL = "\n";
	private static final ObjectMapper json = new ObjectMapper();
	
	public static void main(String[] args) {
		String username = args.length > 0 ? args[0] : null;
		String password = args.length > 1 ? args[1] : null;
		if(username == null || password == null) {
			try(BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in))) {
				if(username == null) {
					System.err.print("AULA Username: ");
					username = wrap(stdin::readLine);
				}
				if(password == null) {
					System.err.print("AULA Password: ");
					password = wrap(stdin::readLine);
				}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
		UniLogin uniLogin = UniLogin.builder()
				.username(username)
				.password(password)
				.build();
		uniLogin.acceptDriver(driver -> {
			Aula aula = Aula.builder()
					.driver(driver)
					.build();
			ProfileContext profileContext = aula.getProfileContext();
			List<Institution> institutions = profileContext.getData().getInstitutions();
			institutions.forEach(institution -> {
				institution.getGroups().stream()
					.sorted((a, b) -> a.getName().compareTo(b.getName()))
					.filter(g -> g.getRole().equals("member"))
					.forEach(group -> {
						List<Contactlist> contactlists = aula.getGuardians(group.getId());
						contactlists.forEach(c -> convert(group.getName(), c));
					});
			});
//			System.out.println(fields.stream().map(f -> s("\"%s\"", f)).collect(Collectors.joining(",")));
		});
		System.exit(0);
	}
	
	private static void convert(String classname, Contactlist contactlist) {
		for(Child child: contactlist.getData()) {
			for(Guardian guardian: child.getRelations()) {
				System.out.println(toVcard(classname, guardian, child));
			}
		}
	}
	
	private static boolean isAddressValid(Address address) {
		return address != null && !(!address.getStreet().contains(" ") && address.getPostalCode() == null && address.getPostalDistrict() == null);
	}
	
	private static String toVcard(String classname, Guardian guardian, Child child) {
		JavaType mapType = TypeFactory.defaultInstance().constructMapLikeType(HashMap.class, String.class, Object.class);
		Map<String, Object> a = json.convertValue(guardian.getAddress(), mapType);
		a = flatten(a);
		List<Map<String, String>> vcardMap = list(
				entry("BEGIN", "VCARD"),
				entry("VERSION", "3.0"),
				entry("FN", s("%s %s", guardian.getFirstName(), guardian.getLastName())),
				entry("N", s("%s;%s", guardian.getLastName(), guardian.getFirstName())),
				entry("EMAIL;TYPE=INTERNET;TYPE=HOME", guardian.getEmail()),
				entry("TEL;TYPE=HOME", guardian.getHomePhoneNumber()),
				entry("TEL;TYPE=WORK", guardian.getWorkPhoneNumber()),
				entry("TEL;TYPE=CELL", guardian.getMobilePhoneNumber()),
				entry("ADR;TYPE=HOME", isAddressValid(guardian.getAddress()) ? subst(";;${street};${postalDistrict};;${postalCode};DK;${street}\\n${postalCode} ${postalDistrict}\\nDK", a) : null),
				entry("TITLE", s("%s til %s", guardian.getRelation(), child.getFirstName())),
				entry("X-ABRELATEDNAMES;TYPE=CHILD", s("%s %s", child.getFirstName(), child.getLastName())),
				entry("NOTE", s("Forældre i %S", classname)),
				entry("CATEGORIES", classname),
//				entry("PHOTO;ENCODING=b;TYPE=JPEG", "iVBORw0KGgoAAAA ... bnwwAAAABJRU5ErkJggg=="),
				entry("END", "VCARD")
		);
		return vcardMap.stream()
				.map(m -> m.entrySet().stream()
						.filter(e -> e.getValue() != null)
						.map(e -> s("%s:%s", e.getKey(), e.getValue()))
						.collect(Collectors.toList()))
				.filter(l -> !l.isEmpty())
				.flatMap(List::stream)
				.collect(Collectors.joining(NL));
	}
}
