package coursework;
import java.util.ArrayList;

import java.util.ArrayList;

import model.Fitness;
import model.Individual;
import model.LunarParameters.DataSet;
import model.NeuralNetwork;

/**
 * Implements a basic Evolutionary Algorithm to train a Neural Network
 * 
 * You Can Use This Class to implement your EA or implement your own class that extends {@link NeuralNetwork} 
 * 
 */
public class ExampleEvolutionaryAlgorithm extends NeuralNetwork {

	/**
	 * The Main Evolutionary Loop
	 */
	@Override
	public void run() {		
		//Initialise a population of Individuals with random weights
		population = initialise();

		//Record a copy of the best Individual in the population
		best = getBest();
		System.out.println("Best From Initialisation " + best);

		/**
		 * main EA processing loop
		 */		
		
		while (evaluations < Parameters.maxEvaluations) {

			/**
			 * this is a skeleton EA - you need to add the methods.
			 * You can also change the EA if you want 
			 * You must set the best Individual at the end of a run
			 * 
			 */

			// Select 2 Individuals from the current population. Currently returns random Individual
			
			/*Individual parent1 = select(); 
			Individual parent2 = select();*/
						
			Individual parent1 = tournamentSelect(); 
			Individual parent2 = tournamentSelect();

			// Generate a child by crossover. Not Implemented	
			
			
			//ArrayList<Individual> children = reproduce(parent1, parent2);	
			ArrayList<Individual> children = crossover2pt(parent1, parent2);
			//ArrayList<Individual> children = UXcrossover(parent1, parent2);
			
			
			//mutate the offspring
			
			mutate(children);
			//children = inversionMutation(children);	//has error
			//children = SwapMutation(children);
						
			
			// Evaluate the children
			evaluateIndividuals(children);		

			// Replace children in population
			replace(children);

			// check to see if the best has improved
			best = getBest();
			
			// Implemented in NN class. 
			outputStats();
			
			//Increment number of completed generations			
		}

		//save the trained network to disk
		saveNeuralNetwork();
	}

	

	/**
	 * Sets the fitness of the individuals passed as parameters (whole population)
	 * 
	 */
	private void evaluateIndividuals(ArrayList<Individual> individuals) {
		for (Individual individual : individuals) 
		{
			individual.fitness = Fitness.evaluate(individual, this);
		}
	}


	/**
	 * Returns a copy of the best individual in the population
	 * 
	 */
	private Individual getBest() {
		best = null;;
		for (Individual individual : population) 
		{
			if (best == null) 
			{
				best = individual.copy();
			} else if (individual.fitness < best.fitness) {
				best = individual.copy();
			}
		}
		return best;
	}

	/**
	 * Generates a randomly initialised population
	 * 
	 */
	private ArrayList<Individual> initialise() {
		population = new ArrayList<>();
		for (int i = 0; i < Parameters.popSize; ++i) 
		{
			//chromosome weights are initialised randomly in the constructor
			Individual individual = new Individual();
			population.add(individual);
		}
		evaluateIndividuals(population);
		return population;
	}

	/**
	 * Selection --
	 * 
	 * NEEDS REPLACED with proper selection this just returns a copy of a random
	 * member of the population
	 */
	
	private Individual tournamentSelect() {
		Individual winner = population.get(Parameters.random.nextInt(Parameters.popSize));
		for (int i = 1; i < Parameters.tounamentSize; i++) 
		{
			Individual candidate2 = population.get(Parameters.random.nextInt(Parameters.popSize));
			if (candidate2.fitness < winner.fitness) 
			{
				winner = candidate2;
			}
		}
		return winner.copy();
	}
	
	
	private Individual select() {		
		Individual parent = population.get(Parameters.random.nextInt(Parameters.popSize));
		return parent.copy();
	}

	/**
	 * Crossover / Reproduction
	 * 
	 * NEEDS REPLACED with proper method this code just returns exact copies of the
	 * parents. 
	 */
		
	//two point crossover
	private ArrayList<Individual> crossover2pt(Individual parent1, Individual parent2) {
		ArrayList<Individual> children = new ArrayList<>();
		Individual child1 = new Individual();
		Individual child2 = new Individual();

		int Point1 = Parameters.random.nextInt(parent1.chromosome.length);
		int Point2 = Parameters.random.nextInt(parent1.chromosome.length);
		if (Point1 > Point2) 
		{
			int temp = Point1;
			Point1 = Point2;
			Point2 = temp;
		}
		
		for (int i = 0; i < Point1; i++) 
		{
			child1.chromosome[i] = parent1.chromosome[i];	
			child2.chromosome[i] = parent2.chromosome[i];				
		}
		
		for (int i = Point1; i < Point2; i++) 
		{
			child1.chromosome[i] = parent2.chromosome[i];	
			child2.chromosome[i] = parent1.chromosome[i];				
		}
		
		for (int i = Point2; i < parent1.chromosome.length; i++) 
		{
			child1.chromosome[i] = parent1.chromosome[i];	
			child2.chromosome[i] = parent2.chromosome[i];				
		}
		
		children.add(child1.copy());
		children.add(child2.copy());		
		return children;
	} 
	
	
	//uniform crossover
	private ArrayList<Individual> UXcrossover(Individual parent1, Individual parent2) 
	{
		Individual child1 = parent1;
		Individual child2 = parent2;	
		
		for (int i = 1; i < parent1.chromosome.length; i++)
		{
			if (Parameters.random.nextDouble() < 0.5)
			{
				child1.chromosome[i] = parent2.chromosome[i];	
				child2.chromosome[i] = parent1.chromosome[i];
			}
		}
		
		ArrayList<Individual> children = new ArrayList<>();
		children.add(child1.copy());
		children.add(child2.copy());
		return children;
	} 
	
	
	private ArrayList<Individual> reproduce(Individual parent1, Individual parent2) {
		ArrayList<Individual> children = new ArrayList<>();
		children.add(parent1.copy());
		children.add(parent2.copy());		
		return children;
	} 
	
	/**
	 * Mutation
	 * 
	 * 
	 */
	
    private ArrayList<Individual> inversionMutation(ArrayList<Individual> individuals)
    {
    	ArrayList<Individual> children = new ArrayList<>();
    	
        for(Individual individual : individuals)
        {
        	if (Parameters.random.nextDouble() < Parameters.mutateRate) 
        	{
        		Individual temp = individual.copy();
        		
        		int loc2 = Parameters.random.nextInt(individual.chromosome.length);
        		int loc1 = Parameters.random.nextInt(loc2);
        		
        		int invertIndex = loc2;
        		for (int i = loc1; i < loc2 + 1; i++)
        		{
        			individual.chromosome[i] = temp.chromosome[invertIndex];                
        			invertIndex--;
        		}        		
        	}
        	children.add(individual.copy());
        }
        return children;                       
    }
	

	
	private ArrayList<Individual> SwapMutation(ArrayList<Individual> individuals) 
	{
		ArrayList<Individual> children = new ArrayList<>();
		
		for(Individual individual : individuals)
		{
			if (Parameters.random.nextDouble() < Parameters.mutateRate) 
			{
				Individual temp = individual.copy();
				
				int pos1 = Parameters.random.nextInt(individual.chromosome.length);
				int pos2 = Parameters.random.nextInt(individual.chromosome.length);
					
				individual.chromosome[pos1] = temp.chromosome[pos2];
				individual.chromosome[pos2] = temp.chromosome[pos1];									
			}
			children.add(individual.copy());
		}
		return children;
	}
	
	
		
	private void mutate(ArrayList<Individual> individuals) {		
		for(Individual individual : individuals) {
			for (int i = 0; i < individual.chromosome.length; i++) 
			{
				if (Parameters.random.nextDouble() < Parameters.mutateRate) 
				{
					if (Parameters.random.nextBoolean()) 
					{
						individual.chromosome[i] += (Parameters.mutateChange);
					} 
					else 
					{
						individual.chromosome[i] -= (Parameters.mutateChange);
					}
				}
			}
		}		
	}

	/**
	 * 
	 * Replaces the worst member of the population 
	 * (regardless of fitness)
	 * 
	 */
	private void replace(ArrayList<Individual> individuals) {
		for(Individual individual : individuals) {
			int idx = getWorstIndex();		
			population.set(idx, individual);
		}		
	}

	

	/**
	 * Returns the index of the worst member of the population
	 * @return
	 */
	private int getWorstIndex() {
		Individual worst = null;
		int idx = -1;
		for (int i = 0; i < population.size(); i++) {
			Individual individual = population.get(i);
			if (worst == null) {
				worst = individual;
				idx = i;
			} else if (individual.fitness > worst.fitness) {
				worst = individual;
				idx = i; 
			}
		}
		return idx;
	}	

	@Override
	public double activationFunction(double x) {
		if (x < -20.0) {
			return -1.0;
		} else if (x > 20.0) {
			return 1.0;
		}
		return Math.tanh(x);
	}
}
