package com.revature.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import org.apache.log4j.Logger;

import com.revature.DAO.BankDAO;
import com.revature.accounts.BankAccount;
import com.revature.users.User;

public class ClientServices {
	public User user;
	BankDAO dao;
	String ip;
	static Logger infolog = Logger.getLogger("infoLogger");
	
	public ClientServices(User user, BankDAO dao) {
		this.user = user;
		this.dao = dao;
		
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = null;
	        try {
	            in = new BufferedReader(new InputStreamReader(
	                    whatismyip.openStream()));
	            String ip = in.readLine();
	            this.ip = ip;
	        } finally {
	            if (in != null) {
	                try {
	                    in.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }
		} catch (Exception e) {
			
		}  
		
		infolog.info("[" + ip + "] Login to " + user.getUsername());
	}

	public boolean withdraw(BankAccount ba, double amount) {
		if (!ba.isApproved()) {
			System.out.println("This account has not been approved yet.");
			return false;
		}
		if (amount <= 0) {
			System.out.println("Cannot withdraw nonpositive values.");
			return false;
		}
		if (ba.getBalance() < amount) {
			System.out.println("Insufficient funds. The current balance is " + ba.getBalance());
			return false;
		} else {
			ba.setBalance(ba.getBalance() - amount);
			infolog.info("[" + ip + "]" + user.getUsername() + " withdrew " + amount + " from " + ba.getAccountID() 
			+ " New Balance: " + ba.getBalance());
			return dao.updateBalance(ba);
		}
	}
	
	public boolean deposit(BankAccount ba, double amount) {
		if (!ba.isApproved()) {
			System.out.println("This account has not been approved yet.");
			return false;
		}
		if (amount <= 0) {
			System.out.println("Cannot deposit nonpositive values.");
			return false;
		}
		ba.setBalance(ba.getBalance()+amount);
		infolog.info("[" + ip + "]" + user.getUsername() + " deposited " + amount + " from " + ba.getAccountID() 
			+" New Balance: " + ba.getBalance());
		return dao.updateBalance(ba);
	}
	
	
	public double checkBalance(BankAccount ba) {
		if (!ba.isApproved()) {
			System.out.println("This account has not been approved yet.");
			return 0;
		} else {
			return dao.getBankAccount(ba.getAccountID()).getBalance();
		}
	}
	
	public double checkBalance(String id) {
		BankAccount ba = dao.getBankAccount(id);
		if (ba == null) {
			System.out.println("Could not find account with ID " + id);
			return 0;
		} else if (!ba.isApproved()) {
			System.out.println("This account has not been approved yet.");
			return 0;
		} else {
			return ba.getBalance();
		}
	}
	
	public boolean transfer(BankAccount sender, BankAccount receiver, double amount) {
		if (sender.isApproved() && receiver.isApproved()) {
			if (sender.getBalance() < amount) {
				System.out.println("Insufficient funds. The current balance is " + sender.getBalance());
				return false;
			}
			sender.setBalance(sender.getBalance() - amount);
			receiver.setBalance(receiver.getBalance() + amount);
			
			infolog.info("[" + ip + "]" + user.getUsername() + " transfered " + amount + " from " + sender.getAccountID() 
			+ " to " + receiver.getAccountID() + " New Balances: [" + sender.getAccountID() + ": " + sender.getBalance() + ", "
			+ receiver.getAccountID() + ": " + receiver.getBalance() + "]");
			
			return dao.updateMultipleAccounts(sender,receiver);
		} else {
			System.out.println("Accounts must be approved first");
			return false;
		}

	}
	
	public boolean applyForAccount() {
		int n = dao.getBankAccountsFromUser(user).size();
		BankAccount newAcct = new BankAccount(user.getUsername() + "_" + ++n, 0, user, false);
		return dao.addAccount(newAcct);
	}
	
	public boolean hasAccount(User u) {
		return dao.getBankAccountsFromUser(u).size() > 0;
	}
}

