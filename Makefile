default: versioncheck

clean:
	./gradlew clean

compile: build

build:
	./gradlew build -xtest

versioncheck:
	./gradlew dependencyUpdates

upgrade-wrapper:
	./gradlew wrapper --gradle-version=8.2.1 --distribution-type=bin