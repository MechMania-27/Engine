# MechMania Game Engine

The internal game engine to be used in the MechMania 27 AI Competition.

## Development

- Most of us use the IntelliJ IDEA IDE, but this isn't necessary.
- This is a gradle-based Java project. `src/main/java/mech/mania/engine` will contain the packages pertaining to the game engine, while `src/test/java/mech/mania/engine` contains the corresponding tests.
- Make sure to create unit tests as necessary to help both you and future code-readers to understand what the code does and why.
- Add any helpful links or discussion in the respective README files under each package folder.
- Create new Github issues for anything that needs to get done.
- Try to create a new branch for any feature development, and create a PR (pull request) to `main` once you think you are done with the feature. This will help us isolate any game-breaking changes.

## Running

- `./gradlew run` to run main function (located in `src/main/java/mech/mania/engine/Main.java`)
- `./gradlew test` to run all unit tests
- `./gradlew jar` to build a jar (creates jar in tar-ed folder in `build/distributions/Engine.{zip,tar}`
