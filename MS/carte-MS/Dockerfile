# Étape de construction de l'image
FROM maven:3.9.9-eclipse-temurin-21 AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier le fichier pom.xml et télécharger les dépendances
COPY pom.xml .
RUN mvn dependency:go-offline

# Copier le code source dans l'image Docker
COPY src ./src

# Compiler et empaqueter l'application avec Maven
RUN mvn clean package -DskipTests

# Étape finale - Utilisation de l'image OpenJDK pour exécuter l'application
FROM eclipse-temurin:21-jre

# Définir le répertoire de travail
WORKDIR /app

# Copier le JAR de l'étape de construction vers l'image finale
COPY --from=build /app/target/carte-MS-1.0-SNAPSHOT.jar carte-MS.jar

# Exposer le port sur lequel l'application écoute
EXPOSE 8085

# Lancer l'application Spring Boot
CMD ["java", "-jar", "carte-MS.jar"]
