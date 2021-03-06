/**
 * LTGA.c
 *
 * Copyright (c) 2010-2013 Peter A.N. Bosman
 *
 * The software in this file is the proprietary information of
 * Peter A.N. Bosman.
 *
 * IN NO EVENT WILL THE AUTHOR OF THIS SOFTWARE BE LIABLE TO YOU FOR ANY
 * DAMAGES, INCLUDING BUT NOT LIMITED TO LOST PROFITS, LOST SAVINGS, OR OTHER
 * INCIDENTIAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR THE INABILITY
 * TO USE SUCH PROGRAM, EVEN IF THE AUTHOR HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES, OR FOR ANY CLAIM BY ANY OTHER PARTY. THE AUTHOR MAKES NO
 * REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. THE
 * AUTHOR SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY ANYONE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * Linkage Tree Genetic Algorithm
 *
 * In this implementation, maximization is assumed.
 *
 * The software in this file is the result of (ongoing) scientific research.
 * The following people have been actively involved in this research over
 * the years:
 * - Peter A.N. Bosman
 * - Dirk Thierens
 */

/*-=-=-=-=-=-=-=-=-=-=-=-=-=-= Section Includes -=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <stdio.h>
#include <time.h>
#include <sys/time.h>
/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/





/*-=-=-=-=-=-=-=-=-=-=-=-= Section Header Functions -=-=-=-=-=-=-=-=-=-=-=-=*/
void *Malloc( long size );
void interpretCommandLine( int argc, char **argv );
void parseCommandLine( int argc, char **argv );
void parseOptions( int argc, char **argv, int *index );
void printAllInstalledProblems( void );
void optionError( char **argv, int index );
void parseParameters( int argc, char **argv, int *index );
void printUsage( void );
void checkOptions( void );
void printVerboseOverview( void );
double randomRealUniform01( void );
int randomInt( int maximum );
char *installedProblemName( int index );
int numberOfInstalledProblems( void );
void installedProblemEvaluation( int index, char *parameters, double *objective_value, double *constraint_value, int number_of_touched_parameters, int *touched_parameters_indices, char *parameters_before, double objective_value_before, double constraint_value_before );
void onemaxFunctionProblemEvaluation( char *parameters, double *objective_value, double *constraint_value );
void deceptiveTrap4TightEncodingFunctionProblemEvaluation( char *parameters, double *objective_value, double *constraint_value );
void deceptiveTrap4LooseEncodingFunctionProblemEvaluation( char *parameters, double *objective_value, double *constraint_value );
void deceptiveTrap5TightEncodingFunctionProblemEvaluation( char *parameters, double *objective_value, double *constraint_value );
void deceptiveTrap5LooseEncodingFunctionProblemEvaluation( char *parameters, double *objective_value, double *constraint_value );
void deceptiveTrapKTightEncodingFunctionProblemEvaluation( char *parameters, double *objective_value, double *constraint_value, int k );
void deceptiveTrapKLooseEncodingFunctionProblemEvaluation( char *parameters, double *objective_value, double *constraint_value, int k );
void initialize();
void initializeMemory();
void initializeRandomNumberGenerator();
void initializePopulationAndFitnessValues();
void writeGenerationalStatistics();
void writeGenerationalSolutions( char is_final_generation );
void writeGenerationalSolutionsBest( char is_final_generation );
void writeGenerationalSolutionsElitist( char is_final_generation );
void writeRunningTime( char *filename );
char checkTerminationCondition();
char checkNumberOfEvaluationsTerminationCondition();
char checkVTRTerminationCondition();
void determineBestSolutionInCurrentPopulation( int *index_of_best );
char checkFitnessVarianceTermination();
char checkImprovementStretchTermination();
void selectFinalSurvivors();
void determineBestSoFarSolution();
char betterFitness( double objective_value_x, double constraint_value_x, double objective_value_y, double constraint_value_y );
char equalFitness( double objective_value_x, double constraint_value_x, double objective_value_y, double constraint_value_y );
void makeOffspring();
void selectForLearningStructure();
void learnStructure();
double *estimateParametersForSingleBinaryMarginal( int *indices, int number_of_indices, int *factor_size );
int determineNearestNeighbour( int index, double **S_matrix, int mpm_length );
double computeUPGMAMICC( int *factor_indices_x, int factor_indices_x_length, int *factor_indices_y, int factor_indices_y_length );
void printLTStructure();
double log2( double x );
void generateAndEvaluateNewSolutionsToFillOffspring();
char *generateNewSolution( int which, double *obj, double *con );
void ezilaitiniMemory( void );
void run();
long getMilliSecondsRunning();
int main( int argc, char **argv );
/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/





/*-=-=-=-=-=-=-=-=-=-=-=- Section Global Variables -=-=-=-=-=-=-=-=-=-=-=-=-*/
char    **population,                       /* The population containing the solutions. */
        **selection,                        /* The selected solutions from which to learn structure. */
        **offspring,                        /* Offspring solutions. */
         *best_so_far_solution;             /* Elitist archive: best solution found so far. */
short     write_generational_statistics,    /* Whether to compute and write statistics every generation (0 = no). */
          write_generational_solutions,     /* Whether to write the population every generation (0 = no). */
          print_verbose_overview,           /* Whether to print a overview of settings (0 = no). */
		  determinePopSize,					/* Whether to run the program multiple times in order to determine the minimal needed population size */
          use_vtr,                          /* Whether to terminate at the value-to-reach (VTR) (0 = no). */
	  first_hit_has_happened;
int       problem_index,                    /* The index of the optimization problem. */
          number_of_parameters,             /* The number of parameters to be optimized. */
          number_of_generations,            /* The current generation count. */
          population_size,                  /* The size of each population. */
          selection_size,                   /* The size of the selection. */
          offspring_size,                   /* The size of the offspring. */
          maximum_number_of_evaluations,    /* The maximum number of evaluations. */
          no_improvement_stretch,           /* The number of subsequent generations without an improvement. */
          maximum_no_improvement_stretch,   /* The maximum number of subsequent generations without an improvement. */
        **mpm,                              /* The marginal product model. */
	 *mpm_number_of_indices,            /* The number of variables in each factor in the mpm. */
	  mpm_length,                       /* The number of factors in the mpm. */
        **lt,                               /* The linkage tree. */
	 *lt_number_of_indices,             /* The number of variables in each factor in the linkage tree. */
	  lt_length;                        /* The number of factors in the linkage tree. */
long      number_of_evaluations,            /* The current number of times a function evaluation was performed. */
          timestamp_start;                  /* The time stamp in milliseconds for when the algorithm was started. */
long long number_of_bit_flip_evaluations;
double   *objective_values,                 /* Objective values for population members. */
         *constraint_values,                /* Sum of all constraint violations for population members. */
         *objective_values_offspring,       /* Objective values of selected solutions. */
         *constraint_values_offspring,      /* Sum of all constraint violations of selected solutions. */
          best_so_far_objective_value,      /* Elitist archive: objective value of best solution found so far. */
          best_so_far_constraint_value,     /* Elitist archive: constraint value of best solution found so far. */
          fitness_variance_tolerance,       /* The minimum fitness variance level that is allowed. */
          vtr,                              /* The value-to-reach (function value of best solution that is feasible). */
        **MI_matrix,                        /* Mutual information between any two variables */
	  best_ever_evaluated_objective_value,
	  best_ever_evaluated_constraint_value;
int64_t   random_seed,                      /* The seed used for the random-number generator. */
          random_seed_changing;             /* Internally used variable for randomly setting a random seed. */
/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/




/*-=-=-=-=-=-=-=-=-=-=-=-=-=-= Section Constants -=-=-=-=-=-=-=-=-=-=-=-=-=-*/
#define FALSE 0
#define TRUE 1
/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/





/*-=-=-=-=-=-=-=-=-=-=-= Section Elementary Operations -=-=-=-=-=-=-=-=-=-=-*/
/**
 * Allocates memory and exits the program in case of a memory allocation failure.
 */
void *Malloc( long size )
{
  void *result;

  result = (void *) malloc( size );
  if( !result )
  {
    printf("\n");
    printf("Error while allocating memory in Malloc( %ld ), aborting program.", size);
    printf("\n");

    exit( 0 );
  }

  return( result );
}
/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/





/*-=-=-=-=-=-=-=-=-=-=- Section Interpret Command Line -=-=-=-=-=-=-=-=-=-=-*/
/**
 * Parses and checks the command line.
 */
void interpretCommandLine( int argc, char **argv )
{
  parseCommandLine( argc, argv );
  
  checkOptions();
}

/**
 * Parses the command line.
 * For options, see printUsage.
 */
void parseCommandLine( int argc, char **argv )
{
  int index;

  index = 1;

  parseOptions( argc, argv, &index );
  
  parseParameters( argc, argv, &index );
}

/**
 * Parses only the options from the command line.
 */
void parseOptions( int argc, char **argv, int *index )
{
  double dummy;

  write_generational_statistics = 0;
  write_generational_solutions  = 0;
  print_verbose_overview        = 0;
  use_vtr                       = 0;

  for( ; (*index) < argc; (*index)++ )
  {
    if( argv[*index][0] == '-' )
    {
      /* If it is a negative number, the option part is over */
      if( sscanf( argv[*index], "%lf", &dummy ) && argv[*index][1] != '\0' )
        break;

      if( argv[*index][1] == '\0' )
        optionError( argv, *index );
      else if( argv[*index][2] != '\0' )
        optionError( argv, *index );
      else
      {
        switch( argv[*index][1] )
        {
          case '?': printUsage(); break;
          case 'P': printAllInstalledProblems(); break;
          case 's': write_generational_statistics = 1; break;
          case 'w': write_generational_solutions  = 1; break;
          case 'v': print_verbose_overview        = 1; break;
		  case 'd': determinePopSize			  = 1; break;
          case 'r': use_vtr                       = 1; break;
          default : optionError( argv, *index );
        }
      }
    }
    else /* Argument is not an option, so option part is over */
     break;
  }
}

/**
 * Writes the names of all installed problems to the standard output.
 */
void printAllInstalledProblems( void )
{
  int i, n;

  n = numberOfInstalledProblems();
  printf("Installed optimization problems:\n");
  for( i = 0; i < n; i++ )
    printf("%3d: %s\n", i, installedProblemName( i ));

  exit( 0 );
}

/**
 * Informs the user of an illegal option and exits the program.
 */
void optionError( char **argv, int index )
{
  printf("Illegal option: %s\n\n", argv[index]);

  printUsage();
}

/**
 * Parses only the EA parameters from the command line.
 */
void parseParameters( int argc, char **argv, int *index )
{
  int noError;

  if( (argc - *index) != 6 )
  {
    printf("Number of parameters is incorrect, require 6 parameters (you provided %d).\n\n", (argc - *index));

    printUsage();
  }

  noError = 1;
  noError = noError && sscanf( argv[*index+0], "%d", &problem_index );
  noError = noError && sscanf( argv[*index+1], "%d", &number_of_parameters );
  noError = noError && sscanf( argv[*index+2], "%d", &population_size );
  noError = noError && sscanf( argv[*index+3], "%d", &maximum_number_of_evaluations );
  noError = noError && sscanf( argv[*index+4], "%lf", &vtr );
  noError = noError && sscanf( argv[*index+5], "%lf", &fitness_variance_tolerance );

  if( !noError )
  {
    printf("Error parsing parameters.\n\n");

    printUsage();
  }
}

/**
 * Prints usage information and exits the program.
 */
void printUsage( void )
{
  printf("Usage: LTGA [-?] [-P] [-s] [-w] [-v] [-r] pro dim pop eva vtr tol\n");
  printf(" -?: Prints out this usage information.\n");
  printf(" -P: Prints out a list of all installed optimization problems.\n");
  printf(" -s: Enables computing and writing of statistics every generation.\n");
  printf(" -w: Enables writing of solutions and their fitnesses every generation.\n");
  printf(" -v: Enables verbose mode. Prints the settings before starting the run.\n");
  printf(" -r: Enables use of vtr in termination condition (value-to-reach).\n");
  printf("\n");
  printf("  pro: Index of optimization problem to be solved (minimization).\n");
  printf("  dim: Number of parameters.\n");
  printf("  pop: Population size per normal.\n");
  printf("  eva: Maximum number of evaluations allowed.\n");
  printf("  vtr: The value to reach. If the objective value of the best feasible solution reaches\n");
  printf("       this value, termination is enforced (if -r is specified).\n");
  printf("  tol: The tolerance level for fitness variance (i.e. minimum fitness variance)\n");
  exit( 0 );
}

/**
 * Checks whether the selected options are feasible.
 */
void checkOptions( void )
{
  if( number_of_parameters < 1 )
  {
    printf("\n");
    printf("Error: number of parameters < 1 (read: %d). Require number of parameters >= 1.", number_of_parameters);
    printf("\n\n");

    exit( 0 );
  }

  if( population_size < 1 )
  {
    printf("\n");
    printf("Error: population size < 1 (read: %d). Require population size >= 1.", population_size);
    printf("\n\n");

    exit( 0 );
  }

  if( installedProblemName( problem_index ) == NULL )
  {
    printf("\n");
    printf("Error: unknown index for problem (read index %d).", problem_index );
    printf("\n\n");

    exit( 0 );
  }
}

/**
 * Prints the settings as read from the command line.
 */
void printVerboseOverview( void )
{
  printf("### Settings ######################################\n");
  printf("#\n");
  printf("# Statistics writing every generation: %s\n", write_generational_statistics ? "enabled" : "disabled");
  printf("# Population file writing            : %s\n", write_generational_solutions ? "enabled" : "disabled");
  printf("# Use of value-to-reach (vtr)        : %s\n", use_vtr ? "enabled" : "disabled");
  printf("#\n");
  printf("###################################################\n");
  printf("#\n");
  printf("# Problem                 = %s\n", installedProblemName( problem_index ));
  printf("# Number of parameters    = %d\n", number_of_parameters);
  printf("# Population size         = %d\n", population_size);
  printf("# Maximum numb. of eval.  = %d\n", maximum_number_of_evaluations);
  printf("# Value to reach (vtr)    = %e\n", vtr);
  printf("# Fitness var. tolerance  = %e\n", fitness_variance_tolerance);
  printf("# Random seed             = %ld\n", (long) random_seed);
  printf("#\n");
  printf("###################################################\n");
}
/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/





/*-=-=-=-=-=-=-=-=-=-=-=-=-= Section Random Numbers -=-=-=-=-=-=-=-=-=-=-=-=*/
/**
 * Returns a random double, distributed uniformly between 0 and 1.
 */
double randomRealUniform01( void )
{
  int64_t n26, n27;
  double  result;

  random_seed_changing = (random_seed_changing * 0x5DEECE66DLLU + 0xBLLU) & ((1LLU << 48) - 1);
  n26                  = (int64_t)(random_seed_changing >> (48 - 26));
  random_seed_changing = (random_seed_changing * 0x5DEECE66DLLU + 0xBLLU) & ((1LLU << 48) - 1);
  n27                  = (int64_t)(random_seed_changing >> (48 - 27));
  result               = (((int64_t)n26 << 27) + n27) / ((double) (1LLU << 53));

  return( result );
}
        
/**
 * Returns a random integer, distributed uniformly between 0 and maximum.
 */
int randomInt( int maximum )
{
  int result;
  
  result = (int) (((double) maximum)*randomRealUniform01());
  
  return( result );
}
/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/





/*-=-=-=-=-=-=-=-=-=-=-=-=-=-= Section Problems -=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
/**
 * Returns the name of an installed problem.
 */
char *installedProblemName( int index )
{
  switch( index )
  {
    case  0: return( (char *) "Onemax" );
    case  1: return( (char *) "Deceptive Trap 4 - Tight Encoding" );
    case  2: return( (char *) "Deceptive Trap 4 - Loose Encoding" );
    case  3: return( (char *) "Deceptive Trap 5 - Tight Encoding" );
    case  4: return( (char *) "Deceptive Trap 5 - Loose Encoding" );
  }

  return( NULL );
}

/**
 * Returns the number of problems installed.
 */
int numberOfInstalledProblems( void )
{
  static int result = -1;
  
  if( result == -1 )
  {
    result = 0;
    while( installedProblemName( result ) != NULL )
      result++;
  }
  
  return( result );
}

/**
 * Computes the value of the single objective
 * and the sum of all constraint violations
 * function.
 */
void installedProblemEvaluation( int index, char *parameters, double *objective_value, double *constraint_value, int number_of_touched_parameters, int *touched_parameters_indices, char *parameters_before, double objective_value_before, double constraint_value_before )
{
  number_of_evaluations++;

  *objective_value  = 0.0;
  *constraint_value = 0.0;

  switch( index )
  {
    case  0: onemaxFunctionProblemEvaluation( parameters, objective_value, constraint_value ); break;
    case  1: deceptiveTrap4TightEncodingFunctionProblemEvaluation( parameters, objective_value, constraint_value ); break;
    case  2: deceptiveTrap4LooseEncodingFunctionProblemEvaluation( parameters, objective_value, constraint_value ); break;
    case  3: deceptiveTrap5TightEncodingFunctionProblemEvaluation( parameters, objective_value, constraint_value ); break;
    case  4: deceptiveTrap5LooseEncodingFunctionProblemEvaluation( parameters, objective_value, constraint_value ); break;
  }

  if( first_hit_has_happened == 0 )
  {
    if( (*constraint_value) == 0 && (*objective_value) >= vtr  )
    {
      first_hit_has_happened = 1;
      writeRunningTime( (char *) "first_hitting_time.dat" );
    }
  }

  if( (number_of_evaluations == 1) || betterFitness( *objective_value, *constraint_value, best_ever_evaluated_objective_value, best_ever_evaluated_constraint_value ) )
  {
    best_ever_evaluated_objective_value  = *objective_value;
    best_ever_evaluated_constraint_value = *constraint_value;
    writeRunningTime( (char *) "best_hitting_time.dat" );
  }
}

void onemaxFunctionProblemEvaluation( char *parameters, double *objective_value, double *constraint_value )
{
  int    i;
  double result;

  result = 0.0;
  for( i = 0; i < number_of_parameters; i++ )
    result += (parameters[i] == TRUE) ? 1 : 0;

  *objective_value  = result;
  *constraint_value = 0;
}

void deceptiveTrap4TightEncodingFunctionProblemEvaluation( char *parameters, double *objective_value, double *constraint_value )
{
  deceptiveTrapKTightEncodingFunctionProblemEvaluation( parameters, objective_value, constraint_value, 4 );
}

void deceptiveTrap4LooseEncodingFunctionProblemEvaluation( char *parameters, double *objective_value, double *constraint_value )
{
  deceptiveTrapKLooseEncodingFunctionProblemEvaluation( parameters, objective_value, constraint_value, 4 );
}

void deceptiveTrap5TightEncodingFunctionProblemEvaluation( char *parameters, double *objective_value, double *constraint_value )
{
  deceptiveTrapKTightEncodingFunctionProblemEvaluation( parameters, objective_value, constraint_value, 5 );
}

void deceptiveTrap5LooseEncodingFunctionProblemEvaluation( char *parameters, double *objective_value, double *constraint_value )
{
  deceptiveTrapKLooseEncodingFunctionProblemEvaluation( parameters, objective_value, constraint_value, 5 );
}

void deceptiveTrapKTightEncodingFunctionProblemEvaluation( char *parameters, double *objective_value, double *constraint_value, int k )
{
  int    i, j, m, u;
  double result;

  if( number_of_parameters % k != 0 )
  {
    printf("Error in evaluating deceptive trap k: Number of parameters is not a multiple of k.\n");
    exit( 0 );
  }

  m      = number_of_parameters / k;
  result = 0.0;
  for( i = 0; i < m; i++ )
  {
    u = 0;
    for( j = 0; j < k; j++ )
      u += (parameters[i*k+j] == TRUE) ? 1 : 0;

    if( u == k )
      result += 1.0;
    else
      result += ((double) (k-1-u))/((double) k);
  }

  *objective_value  = result;
  *constraint_value = 0;
}

void deceptiveTrapKLooseEncodingFunctionProblemEvaluation( char *parameters, double *objective_value, double *constraint_value, int k )
{
  int    i, j, m, u;
  double result;

  if( number_of_parameters % k != 0 )
  {
    printf("Error in evaluating deceptive trap k: Number of parameters is not a multiple of k.\n");
    exit( 0 );
  }

  m      = number_of_parameters / k;
  result = 0.0;
  for( i = 0; i < m; i++ )
  {
    u = 0;
    for( j = 0; j < k; j++ )
      u += (parameters[i+m*j] == TRUE) ? 1 : 0;

    if( u == k )
      result += 1.0;
    else
      result += ((double) (k-1-u))/((double) k);
  }

  *objective_value  = result;
  *constraint_value = 0;
}
/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/





/*-=-=-=-=-=-=-=-=-=-=-=-=- Section Initialize -=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/
/**
 * Performs initializations that are required before starting a run.
 */
void initialize()
{
  maximum_no_improvement_stretch = 1000000;

  number_of_generations          = 0;
  number_of_evaluations          = 0;
  number_of_bit_flip_evaluations = 0;

  initializeMemory();

  initializeRandomNumberGenerator();

  initializePopulationAndFitnessValues();
}

/**
 * Initializes the memory.
 */
void initializeMemory()
{
  int i;

  selection_size = population_size;
  offspring_size = population_size;

  population                       = (char **) Malloc( population_size*sizeof( char * ) );
  objective_values                 = (double *) Malloc( population_size*sizeof( double ) );
  constraint_values                = (double *) Malloc( population_size*sizeof( double ) );
  selection                        = (char **) Malloc( selection_size*sizeof( char * ) );
  offspring                        = (char **) Malloc( offspring_size*sizeof( char * ) );
  objective_values_offspring       = (double *) Malloc( offspring_size*sizeof( double ) );
  constraint_values_offspring      = (double *) Malloc( offspring_size*sizeof( double ) );
  best_so_far_solution             = (char *) Malloc( number_of_parameters*sizeof( char ) );
  MI_matrix                        = (double **) Malloc( number_of_parameters*sizeof( double * ) );

  for( i = 0; i < population_size; i++ )
    population[i] = (char *) Malloc( number_of_parameters*sizeof( char ) );

  for( i = 0; i < selection_size; i++ )
    selection[i] = (char *) Malloc( number_of_parameters*sizeof( char ) );

  for( i = 0; i < offspring_size; i++ )
    offspring[i] = (char *) Malloc( number_of_parameters*sizeof( char ) );

  for( i = 0; i < number_of_parameters; i++ )
    MI_matrix[i] = (double *) Malloc( number_of_parameters*sizeof( double ) );

  lt = NULL;
}

/**
 * Initializes the pseudo-random number generator.
 */
void initializeRandomNumberGenerator()
{
  struct timeval tv;
  struct tm *timep;

  while( random_seed_changing == 0 )
  {
    gettimeofday( &tv, NULL );
    timep = localtime (&tv.tv_sec);
    random_seed_changing = timep->tm_hour * 3600 * 1000 + timep->tm_min * 60 * 1000 + timep->tm_sec * 1000 + tv.tv_usec / 1000;
  }

  random_seed = random_seed_changing;
}

/**
 * Initializes the population and the objective values by randomly
 * generation n solutions.
 */
void initializePopulationAndFitnessValues()
{
  int     i, j;
  double  obj, con;

  for( i = 0; i < population_size; i++ )
  {
    for( j = 0; j < number_of_parameters; j++ )
      population[i][j] = (randomInt( 2 ) == 1) ? TRUE : FALSE;

    installedProblemEvaluation( problem_index, population[i], &obj, &con, 0, NULL, NULL, 0, 0 );
    objective_values[i]  = obj;
    constraint_values[i] = con;
  }
}
/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/





/*-=-=-=-=-=-=-=-=-=-=-=-=-=- Section Output =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
/**
 * Writes (appends) statistics about the current generation to a
 * file named "statistics.dat".
 */
void writeGenerationalStatistics()
{
  char    string[10000];
  int     i;
  double  objective_avg, objective_var, objective_best, objective_worst,
          constraint_avg, constraint_var, constraint_best, constraint_worst;
  FILE   *file;

  /* First compute the statistics */
  /* Average, best and worst */
  objective_avg    = 0.0;
  constraint_avg   = 0.0;
  objective_best   = objective_values[0];
  objective_worst  = objective_values[0];
  constraint_best  = constraint_values[0];
  constraint_worst = constraint_values[0];
  for( i = 0; i < population_size; i++ )
  {
    objective_avg += objective_values[i];
    constraint_avg += constraint_values[i];
    if( betterFitness( objective_values[i], constraint_values[i], objective_best, constraint_best ) )
    {
      objective_best  = objective_values[i];
      constraint_best = constraint_values[i];
    }
    if( betterFitness( objective_worst, constraint_worst, objective_values[i], constraint_values[i] ) )
    {
      objective_worst  = objective_values[i];
      constraint_worst = constraint_values[i];
    }
  }
  objective_avg = objective_avg / ((double) population_size);
  constraint_avg = constraint_avg / ((double) population_size);

  /* Variance */
  objective_var  = 0.0;
  constraint_var = 0.0;
  for( i = 0; i < population_size; i++ )
  {
    objective_var += (objective_values[i] - objective_avg)*(objective_values[i] - objective_avg);
    constraint_var += (constraint_values[i] - constraint_avg)*(constraint_values[i] - constraint_avg);
  }
  objective_var = objective_var / ((double) population_size);
  constraint_var = constraint_var / ((double) population_size);

  if( objective_var <= 0.0 )
     objective_var = 0.0;
  if( constraint_var <= 0.0 )
     constraint_var = 0.0;

  /* Then write them */
  file = NULL;
  if( number_of_generations == 0 )
  {
    file = fopen( "statistics.dat", "w" );

    sprintf( string, "# Generation Evaluations      Average-obj.     Variance-obj.         Best-obj.        Worst-obj.        Elite-obj.      Average-con.     Variance-con.         Best-con.        Worst-con.        Elite-con.\n");
    fputs( string, file );
  }
  else
    file = fopen( "statistics.dat", "a" );

  sprintf( string, "  %10d %11ld %17.10e %17.10e %17.10e %17.10e %17.10e %17.10e %17.10e %17.10e %17.10e %17.10e\n", number_of_generations, number_of_evaluations, objective_avg, objective_var, objective_best, objective_worst, best_so_far_objective_value, constraint_avg, constraint_var, constraint_best, constraint_worst, best_so_far_constraint_value );
  fputs( string, file );

  fclose( file );
}

/**
 * Writes the solutions to various files. The filenames
 * contain the generation. If the flag is_final_generation
 * is set the generation number in the filename
 * is replaced with the word "final".
 *
 * population_xxxxx_generation_xxxxx.dat: the population
 * offspring_xxxxx_generation_xxxxx.dat : the offspring
 */
void writeGenerationalSolutions( char is_final_generation )
{
  char  filename[1000], string[10000];
  int   i, j;
  FILE *file;

  /* Population */
  if( is_final_generation )
    sprintf( filename, "population_generation_final.dat" );
  else
    sprintf( filename, "population_generation_%05d.dat", number_of_generations );
  file = fopen( filename, "w" );
  for( i = 0; i < population_size; i++ )
  {
    for( j = 0; j < number_of_parameters; j++ )
    {
      sprintf( string, "%d", (population[i][j] == TRUE) ? 1 : 0 );
      fputs( string, file );
    }
    sprintf( string, "     %17.10e %17.10e\n", objective_values[i], constraint_values[i] );
    fputs( string, file );
  }
  fclose( file );
  
  /* Offspring */
  if( (number_of_generations > 0) && (is_final_generation == FALSE) )
  {
    sprintf( filename, "offspring_generation_%05d.dat", number_of_generations-1 );
    file = fopen( filename, "w" );

    for( i = 0; i < offspring_size; i++ )
    {
      for( j = 0; j < number_of_parameters; j++ )
      {
        sprintf( string, "%d", (offspring[i][j] == TRUE) ? 1 : 0 );
        fputs( string, file );
      }
      sprintf( string, "     %17.10e %17.10e\n", objective_values_offspring[i], constraint_values_offspring[i] );
      fputs( string, file );
    }
    fclose( file );
  }

  writeGenerationalSolutionsBest( is_final_generation );

  writeGenerationalSolutionsElitist( is_final_generation );
}

/**
 * Writes the best solution to a file named
 * best_generation_xxxxx.dat where xxxxx is the
 * generation number. If the flag is_final_generation
 * is set the generation number in the filename
 * is replaced with the word "final".The output
 * file contains the solution,
 * followed by five white spaces and then the
 * single objective value for that solution
 * and its sum of constraint violations.
 */
void writeGenerationalSolutionsBest( char is_final_generation )
{
  char  filename[1000], string[10000];
  int   i, individual_index_best;
  FILE *file;

  /* First find the best of all */
  determineBestSolutionInCurrentPopulation( &individual_index_best );

  /* Then output it */
  if( is_final_generation == TRUE )
    sprintf( filename, "best_generation_final.dat" );
  else
    sprintf( filename, "best_generation_%05d.dat", number_of_generations );
  file = fopen( filename, "w" );

  for( i = 0; i < number_of_parameters; i++ )
  {
    sprintf( string, "%d", (population[individual_index_best][i] == TRUE) ? 1 : 0 );
    fputs( string, file );
  }
  sprintf( string, "     %17.10e %17.10e\n", objective_values[individual_index_best], constraint_values[individual_index_best] );
  fputs( string, file );

  fclose( file );
}

/**
 * Writes the elitist solution to a file named
 * elitist_generation_xxxxx.dat where xxxxx is the
 * generation number. If the flag is_final_generation
 * is set the generation number in the filename
 * is replaced with the word "final".The output
 * file contains the solution,
 * followed by five white spaces and then the
 * single objective value for that solution
 * and its sum of constraint violations.
 */
void writeGenerationalSolutionsElitist( char is_final_generation )
{
  char  filename[1000], string[10000];
  int   i;
  FILE *file;

  /* Then output it */
  if( is_final_generation == TRUE )
    sprintf( filename, "elitist_generation_final.dat" );
  else
    sprintf( filename, "elitist_generation_%05d.dat", number_of_generations );
  file = fopen( filename, "w" );

  for( i = 0; i < number_of_parameters; i++ )
  {
    sprintf( string, "%d", (best_so_far_solution[i] == TRUE) ? 1 : 0 );
    fputs( string, file );
  }
  sprintf( string, "     %17.10e %17.10e\n", best_so_far_objective_value, best_so_far_constraint_value );
  fputs( string, file );

  fclose( file );
}

void writeRunningTime( char *filename )
{
  char  string[10000];
  FILE *file;

  file = fopen( filename, "w" );
  sprintf( string, "%ld %ld %lld\n", getMilliSecondsRunning(), number_of_evaluations, number_of_bit_flip_evaluations );
  fputs( string, file );
  fclose( file );
}
/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/





/*-=-=-=-=-=-=-=-=-=-=-=-=- Section Termination -=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
/**
 * Returns TRUE if termination should be enforced, FALSE otherwise.
 */
char checkTerminationCondition()
{
  if( maximum_number_of_evaluations >= 0 )
  {
    if( checkNumberOfEvaluationsTerminationCondition() )
      return( TRUE );
  }
  
  if( use_vtr )
  {
    if( checkVTRTerminationCondition() )
      return( TRUE );
  }

  if( checkFitnessVarianceTermination() )
    return( TRUE );

  if( checkImprovementStretchTermination() )
    return( TRUE );

  return( FALSE );
}

/**
 * Returns TRUE if the maximum number of evaluations
 * has been reached, FALSE otherwise.
 */
char checkNumberOfEvaluationsTerminationCondition()
{
	if( number_of_evaluations >= maximum_number_of_evaluations ){
		if(!determinePopSize){
			printf("Number of evaluations exceeded max number of evaluations.\n");
		}
		return( TRUE );
	}

  return( FALSE );
}

/**
 * Returns TRUE if the value-to-reach has been reached.
 */
char checkVTRTerminationCondition()
{
  int individual_index_best;

  determineBestSolutionInCurrentPopulation( &individual_index_best );

	if( constraint_values[individual_index_best] == 0 && objective_values[individual_index_best] >= vtr  ){
		if(!determinePopSize){
			printf("VTR was reached.\n");
		}
		return( TRUE );
	}
    
  return( FALSE );
}

/**
 * Determines which solution is the best of all solutions in the current population.
 */
void determineBestSolutionInCurrentPopulation( int *index_of_best )
{
  int i;

  *index_of_best = 0;
  for( i = 0; i < population_size; i++ )
  {
    if( betterFitness( objective_values[i], constraint_values[i],
                       objective_values[*index_of_best], constraint_values[*index_of_best] ) )
      *index_of_best = i;
  }
}

/**
 * Checks whether the fitness variance
 * has become too small (user-defined tolerance).
 */
char checkFitnessVarianceTermination()
{
  int    i;
  double objective_avg, objective_var;
  
  objective_avg = 0.0;
  for( i = 0; i < population_size; i++ )
    objective_avg  += objective_values[i];
  objective_avg  = objective_avg / ((double) population_size);

  objective_var = 0.0;
  for( i = 0; i < population_size; i++ )
    objective_var  += (objective_values[i]-objective_avg)*(objective_values[i]-objective_avg);
  objective_var  = objective_var / ((double) population_size);

  if( objective_var <= 0.0 )
     objective_var = 0.0;

	if( objective_var <= fitness_variance_tolerance ){
		if(!determinePopSize){
			printf("Fitness variance tolerance reached.\n");
		}
		return( TRUE );
	}

  return( FALSE );
}

/**
 * Checks whether the best fitness hasn't improved for too long.
 */
char checkImprovementStretchTermination()
{
	if( no_improvement_stretch > maximum_no_improvement_stretch ){
		if(!determinePopSize){
			printf("No improvement stretch reached.\n");
		}
		return( TRUE );
	}

  return( FALSE );
}
/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/





/*=-=-=-=-=-=-=-=-=-=-= Section Survivor Selection =-=-=-=-=-=-=-=-=-=-=-=-=*/
/**
 * Determines the solutions that finally survive the generation (offspring only).
 */
void selectFinalSurvivors()
{
  int i, j;

  for( i = 0; i < population_size; i++ )
  {
    for( j = 0; j < number_of_parameters; j++ )
      population[i][j] = offspring[i][j];
    objective_values[i]  = objective_values_offspring[i];
    constraint_values[i] = constraint_values_offspring[i];
  }
}

/**
 * Determines the best solution in the population
 * and compares it to the best solution found so far.
 * Then stores the very best solution found so far.
 */
void determineBestSoFarSolution()
{
  char replace_best_so_far;
  int  i, index, individual_index_best;

  /* First find the best of all */
  determineBestSolutionInCurrentPopulation( &index );
  individual_index_best = index;

  replace_best_so_far = FALSE;
  if( number_of_generations == 0 )
    replace_best_so_far = TRUE;
  else
  {
    if( betterFitness( objective_values[individual_index_best], constraint_values[individual_index_best],
                       best_so_far_objective_value, best_so_far_constraint_value ) )
      replace_best_so_far = TRUE;
  }

  if( replace_best_so_far == TRUE )
  {
    for( i = 0; i < number_of_parameters; i++ )
      best_so_far_solution[i] = population[individual_index_best][i];
    best_so_far_objective_value  = objective_values[individual_index_best];
    best_so_far_constraint_value = constraint_values[individual_index_best];
    no_improvement_stretch       = 0;
  }
  else
    no_improvement_stretch++;
}

/**
 * Returns 1 if x is better than y, 0 otherwise.
 * x is not better than y unless:
 * - x and y are both infeasible and x has a smaller sum of constraint violations, or
 * - x is feasible and y is not, or
 * - x and y are both feasible and x has a larger objective value than y
 */
char betterFitness( double objective_value_x, double constraint_value_x, double objective_value_y, double constraint_value_y )
{
  char result;
  
  result = FALSE;

  if( constraint_value_x > 0 ) /* x is infeasible */
  {
    if( constraint_value_y > 0 ) /* Both are infeasible */
    {
      if( constraint_value_x < constraint_value_y )
       result = TRUE;
    }
  }
  else /* x is feasible */
  {
    if( constraint_value_y > 0 ) /* x is feasible and y is not */
      result = TRUE;
    else /* Both are feasible */
    {
      if( objective_value_x > objective_value_y )
        result = TRUE;
    }
  }

  return( result );
}

/**
 * Returns 1 if x is equally preferable to y, 0 otherwise.
 */
char equalFitness( double objective_value_x, double constraint_value_x, double objective_value_y, double constraint_value_y )
{
  char result;
  
  result = FALSE;

  if( (constraint_value_x == constraint_value_y) && (objective_value_x == objective_value_y) )
    result = TRUE;

  return( result );
}
/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/





/*-=-=-=-=-=-=-=-=-=-=-=-=-= Section Variation -==-=-=-=-=-=-=-=-=-=-=-=-=-=*/
/**
 * First estimates the parameters of the distribution in the
 * parameter space from the selection. Then copies the selection
 * to the offspring. Finally fills up the offspring pool
 * by drawing new samples from the distribution.
 */
void makeOffspring()
{
  selectForLearningStructure();

  learnStructure();

  generateAndEvaluateNewSolutionsToFillOffspring();
}

/**
 * Performs tournament selection to create the dataset from which to perform structure learning.
 */
void selectForLearningStructure()
{
  int i, j, a, b, c, *order, from, to, index_best, tournament_size;

  tournament_size = 2;

  order = (int *) Malloc( population_size*sizeof( int ) );
  for( i = 0; i < population_size; i++ )
    order[i] = i;
  for( i = 0; i < population_size; i++ )
  {
    a        = randomInt( population_size );
    b        = randomInt( population_size );
    c        = order[a];
    order[a] = order[b];
    order[b] = c;
  }

  from = 0;
  to   = tournament_size-1;
  for( i = 0; i < selection_size; i++ )
  {
    index_best = from;
    for( j = from+1; j <= to; j++ )
      if( betterFitness( objective_values[order[j%population_size]], constraint_values[order[j%population_size]], objective_values[order[index_best]], constraint_values[order[index_best]] ) )
        index_best = j%population_size;

    for( j = 0; j < number_of_parameters; j++ )
      selection[i][j] = population[order[index_best]][j];

    from += tournament_size;
    to   += tournament_size;
    if( to >= population_size )
    {
      for( j = 0; j < population_size; j++ )
      {
        a        = randomInt( population_size );
        b        = randomInt( population_size );
        c        = order[a];
        order[a] = order[b];
        order[b] = c;
      }
      from = 0;
      to   = tournament_size-1;
    }
  }

  free( order );
}

/**
 * Estimates the distribution parameters.
 */
void learnStructure()
{
  char   done;
  int    i, j, k, a, b, c, r0, r1, *indices, *order,
         lt_index, factor_size, **mpm_new, *mpm_new_number_of_indices, mpm_new_length,
        *NN_chain, NN_chain_length;
  double p, *cumulative_probabilities, **S_matrix, mul0, mul1;

  /* Compute joint entropy matrix */
  for( i = 0; i < number_of_parameters; i++ )
  {
    for( j = i+1; j < number_of_parameters; j++ )
    {
      indices                  = (int *) Malloc( 2*sizeof( int ) );
      indices[0]               = i;
      indices[1]               = j;
      cumulative_probabilities = estimateParametersForSingleBinaryMarginal( indices, 2, &factor_size );

      MI_matrix[i][j] = 0.0;
      for( k = 0; k < factor_size; k++ )
      {
        if( k == 0 )
          p = cumulative_probabilities[k];
        else
          p = cumulative_probabilities[k]-cumulative_probabilities[k-1];
        if( p > 0 )
          MI_matrix[i][j] += -p*log2(p);
      }

      MI_matrix[j][i] = MI_matrix[i][j];

      free( indices );
      free( cumulative_probabilities );
    }
    indices                  = (int *) Malloc( 1*sizeof( int ) );
    indices[0]               = i;
    cumulative_probabilities = estimateParametersForSingleBinaryMarginal( indices, 1, &factor_size );

    MI_matrix[i][i] = 0.0;
    for( k = 0; k < factor_size; k++ )
    {
      if( k == 0 )
        p = cumulative_probabilities[k];
      else
        p = cumulative_probabilities[k]-cumulative_probabilities[k-1];
       if( p > 0 )
       MI_matrix[i][i] += -p*log2(p);
    }

    free( indices );
    free( cumulative_probabilities );
  }

  /* Then transform into mutual information matrix MI(X,Y)=H(X)+H(Y)-H(X,Y) */
  for( i = 0; i < number_of_parameters; i++ )
    for( j = i+1; j < number_of_parameters; j++ )
    {
      MI_matrix[i][j] = MI_matrix[i][i] + MI_matrix[j][j] - MI_matrix[i][j]; //This is the actual MI calculation
      MI_matrix[j][i] = MI_matrix[i][j];
    }

  /* Initialize MPM to the univariate factorization */
  order = (int *) Malloc( number_of_parameters*sizeof( int ) );
  for( i = 0; i < number_of_parameters; i++ )
    order[i] = i;
  for( i = 0; i < number_of_parameters; i++ )
  {
    a        = randomInt( number_of_parameters );
    b        = randomInt( number_of_parameters );
    c        = order[a];
    order[a] = order[b];
    order[b] = c;
  }
  mpm                   = (int **) Malloc( number_of_parameters*sizeof( int * ) );
  mpm_number_of_indices = (int *) Malloc( number_of_parameters*sizeof( int ) );
  mpm_length            = number_of_parameters;
  for( i = 0; i < number_of_parameters; i++ )
  {
    indices                  = (int *) Malloc( 1*sizeof( int ) );
    indices[0]               = order[i];
    mpm[i]                   = indices;
    mpm_number_of_indices[i] = 1;
  }
  free( order );

  /* Initialize LT to the initial MPM */
  if( lt != NULL )
  {
    for( i = 0; i < lt_length; i++ )
      free( lt[i] );
    free( lt );
    free( lt_number_of_indices );
  }
  lt                   = (int **) Malloc( (number_of_parameters+number_of_parameters-1)*sizeof( int * ) );
  lt_number_of_indices = (int *) Malloc( (number_of_parameters+number_of_parameters-1)*sizeof( int ) );
  lt_length            = number_of_parameters+number_of_parameters-1;
  lt_index             = 0;
  for( i = 0; i < mpm_length; i++ )
  {
    lt[lt_index]                   = mpm[i];
    lt_number_of_indices[lt_index] = mpm_number_of_indices[i];
    lt_index++;
  }

  /* Initialize similarity matrix */
  S_matrix = (double **) Malloc( number_of_parameters*sizeof( double * ) );
  for( i = 0; i < number_of_parameters; i++ )
    S_matrix[i] = (double *) Malloc( number_of_parameters*sizeof( double ) );
  for( i = 0; i < mpm_length; i++ )
    for( j = 0; j < mpm_length; j++ )
      S_matrix[i][j] = MI_matrix[mpm[i][0]][mpm[j][0]];
  for( i = 0; i < mpm_length; i++ )
    S_matrix[i][i] = 0;

  NN_chain        = (int *) Malloc( (number_of_parameters+2)*sizeof( int ) );
  NN_chain_length = 0;
  done            = FALSE;
  while( done == FALSE )
  {
    if( NN_chain_length == 0 )
    {
      NN_chain[NN_chain_length] = randomInt( mpm_length );
      NN_chain_length++;
    }

    while( NN_chain_length < 3 )
    {
      NN_chain[NN_chain_length] = determineNearestNeighbour( NN_chain[NN_chain_length-1], S_matrix, mpm_length );
      NN_chain_length++;
    }

    while( NN_chain[NN_chain_length-3] != NN_chain[NN_chain_length-1] )
    {
      NN_chain[NN_chain_length] = determineNearestNeighbour( NN_chain[NN_chain_length-1], S_matrix, mpm_length );
      if( ((S_matrix[NN_chain[NN_chain_length-1]][NN_chain[NN_chain_length]] == S_matrix[NN_chain[NN_chain_length-1]][NN_chain[NN_chain_length-2]])) && (NN_chain[NN_chain_length] != NN_chain[NN_chain_length-2]) )
        NN_chain[NN_chain_length] = NN_chain[NN_chain_length-2];
      NN_chain_length++;
    }
    r0 = NN_chain[NN_chain_length-2];
    r1 = NN_chain[NN_chain_length-1];
    if( r0 > r1 )
    {
      a  = r0;
      r0 = r1;
      r1 = a;
    }
    NN_chain_length -= 3;

    if( r1 < mpm_length ) // This test is required for exceptional cases in which the nearest-neighbor ordering has changed within the chain while merging within that chain
    {
      indices = (int *) Malloc( (mpm_number_of_indices[r0]+mpm_number_of_indices[r1])*sizeof( int ) );
  
      i = 0;
      for( j = 0; j < mpm_number_of_indices[r0]; j++ )
      {
        indices[i] = mpm[r0][j];
        i++;
      }
      for( j = 0; j < mpm_number_of_indices[r1]; j++ )
      {
        indices[i] = mpm[r1][j];
        i++;
      }
    
      lt[lt_index]                   = indices;
      lt_number_of_indices[lt_index] = mpm_number_of_indices[r0]+mpm_number_of_indices[r1];
      lt_index++;
  
      mul0 = ((double) mpm_number_of_indices[r0])/((double) mpm_number_of_indices[r0]+mpm_number_of_indices[r1]);
      mul1 = ((double) mpm_number_of_indices[r1])/((double) mpm_number_of_indices[r0]+mpm_number_of_indices[r1]);
      for( i = 0; i < mpm_length; i++ )
      {
        if( (i != r0) && (i != r1) )
        {
          S_matrix[i][r0] = mul0*S_matrix[i][r0] + mul1*S_matrix[i][r1];
          S_matrix[r0][i] = S_matrix[i][r0];
        }
      }
  
      mpm_new                   = (int **) Malloc( (mpm_length-1)*sizeof( int * ) );
      mpm_new_number_of_indices = (int *) Malloc( (mpm_length-1)*sizeof( int ) );
      mpm_new_length            = mpm_length-1;
      for( i = 0; i < mpm_new_length; i++ )
      {
        mpm_new[i]                   = mpm[i];
        mpm_new_number_of_indices[i] = mpm_number_of_indices[i];
      }
  
      mpm_new[r0]                   = indices;
      mpm_new_number_of_indices[r0] = mpm_number_of_indices[r0]+mpm_number_of_indices[r1];
      if( r1 < mpm_length-1 )
      {
        mpm_new[r1]                   = mpm[mpm_length-1];
        mpm_new_number_of_indices[r1] = mpm_number_of_indices[mpm_length-1];
  
        for( i = 0; i < r1; i++ )
        {
          S_matrix[i][r1] = S_matrix[i][mpm_length-1];
          S_matrix[r1][i] = S_matrix[i][r1];
        }
  
        for( j = r1+1; j < mpm_new_length; j++ )
        {
          S_matrix[r1][j] = S_matrix[j][mpm_length-1];
          S_matrix[j][r1] = S_matrix[r1][j];
        }
      }
  
      for( i = 0; i < NN_chain_length; i++ )
      {
        if( NN_chain[i] == mpm_length-1 )
        {
          NN_chain[i] = r1;
          break;
        }
      }
  
      free( mpm );
      free( mpm_number_of_indices );
      mpm                   = mpm_new;
      mpm_number_of_indices = mpm_new_number_of_indices;
      mpm_length            = mpm_new_length;
  
      if( mpm_length == 1 )
        done = TRUE;
    }
  }

  free( NN_chain );

  free( mpm_new );
  free( mpm_number_of_indices );

  for( i = 0; i < number_of_parameters; i++ )
    free( S_matrix[i] );
  free( S_matrix );
}

/**
 * Estimates the cumulative probability distribution of a
 * single binary marginal.
 */
double *estimateParametersForSingleBinaryMarginal( int *indices, int number_of_indices, int *factor_size )
{
  int     i, j, index, power_of_two;
  double *result;

  *factor_size = (int) pow( 2, number_of_indices );
  result       = (double *) Malloc( (*factor_size)*sizeof( double ) );

  for( i = 0; i < (*factor_size); i++ )
    result[i] = 0.0;

  for( i = 0; i < selection_size; i++ )
  {
    index        = 0;
    power_of_two = 1;
    for( j = number_of_indices-1; j >= 0; j-- )
    {
      index += (selection[i][indices[j]] == TRUE) ? power_of_two : 0;
      power_of_two *= 2;
    }

    result[index] += 1.0;
  }

  for( i = 0; i < (*factor_size); i++ )
    result[i] /= (double) selection_size;

  for( i = 1; i < (*factor_size); i++ )
    result[i] += result[i-1];

  result[(*factor_size)-1] = 1.0;

  return( result );
}

/**
 * Determines nearest neighbour according to similarity values.
 */
int determineNearestNeighbour( int index, double **S_matrix, int mpm_length )
{
  int i, result;

  result = 0;
  if( result == index )
    result++;
  for( i = 1; i < mpm_length; i++ )
  {
//    if( (S_matrix[index][i] > S_matrix[index][result]) && (i != index) )
    if( ((S_matrix[index][i] > S_matrix[index][result]) || ((S_matrix[index][i] == S_matrix[index][result]) && (mpm_number_of_indices[i] < mpm_number_of_indices[result]))) && (i != index) )
      result = i;
  }

  return( result );
}

/**
 * Computes the change in metric if two binary marginals
 * were to be merged.
 */
double computeUPGMAMICC( int *factor_indices_x, int factor_indices_x_length, int *factor_indices_y, int factor_indices_y_length )
{
  int    i, j;
  double result, n;

  if( factor_indices_x_length == 1 && factor_indices_y_length == 1 )
    return( MI_matrix[factor_indices_x[0]][factor_indices_y[0]] );

  result = 0.0;
  n      = 0;
  for( i = 0; i < factor_indices_x_length; i++ )
    for( j = 0; j < factor_indices_y_length; j++ )
    {
      result += MI_matrix[factor_indices_x[i]][factor_indices_y[j]];
      n++;
    }
  result /= n;

  return( result );
}

void printLTStructure()
{
  int i, j;

  for( i = 0; i < lt_length; i++ )
  {
    printf("[");
    for( j = 0; j < lt_number_of_indices[i]; j++ )
    {
      printf("%d",lt[i][j]);
      if( j < lt_number_of_indices[i]-1 )
        printf(" ");
    }
    printf("]\n");
  }
  printf("\n");
  fflush( stdout );
}

/**
 * Computes the two-log of x.
 */
/*
 * This was removed because this is a default function of the Math lib.
double math_log_two = log(2.0);
double log2( double x )
{
  return( log(x) / math_log_two );
}
*/

/**
 * Generates new solutions.
 */
void generateAndEvaluateNewSolutionsToFillOffspring()
{
  char  *solution;
  int    i, j;
  double obj, con;

  for( i = 0; i < offspring_size; i++ )
  {
    solution = generateNewSolution( i%population_size, &obj, &con );

    for( j = 0; j < number_of_parameters; j++ )
      offspring[i][j] = solution[j];

    objective_values_offspring[i]  = obj;
    constraint_values_offspring[i] = con;

    free( solution );
  }
}

/**
 * Generates and returns a single new solution by drawing
 * a single sample from the estimated distribution.
 */
char *generateNewSolution( int which, double *obj, double *con )
{
  char   *result, *backup, is_unchanged;
  short   solution_has_changed;
  int     i, j, index;
  double  obj_backup, con_backup;

  solution_has_changed = 0;

  result = (char *) Malloc( number_of_parameters*sizeof( char ) );
  for( i = 0; i < number_of_parameters; i++ )
    result[i] = population[which][i];

  *obj = objective_values[which];
  *con = constraint_values[which];

  backup = (char *) Malloc( number_of_parameters*sizeof( char ) );
  for( i = 0; i < number_of_parameters; i++ )
    backup[i] = result[i];

  obj_backup = *obj;
  con_backup = *con;

  for( i = lt_length-2; i >= 0; i-- )
  {
    index = randomInt( population_size );

    /* Convert index to binary representation and set factor variables. */
    for( j = 0; j < lt_number_of_indices[i]; j++ )
      result[lt[i][j]] = population[index][lt[i][j]];

    /* Test if the change is for the better */
    is_unchanged = TRUE;
    for( j = 0; j < lt_number_of_indices[i]; j++ )
    {
      if( backup[lt[i][j]] != result[lt[i][j]] )
      {
        is_unchanged = FALSE;
        break;
      }
    }
    if( is_unchanged == FALSE )
    {
      installedProblemEvaluation( problem_index, result, obj, con, lt_number_of_indices[i], lt[i], backup, obj_backup, con_backup );

      if( betterFitness( *obj, *con, obj_backup, con_backup ) || equalFitness( *obj, *con, obj_backup, con_backup ) )
      {
        for( j = 0; j < lt_number_of_indices[i]; j++ )
          backup[lt[i][j]] = result[lt[i][j]];

        obj_backup = *obj;
        con_backup = *con;

	solution_has_changed = 1;
      }
      else
      {
        for( j = 0; j < lt_number_of_indices[i]; j++ )
          result[lt[i][j]] = backup[lt[i][j]];

        *obj = obj_backup;
        *con = con_backup;
      }
    }
  }

  if( (solution_has_changed != 1) || (no_improvement_stretch > (1+(log(population_size)/log(10)))) )
  {
    solution_has_changed = 0;
    for( i = lt_length-2; i >= 0; i-- )
    {
      /* Convert elite solution to binary representation and set factor variables. */
      for( j = 0; j < lt_number_of_indices[i]; j++ )
        result[lt[i][j]] = best_so_far_solution[lt[i][j]];

      /* Test if the change is for the better */
      is_unchanged = TRUE;
      for( j = 0; j < lt_number_of_indices[i]; j++ )
      {
        if( backup[lt[i][j]] != result[lt[i][j]] )
        {
          is_unchanged = FALSE;
          break;
        }
      }
      if( is_unchanged == FALSE )
      {
        installedProblemEvaluation( problem_index, result, obj, con, lt_number_of_indices[i], lt[i], backup, obj_backup, con_backup );

        if( betterFitness( *obj, *con, obj_backup, con_backup ) )
        {
          for( j = 0; j < lt_number_of_indices[i]; j++ )
            backup[lt[i][j]] = result[lt[i][j]];

          obj_backup = *obj;
          con_backup = *con;

          solution_has_changed = 1;
        }
        else
        {
          for( j = 0; j < lt_number_of_indices[i]; j++ )
            result[lt[i][j]] = backup[lt[i][j]];

          *obj = obj_backup;
          *con = con_backup;
        }
      }
      if( solution_has_changed == 1 )
        break;
    }

    if( solution_has_changed != 1 )
    {
      if( betterFitness( best_so_far_objective_value, best_so_far_constraint_value, *obj, *con ) )
        solution_has_changed = 1;

      for( i = 0; i < number_of_parameters; i++ )
        result[i] = best_so_far_solution[i];
      *obj = best_so_far_objective_value;
      *con = best_so_far_constraint_value;
    }
  }

  free( backup );

  return( result );
}
/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/





/*-=-=-=-=-=-=-=-=-=-=-=-=- Section Ezilaitini -=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/
/**
 * Undoes initialization procedure by freeing up memory.
 */
void ezilaitiniMemory( void )
{
  int i;

  for( i = 0; i < number_of_parameters; i++ )
    free( MI_matrix[i] );

  for( i = 0; i < offspring_size; i++ )
    free( offspring[i] );

  for( i = 0; i < selection_size; i++ )
    free( selection[i] );

  for( i = 0; i < population_size; i++ )
    free( population[i] );

  free( population );
  free( objective_values );
  free( constraint_values );
  free( selection );
  free( offspring );
  free( objective_values_offspring );
  free( constraint_values_offspring );
  free( best_so_far_solution );
  free( MI_matrix );

  if( lt != NULL )
  {
    for( i = 0; i < lt_length; i++ )
      free( lt[i] );
    free( lt );
    free( lt_number_of_indices );
  }
}
/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/





/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Section Run -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
/**
 * Runs the EDA.
 */
void run()
{
  struct timeval tv;
  struct tm *timep;

  gettimeofday( &tv, NULL );
  timep = localtime (&tv.tv_sec);
  timestamp_start = timep->tm_hour * 3600 * 1000 + timep->tm_min * 60 * 1000 + timep->tm_sec * 1000 + tv.tv_usec / 1000;
  first_hit_has_happened = 0;

  initialize();

  if( print_verbose_overview )
    printVerboseOverview();

  determineBestSoFarSolution();
  while( !checkTerminationCondition() )
  {
	  //printf("Generation %d\n", number_of_generations);
	  
    if( write_generational_statistics )
      writeGenerationalStatistics();

    if( write_generational_solutions )
      writeGenerationalSolutions( FALSE );

    makeOffspring();

    selectFinalSurvivors();

    number_of_generations++;

    determineBestSoFarSolution();
  }
  writeRunningTime( (char *) "total_running_time.dat" );

  writeGenerationalStatistics();

  //    writeGenerationalSolutions( TRUE );
  writeGenerationalSolutionsBest( TRUE ); writeGenerationalSolutionsElitist( TRUE );

  ezilaitiniMemory();
}

long getMilliSecondsRunning()
{
  struct timeval tv;
  struct tm *timep;
  long   timestamp_now, difference;

  gettimeofday( &tv, NULL );
  timep = localtime (&tv.tv_sec);
  timestamp_now = timep->tm_hour * 3600 * 1000 + timep->tm_min * 60 * 1000 + timep->tm_sec * 1000 + tv.tv_usec / 1000;

  difference = timestamp_now-timestamp_start;
  if( difference < 0 )
    difference += 86399999;

  return( difference );
}
/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/





/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=- Section Main -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-*/
/**
 * The main function:
 * - interpret parameters on the command line
 * - run the algorithm with the interpreted parameters
 */
int oldMain( int argc, char **argv )
{

  run();

  return( 0 );
}


/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=- PopSizeDeterminator Functions -=-=-=-==-=-=-*/
int tryPopSizeComplete(){
	int fails = 0;
	int runs = 0;
	while(runs < 100){
		run();
		if(vtr != best_so_far_objective_value){
			fails++;
		}
		runs++;
	}
	return 100 - fails;
}

int doBinSearch(int start, int end){
	int result = -1;
	if(end - start <= 1){
		printf("Threshold population found: %d\n", start);
		result = start;
	} else {
		int middle = floor((start + end) / 2);
		population_size = middle;
		if(tryPopSizeComplete() > 98){
			result = doBinSearch(start, middle);
		} else {
			result = doBinSearch(middle, end);
		}
	}
	return result;
}

int determinePopSizesComplete(){
	population_size = 1;
	int succeeded = tryPopSizeComplete();
	while(succeeded < 98){
		printf("Problem could not be solved for n=%d\n", population_size);
		population_size = population_size * 2;
		succeeded = tryPopSizeComplete();
	}
	printf("Problem could be solved for n=%d\n", population_size);
	return doBinSearch(population_size / 2, population_size);
}

int main(int argc, char **argv){
	interpretCommandLine(argc, argv);
	if(determinePopSize){
		int sizeSum = 0;
		for(int i = 0; i < 10; i++){
			sizeSum += determinePopSizesComplete();
		}
		printf("Average needed population size: %f\n", (sizeSum / 10.0));
	} else {
		oldMain(argc, argv);
	}
	
}
/*-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=*/
