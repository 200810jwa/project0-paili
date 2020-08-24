import java.util.HashSet;

import com.revature.accounts.BankAccount;
import com.revature.services.EmployeeServices;
import com.revature.users.User;

public class TestDataContainer {
	private User c = new User("jdoe",
			"hunter2",
			"jdoe@mail.mail",
			123456789,
			"John",
			"Doe",
			"1234567890",
			"123 Fake St.",
			-1);
	private User teller = new User("jane.doe", "employee1", "jdoe@mail.mail", 123456780, "Jane", "Doe", "1234567890",
			"123 Fake St.", 0, "JANEDOE123", 50.00);
	private User admin = new User("bob.smith", "admin", "admin@mail.mail", 111111111, "Bob", "Smith", "1234567890",
			"123 Fake St.", 1, "BOBSMITH123", 100.00);
	
	private HashSet<BankAccount> accounts1, accounts2;
	private BankAccount acc1, acc2, acc3;
	private EmployeeServices es1, es2;
	
	public TestDataContainer() {
		// c has no accounts
		// admin has acc1, in accounts1
		// teller has acc2/3, in accounts2
		
		accounts1 = new HashSet<BankAccount>();
		accounts2 = new HashSet<BankAccount>();
		acc1 = new BankAccount("ABC123", 100.00, admin, true);
		acc2 = new BankAccount("XYZ890", 0.00, teller, false);
		acc3 = new BankAccount("AAA000", 100.00, teller, true);
		accounts1.add(acc1);
		accounts2.add(acc2);
		accounts2.add(acc3);
	};
	
}
