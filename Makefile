default: versioncheck

clean:
	./gradlew clean

compile: build

build:
	./gradlew build -xtest

versioncheck:
	./gradlew dependencyUpdates

upgrade-wrapper:
	./gradlew wrapper --gradle-version=8.3 --distribution-type=bin