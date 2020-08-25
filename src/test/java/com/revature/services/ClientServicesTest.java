package com.revature.services;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.revature.DAO.BankDAO;
import com.revature.accounts.BankAccount;
import com.revature.users.User;

public class ClientServicesTest {
	private User c = new User("jdoe", "hunter2", "jdoe@mail.mail", 123456789, "John", "Doe", "1234567890",
			"123 Fake St.", -1);
	private User teller = new User("jane.doe", "hunter3", "jdoe@mail.mail", 123456788, "Jane", "Doe", "1234567890",
			"123 Fake St.", 0, "JANEDOE123", 50.00);
	private HashSet<BankAccount> accounts1, accounts2;
	private BankAccount acc1, acc2, acc3;
	private ClientServices cs1, cs2;

	@Mock
	BankDAO dao = new BankDAO();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		// Mockito.when(object.method(whateve ryou want).thenReturn(what you want to return);
		// see moberlies findByUsername example

		// in the @Test
		// verify(mockedDao, times(n)).findbyUsername(m).times(1);
		// checks that mockedDao invoked findbyusername n times with m arg
		
		MockitoAnnotations.initMocks(this);

		accounts1 = new HashSet<BankAccount>();
		accounts2 = new HashSet<BankAccount>();
		acc1 = new BankAccount("ABC123", 100.00, c, true);
		acc2 = new BankAccount("XYZ890", 0.00, teller, false);
		acc3 = new BankAccount("AAA000", 100.00, teller, true);
		accounts1.add(acc1);
		accounts2.add(acc1);
		accounts2.add(acc2);
//		c.setAccounts(accounts1);
//		teller.setAccounts(accounts2);
		MockitoAnnotations.initMocks(this);
		
		cs1 = new ClientServices(c,dao);
		cs2 = new ClientServices(teller,dao);
		
		
		when(dao.updateBalance(acc1)).thenReturn(true);
		when(dao.updateBalance(acc2)).thenReturn(false);
		when(dao.updateBalance(acc3)).thenReturn(true);
		when(dao.getBankAccount(acc1.getAccountID())).thenReturn(acc1);
		when(dao.getBankAccount(acc2.getAccountID())).thenReturn(acc2);
		when(dao.getBankAccountsFromUser(teller)).thenReturn(accounts2);
		when(dao.getBankAccountsFromUser(c)).thenReturn(accounts1);
		when(dao.updateMultipleAccounts(acc1,acc3)).thenReturn(true);
		when(dao.updateMultipleAccounts(acc1,acc2)).thenReturn(false);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testWithdraw() {
		cs1.withdraw(acc1, 50);
		assertTrue(acc1.getBalance() == 50.00);
		assertFalse(cs1.withdraw(acc1, -50));
		assertFalse(cs1.withdraw(acc1,0));
	}

	@Test
	public void testDeposit() {
		cs1.deposit(acc1, 50);
		assertTrue(acc1.getBalance() == 150.00);
		assertFalse(cs1.deposit(acc1, -50));
		assertFalse(cs1.deposit(acc1,0));
	}

	@Test
	public void testCheckBalance() {
		assertTrue(acc1.getBalance() == cs1.checkBalance(acc1));
		assertTrue(acc2.getBalance() == cs2.checkBalance(acc2));
		verify(dao).getBankAccount(acc1.getAccountID());
	}

	@Test
	public void testTransfer() {
		assertTrue(cs1.transfer(acc1, acc3, 50));
		assertTrue(acc3.getBalance() - acc1.getBalance() == 100);
		assertFalse(cs1.transfer(acc1, acc2, 50));
		assertFalse(cs1.transfer(acc1, acc3, -10));
	}

	@Test
	public void testHasAccount() {
		assertFalse(cs1.hasAccount(new User()));
		assertTrue(cs1.hasAccount(c));
	}

}
