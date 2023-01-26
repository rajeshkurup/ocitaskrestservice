# OCI Task Service

OCI Task Service REST APIs to persist and manage OCI Tasks.

## Requirements

1. JDK 17
2. MySQL 8
3. Copy `application-test.properties` as `application-prod.properties` in `src/main/resources` folder.
4. Provide MySQL Connection String and Credentials in `application-prod.properties` file.

## Prerequisites

1. Log on to [OCI Cloud](https://cloud.oracle.com).
2. Create a Compartment `ocitask-compartment` for OCI Task System.
3. Create Container Registry for `ocitaskrestserv` (select `ocitask-compartment` Compartment).
4. Create VCN and Subnet (select `ocitask-compartment` Compartment).
5. Add following Ingress Rules in default Security List of the VCN Subnet.
6. Rule for `ocitaskrestserv`: Allow TCP Traffic for IP range `0.0.0.0/0` for destination port `8080` (all Source Ports).
7. Rule for `SonarQube`: Allow TCP Traffic for IP range `0.0.0.0/0` for destination port `9000` (all Source Ports).
8. Rule for `MySQL`: Allow TCP Traffic for IP range `0.0.0.0/0` for destination port `3306` (all Source Ports).
9. Rule for `ocitasknodeweb`: Allow TCP Traffic for IP range `0.0.0.0/0` for destination port `3000` (all Source Ports).

## Build Project Using OCI

1. Logon to [OCI Cloud](https://cloud.oracle.com).
2. Launch OCI Visual Builder and Configure OCI Account.
3. Create Build Executor Template for Oracle Linux 8 (Select Required Software, Java-17, JUnit and Docker-20).
4. Create Build Executor using above Template.
5. Create Build Project and select above Build Executor Template.
6. Configure Docker Registry.
7. Configure Git Repository.
8. Create Build Job.
9. Add Steps like Maven Build, Maven Verify and Maven Publish Reports.
10. Add Steps like Docker Login, Docker Build Image and Docker Push Image. Use Docker Image name like `sjc.ocir.io/<Namespace of the OCI Container Registry>/ocitaskrestserv:latest`.

## Deploy Project Using OCI

1. Logon to [OCI Cloud](https://cloud.oracle.com).
2. Use Compartment, VCN and Subnet created in Prerequisites section above.
3. [Deploy MySQL into OCI Kubernetes Cluster](https://dev.mysql.com/doc/mysql-operator/en/mysql-operator-innodbcluster-simple-kubectl.html).
4. Copy and Save Public IP of the MySQL Kubernetes Cluster.
5. [Deploy SonarQube into OCI Kubernetes Cluster](https://docs.sonarqube.org/9.6/setup-and-upgrade/deploy-on-kubernetes/deploy-sonarqube-on-kubernetes/).
6. Copy and Save Public IP of the SonarQube Kubernetes Cluster.
7. Deploy `sjc.ocir.io/<Namespace of the OCI Container Registry>/ocitaskrestserv:latest` Docker Image into [Oracle Managed Kubernetes Cluster](https://docs.oracle.com/en/solutions/monitor-applications-on-kubernetes/deploy-application-oracle-managed-kubernetes-cluster.html#GUID-B2D9C6EC-DCDF-4BB7-B9C1-3493DA03A3FF).
8. Copy and Save Public IP of the Kubernetes Cluster.

## Build Project Manually

### Build

Run `mvn clean install` from root folder.

### Testing

Run `mvn clean verify -P all-tests` from root folder to run all Unit Tests.

#### Functional Testing

1. Clone into [Functional Test Project](https://github.com/rajeshkurup/ocitaskrestserv-functional-test) for OCI Task Service.
2. Update Test Environment for OCI Task Service [here](https://github.com/rajeshkurup/ocitaskrestserv-functional-test/blob/main/src/test/java/org/oci/task/api/OciTaskApiFunctionalTest.java#L29).
3. Run `mvn clean install` from root folder. 
4. Run `mvn clean verify -P all-tests` from root folder to run all Functional Tests.

### Publish Unit Test Coverage Metrics

Run `mvn sonar:sonar -Dsonar.login=<User Token to access SonarQube Server> -Dsonar.host.url=<SonarQube URL> -Pcoverage` from root folder.

### View Unit Test Coverage

Visit SonarQube Server and find `org.oci.task:ocitaskrestserv` coverage report.
For example, `<SonarQube URL>/dashboard?id=org.oci.task%3Aocitaskrestserv`.

### Build Docker Image

Run `docker build . -t sjc.ocir.io/<Namespace of the OCI Container Registry>/ocitaskrestserv:latest` from root folder.

### Publish Docker Image

#### Login to DockerHub

Run `docker login sjc.ocir.io` from root folder. Provide DockerHub User Id and Password (API Token from `Identity->Domains->Default domain->Users-><UserId>->API keys`).

#### Publish Image into DockerHub

Run `docker push sjc.ocir.io/<Namespace of the OCI Container Registry>/ocitaskrestserv:latest` from root folder.

## Deploy Project Manually

### Create a VM Instance for MySQL

1. Select `ocitask-compartment` Compartment.
2. Use Oracle Linux 8.
3. Select Single Processor with 16GB Memory.
4. Save Public and Private Key files to enable SSH into the VM Instance.
5. Select VCN and Subnet from `ocitask-compartment` Compartment.
6. Create VM Instance `ocitask-mysql-host-1`.
7. Copy and Save Public IP.
8. SSH into new VM Instance `ssh -i <private key file> opc@<Public IP>`.
9. [Deploy Docker into the VM Instance](https://oracle-base.com/articles/linux/docker-install-docker-on-oracle-linux-ol8).
10. [Deploy MySQL Server into VM Instance using Docker](https://docs.oracle.com/cd/E17952_01/mysql-5.6-en/docker-mysql-getting-started.html).

### Create a VM Instance for OCI Task Service

1. Select `ocitask-compartment` Compartment.
2. Use Oracle Linux 8. 
3. Select Single Processor with 16GB Memory. 
4. Save Public and Private Key files to enable SSH into the VM Instance.
5. Select VCN and Subnet from `ocitask-compartment` Compartment.
6. Create VM Instance `ocitaskrestserv-host-1`.
7. Copy and Save Public IP.
8. SSH into new VM Instance `ssh -i <private key file> opc@<Public IP>`.
9. [Deploy Docker into the VM Instance](https://oracle-base.com/articles/linux/docker-install-docker-on-oracle-linux-ol8).

### Create a VM Instance for SonarQube

1. Select `ocitask-compartment` Compartment.
2. Use Oracle Linux 8.
3. Select Single Processor with 16GB Memory.
4. Save Public and Private Key files to enable SSH into the VM Instance.
5. Select VCN and Subnet from `ocitask-compartment` Compartment.
6. Create VM Instance `ocitask-sonarqube-host-1`.
7. Copy and Save Public IP.
8. SSH into new VM Instance `ssh -i <private key file> opc@<Public IP>`.
9. [Deploy Docker into the VM Instance](https://oracle-base.com/articles/linux/docker-install-docker-on-oracle-linux-ol8).
10. [Deploy SonarQube Server into VM Instance using Docker](https://www.techrepublic.com/article/deploy-sonarqube-docker/).

### Load Docker Image for Deployment

Run `docker pull sjc.ocir.io/<Namespace of the OCI Container Registry>/ocitaskrestserv:latest` on Host where Docker Image is going to run.

### Run Docker Container on a Host

Run `docker run -d -p 8080:8080 --restart always --name ocitaskrestserv_latest sjc.ocir.io/<Namespace of the OCI Container Registry>/ocitaskrestserv:latest` on Host where Docker Image is loaded.

### Verify Docker Container

Run `docker ps --all` on Host where Docker Image is deployed.

### Stop Docker Container

Run `docker stop ocitaskrestserv_latest` on Host where Docker Image is deployed.

### Remove Loaded Docker Image from Host

Run `docker rm ocitaskrestserv_latest` on Host where Docker Image is loaded.

## Access OCI Task REST APIs

- Use swagger for API Documentation: `http://<Host Name or IP where ocitaskrestserv_latest Docker Container in Running>:8080/application/swagger`
- Use Postman or Curl to access `ocitaskrestserv_latest` Docker Container.

### Load All Tasks

GET `http://<Host Name or IP where ocitaskrestserv_latest Docker Container in Running>:8080/application/v1/ocitaskrestservice/tasks`

### Load a Task

- GET `http://<Host Name where ocitaskrestserv_latest Docker Container in Running>:8080/application/v1/ocitaskrestservice/tasks/{id}`
- Path Variable: `id` (Task Identifier as Number)

### Create a Task

- POST `http://<Host Name where ocitaskrestserv_latest Docker Container in Running>:8080/application/v1/ocitaskrestservice/tasks`
- Sample Post Body:
```
{
    "title": "New Task",
    "description": "New Task Description",
    "priority": 1,
    "startDate": "yyyy-mm-dd",
    "dueDate": "yyyy-mm-dd",
    "completed": false
}
```

### Update a Task

- PUT `http://<Host Name where ocitaskrestserv_latest Docker Container in Running>:8080/application/v1/ocitaskrestservice/tasks/{id}`
- Path Variable: `id` (Task Identifier as Number)
- Sample Put Body:
```
{
    "id": 101,
    "title": "New Task",
    "description": "New Task Description",
    "priority": 1,
    "startDate": "yyyy-mm-dd",
    "dueDate": "yyyy-mm-dd",
    "completed": false
}
```

### Delete a Task

- DELETE `http://<Host Name where ocitaskrestserv_latest Docker Container in Running>:8080/application/v1/ocitaskrestservice/tasks/{id}`
- Path Variable: `id` (Task Identifier as Number)
