package com.revature.DAO;

import java.util.Set;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.revature.accounts.BankAccount;
import com.revature.exceptions.AccountMismatchException;
import com.revature.users.*;

public class BankDAO implements BankDAOInterface {
	private static Logger log = Logger.getLogger(BankDAO.class);
	PreparedStatement stmt = null;

	public BankDAO() {
	}

	@Override
	public boolean addAccount(BankAccount ba) {
		String id = ba.getAccountID();
		
		if (getBankAccount(id) != null) {
			System.out.println("That account ID already exists");
			return false;
		}
		
		try (Connection connection = DAOUtilities.getConnection()) {
			String sql = "INSERT INTO project0.bank_accounts (account_id, "
					+ "balance, "
					+ "approved, owner) VALUES (?,?,?, ?)";
			stmt = connection.prepareStatement(sql);
			
			stmt.setString(1, ba.getAccountID());
			stmt.setDouble(2, ba.getBalance());
			stmt.setBoolean(3, ba.isApproved());
			stmt.setLong(4, ba.getOwner().getSSN());
			
			log.info("Executing query: " + stmt + " from addAccount");
			return stmt.executeUpdate() == 1;
		} catch (SQLException e) {
			log.error(e);
			log.trace(e, e);
		} catch (Exception e) {
			log.error(e);
			log.trace(e, e);
		}
		return false;
	}
	
	@Override
	public boolean addUser(User u) {
		if (getUser(u.getUsername()) != null) {
			System.out.println("The username " + u.getUsername() + " is already taken! Please select another");
			return false;
		} else if (getUser(u.getSSN()) != null) {
			User b = getUser(u.getSSN());
			System.out
					.println("You already have an account! Your username is " + b.getUsername());
			return false;
		}

		try (Connection connection = DAOUtilities.getConnection()) {
			String sql = "INSERT INTO project0.users (" 
					+ "username, " 
					+ "passhash, " 
					+ "email, "
					+ "SSN, "
					+ "first_name, " 
					+ "last_name, "
					+ "phone_number, " 
					+ "address, " 
					+ "auth,";

			if (u.getAuth() < 0) {
				// client
				sql = sql.concat("FICO) " + "VALUES(?,?,?,?,?,?,?,?,?,?)");
				stmt = connection.prepareStatement(sql);

				stmt.setString(1, u.getUsername());
				stmt.setString(2, u.getPasshash());
				stmt.setString(3, u.getEmail());
				stmt.setLong(4, u.getSSN());
				stmt.setString(5, u.getFirstName());
				stmt.setString(6, u.getLastName());
				stmt.setString(7, u.getPhoneNumber());
				stmt.setString(8, u.getAddress());
				stmt.setInt(9, u.getAuth());
				stmt.setInt(10, u.getFICO());

				log.info("Executing Query: " + stmt + " from addUser()");
				return stmt.executeUpdate() == 1;
			} else {
				// employee
				sql = sql.concat("FICO, employee_id, salary) " + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)");
				stmt = connection.prepareStatement(sql);

				stmt.setString(1, u.getUsername());
				stmt.setString(2, u.getPasshash());
				stmt.setString(3, u.getEmail());
				stmt.setLong(4, u.getSSN());
				stmt.setString(5, u.getFirstName());
				stmt.setString(6, u.getLastName());
				stmt.setString(7, u.getPhoneNumber());
				stmt.setString(8, u.getAddress());
				stmt.setInt(9, u.getAuth());
				stmt.setInt(10, u.getFICO());
				stmt.setString(11, u.getEmployeeID());
				stmt.setDouble(12, u.getSalary());
				log.info("Executing Query: " + stmt + " from addUser()");
				return stmt.executeUpdate() == 1;
			}
		} catch (SQLException e) {
			log.error(e);
			log.trace(e, e);
		} catch (Exception e) {
			log.error(e);
			log.trace(e, e);
		}
		return false;
	}

	@Override
	public double checkBalance(BankAccount ba) {
		try (Connection connection = DAOUtilities.getConnection()) {
			String accountID = ba.getAccountID();
			String sql = "SELECT * FROM project0.bank_accounts WHERE account_id=? LIMIT 1";
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, accountID);

			log.info("Executing: " + stmt);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				String verifyID = rs.getString("account_id");
				if (!accountID.equals(verifyID)) {
					throw new AccountMismatchException("Account IDs do not match!");
				}

				return rs.getDouble("balance");
			}
		} catch (SQLException e) {
			log.error(e);
			log.trace(e, e);
		} catch (AccountMismatchException e) {
			log.error(e);
			log.trace(e, e);
		} catch (NumberFormatException e) {
			log.error(e);
			log.trace(e, e);
		}
		return -1;
	}
	
	private BankAccount createBankAccountObject(ResultSet rs) throws SQLException {
		BankAccount ba = new BankAccount();
		ba.setAccountID(rs.getString("account_id"));
		ba.setBalance(rs.getDouble("balance"));
		ba.setOwner(getUser(rs.getLong("owner")));
		ba.setApproved(rs.getBoolean("approved"));
		return ba;
	}
	
	

	/**
	 * Creates a user object from the resultset RS. Does not call rs.next()
	 */
	private User createUserObject(ResultSet rs) throws SQLException {
		User u = new User();
		u.setUsername(rs.getString("username"));
		u.setPasshash(rs.getString("passhash"));
		u.setEmail(rs.getString("email"));
		u.setSSN(rs.getLong("SSN"));
		u.setFirstName(rs.getString("first_name"));
		u.setLastName(rs.getString("last_name"));
		u.setPhoneNumber(rs.getString("phone_number"));
		u.setAddress(rs.getString("address"));
		u.setAuth(rs.getInt("auth"));
		u.setFICO(rs.getInt("FICO"));
		if (u.getAuth() >= 0) {
			u.setEmployeeID(rs.getString("employee_id"));
			u.setSalary(rs.getDouble("salary"));
		}
		return u;
	}

	@Override
	public boolean deleteBankAccount(BankAccount ba) {
		return deleteBankAccount(ba.getAccountID());
	}

	@Override
	public boolean deleteBankAccount(String accountID) {
		try (Connection connection = DAOUtilities.getConnection()) {
			String sql = "DELETE FROM project0.bank_accounts WHERE account_id=?";
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, accountID);
			log.info("Executing: " + stmt + " from deleteUser");
			
			return stmt.executeUpdate() > 0;
			
		} catch (SQLException e) {
			log.error(e);
			log.trace(e, e);
		} catch (Exception e) {
			log.error(e);
			log.trace(e, e);
		}
		return false;
	}

	@Override
	public boolean deleteUser(String username) {
		try (Connection connection = DAOUtilities.getConnection()) {
			String sql = "DELETE FROM project0.users WHERE username=?";
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, username);
			log.info("Executing: " + stmt + " from deleteUser");
			
			return stmt.executeUpdate() > 0;
			
		} catch (SQLException e) {
			log.error(e);
			log.trace(e, e);
		} catch (Exception e) {
			log.error(e);
			log.trace(e, e);
		}
		return false;
	}
	
	@Override
	public boolean deleteUser(User u) {
		return deleteUser(u.getUsername());
	}

	@Override
	public Set<BankAccount> getAllBankAccounts() {
		try (Connection connection = DAOUtilities.getConnection()) {
			String sql = "SELECT * FROM project0.bank_accounts";
			stmt = connection.prepareStatement(sql);
			log.info("Executing: " + stmt + " from getAllBankAccounts");

			ResultSet rs = stmt.executeQuery();
			Set<BankAccount> temp = new TreeSet<BankAccount>();
			while (rs.next()) {
				temp.add(createBankAccountObject(rs));
			}
			return temp;
		} catch (SQLException e) {
			log.error(e);
			log.trace(e, e);
		} catch (Exception e) {
			log.error(e);
			log.trace(e, e);
		}

		return null;
	}

//	Could also just get all users, and implement the search in Java I suppose?
//	@Override
//	public Set<User> searchUser(String search_term) {
//		try (Connection connection = DAOUtilities.getConnection()) {
//			String sql = "SELECT * FROM project0.users LIKE '&?&'";
//			stmt = connection.prepareStatement(sql);
//			stmt.setString(1, search_term);
//			
//			ResultSet rs = stmt.executeQuery();
//			Set<User> temp = new TreeSet<User>();
//			
//			while (rs.next()) {
//				temp.add(createUserObject(rs));
//			}
//			return temp;
//		} catch (SQLException e) {
//			log.error(e);
//			log.trace(e, e);
//		} catch (Exception e) {
//			log.error(e);
//			log.trace(e, e);
//		}
//		return null;
//	}


	@Override
	public Set<User> getAllUsers() {
		try (Connection connection = DAOUtilities.getConnection()) {
			String sql = "SELECT * FROM project0.users";
			stmt = connection.prepareStatement(sql);
			
			ResultSet rs = stmt.executeQuery();
			Set<User> temp = new TreeSet<User>();
			
			while (rs.next()) {
				temp.add(createUserObject(rs));
			}
			return temp;
		} catch (SQLException e) {
			log.error(e);
			log.trace(e, e);
		} catch (Exception e) {
			log.error(e);
			log.trace(e, e);
		}
		return null;
	}

	@Override
	public BankAccount getBankAccount(BankAccount ba) {
		return getBankAccount(ba.getAccountID());
	}

	@Override
	public BankAccount getBankAccount(String accountID) {
		try (Connection connection = DAOUtilities.getConnection()) {
			String sql = "SELECT * FROM project0.bank_accounts WHERE account_id=?";
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, accountID);
			
			log.info("Executing: " + stmt + " from getBankAccount");
			
			ResultSet rs = stmt.executeQuery();
			
			if (rs.next()) {
				return createBankAccountObject(rs);
			}
			
		} catch (SQLException e) {
			log.error(e);
			log.trace(e, e);
		} catch (Exception e) {
			log.error(e);
			log.trace(e, e);
		}

		return null;
	}


	@Override
	public Set<BankAccount> getBankAccountsFromUser(long SSN) {
		try (Connection connection = DAOUtilities.getConnection()) {
			String sql = "SELECT * FROM project0.bank_accounts WHERE owner=?";
			stmt = connection.prepareStatement(sql);
			stmt.setLong(1, SSN);
			
			log.info("Executing: " + stmt + " from getBankAccountsFromUser");
			ResultSet rs = stmt.executeQuery();
			
			Set<BankAccount> temp = new TreeSet<BankAccount>();
			
			while (rs.next()) {
				temp.add(createBankAccountObject(rs));
			}
			return temp;
		} catch (SQLException e) {
			log.error(e);
			log.trace(e, e);
		} catch (Exception e) {
			log.error(e);
			log.trace(e, e);
		}
		return null;
	}
	
	@Override
	public Set<BankAccount> getBankAccountsFromUser(String username) {
		User temp = getUser(username);
		return getBankAccountsFromUser(temp.getSSN());
	}

	@Override
	public Set<BankAccount> getBankAccountsFromUser(User u) {
		return getBankAccountsFromUser(u.getSSN());
	}

	@Override
	public Set<BankAccount> getPendingAccounts() {
		try (Connection connection = DAOUtilities.getConnection()) {
			String sql = "SELECT * FROM project0.bank_accounts WHERE approved=?";
			stmt = connection.prepareStatement(sql);
			stmt.setBoolean(1, false);
			log.info("Executing: " + stmt + " from getPendingAccounts()");
			
			ResultSet rs = stmt.executeQuery();
			
			Set<BankAccount> temp = new TreeSet<BankAccount>();
			while (rs.next()) {
				temp.add(createBankAccountObject(rs));
			}
			return temp;
		} catch (SQLException e) {
			log.error(e);
			log.trace(e, e);
		} catch (Exception e) {
			log.error(e);
			log.trace(e, e);
		}
		return null;
	}

	@Override
	public User getUser(long SSN) {
		try (Connection connection = DAOUtilities.getConnection()) {
			String sql = "SELECT * FROM project0.users WHERE SSN=?";
			stmt = connection.prepareStatement(sql);
			stmt.setLong(1, SSN);

			ResultSet rs = stmt.executeQuery();

			// make call to check for empty results
			if (rs.next()) {
				return createUserObject(rs);
			}
		} catch (SQLException e) {
			log.error(e);
			log.trace(e, e);
		} catch (Exception e) {
			log.error(e);
			log.trace(e, e);
		}
		return null;
	}

	@Override
	public User getUser(String username) {
		try (Connection connection = DAOUtilities.getConnection()) {
			String sql = "SELECT * FROM project0.users WHERE username=?";
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, username);

			ResultSet rs = stmt.executeQuery();

			// make call to check for empty results
			if (rs.next()) {
				return createUserObject(rs);
			}
		} catch (SQLException e) {
			log.error(e);
			log.trace(e, e);
		} catch (Exception e) {
			log.error(e);
			log.trace(e, e);
		}
		return null;
	}

	@Override
	public boolean updateBalance(BankAccount ba) {
//		try (Connection connection = DAOUtilities.getConnection()) {
//			String sql = "UPDATE project0.bank_accounts SET balance=? " + "WHERE account_id=?";
//			stmt = connection.prepareStatement(sql);
//			stmt.setDouble(1, ba.getBalance());
//			stmt.setString(2, ba.getAccountID());
//			log.info("Executing Query: " + stmt);
//			return (stmt.executeUpdate() > 0);
//		} catch (SQLException e) {
//			log.error(e);
//			log.trace(e, e);
//		} catch (AccountMismatchException e) {
//			log.error(e);
//			log.trace(e, e);
//		} catch (NumberFormatException e) {
//			log.error(e);
//			log.trace(e, e);
//		}
//		return false;
		return updateBalance(ba.getAccountID(), ba.getBalance());
	}
	@Override
	public boolean updateBalance(String account_id, double balance) {
		try (Connection connection = DAOUtilities.getConnection()) {
			String sql = "UPDATE project0.bank_accounts SET balance=? " + "WHERE account_id=?";
			stmt = connection.prepareStatement(sql);
			stmt.setDouble(1, balance);
			stmt.setString(2, account_id);
			log.info("Executing Query: " + stmt);
			return (stmt.executeUpdate() > 0);
		} catch (SQLException e) {
			log.error(e);
			log.trace(e, e);
		} catch (AccountMismatchException e) {
			log.error(e);
			log.trace(e, e);
		} catch (NumberFormatException e) {
			log.error(e);
			log.trace(e, e);
		} 
		return false;
	}

	@Override
	public boolean updateBankAccount(BankAccount ba) {
		try (Connection connection = DAOUtilities.getConnection()) {
			String sql = "UPDATE project0.bank_accounts SET ("
					+ "account_id, "
					+ "balance,"
					+ "approved,"
					+ "owner) = (?,?,?,?) WHERE account_id=?";
			stmt = connection.prepareStatement(sql);
			stmt.setString(1, ba.getAccountID());
			stmt.setDouble(2, ba.getBalance());
			stmt.setBoolean(3, ba.isApproved());
			stmt.setLong(4, ba.getOwner().getSSN());
			stmt.setString(5, ba.getAccountID());
			
			log.info("Executing Query: " + stmt);
			return (stmt.executeUpdate() > 0);
		} catch (SQLException e) {
			log.error(e);
			log.trace(e, e);
		} catch (AccountMismatchException e) {
			log.error(e);
			log.trace(e, e);
		} catch (NumberFormatException e) {
			log.error(e);
			log.trace(e, e);
		} catch (NullPointerException e) {
			System.out.println("Missing values. Could not update account!");
			log.trace(e, e);
		}
		return false;
	}

	@Override
	public boolean updateUser(User u) {
		try (Connection connection = DAOUtilities.getConnection()) {
			String sql = "UPDATE project0.users SET("
					+ "username, " 
					+ "passhash, " 
					+ "email, "
					+ "SSN, "
					+ "first_name, " 
					+ "last_name, "
					+ "phone_number, " 
					+ "address, " 
					+ "auth,"
					+ "FICO,"
					+ "employee_id,"
					+ "salary) = (?.?.?.?.?.?.?.?.?.?.?.?) WHERE SSN=?";
			stmt = connection.prepareStatement(sql);

			stmt.setString(1, u.getUsername());
			stmt.setString(2, u.getPasshash());
			stmt.setString(3, u.getEmail());
			stmt.setLong(4, u.getSSN());
			stmt.setString(5, u.getFirstName());
			stmt.setString(6, u.getLastName());
			stmt.setString(7, u.getPhoneNumber());
			stmt.setString(8, u.getAddress());
			stmt.setInt(9, u.getAuth());
			stmt.setInt(10, u.getFICO());
			stmt.setString(11, u.getEmployeeID());
			stmt.setDouble(12, u.getSalary());
			stmt.setLong(13, u.getSSN());
			log.info("Executing Query: " + stmt + " from addUser()");
			
			return stmt.executeUpdate() == 1;
					
		}catch (SQLException e) {
			log.error(e);
			log.trace(e, e);
		} catch (AccountMismatchException e) {
			log.error(e);
			log.trace(e, e);
		} catch (NullPointerException e) {
			System.out.println("Missing values. Could not update user info.");
			log.error(e);
			log.trace(e,e);
		}

		return false;
	}

	@Override
	public boolean updateMultipleAccounts(BankAccount...list) {
		try (Connection connection = DAOUtilities.getConnection()) {
			boolean success = true;
			connection.setAutoCommit(false);
			connection.setSavepoint();
			
			for (BankAccount ba : list) {
				String sql = "UPDATE project0.bank_accounts SET ("
						+ "account_id, "
						+ "balance,"
						+ "approved,"
						+ "owner) = (?,?,?,?) WHERE account_id=?";
				stmt = connection.prepareStatement(sql);
				stmt.setString(1, ba.getAccountID());
				stmt.setDouble(2, ba.getBalance());
				stmt.setBoolean(3, ba.isApproved());
				stmt.setLong(4, ba.getOwner().getSSN());
				stmt.setString(5, ba.getAccountID());
				
				log.info("Executing: " + stmt + " from updateMultipleAccounts");
				
				success = success && (stmt.executeUpdate()==1); 
			}
			
			if (success) {
				connection.commit();
				return success;
			} 
			
			connection.rollback();
		}catch (SQLException e) {
			log.error(e);
			log.trace(e, e);
		}catch (NullPointerException e1) {
			System.out.println("Missing values. Could not update account!");
			log.error(e1);
			log.trace(e1,e1);
		}
		return false;
	}
}
