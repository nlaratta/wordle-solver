JAVA ?= java
JAVAC ?= javac

.PHONY: build test run clean
build:
	mkdir -p out
	$(JAVAC) -d out src/*.java test/*.java

test: build
	$(JAVA) -cp out FeedbackTest

run: build
	$(JAVA) -cp out WordleSolver

clean:
	rm -rf out
