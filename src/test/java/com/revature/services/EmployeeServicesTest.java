package com.revature.services;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.revature.DAO.BankDAO;
import com.revature.DAO.DAOUtilities;
import com.revature.accounts.BankAccount;
import com.revature.users.User;

public class EmployeeServicesTest {
	private User teller = new User("jane.doe", "hunter3", "jane.doe@mail.com", 123456780, "Jane", "Doe", "1234567890",
			"123 Fake St.", 0, "janedoe123", 50.00);
	private User admin = new User("bob.smith", "admin", "admin@mail.mail", 111111111, "Bob", "Smith", "1234567890",
			"123 Fake St.", 1, "bobsmith123", 100.00);
	private HashSet<BankAccount> accounts1, accounts2;
	private BankAccount acc1, acc2, acc3;
	private EmployeeServices es1, es2;

	@Mock
	BankDAO dao;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		accounts1 = new HashSet<BankAccount>();
		accounts2 = new HashSet<BankAccount>();
		acc1 = new BankAccount("ABC123", 100.00, admin, true);
		acc2 = new BankAccount("XYZ890", 0.00, teller, false);
		acc3 = new BankAccount("XYZ123", 0.00, teller, true);

		accounts1.add(acc1);
		accounts2.add(acc2);
		accounts2.add(acc3);
//		admin.setAccounts(accounts1);
//		teller.setAccounts(accounts2);
		
		es1 = new EmployeeServices(admin,dao);
		es2 = new EmployeeServices(teller,dao);
		
		
		when(dao.updateBalance(any(BankAccount.class))).thenReturn(true);
		when(dao.updateBankAccount(any(BankAccount.class))).thenReturn(true);
		when(dao.deleteBankAccount(any(BankAccount.class))).thenReturn(true);
		when(dao.getBankAccount(acc1.getAccountID())).thenReturn(acc1);
		when(dao.getBankAccount(acc2.getAccountID())).thenReturn(acc2);
		when(dao.getBankAccountsFromUser(teller)).thenReturn(accounts2);
		when(dao.getBankAccountsFromUser(admin)).thenReturn(accounts1);
		
		Set<User> users = new HashSet<User>();
		users.add(teller);
		users.add(admin);
		when(dao.getAllUsers()).thenReturn(users);
		Set<BankAccount> accounts = new HashSet<BankAccount>();
		accounts.addAll(accounts1);
		accounts.addAll(accounts2);
		when(dao.getAllBankAccounts()).thenReturn(accounts);
		
		
		Set<BankAccount> totalAccounts = new HashSet<BankAccount>();
		accounts1.addAll(accounts2);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testApproveApplication() {
		assertTrue(es1.approveApplication(acc2));
		assertTrue(acc2.isApproved());

		// already approved
		assertFalse(es1.approveApplication(acc1));
		assertFalse(es1.approveApplication(acc2));
	}
	
	@Test
	public void testDenyApplication() {
		assertFalse(es2.denyApplication(acc1));
		assertTrue(es2.denyApplication(acc2));

	}
	
	@Test
	public void testCancelAccount() {
		assertTrue(es1.cancelAccount(acc1));
		assertFalse(es2.cancelAccount(acc1));
	}
	
	@Test
	public void viewAllAccountsTest() {
		//System.out.println(es1.viewAllAccounts());
		assertTrue(es1.viewAllAccounts().size() == 3);
	}
	
	@Test
	public void viewForUserTest() {
		EmployeeServices es = new EmployeeServices(admin, dao);
		
		Set<BankAccount> view = es.viewAccountsForUser(admin);
		System.out.println(view);
		System.out.println(view.size());
		assertTrue(view.size() == 3);
		view = es.viewAccountsForUser(teller);
		for (BankAccount b : view) {
			System.out.println(b);
		}
		assertTrue(view.size() == 2);
	}
	
}
