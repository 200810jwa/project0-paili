package com.revature;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;
import com.revature.DAO.BankDAO;
import com.revature.DAO.DAOUtilities;
import com.revature.accounts.BankAccount;
import com.revature.services.UI;
import com.revature.users.User;

public class Main {
	
	public static void main(String[] args) {
//		resetDB();
//		dbSetup();
		
		UI ui = new UI(new Scanner(System.in));
		ui.run();
	}
	
	public static void dbSetup() {
		BankDAO dao = new BankDAO();
		
		// John Doe with 2 accounts
		User jdoe = new User();
		jdoe.setUsername("jdoe");
		jdoe.setPasshash(jdoe.hashPass("hunter2"));
		jdoe.setFirstName("John");
		jdoe.setLastName("Doe");
		jdoe.setPhoneNumber("11234567890");
		jdoe.setAddress("123 Fake St.");
		jdoe.setEmail("jdoe@mail.com");
		jdoe.setSSN(123456789);	
		jdoe.setFICO(800);
		dao.addUser(jdoe);
		
		BankAccount acc1 = new BankAccount("JDOE_1",100.00,jdoe,true);
		BankAccount acc2 = new BankAccount("JDOE_2", 0.0,jdoe,false);
		
		dao.addAccount(acc1);
		dao.addAccount(acc2);
		
		User employee = new User();
		employee.setUsername("jane.doe");
		employee.setPasshash(employee.hashPass("employee1"));
		employee.setFirstName("Jane");
		employee.setLastName("Doe");
		employee.setPhoneNumber("11234567890");
		employee.setAddress("123 Fake St.");
		employee.setEmail("jane.doe@mail.com");
		employee.setSSN(123456780);
		employee.setAuth(0);
		employee.setEmployeeID("janedoe123");
		employee.setSalary(10.00);
		employee.setFICO(400);
		dao.addUser(employee);
		
		BankAccount acc3 = new BankAccount("JANE_1", 50.00,employee,true);
		dao.addAccount(acc3);
		
		User admin = new User();
		admin.setUsername("bob.smith");
		admin.setPasshash(admin.hashPass("admin"));
		admin.setFirstName("Bob");
		admin.setLastName("Smith");
		admin.setPhoneNumber("1111111111");
		admin.setAddress("123 Street Ave.");
		admin.setEmail("bob.smith@mail.com");
		admin.setSSN(111111111L);
		admin.setAuth(1);
		admin.setEmployeeID("bobsmith123");
		admin.setFICO(800);
		admin.setSalary(20.00);
		dao.addUser(admin);		
	}
	
	public static void resetDB() {
		try (Connection connection = DAOUtilities.getConnection()) {
			String sql = "CALL project0.setup()";
			CallableStatement stmt = connection.prepareCall(sql);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
