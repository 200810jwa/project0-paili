package com.revature.DAO;

import java.util.Set;

import com.revature.accounts.BankAccount;
import com.revature.users.User;

public interface BankDAOInterface {
	
	public boolean addUser(User u);
	public boolean addAccount(BankAccount ba);
	
	public double checkBalance(BankAccount ba);
	public boolean updateMultipleAccounts(BankAccount...b);
	
	public boolean updateUser(User u);
	public boolean updateBalance(BankAccount ba);
	public boolean updateBankAccount(BankAccount ba);
	
	// employee functions
	public Set<User> getAllUsers();
	public Set<BankAccount> getAllBankAccounts();
	public Set<BankAccount> getPendingAccounts();

	//public Set<User> searchUser(String search_term);
	public User getUser(long SSN);
	public User getUser(String username);
	public BankAccount getBankAccount(BankAccount ba);
	public BankAccount getBankAccount(String accountID);
	
	public Set<BankAccount> getBankAccountsFromUser(User u);
	public Set<BankAccount> getBankAccountsFromUser(long SSN);
	public Set<BankAccount> getBankAccountsFromUser(String username);
	
	public boolean deleteBankAccount(String accountID);
	public boolean deleteBankAccount(BankAccount ba);
	public boolean deleteUser(String username);
	public boolean deleteUser(User u);
	boolean updateBalance(String account_id, double amount);
	

}
