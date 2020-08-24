package com.revature.accounts;

import com.revature.users.User;

public class BankAccount implements Comparable<BankAccount> {
	private String accountID;
	private double balance = 0;
	private User owner;
	private boolean approved = false;

	// Constructors
	public BankAccount() {
		super();
	}
	
	public BankAccount(String accountID, double balance, User owner, boolean approved) {
		super();
		this.accountID = accountID;
		this.balance = balance;
		this.owner = owner;
		this.approved = approved;
	}
	
	// Getters/setters
	
	public String getAccountID() {
		return accountID;
	}

	public double getBalance() {
		return balance;
	}
	
	public void setBalance(double b) {
		balance = b;
	}

	public User getOwner() {
		return owner;
	}
	
	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public void setAccountID(String accountID) {
		this.accountID = accountID;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountID == null) ? 0 : accountID.hashCode());
		long temp;
		temp = Double.doubleToLongBits(balance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BankAccount other = (BankAccount) obj;
		if (accountID == null) {
			if (other.accountID != null)
				return false;
		} else if (!accountID.equals(other.accountID))
			return false;
		if (Double.doubleToLongBits(balance) != Double.doubleToLongBits(other.balance))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[accountID=" + accountID +  ", balance=" + balance + ", owner="
				+ owner.getSSN() + ", approved= " + approved + "]";
	}

	@Override
	public int compareTo(BankAccount o) {
		return this.getAccountID().compareTo(o.getAccountID());
	}
	
	
}
