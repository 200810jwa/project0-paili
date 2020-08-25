CREATE OR REPLACE PROCEDURE project0.setup() 
AS
$$
BEGIN
DROP TABLE IF EXISTS project0.users CASCADE;

CREATE TABLE project0.users (
	username VARCHAR(25) PRIMARY KEY,
	passhash VARCHAR(500) NOT NULL,
	email VARCHAR(250) NOT NULL,
	SSN BIGINT UNIQUE CHECK (SSN >= 0) NOT NULL,
	first_name VARCHAR(250) NOT NULL,
	last_name VARCHAR(250) NOT NULL,
	phone_number VARCHAR(50) NOT NULL,
	address VARCHAR(500),
	auth INT DEFAULT -1,
	FICO INT CHECK (FICO >= 350 AND FICO <= 850),
	employee_id VARCHAR(50) UNIQUE,
	salary DECIMAL(50,2) CHECK (salary >= 0)
);

DROP TABLE IF EXISTS project0.bank_accounts CASCADE;

CREATE TABLE project0.bank_accounts (
	account_id VARCHAR(250) PRIMARY KEY,
	balance DECIMAL(50,2) DEFAULT 0,
	approved BOOLEAN,
	owner BIGINT CHECK (owner > 0) NOT NULL,
	FOREIGN KEY (owner) REFERENCES project0.users(SSN) ON DELETE CASCADE
);


--INSERT INTO project0.users (username, passhash, email, ssn, first_name, last_name, phone_number, address, auth, FICO) VALUES 
--('DUMMY123', 'dummy', 'A@B.C', 111111111, 'JOHN', 'DOE', 1111111111, '123 FAKE ST.', -1, 800);
--
--INSERT INTO project0.users (username, passhash, email, ssn, first_name, last_name, phone_number, address, auth, FICO) VALUES 
--('DUMMY000', 'dummy', 'A@B.C', 444444444, 'JOHN', 'DOE', 1111111111, '123 FAKE ST.', -1, 400);
--
--INSERT INTO project0.users (username, passhash, email, ssn, first_name, last_name, phone_number, address, auth, FICO, employee_id, salary) VALUES 
--('EMPLOYEE123', 'dummy', 'A@B.C', 222222222, 'JOHN', 'DOE', 1111111111, '123 FAKE ST.', 0, 800, 'JOHNDOE', 10.50);
--
--INSERT INTO project0.users (username, passhash, email, ssn, first_name, last_name, phone_number, address, auth, FICO, employee_id, salary) VALUES 
--('ADMIN123', 'dummy', 'A@B.C', 333333333, 'JANE', 'DOE', 1111111111, '123 FAKE ST.', 1, 800, 'JANEDOE', 20.50);

--INSERT INTO project0.bank_accounts (account_id, balance, approved, owner) VALUES ('APPROVED123', 10.00, TRUE,111111111);
--INSERT INTO project0.bank_accounts (account_id, balance, approved, owner) VALUES ('PENDING123', 10.00, FALSE,111111111);
END
$$ LANGUAGE plpgsql;

-- junction table
--DROP TABLE IF EXISTS project0.user_account_relation CASCADE;

--CREATE TABLE project0.user_account_relation (
--	SSN BIGINT CHECK (SSN > 0),
--	account_id VARCHAR(250),
--	FOREIGN KEY (SSN) REFERENCES project0.users(SSN),
--	FOREIGN KEY (account_id) REFERENCES project0.bank_accounts(account_id)
--);