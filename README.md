# API part of Cloud Native Application
CSYE 6225:  Network Structure & Cloud Computing (Spring 2024) - Prof. Tejas Parikh

### Assignment 1
```
brew services start postgresql
```
```
curl -vvvv http://localhost:8080/healthz 
```
```
curl -vvvv -XPUT http://localhost:8080/healthz
```
```
brew services stop postgresql
```
Notes:
- Encrypted DB password in application.yml using [Jasypt](http://www.jasypt.org/)
- Made password required for localhost in /opt/homebrew/var/postgresql@14/pg_hba.conf
```
cat /opt/homebrew/var/postgresql@14/pg_hba.conf
```
- Set DB connection timeout to 10 secs via HikariCP, the default connection pool in Spring Boot
- Able to start the application even if DB is down [Stackoverflow](https://stackoverflow.com/a/60348220)

### Assignment 2

##### Demo in Digital Ocean [Reference](https://www.digitalocean.com/community/tutorials/how-to-install-and-use-postgresql-on-centos-8)
1. Connect to droplet
```
ssh -i /Users/ashutosh/.ssh/digitalocean root@IP_ADDRESS
```
2. Create directories for cleanliness
```
mkdir demo_02 scripts
```
3. Copy scripts to the server
```
scp -i /Users/ashutosh/.ssh/digitalocean /Users/ashutosh/Downloads/ashutosh_singh_002855013_02.zip root@IP_ADDRESS:~/demo_02/
```
```
scp -i /Users/ashutosh/.ssh/digitalocean /Users/ashutosh/Documents/NEU/SEM2/Cloud/webapp/scripts/digital-ocean/setup-postgres.sh root@IP_ADDRESS:~/scripts/
```
```
scp -i /Users/ashutosh/.ssh/digitalocean /Users/ashutosh/Documents/NEU/SEM2/Cloud/webapp/scripts/digital-ocean/setup-java-maven.sh root@IP_ADDRESS:~/scripts/
```
```
scp -i /Users/ashutosh/.ssh/digitalocean /Users/ashutosh/Documents/NEU/SEM2/Cloud/webapp/scripts/digital-ocean/setup-app-and-run.sh root@IP_ADDRESS:~/scripts/
```
4. Give execute permission to the scripts
```
cd scripts
```
```
chmod +x setup-java-maven.sh setup-postgres.sh setup-app-and-run.sh
```
5. Setup postgres
```
./setup-postgres.sh
```
6. Setup java and maven
```
./setup-java-maven.sh
```
7. Source so you can use java and mvn without restart
```
source /etc/profile.d/java21.sh source /etc/profile.d/maven.sh
```
8. Check mvn
```
mvn -version
```
9. Unzip and run 
```
./setup-app-and-run.sh
```
- [Optional] Kill process for some reason
```
ps -ef
kill pid
```
Notes:
- Handled: Get 503 when DB is down during authentication/querying the DB instead of 4XX
- Added Swagger docs for better readability
- Added Custom Exception and Global Exception handler for cleaner code
- Added secrets in github environment for DB user and password

#### Demo create user
```
curl --location 'http://localhost:8080/v1/user' \
--header 'Content-Type: application/json' \
--data-raw '{
  "firstName": "Jon",
  "lastName": "Doe",
  "password": "TestPass@123",
  "email": "jon@doe.com"
}'
```
#### Demo update user
```
curl --location --request PUT 'http://localhost:8080/v1/user/self' \
--header 'Authorization: Basic am9uQGRvZS5jb206VGVzdFBhc3NAMTIz' \
--header 'Content-Type: application/json' \
--data-raw '{
  "firstName": "Johns",
  "lastName": "Doe",
  "password": "TestPass@123"
}'
```
#### Demo get user
```
curl --location 'http://localhost:8080/v1/user/self' \
--header 'Authorization: Basic am9uQGRvZS5jb206VGVzdFBhc3NAMTIz'
```