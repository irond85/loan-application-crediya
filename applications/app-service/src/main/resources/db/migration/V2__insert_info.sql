INSERT INTO status
("name", description)
VALUES('Pendiente por revisión', 'Solicitud pendiente por revision de un asesor');
INSERT INTO status
("name", description)
VALUES('Rechazada', 'Solicitud rechazada');
INSERT INTO status
("name", description)
VALUES('Revision manual', 'Solicitud debe realizarse manualmente');
INSERT INTO status
("name", description)
VALUES('Aprobada', 'Solicitud aprobada');

INSERT INTO loan_type
("name", min_amount, max_amount, interest_rate, auto_valid)
VALUES('libre inversion', 500000.00, 10000000.00, 1.76, true);

INSERT INTO application
(amount, term, email, id_status, id_loan_type)
VALUES(800000, 12, 'cliente1@email.co', 1, 1);
INSERT INTO application
(amount, term, email, id_status, id_loan_type)
VALUES(23213, 24, 'cliente2@email.co', 2, 1);
INSERT INTO application
(amount, term, email, id_status, id_loan_type)
VALUES(250000, 12, 'cliente2@email.co', 1, 1);
INSERT INTO application
(amount, term, email, id_status, id_loan_type)
VALUES(350000, 12, 'cliente1@email.co', 2, 1);