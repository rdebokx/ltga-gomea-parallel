# ltga-gomea-parallel
This repository contains the parallel implementation of LTGA GOMEA

## Work in Progress

I decided to publish this algorithm that I developed during my MSc. Thesis at Delft University of Technology.

Right now, this repository only contains an initial dump of the code that I found in my archives. 

Please bear with me as I cleanup and polish the code and documentation to make it easier to understand and use.

TODO: disclaimer, also point to publications

## Running the application

### Requirements

* Java 8+

### Using Gradle
To run LTGA GOMEA directly on your machine, use one of the defined Gralde tasks as like this:

```shell
./gradlew <task>
```

The following tasks are defined:

* `RunSeqOnemax`
* `RunSeqDeceptiveTrap`
* `RunSeqNkLandscapes`
* `RunSeqMaxcut`
* `RunPPOnemax`
* `RunPPDeceptiveTrap`
* `RunPPNkLandscapes`
* `RunPPMaxcut`
* `RunEPOnemax`
* `RunEPDeceptiveTrap`
* `RunEPNkLandscapes`
* `RunEPMaxcut`


You can run a representative demo like this:

```shell
./gradlew RunPPDeceptiveTrap
```

For all tasks, and to play around with the arguments of these tasks, check out [`build.gradle.kts`](app/build.gradle.kts)

#### Configurations
In the task definitions in [`build.gradle.kts`](app/build.gradle.kts), the following configuration parameters are used.

| Parameter | Meaning |
|---|---|
| `problem` | The combinatorial problem that the LTGA needs to solve. Should be one of the problems as defined [here](app/src/main/java/com/rdebokx/ltga/shared/Problem.java).
| `numberOfParameters` (_n_) | The number of binary parameters that a solution for this problem consists of. |
| `populationSize` (_p_) | The number of solutions that a population in a single evaluation consists of |
| `maxEvaluations` | The maximum number of evaluations that the LTGA is allowed to perform, before it is terminated. Each generation, each generated solution is evaluated once. |
| `useValueToReach` | Boolean indicating whether the `valueToReach` parameter should be used. |
| `valueToReach` | The fitness value of the best value so far that should be reached. If the fitness of the best solution of a generation exceeds this value, the LTGA is terminated. This parameter is ignored if `useValueToReach` is set to `false` |
| `fitnessVarianceTolerance` | The minimal variance that the fitness value should show within a population. If the variance of the fitness values of a population drops below this value for a particular generation, the LTGA is terminated. |
| `maxNoImprovementStretch` | The maximum stretch of generations within not improvement was achieved. The number of generations within which no improvement was achieved within the fitness value of the best solution is recorded. If this number exceeds `maxNoImprovementStretch`, the LTGA is terminated. This avoids getting stuck in local optima. |

### Using Docker

Running the LTGA GOMEA Parallel in Docker is not supported yet.

## Development

* To run all unit tests, use `./gradlew test`

## Resources

TODO: point to publications.

## License

LTGA GOMEA Parallel is licensed under the [MIT](LICENSE) license.
