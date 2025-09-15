# Loan Application Service for CrediYa
Api manages the loan applications for CrediYa

# Modules
* Database -> PostgreSQL with R2DBC

# Features
* 1.0.0 -> Implements register loan applications in endpoint /api/v1/solicitud - POST
* 1.0.1 -> Improvements to the code, business exceptions, and validations are implemented when creating a loan application.
* 1.1.0 -> Implements requires jwt and get loan applications by paging
* 1.2.0 -> Feature HU 5, 6, 7. Add Docker, Lambdas for send notification and calculate debt capacity