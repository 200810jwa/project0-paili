package com.revature.services;

import static org.junit.Assert.*;

import java.util.Scanner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.revature.DAO.BankDAO;
import com.revature.users.*;

public class UITest {
	private User c = new User("testclient",
			"hunter2",
			"jdoe@mail.mail",
			222222222L,
			"John",
			"Doe",
			"11234567890",
			"123 Fake St.",
			-1);
	private User teller = new User("jane.doe",
			"hunter3", 
			"jane.doe@mail.mail",
			123456788,
			"Jane",
			"Doe",
			"1234567890",
			"123 Fake St.",
			0,
			"JANEDOE123",
			50.00);
	BankDAO dao;
	private UI uiTest = new UI(new Scanner(System.in));

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		dao = new BankDAO();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void validateAccountTypeTest() {
		System.out.println("-------- validateAccountTypeTest() --------");
		uiTest = new UI(new Scanner("employee\n"));
		String type = uiTest.validateAccountType();
		assertTrue(type.equals("CLIENT") | type.equals("EMPLOYEE"));
		uiTest = new UI(new Scanner("Client\n"));
		type = uiTest.validateAccountType();
		assertTrue(type.equals("CLIENT") | type.equals("EMPLOYEE"));
	}
	
	@Test 
	public void validatePhoneNumberTest() {
		System.out.println("-------- validatePhoneNumberTest --------");
		// int pn = uiTest.validatePhoneNumber();
		// assertTrue(Integer.class.isInstance(pn));
		uiTest = new UI(new Scanner("+1 (123) 456 - 7890\n"));
		assertEquals(uiTest.validatePhoneNumber(), "11234567890");
	}
	
	@Test
	public void validateEmailTest() {
		System.out.println("-------- validateEmailTest --------");
		uiTest = new UI(new Scanner("jdoe@mail.mail\n"));
		assertFalse(uiTest.validateEmail().equals(teller.getEmail()));
		uiTest = new UI(new Scanner("jdoe@mail.mail\n"));
		assertTrue(uiTest.validateEmail().equalsIgnoreCase(c.getEmail()));
	}
	
	@Test
	public void validateSSNTest() {
		System.out.println("-------- validateSSNTest --------");
		uiTest = new UI(new Scanner("   123-45-6789++dwdqwd\n"));
		assertEquals(uiTest.validateSSN(), 123456789);
	}
	
	@Test
	public void registerTest() {
		System.out.println("-------- registerTest -------- ");
		String temp = "Client\n"
				+ "testclient\n"
				+ "hunter2\n"
				+ "John\n"
				+ "Doe\n"
				+ "+1 (123) 456-7890\n"
				+ "123 Fake St.\n"
				+ "jdoe@mail.mail\n"
				+ "222-22-2222\n";		
		uiTest = new UI(new Scanner(temp));
		User testAcct = uiTest.register();
		System.out.println(testAcct);
		
		assertEquals(testAcct, c);
		assertFalse(testAcct.equals(teller));
		
		// password case sensitive, username is not
		assertTrue(testAcct.getUsername().equalsIgnoreCase("testclient"));
		assertTrue(testAcct.passMatch("hunter2"));
		// check password is hashed
		assertFalse(testAcct.getPasshash().equals("hunter2")); 
		assertFalse(testAcct.passMatch("HUNTER2"));
		testAcct.toString();
	}
	

}
