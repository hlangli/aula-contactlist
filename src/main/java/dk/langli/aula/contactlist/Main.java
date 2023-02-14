package dk.langli.aula.contactlist;

import static dk.langli.bahco.Bahco.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
	private static final String TYPE = "Home";
	private static final String NL = "\n";
	private static final List<String> fields = list(
			"Name",
			"Address 1 - Type",
			"Address 1 - Street",
			"Address 1 - City",
			"Address 1 - Postal Code",
			"Subject",
			"Notes",
			"Group Membership",
			"E-mail 1 - Type",
			"E-mail 1 - Value",
			"Phone 1 - Type",
			"Phone 1 - Value",
			"Phone 2 - Type",
			"Phone 2 - Value",
			"Phone 3 - Type",
			"Phone 3 - Value"
	);
	
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
			Map<String, String> groupIds = institutions.stream()
					.map(Institution::getGroups)
					.flatMap(List::stream)
					.filter(g -> g.getRole().equals("member"))
					.collect(Collectors.toMap(g -> g.getId(), g -> g.getName()));
			System.out.println(fields.stream().map(f -> s("\"%s\"", f)).collect(Collectors.joining(",")));
			groupIds.forEach((groupId, groupName) -> {
				List<Contactlist> contactlists = aula.getGuardians(groupId);
				contactlists.forEach(c -> convert(groupName, c));
			});
		});
		System.exit(0);
	}
	
	private static void convert(String classname, Contactlist contactlist) {
		StringBuilder sb = new StringBuilder();
		for(Child child: contactlist.getData()) {
			for(Guardian guardian: child.getRelations()) {
				sb.append(toCsv(classname, guardian, child)+NL);
			}
		}
		System.out.print(sb.toString());
	}
	
	private static String toCsv(String classname, Guardian guardian, Child child) {
		Address address = guardian.getAddress();
		return list(
				s("%s %s", guardian.getFirstName(), guardian.getLastName()),
				TYPE,
				address == null ? "" : address.getStreet(),
				address == null ? "" : address.getPostalDistrict(),
				address != null && address.getPostalCode() != null ? s("%s", address.getPostalCode()) : null,
				s("Forældre i %S", classname),
				s("%s til %s", guardian.getRelation(), child.getFirstName()),
				classname,
				TYPE,
				guardian.getEmail(),
				TYPE,
				guardian.getHomePhoneNumber(),
				"Mobile",
				guardian.getMobilePhoneNumber(),
				"Work",
				guardian.getWorkPhoneNumber()
		).stream()
				.map(f -> f == null ? "" : s("\"%s\"", f))
				.collect(Collectors.joining(","));
	}
}
