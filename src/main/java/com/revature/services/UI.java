package com.revature.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.log4j.Logger;

import com.revature.DAO.BankDAO;
import com.revature.accounts.BankAccount;
import com.revature.users.User;

public class UI {
	private Scanner s;
	private transient String input = ""; // init to empty string;
	BankDAO dao = new BankDAO();
	private static Logger infolog = Logger.getLogger("infoLogger");

	// ------------------------- Constructor -------------------------
	public UI(Scanner s) {
		this.s = s;
	}

	// ------------------------- Run -------------------------

	public void run() {
		while (!input.equalsIgnoreCase("QUIT")) {
			System.out.println("Welcome! What would you like to do? [Login / Register / Quit]");
			parse();

			boolean validCommand = false;

			do {
				switch (input) {
				case ("LOGIN"):
					// goto login UI
					login();

					validCommand = true;
					break;
				case ("REGISTER"):
					dao = new BankDAO();

					// goto registration UI
					User newAcct = register();

					// send info to SQL database
					// check against database to see if username/eID/SSN already exists
					if (dao.addUser(newAcct)) {
						System.out.println("You have completed registration!");
					} else {
						System.out.println("There was an issue with registration!");
					}
					System.out.println("Returning you to the main menu.");

					validCommand = true;
					break;
				case ("QUIT"):
					// go to main while loop to terminate
					validCommand = true;
					break;
				default:
					validCommand = false;
					System.out.println("Unrecognized command. Enter login to login, or register to register.");
					parse();
				}
			} while (!validCommand);
		}
	}

	// Menu input parsing
	private String parse() {
		input = s.nextLine().trim().toUpperCase();
		// System.out.println(input);
		return input;
	}

	// Command parsing
	private List<String> parseCommand() {
		input = s.nextLine().trim().toUpperCase();
		return Arrays.asList(input.split("\s"));
	}

	public void login() {
		boolean correctPass = false;
		while (!correctPass) {
			System.out.println("Enter your username:");
			input = s.nextLine();
			String user = input.toUpperCase();
			System.out.println("Enter your password:");
			input = s.nextLine();
			String pass = input;

			// get account with username from database, if exists, then check password
			dao = new BankDAO();
			User acct = dao.getUser(user);

			if (acct != null) {
				if (acct.passMatch(pass)) {
					if (acct.getAuth() < 0) {
						clientUI(acct);
						correctPass = true;
					} else  {

						input = chooseSitePortal();
						if (input.equalsIgnoreCase("CLIENT")) {
							clientUI(acct);
						} else if (input.equalsIgnoreCase("EMPLOYEE")) {
							employeeUI(acct);
						}

						correctPass = true;
					} 
				} else {
					System.out.println("Incorrect password");
				}
			} else {
				System.out.println("Invalid username");
			}
		}
	}

	// ------------------------- Client UI -----------------------------

	public void clientUI(User acct) {
		// different message for first iteration
		int instance = 0;
		ClientServices cs = new ClientServices(acct, dao);
		Set<BankAccount> userAccounts_temp = dao.getBankAccountsFromUser(acct);
		Map<String, BankAccount> userAccounts = new HashMap<String, BankAccount>();

		System.out.println("Welcome " + acct.getFirstName() + ", your accounts are: ");
		for (BankAccount ba : userAccounts_temp) {
			System.out.println(ba);
			userAccounts.put(ba.getAccountID(), ba);
		}
		System.out.println("What would you like to do today? Enter HELP for options.");

		while (!input.equals("LOGOUT")) {
			if (instance != 0) {
				System.out.println("What else would you like to do today?");
			}

			List<String> parsedInput = parseCommand();
			String cmd = parsedInput.get(0);
			boolean valid = false;

			userAccounts_temp = dao.getBankAccountsFromUser(acct);
			for (BankAccount b : userAccounts_temp) {
				userAccounts.put(b.getAccountID(), b);
			}

			switch (cmd) {
			case "HELP":
				if (parsedInput.size() == 1) {
					System.out.println(
							"Valid commands are: DEPOSIT, WITHDRAW, TRANSFER, CHECKBALANCE, APPLY, ALLACCOUNTS, LOGOUT");
					System.out.println("Type HELP COMMAND to for details");
				} else {
					String helpcmd = parsedInput.get(1);
					switch (helpcmd) {
					case "DEPOSIT":
						System.out.println("To deposit, enter: DEPOSIT ACCOUNTID AMOUNT\n"
								+ "For example: DEPOSIT MYACCOUNT123 50.00");
						break;
					case "WITHDRAW":
						System.out.println("To withdraw, enter: WITHDRAW ACCOUNTID AMOUNT\n"
								+ "For example: WITHDRAW MYACCOUNT123 50.00");
						break;
					case "TRANSFER":
						System.out.println("To transfer funds, enter: TRANSFER SENDERID AMOUNT RECEIVERID\n"
								+ "For example: TRANSFER MYACCOUNT123 50.00 OTHERACCOUNT123");
						break;
					case "CHECKBALANCE":
						System.out.println("To check the balance of an account, enter: CHECKBALANCE ACCTID\n"
								+ "For example: CHECKBALANCE MYACCOUNT123");
						break;
					case "APPLY":
						System.out.println("To apply for an account, enter: APPLY");
						break;
					case "ALLACCOUNTS":
						System.out.println("To view info for all of your accounts, enter: ALLACCOUNTS");
					case "LOGOUT":
						System.out.println("Type LOGOUT to logout");
						break;
					default:
						System.out.println("Invalid help command");
					}
				}
				valid = true;
				break;
			case "DEPOSIT":
				if (parsedInput.size() != 3) {
					break;
				} else {
					try {
						String acctID = parsedInput.get(1);
						Double amt = Double.parseDouble(parsedInput.get(2));
						if (!userAccounts.containsKey(acctID)) {
							System.out.println("Account does not exist. Make sure you typed the account ID correctly.");
							valid = true;
							break;
						} else if (cs.deposit(userAccounts.get(acctID), amt)) {
							System.out.println("Deposit completed. Your new balance is: "
									+ cs.checkBalance(userAccounts.get(acctID)));
							valid = true;
							break;
						}
						valid = true;
						break;
					} catch (NumberFormatException e) {
						System.out.println("Please enter a valid number for the deposit amount.");
						valid = true;
					}
				}
			case "WITHDRAW":
				if (parsedInput.size() != 3) {
					break;
				} else {
					try {
						String acctID = parsedInput.get(1);
						Double amt = Double.parseDouble(parsedInput.get(2));
						if (!userAccounts.containsKey(acctID)) {
							System.out.println("Account does not exist. Make sure you typed the account ID correctly.");
							valid = true;
							break;
						} else if (cs.withdraw(userAccounts.get(acctID), amt)) {
							System.out.println("Withdrawal completed. Your new balance is: $"
									+ cs.checkBalance(userAccounts.get(acctID)));
							valid = true;
							break;
						}
						valid = true;
						break;
					} catch (NumberFormatException e) {
						System.out.println("Please enter a valid number for the withdraw amount.");
					}
				}
			case "TRANSFER":
				if (parsedInput.size() != 4) {
					break;
				} else {
					try {
						String sendID = parsedInput.get(1);
						Double amt = Double.parseDouble(parsedInput.get(2));
						String recID = parsedInput.get(3);
						BankAccount sendAcc, recAcc;

						if (!userAccounts.containsKey(sendID)) {
							System.out.println("Account " + sendID + " does not exist. "
									+ "Make sure you typed the account ID correctly");
							valid = true;
							break;
						} else if ((recAcc = dao.getBankAccount(recID)) != null) {
							sendAcc = dao.getBankAccount(sendID);
							recAcc = dao.getBankAccount(recID);
							if (cs.transfer(sendAcc, recAcc, amt)) {
								System.out.println("Transfer complete. Your new balance for account " + sendID + " is $"
										+ sendAcc.getBalance());
							} else {
								System.out.println("There was an error during transfer.");
							}
							valid = true;
							break;
						} else {
							System.out.println("Account " + recID + " does not exist. "
									+ "Make sure you typed the account ID correctly");
							valid = true;
							break;
						}

					} catch (NumberFormatException e) {
						System.out.println("Please enter a valid number for the transfer amount");
					}
				}
				valid = true;
				break;
			case "CHECKBALANCE":
				if (parsedInput.size() != 2) {
				} else {
					String acctID = parsedInput.get(1);

					if (!userAccounts.containsKey(acctID)) {
						System.out.println("Account does not exist. Make sure you typed the account ID correctly.");
						valid = true;
						break;
					} else {
						System.out.println("Your balance for account " + acctID + " is $" + cs.checkBalance(acctID));
						valid = true;
						break;
					}
				}
			case "APPLY":
				if (cs.applyForAccount()) {
					System.out.println("Your application has been received.");
				} else {
					System.out.println("There was an error processing your application.");
				}
				valid = true;
				break;
			case "ALLACCOUNTS":
				userAccounts_temp = dao.getBankAccountsFromUser(acct);
				if (userAccounts_temp.size() == 0) {
					System.out.println("You have no accounts or applications");
				} else {
					for (BankAccount ba : userAccounts_temp) {
						System.out.println(ba);
					}
				}
				valid = true;
				break;
			case "LOGOUT":
				valid = true;
				break;
			default:
				valid = false;
			}

			if (!valid) {
				System.out.println("Invalid command. Enter HELP for help.");
			}

			instance++;
		}

	}

	// ------------------------- Employee UI ----------------------------------

	public void employeeUI(User acct) {
		boolean first = true;
		EmployeeServices es = new EmployeeServices(acct, dao);
		System.out.println("Welcome, " + acct.getFirstName() + ". What would you like to do? Enter HELP for options.");

		List<String> parsedInput;
		String cmd;

		while (!input.equals("LOGOUT")) {
			if (first != true) {
				System.out.println("What else would you like to do today?");

			}

			parsedInput = parseCommand();
			cmd = parsedInput.get(0);
			boolean valid = false;

			switch (cmd) {
			case "HELP":
				if (parsedInput.size() == 1) {
					System.out.println("Valid commands are: ACCOUNT, CLIENT, ALLACCOUNTS, ALLCLIENTS, VIEWFOR, "
							+ "PENDING, APPROVE, DENY, and LOGOUT");
					System.out.println("Admins may also DEPOSIT, WITHDRAW, TRANSFER, or CANCEL any account");
					System.out.println("Type HELP COMMAND to for details");
				} else {
					String helpcmd = parsedInput.get(1);
					switch (helpcmd) {
					case "ACCOUNT":
						System.out.println("To view a specific account, type: ACCOUNT ACCOUNTID\n"
								+ "For example: ACCOUNT ABC_123");
						break;
					case "CLIENT":
						System.out.println("To view a specific client: type CLIENT USERNAME/SSN\n"
								+ "For example: CLIENT 123456789 or CLIENT MYUSERNAME");
						break;
					case "ALLCLIENTS":
						System.out.println("To view all clients, type ALLCLIENTS");
						break;
					case "ALLACCOUNTS":
						System.out.println("To view all accounts, type ALLACCOUNTS");
						break;
					case "VIEWFOR":
						System.out.println("To view all accounts for a specific person, type VIEW USERNAME or SSN\n"
								+ "For example: VIEW MYUSERNAME or VIEW 123456789");
						break;
					case "PENDING":
						System.out.println("To get applications pending approval, type PENDING");
						break;
					case "APPROVE":
						System.out.println("To approve or deny pending applications, type APPROVE/DENY ACCOUNTID\n"
								+ "For example: APPROVE ACCOUNT_123");
						break;
					case "DENY":
						System.out.println("To approve or deny pending applications, type APPROVE/DENY ACCOUNTID\n"
								+ "For example: DENY ACCOUNT_123");
						break;
					case "DEPOSIT":
						System.out.println("To deposit, enter: DEPOSIT ACCOUNTID AMOUNT\n"
								+ "For example: DEPOSIT MYACCOUNT123 50.00");
						break;
					case "WITHDRAW":
						System.out.println("To withdraw, enter: WITHDRAW ACCOUNTID AMOUNT\n"
								+ "For example: WITHDRAW MYACCOUNT123 50.00");
						break;
					case "TRANSFER":
						System.out.println("To transfer funds, enter: TRANSFER SENDERID AMOUNT RECEIVERID\n"
								+ "For example: TRANSFER MYACCOUNT123 50.00 OTHERACCOUNT123");
						break;
					case "CANCEL":
						System.out.println(
								"To cancel an account, enter CANCEL ACCOUNTID\n" + "FOr example: CANCEL ACCOUNT123");
						break;
					case "LOGOUT":
						System.out.println("Type LOGOUT to logout");
						break;
					default:
						System.out.println("Invalid help command");
					}

				}
				valid = true;
				break;
			case "ACCOUNT":
				if (parsedInput.size() == 2) {
					String acctID = parsedInput.get(1);
					BankAccount account = dao.getBankAccount(acctID);
					if (account != null) {
						System.out.println(account);
					} else {
						System.out.println("Account does not exist. Are you sure you entered the correct ID?");
					}
					valid = true;
					break;
				}
			case "CLIENT":
				if (parsedInput.size() == 2) {
					String person = parsedInput.get(1);
					User u;
					try {
						if (person.length() != 9) {
							throw new NumberFormatException();
						}
						long SSN = Long.parseLong(person);
						u = es.getUser(SSN);
						if (u == null) {
							System.out.println("No client found for SSN " + SSN);
							valid = true;
							break;
						}

					} catch (NumberFormatException e) {
						u = es.getUser(person);
						if (u == null) {
							System.out.println("No client found for username " + person);
							valid = true;
							break;
						}
					}
					System.out.println(u);
					valid = true;

				}
				break;
			case "ALLCLIENTS":
				Set<User> allUsers = es.viewAllUsers();
				for (User u : allUsers) {
					System.out.println(u);
				}
				valid = true;
				break;
			case "ALLACCOUNTS":
				Set<BankAccount> allAccounts = es.viewAllAccounts();
				for (BankAccount b : allAccounts) {
					System.out.println(b);
				}
				valid = true;
				break;
			case "VIEWFOR":
				if (parsedInput.size() == 2) {
					String person = parsedInput.get(1);
					Set<BankAccount> userAccounts;
					try {
						if (person.length() != 9) {
							throw new NumberFormatException();
						}
						long SSN = Long.parseLong(person);
						if (dao.getUser(SSN) != null) {
							userAccounts = es.viewAccountsForUser(SSN);
							if (userAccounts.size() != 0) {
								for (BankAccount b : userAccounts) {
									System.out.println(b);
								}
								valid = true;
								break;
							} else {
								System.out.println("No accounts found for " + SSN);
								valid = true;
								break;
							}
						} else {
							System.out.println("No client found for SSN " + SSN);
							valid = true;
							break;
						}

					} catch (NumberFormatException e) {
						if (dao.getUser(person) != null) {
							userAccounts = es.viewAccountsForUser(person);
							if (userAccounts.size() != 0) {
								for (BankAccount b : userAccounts) {
									System.out.println(b);
								}
								valid = true;
								break;
							} else {
								System.out.println("No accounts found for " + person);
								valid = true;
								break;
							}
						} else {
							System.out.println("No client found for username " + person);
							valid = true;
							break;
						}

					}
				}
				break;
			case "PENDING":
				Set<BankAccount> pending = es.getPending();
				if (pending.size() != 0) {
					for (BankAccount b : pending) {
						System.out.println(b);
					}
					valid = true;
					break;
				} else {
					System.out.println("No pending applications!");
					valid = true;
					break;
				}
			case "APPROVE":
				if (parsedInput.size() == 2) {
					String id = parsedInput.get(1);
					BankAccount ba = dao.getBankAccount(id);
					if (ba != null) {
						if (es.approveApplication(ba)) {
							System.out.println("Application for account " + id + " has been approved.");
						} else {
							System.out.println("Error approving application. The account may already be active!");
						}
						valid = true;
						break;
					} else {
						System.out.println("No application found for ID " + id);
						valid = true;
						break;
					}
				}
				break;
			case "DENY":
				if (parsedInput.size() == 2) {
					String id = parsedInput.get(1);
					BankAccount ba = dao.getBankAccount(id);
					if (ba != null) {
						if (es.denyApplication(ba)) {
							System.out.println("Application for account " + id + " has been denied.");
						} else {
							System.out.println("Error denying application. The account may already be active!");
						}
						valid = true;
						break;
					} else {
						System.out.println("No application found for ID " + id);
						valid = true;
						break;
					}
				}
				break;
			// ==========================================================================
			case "DEPOSIT":
				if (parsedInput.size() == 3) {
					if (es.user.getAuth() > 0) {
						try {
							String acctID = parsedInput.get(1);
							Double amt = Double.parseDouble(parsedInput.get(2));
							BankAccount ba = dao.getBankAccount(acctID);
							if (ba == null) {
								System.out.println(
										"Account does not exist. Make sure you typed the account ID correctly.");
								valid = true;
								break;
							} else if (es.deposit(ba, amt)) {
								System.out.println("Deposit completed. The new balance is: " + es.checkBalance(ba));
								valid = true;
								break;
							}
							valid = true;
							break;
						} catch (NumberFormatException e) {
							System.out.println("Please enter a valid number for the deposit amount.");
							valid = true;
						}
					} else {
						System.out.println("Unauthorized action");
						valid = true;
					}
				}
				break;
			case "WITHDRAW":
				if (parsedInput.size() == 3) {
					if (es.user.getAuth() > 0) {
						try {
							String acctID = parsedInput.get(1);
							Double amt = Double.parseDouble(parsedInput.get(2));
							BankAccount ba = dao.getBankAccount(acctID);
							if (ba == null) {
								System.out.println(
										"Account does not exist. Make sure you typed the account ID correctly.");
								valid = true;
								break;
							} else if (es.withdraw(ba, amt)) {
								System.out.println("Deposit completed. The new balance is: " + es.checkBalance(ba));
								valid = true;
								break;
							}
							valid = true;
							break;
						} catch (NumberFormatException e) {
							System.out.println("Please enter a valid number for the deposit amount.");
							valid = true;
						}
					} else {
						System.out.println("Unauthorized action");
						valid = true;
					}
				}

				break;
			case "TRANSFER":
				if (parsedInput.size() == 4) {
					if (es.user.getAuth() > 0) {
						try {
							String sendID = parsedInput.get(1);
							Double amt = Double.parseDouble(parsedInput.get(2));
							String recID = parsedInput.get(3);
							BankAccount sendAcc = dao.getBankAccount(sendID);
							BankAccount recAcc = dao.getBankAccount(recID);

							if (sendAcc == null) {
								System.out.println("Account " + sendID + " does not exist. "
										+ "Make sure you typed the account ID correctly");
								valid = true;
								break;
							} else if (recAcc == null) {
								System.out.println("Account " + recID + " does not exist. "
										+ "Make sure you typed the account ID correctly");
								valid = true;
								break;
							} else {
								if (es.transfer(sendAcc, recAcc, amt)) {
									System.out.println("Transfer complete. Your new balance for account " + sendID
											+ " is $" + sendAcc.getBalance());
								} else {
									System.out.println("There was an error during transfer.");
								}
								valid = true;
								break;
							}
						} catch (NumberFormatException e) {
							System.out.println("Please enter a valid number for the transfer amount");
							valid = true;
						}
					} else {
						System.out.println("Unauthorized action");
						valid = true;
					}
				}
				break;
			case "CANCEL":
				if (parsedInput.size() == 2) {
					if (es.user.getAuth() > 0) {
						String id = parsedInput.get(1);
						BankAccount ba = dao.getBankAccount(id);

						if (ba == null) {
							System.out.println("No account found for account ID " + id);
							valid = true;
						} else if (ba.getBalance() > 0) {
							System.out.println("Cannot cancel accounts with a standing balance");
							valid = true;
						} else {
							if (es.cancelAccount(ba)) {
								System.out.println("Account " + id + " successfully canceled");
							} else {
								System.out.println("There was an error cancelling account " + id);
							}
							valid = true;
						}
					} else {
						System.out.println("Unauthorized action");
						valid = true;
					}

				}
				break;
			// ==========================================================================
			case "LOGOUT":
				valid = true;
				break;
			default:
				valid = false;
			}
			if (!valid) {
				System.out.println("Invalid command. ");
			}

		}

	}

	//
	public String chooseSitePortal() {
		while (true) {
			System.out.println("Are you signing in as a client or as an employee?");
			String temp = parse();

			if (temp.equalsIgnoreCase("client") || temp.equalsIgnoreCase("employee")) {
				return temp;
			} else {
				System.out.println("Please type client or employee");
			}
		}
	}

	// ------------------------- Registration Methods -------------------------

	// check if SSN has been used

	public User register() {
		// fields that require validation (email, phone, etc) have methods for
		// validation
		String type = validateAccountType();

		String username = validateUsername();

		System.out.println("Set your password:");
		input = s.nextLine().trim();
		String pass = input;

		System.out.println("Please enter your first name:");
		String firstName = parse();

		System.out.println("Please enter your last name:");
		String lastName = parse();

		String phoneNumber = validatePhoneNumber();

		System.out.println("Please enter your address:");
		String address = parse();

		String email = validateEmail();

		long SSN = validateSSN();

		int auth = -1; // default
		String eID = ""; // default

		if (type.equals("EMPLOYEE")) {
			auth = 0;
			System.out.println("Please enter your employee ID:");
			eID = parse();
			return new User(username, pass, email, SSN, firstName, lastName, phoneNumber, address, auth, eID, 0);
		} else {
			return new User(username, pass, email, SSN, firstName, lastName, phoneNumber, address, auth);
		}
	}

	// check if username is available
	public String validateUsername() {
		while (true) {
			System.out.println("Set your username:");
			String temp = parse();
			if (dao.getUser(temp) == null) {
				return temp;
			}
			System.out.println("That username is already taken! Please select another.");
		}
	}

	// method to ensure proper account typing
	public String validateAccountType() {
		while (true) {
			System.out.println("Are you registering as a client or an employee?");
			String temp = parse();

			if (temp.equalsIgnoreCase("client") || temp.equalsIgnoreCase("employee")) {
				return temp;
			} else {
				System.out.println("Please enter client or employee");
			}
		}
	}

	// method to validate phone numbers
	public String validatePhoneNumber() {
		// since we want int phone, we use a while loop until it parses correctly
		while (true) {
			try {
				System.out.println("Please enter your phone number: ");
				String temp = parse();
				// replace all non-numeric characters
				temp = temp.replaceAll("[\\D]", "");

				// System.out.println(temp);

				// check for empty string as a result of no numeric characters
				if (temp.length() < 1) {
					throw new NumberFormatException();
				}

				// System.out.println((temp.length()));

				return temp;
			} catch (NumberFormatException e) {
				System.out.println("There was an error parsing your phone number. Please try again.");
			}
		}
	}

	// method to validate email address
	public String validateEmail() {
		while (true) {
			try {
				// from JavaMail library
				// could also do manually with regex
				System.out.println("Please enter your email: ");
				input = s.nextLine();
				String temp = input;
				InternetAddress add = new InternetAddress(temp);
				add.validate();
				return temp;
			} catch (AddressException e) {
				System.out.println("There was an error parsing your email address. Please try again.");
			}
		}
	}

	// validate SSN
	public long validateSSN() {
		while (true) {
			try {
				System.out.println("Please enter your SSN: ");

				// remove non-numeric, then check length
				String temp = parse().replaceAll("[\\D]", "");
				// System.out.println(temp);
				// System.out.println(temp.length());

				if (temp.length() != 9) {
					System.out.print("Incorrect number of digits! ");
					throw new NumberFormatException();
				}

				if (dao.getUser(temp) == null) {

				}

				return Long.parseLong(temp);
			} catch (NumberFormatException e) {
				System.out.println("There was an error parsing your social security number. Please try again.");
			}
		}
	}

}
