# Demo - Users App deployed on Kubernetes

Simple Users Spring Boot deployment on Minikube

## Start Minikube
	- Install VirtualBox from https://www.virtualbox.org/wiki/Downloads	
	- Install Minikube from https://kubernetes.io/docs/tasks/tools/install-minikube/ 
	- Install kubectl from https://kubernetes.io/docs/tasks/tools/install-kubectl/

Then, start Minikube:

```bash
# minikube start
```

## Create and run a simple Spring Boot app
1 -  To create a simple app go to [Spring initializr](http://start.spring.io) and then add 
the following dependencies: web, actuator, jpa, rest, hsqldb and lombok.


Alternatively, you can do the following



```bash
# curl https://start.spring.io/hello.zip -d bootVersion=1.5.9.RELEASE \
     -d dependencies=web, actuator, jpa, rest, hsqldb, lombok \
     -d groupId=com.ssense.k8s -d artifactId=users \
     -d name=user -d baseDir=user -o user.zip
#unzip user.zip
#cd user
```

2 -  Once you create the endpoints you need on Spring Boot, you need to add a property enabling the /application/env endpoint. 
This allows us to inspect environment variables added by Kubernetes.

src/main/resources/application.properties  
```bash
endpoints.env.enabled=true
```
Also we need to add in the same file the in-memory db connection properties
```bash
spring.datasource.url=jdbc:hsqldb:file:target/testdb
spring.datasource.username=sa
spring.jpa.hibernate.ddl-auto=create
spring.datasource.initialize=true
```

3 - Create a Dockerfile so we can package this app as a Docker image

Dockerfile
```bash
 #the parent image
 FROM openjdk:8
 
 # the volume where your docker image will be saved locally
 VOLUME /tmp
 
 # copy your app into the container
 ADD ./target/users-0.0.1-SNAPSHOT.jar /users.jar
 
 
 RUN sh -c 'touch /users.jar'
 
 # allows to configure a container that will run as an executable
 ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/users.jar"]
```

The Dockerfile will define what goes on the environment inside your container. 

4 - start Docker. Start the virtualBox and the minikube with a local Kubernetes cluster
```bash
minikube start
```
5 - Build the app and the Docker image.
    Note: we share the Docker environment used by Minikube, so we can communicate with the built-in Docker inside the Minikube


```bash
eval $(minikube docker-env)
./mvnw clean package
docker build -t $USER/users:0.0.1 .
```

One you create the image you can check where your image is with 
```bash
doker images
```

6 - Now we run a Kubernetes deployment on the running Minikube cluster
```bash
kubectl run users --image $USER/users:0.0.1 --port 8080
kubectl expose deployment users --type=NodePort
```

7 - Our service now run on the Minikube cluster, let's find out the IP address for the minikube and the external port for our service.
```bash
minikube service users --url
kubectl get service users
kubectl describe services users
```
And if you need to check the pods deployed
```bash
kubectl get pods
```

Now if you need to scale up your application (the sales are coming??), we add 4 more pods (5 total)
```bash
kubectl scale deployment users --replicas=5
kubectl get pods
```

Finally if you need to update your deployed image, build a new version of the application, create the new image and update it in Kubernetes:
```bash
docker build -t $USER/users:0.0.2 .
kubectl set image deployment/users users=users:0.0.2
```

Clean everything
```bash
 kubectl delete all -l run=users
 minikube stop
```