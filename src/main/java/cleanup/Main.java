package cleanup;

public class Main {

	public static void main(String[] args) {

		// get username and password from user input?
		// ask user to confirm delete?
		// config as params or in interface?

		// alles in eine properties datei legen
		// username und pw über args holen
		// config.java auflösen

		Cleaner cleaner = new Cleaner("admin", "Harbor12345");
		cleaner.clean();
	}
}
