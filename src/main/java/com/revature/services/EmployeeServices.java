package com.revature.services;

import java.util.Set;
import org.apache.log4j.Logger;

import com.revature.DAO.BankDAO;
import com.revature.accounts.BankAccount;
import com.revature.users.User;

public class EmployeeServices extends ClientServices {
	
	public EmployeeServices(User user, BankDAO dao) {
		super(user, dao);
		// TODO Auto-generated constructor stub
	}
	public boolean approveApplication(BankAccount app) {
		if (super.user.getAuth() < 0) {
			System.out.println("Unauthorized action.");
			return false;
		}
		if (app.isApproved()) {
			return false;
		} else {
			app.setApproved(true);
			infolog.info("[" + super.ip + "] " + app.getAccountID() + " approved by " + user.getEmployeeID());
			return dao.updateBankAccount(app);
		}
	}
	public boolean denyApplication(BankAccount app) {
		if (super.user.getAuth() < 0) {
			System.out.println("Unauthorized action.");
			return false;
		}
		if (app.isApproved()) {
			System.out.println("This account has already been approved!");
			return false;
		} 
		infolog.info("[" + super.ip + "] " + app.getAccountID() + " rejected by " + user.getEmployeeID());
		return dao.deleteBankAccount(app);
	}
	public boolean cancelAccount(BankAccount ba) {
		if (super.user.getAuth() < 1) {
			System.out.println("Unauthorized action.");
			return false;
		}
		infolog.info("[" + super.ip + "] " + ba.getAccountID() + " canceled by " + user.getEmployeeID());
		return dao.deleteBankAccount(ba);
	}
	
	public Set<BankAccount> viewAccountsForUser(long SSN) {
		return dao.getBankAccountsFromUser(SSN);
	}
	public Set<BankAccount> viewAccountsForUser(String username) {
		return dao.getBankAccountsFromUser(username);
	}
	public Set<BankAccount> viewAccountsForUser(User u) {
		return dao.getBankAccountsFromUser(u);
	}
	
	public Set<BankAccount> viewAllAccounts() {
		if (super.user.getAuth() < 0) {
			System.out.println("Unauthorized action.");
			return null;
		}
		return dao.getAllBankAccounts();
	}
	
	public Set<User> viewAllUsers() {
		if (super.user.getAuth() < 0) {
			System.out.println("Unauthorized Action");
			return null;
		}
		return dao.getAllUsers();
	}
	
	public Set<BankAccount> getPending() {
		if (user.getAuth() < 0) {
			System.out.println("Unauthorized action.");
			return null;
		}
		return dao.getPendingAccounts();
	}
	
	public User getUser(String username) {
		if (user.getAuth() < 0) {
			System.out.println("Unauthorized action.");
			return null;
		}
		return dao.getUser(username);
	}
	
	public User getUser(long SSN) {
		if (user.getAuth() < 0) {
			System.out.println("Unauthorized action.");
			return null;
		}
		return dao.getUser(SSN);
	}
	
}
