package com.revature.DAO;
import java.util.HashSet;

import com.revature.accounts.BankAccount;
import com.revature.users.User;

public class TestDataContainer {
	private User c; 
	private User teller;
	private User admin;
	private HashSet<BankAccount> accounts1, accounts2;
	private BankAccount acc1, acc2, acc3;
	
	public TestDataContainer() {
		// c has no accounts
		// admin has acc1, in accounts1
		// teller has acc2/3, in accounts2
		c = new User("jdoe",
				"hunter2",
				"jdoe@mail.mail",
				123456789,
				"John",
				"Doe",
				"1234567890",
				"123 Fake St.",
				-1);
		teller = new User("jane.doe", "hunter3", "jdoe@mail.mail", 123456788, "Jane", "Doe", "1234567890",
				"123 Fake St.", 0, "JANEDOE123", 50.00);
		admin = new User("admin456", "hunter1", "admin@mail.mail", 123456780, "Bob", "Smith", "1234567890",
				"123 Fake St.", 1, "BOBSMITH123", 100.00);
		accounts1 = new HashSet<BankAccount>();
		accounts2 = new HashSet<BankAccount>();
		acc1 = new BankAccount("ABC123", 100.00, admin, true);
		acc2 = new BankAccount("XYZ890", 0.00, teller, false);
		acc3 = new BankAccount("AAA000", 100.00, teller, true);
		accounts1.add(acc1);
		accounts2.add(acc2);
		accounts2.add(acc3);
	}

	public User getClient() {
		return c;
	}

	public User getTeller() {
		return teller;
	}

	public User getAdmin() {
		return admin;
	}

	public HashSet<BankAccount> getAccounts1() {
		return accounts1;
	}

	public HashSet<BankAccount> getAccounts2() {
		return accounts2;
	}

	public BankAccount getAcc1() {
		return acc1;
	}

	public BankAccount getAcc2() {
		return acc2;
	}

	public BankAccount getAcc3() {
		return acc3;
	}
	
	
}
