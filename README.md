# LTGA GOMEA Parallel
This repository contains the parallel Java implementation of [LTGA GOMEA](https://homepages.cwi.nl/~bosman/source_code.php), a Genetic Algorithm, which is part of the Evolutionary Algorithm subset of Artificial Intelligence algorithms.

More than 5 years after my graduation I decided to publish the source code of [my Master's thesis](https://repository.tudelft.nl/islandora/object/uuid%3A6cf6c908-0f5d-4096-b3ad-aa96fd1ff382) Computer Science at Delft University of Technology, of which the results were also presented at [GECCO '15](https://dl.acm.org/doi/10.1145/2739482.2764679). 
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

_This requires [Docker](https://www.docker.com/get-started) to be installed on your machine_

A prebuilt Docker image is available on [DockerHub](https://hub.docker.com/r/roydb/ltga-gomea-parallel). You can pull and use this image directly to execute the Gradle tasks listed above.  
Simply run the following command:

```shell
docker pull roydb/ltga-gomea-parallel
docker run -it roydb/ltga-gomea-parallel:latest ./gradlew <task>
```

#### Building the container.

To run the LTGA code with alternative parameters or a change in the code, make sure you build the Docker container before using the command above:

```shell
docker build -t roydb/ltga-gomea-parallel:latest .
```

### An example

One of the combinatorial problems that this LTGA implementation is able to solve, is the [MAXCUT](https://en.wikipedia.org/wiki/Maximum_cut) problem.  
This repository contains a set of pre-generated problem instances in the [problemdata/maxcut](app/src/main/resources/problemdata/maxcut) folder. For each of these problems, a Best Known Value (BKV) and Average Random Value (ARV) is stored in the `BKV` and `ARV` folder respecitvely.

You can let LTGA solve a MAXCUT problem in parallel by running the following command:

```shell
./gradlew RunPPMaxcut
```

As you can see in [`build.gradle.kts`](app/build.gradle.kts), this will start `com.rdebokx.ltga.parallel.executables.Main`, which will load the problem data as defined in [`n0000100i00.txt`](app/src/main/resources/problemdata/maxcut/n0000100i00.txt).
This problem definition describes a (fully connected, undirected) graph with of **100** nodes and **4950** edges (see line 1). In the consequent lines, the weight of all vertices are defined in the format `<node1> <node2> <weight>`.

The Main executable will start a `ParallelJobRunner` that will instruct the LTGA to solve this problem with a population size of 250, using 4 threads.

The output of this task should be something like this:

```shell
./gradlew RunPPMaxcut

> Task :app:RunPPMaxcut
Running ParallelRunner generation 0
Running ParallelRunner generation 1
Running ParallelRunner generation 2
Running ParallelRunner generation 3
Running ParallelRunner generation 4
Running ParallelRunner generation 5
Running ParallelRunner generation 6
Running ParallelRunner generation 7
Running ParallelRunner generation 8
Termination condition met: best value so far with fitness 7975.0 matched the valueToReach
Jobrunner finished. 9 generations needed.
Best found solution: 
Solution:
        objectiveValue: 7975.0
        constraintValue: 0.0
        solution: {0100001001001110011011000110110100101010011011011101000110010000101100111111111001001011100100010110}

BUILD SUCCESSFUL in 2s
```

As shown, LTGA needed a total of 9 generations to get to the optimal value. This optimal value is printed as a sequence of bits, where x<sub>i</sub>=0 if that node belongs in the first half, and x<sub>i</sub>=1 if it belongs to the second half of the graph that was cut.

Note that the exact behavior can differ, due to the stochastic nature of the LTGA and depending on the parameters that are defined in the task.   
For all tasks, and to play around with the arguments of these tasks, check out [`build.gradle.kts`](app/build.gradle.kts)

## Training Data

This repository contains a set of pre-generated combinatorial problem definitions for the problems `NK_LANDSCAPES` and `MAXCUT`.
In total, this consists of more than 200MB of problem definition data, which is situated in the resources' [`problemdata` folder](app/src/main/resources/problemdata).

## Development

To run all unit tests, use `./gradlew test`

## Resources

* [My Master's Thesis at Delft University of Technology](https://repository.tudelft.nl/islandora/object/uuid%3A6cf6c908-0f5d-4096-b3ad-aa96fd1ff382)
* [GECCO '15 Publication](http://dl.acm.org/citation.cfm?id=2739482.2764679)

## License

LTGA GOMEA Parallel is licensed under the [MIT](LICENSE) license.
