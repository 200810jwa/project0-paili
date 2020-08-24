package com.revature.users;

import org.mindrot.jbcrypt.BCrypt;

public class User implements Comparable<User> {
	private String username;
	private String passhash;
	private String email;
	private long SSN;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String address;
	private int auth = -1;
	private int FICO = 350;
	private String employeeID;
	private double salary;
	//private Set<BankAccount> accounts;
	
	// Constructors
	public User() {
		
	}
	
	public User(String username, String password, String email, long sSN, String firstName, String lastName,
			String phoneNumber, String address, int auth) {
		super();
		this.username = username.toUpperCase();
		this.passhash = hashPass(password);
		this.email = email.toUpperCase();
		SSN = sSN;
		this.firstName = firstName.toUpperCase();
		this.lastName = lastName.toUpperCase();
		this.setPhoneNumber(phoneNumber);
		this.address = address.toUpperCase();
		this.auth = auth;
	}

	public User(String username, String password, String email, long sSN, String firstName, String lastName,
			String phoneNumber, String address, int auth, String employeeID, double salary) {
		this(username, password, email, sSN, firstName, lastName, phoneNumber, address, auth);
		this.employeeID = employeeID;
		this.salary = salary;
	}


	// -------------------------   Password Hashing Methods   -------------------------------
	public String hashPass(String pass) {
		return BCrypt.hashpw(pass,BCrypt.gensalt());
	}
	
	public boolean passMatch(String pass) {
		return BCrypt.checkpw(pass, passhash);
	}
	
	// -------------------------- Getters and Setters ------------------------------------

	public void setPhoneNumber(String phoneNumber) {
		if (Long.parseLong(phoneNumber) < 0) {
			// do nothing, can't have negative phone number
		} else {
			this.phoneNumber = phoneNumber;
		}
	}

	public String getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(String employeeID) {
		this.employeeID = employeeID;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username.toUpperCase();
	}

	public String getPasshash() {
		return passhash;
	}

	public void setPasshash(String passhash) {
		this.passhash = passhash;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email.toUpperCase();
	}

	public long getSSN() {
		return SSN;
	}

	public void setSSN(long sSN) {
		SSN = sSN;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName.toUpperCase();
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName.toUpperCase();
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address.toUpperCase();
	}

	public int getAuth() {
		return auth;
	}

	public void setAuth(int auth) {
		this.auth = auth;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public int getFICO() {
		return FICO;
	}

	public boolean setFICO(int FICO) {
		if (FICO > 850 || FICO < 350) {
			System.out.println("Invalid FICO Score.");
			return false;
		} else {
			this.FICO = FICO;
			return true;
		}
	}
	
//	public Set<BankAccount> getAccounts() {
//		return accounts;
//	}
//
//	public void setAccounts(Set<BankAccount> accounts) {
//		this.accounts = accounts;
//	}
	
	
	// --------------------------------------------------------------------



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + FICO;
		result = prime * result + (int) (SSN ^ (SSN >>> 32));
		//result = prime * result + ((accounts == null) ? 0 : accounts.hashCode());
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + auth;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((employeeID == null) ? 0 : employeeID.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((passhash == null) ? 0 : passhash.hashCode());
		result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
		long temp;
		temp = Double.doubleToLongBits(salary);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		User other = (User) obj;
		if (FICO != other.FICO)
			return false;
		if (SSN != other.SSN)
			return false;
//		if (accounts == null) {
//			if (other.accounts != null)
//				return false;
//		} else if (!accounts.equals(other.accounts))
//			return false;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equalsIgnoreCase(other.address))
			return false;
		if (auth != other.auth)
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equalsIgnoreCase(other.email))
			return false;
		if (employeeID == null) {
			if (other.employeeID != null)
				return false;
		} else if (!employeeID.equals(other.employeeID))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equalsIgnoreCase(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equalsIgnoreCase(other.lastName))
			return false;
		if (passhash == null) {
			if (other.passhash != null)
				return false;
		}
		if (phoneNumber == null) {
			if (other.phoneNumber != null)
				return false;
		} else if (!phoneNumber.equals(other.phoneNumber))
			return false;
		if (Double.doubleToLongBits(salary) != Double.doubleToLongBits(other.salary))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equalsIgnoreCase(other.username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[username=" + username +  ", email=" + email + ", SSN=" + SSN
				+ ", firstName=" + firstName + ", lastName=" + lastName + ", phoneNumber=" + phoneNumber + ", address="
				+ address + ", auth=" + auth + ", FICO=" + FICO + ", employeeID=" + employeeID + ", salary=" + salary
				+ "]";
	}

	@Override
	public int compareTo(User u) {
		return (this.username.compareTo(u.username));
	}

//	public List<String> toList() {
//		List<String> r = new ArrayList<String>();
//		r.add(username);
//		r.add(passhash);
//		r.add(email);
//		r.add(Long.toString(SSN));
//		r.add(firstName);
//		r.add(lastName);
//		r.add(phoneNumber);
//		r.add(address);
//		r.add(Integer.toString(auth));
//		r.add(Integer.toString(FICO));
//		r.add(employeeID);
//		r.add(Double.toString(salary));
//		return r;
//	}
	
}
