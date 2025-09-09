CREATE TABLE IF NOT EXISTS status (
	id_status int8 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
	"name" varchar(50) NULL,
	description varchar(100) NULL,
	CONSTRAINT status_pkey PRIMARY KEY (id_status)
);

CREATE TABLE IF NOT EXISTS loan_type (
	id_loan_type int8 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
	"name" varchar(50) NULL,
	min_amount numeric NULL,
	max_amount numeric NULL,
	interest_rate numeric NULL,
	auto_valid bool NULL,
	CONSTRAINT loan_type_pkey PRIMARY KEY (id_loan_type)
);

CREATE TABLE IF NOT EXISTS application (
	id_application int8 GENERATED ALWAYS AS IDENTITY( INCREMENT BY 1 MINVALUE 1 MAXVALUE 9223372036854775807 START 1 CACHE 1 NO CYCLE) NOT NULL,
	amount numeric NULL,
	term int4 NULL,
	email varchar(100) NULL,
	id_status int8 NULL,
	id_loan_type int8 NULL,
	CONSTRAINT application_pkey PRIMARY KEY (id_application)
);

ALTER TABLE application ADD CONSTRAINT loan_type_application FOREIGN KEY (id_loan_type) REFERENCES loan_type(id_loan_type);
ALTER TABLE application ADD CONSTRAINT status_application FOREIGN KEY (id_status) REFERENCES status(id_status);