package com.revature.DAO;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

import com.revature.accounts.BankAccount;
import com.revature.users.User;

public class BankDAOTest {
	private TestDataContainer testdata;
	BankDAO dao;
	private PreparedStatement stmt;
	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		dao = new BankDAO();
		testdata = new TestDataContainer();
		dbSetup();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddGetDeleteUser() {		
		System.out.println("----- Add/Get/Delete User -----");
		assertTrue(dao.addUser(testdata.getClient()));
		assertFalse(dao.addUser(testdata.getClient()));
		assertEquals(testdata.getClient(),dao.getUser(123456789));
		assertTrue(dao.deleteUser(testdata.getClient()));
		
	}
	
	@Test
	public void testAddGetDeleteAccount() {		
		System.out.println("----- Add/Get/Delete Account -----");
		assertTrue(dao.addUser(testdata.getAdmin()));
		assertTrue(dao.addAccount(testdata.getAcc1()));
		assertFalse(dao.addAccount(testdata.getAcc1()));
		assertEquals(testdata.getAcc1(),dao.getBankAccount(testdata.getAcc1()));
		assertTrue(dao.deleteBankAccount(testdata.getAcc1()));
	}
	
	@Test
	public void testCheckBalance() {
		System.out.println("----- Check Balance -----");
		assertTrue(dao.checkBalance(testdata.getAcc1()) < 0);
		dao.addUser(testdata.getAdmin());
		dao.addAccount(testdata.getAcc1()); 
		assertTrue(dao.checkBalance(testdata.getAcc1()) == 100);
	}
	
	@Test
	public void testUpdate() {
		System.out.println("----- Update Methods -----");
		dao.addUser(testdata.getAdmin());
		dao.addUser(testdata.getClient());
		dao.addAccount(testdata.getAcc1()); 
		
		// updatebalance
		assertTrue(dao.updateBalance("ABC123",20));
		assertTrue(dao.getBankAccount("ABC123").getBalance() == 20);

		assertFalse(dao.updateBalance("AASDADSASDASDg89b3478", 0));
		//assertFalse(dao.updateBalance("APPROVED123", -10));
		assertFalse(dao.updateBalance(testdata.getAcc2()));
		
		// updatebankaccount

		BankAccount temp = testdata.getAcc1();
		temp.setOwner(testdata.getClient());
		assertTrue(dao.updateBankAccount(temp));
		

	}
	
	@Test
	public void testViewMethods() {
		System.out.println("----- View Methods -----");
		
		// view all users
		Set<User> results = dao.getAllUsers();
		//System.out.println(results);
		for (User u : results) {
			System.out.println(u);
		}
		
		// view all bank
		System.out.println("ALL ACCOUNTS: ");
		Set<BankAccount> results2 = dao.getAllBankAccounts();
		for (BankAccount b : results2) {
			System.out.println(b);
		}
		// view pending
		System.out.println("PENDING ACCOUNTS: ");
		results2 = dao.getPendingAccounts();
		for (BankAccount b : results2) {
			System.out.println(b);
		}
	}
	
	@Test
	public void testGetBankAccountsFromUser() {
		System.out.println("----- Get Accounts From User -----");
		dao.addUser(testdata.getTeller());
		dao.addUser(testdata.getClient());
		
		dao.addAccount(testdata.getAcc2());
		dao.addAccount(testdata.getAcc3());
		Set<BankAccount> results = dao.getBankAccountsFromUser(testdata.getTeller());
		assertEquals(results,testdata.getAccounts2());
		assertTrue(dao.getBankAccountsFromUser(testdata.getClient()).size() == 0);
//		for (BankAccount b : results) {
//			System.out.println(b);
//		}

	}
	
	@Test
	public void testUpdateMultiple() {
		
		assertFalse(dao.updateMultipleAccounts(testdata.getAcc1(),testdata.getAcc2(),testdata.getAcc3()));
		
		dao.addUser(testdata.getTeller());
		dao.addAccount(testdata.getAcc2());
		dao.addAccount(testdata.getAcc3());
		dao.addUser(testdata.getAdmin());
		dao.addUser(testdata.getClient());
		dao.addAccount(testdata.getAcc1()); 
		
		testdata.getAcc1().setBalance(1000);
		
		assertTrue(dao.updateMultipleAccounts(testdata.getAcc1(),testdata.getAcc2(),testdata.getAcc3()));
		assertTrue(dao.getBankAccount(testdata.getAcc1()).getBalance() == 1000);
		
		BankAccount b = new BankAccount();
		b.setAccountID(testdata.getAcc1().getAccountID());
		assertFalse(dao.updateMultipleAccounts(testdata.getAcc1(),b));
		
	}
	
	private void dbSetup() {
		try (Connection connection = DAOUtilities.getConnection()) {
			stmt = connection.prepareStatement("CALL project0.setup()");
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
