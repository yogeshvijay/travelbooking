# Travel Booking Service


Within the project there is an example REST service for creating and storing contacts which can be accessed via the Swagger UI endpoint (http://localhost:8080/q/swagger-ui). It is encouraged that students spend spend time reading through this code to gain a strong understanding of how the project works. Not only this, but students are also encouraged to follow a similar packaging structure.

The 3 basic Rest Services in the application are Customer, Taxi and Bookings. Each have a multitude of endpoints that include creation, deletion and get methods.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.
