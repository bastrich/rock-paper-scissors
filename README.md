**Run** - _docker-compose up --build_<br>
**Swagger** - _http://localhost:8080/swagger-ui.html_

# What pay attention to
1) Parallel games of different users don't depend on each other, so a horizontal scaling is easily achievable here. 
   All we need is just create as many instances of the application as we need and provide horizontally scalable storage (for example partitioned postgres or some no-sql storage)
2) Didn't implement unit-tests right now as doing that task at night after difficult day. But if needed, they can be added. I think it will take a couple of hours.
3) Implementation of DAO classes can be easily replaced with any other implementation without changing interfaces.
4) Implementation of GameLogicService can be easily replaced with any other implementation without changing the interface.
5) Didn't implement DB migration process using some special tool in purpose of simplification. But if needed, liquibase, for example, can be added here.