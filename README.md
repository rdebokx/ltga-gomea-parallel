# LTGA GOMEA Parallel
This repository contains the parallel Java implementation of [LTGA GOMEA](https://homepages.cwi.nl/~bosman/source_code.php), a Genetic Algorithm, which is part of the Evolutionary Algorithm subset of Artificial Intelligence algorithms.

More than 5 years after my graduation I decided to publish the source code of [my Master's thesis](https://repository.tudelft.nl/islandora/object/uuid%3A6cf6c908-0f5d-4096-b3ad-aa96fd1ff382) Computer Science at Delft University of Technology, of which the results were also presented at [GECCO '15](). 
This was mostly done in the hope to help any Evolutionary Algorithm enthousiasts in any way.

#### \*\*DISCLAIMER\*\* 

This code is provided **as is**. Please also see the [licence](#license) below. The code quality is poor and does not represent my current style of software development.

This code was written during my Master's Thesis with the main objective to be readable and to quickly be able to run a large number of experiments.
Code maintainability was not a requirement and as a result, this code has not been maintained since. While publishing this code I've done the bare minimum to get it running again. 

Again, this is simply published in the hope to help any EA enthousiasts and to give an insight of the AI that I developed in the past.

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
| `populationSize` (_p_) | The number of solutions that a population in a single evaluation consists of. |
| `maxEvaluations` | The maximum number of evaluations that the LTGA is allowed to perform, before it is terminated. Each generation, each generated solution is evaluated once. |
| `useValueToReach` | Boolean indicating whether the `valueToReach` parameter should be used. |
| `valueToReach` | The fitness value of the best value so far that should be reached. If the fitness of the best solution of a generation is larger or equal to this value, the LTGA is terminated. This parameter is ignored if `useValueToReach` is set to `false`. |
| `fitnessVarianceTolerance` | The minimal variance that the fitness value should show within a population. If the variance of the fitness values of a population drops below this value for a particular generation, the LTGA is terminated. |
| `maxNoImprovementStretch` | The maximum stretch of generations within no improvement is allowed. The number of generations within which no improvement was achieved in the fitness value of the best solution is recorded. If this number exceeds `maxNoImprovementStretch`, the LTGA is terminated. This avoids getting stuck in local optima. |

### Using Docker

Running the LTGA GOMEA Parallel in Docker is not supported yet.

## Development

* To run all unit tests, use `./gradlew test`

## Resources

* [My Master's Thesis at Delft University of Technology](https://repository.tudelft.nl/islandora/object/uuid%3A6cf6c908-0f5d-4096-b3ad-aa96fd1ff382)
* [GECCO '15 Publication](http://dl.acm.org/citation.cfm?id=2739482.2764679)

## License

LTGA GOMEA Parallel is licensed under the [MIT](LICENSE) license.
