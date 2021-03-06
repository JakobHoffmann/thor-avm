package jmetal.problems.FMr;
//  FM56r.java
//
//  Author:
//       Abdel Salam Sayyad
//
//  Copyright (c) Abdel Salam Sayyad - West Virginia University
//
import jmetal.core.*;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.encodings.variable.Binary;
import jmetal.util.JMException;
import jmetal.util.Configuration.*;
import main.ObjectiveParser;
import main.ParsedFM;

public class FM56r extends Problem {   

  private static ParsedFM featureModel;
    
  public FM56r(String solutionType) throws ClassNotFoundException {
    this(solutionType, 56, 5, new ParsedFM("..\\trunk2\\jmetal\\problems\\FMr\\FM-56.xml"));
  }

  public FM56r(String solutionType, 
               Integer numberOfBits, 
  		         Integer numberOfObjectives,
                         	ParsedFM pfm) throws ClassNotFoundException {
    numberOfVariables_  = 1;
    numberOfObjectives_ = numberOfObjectives.intValue();
    numberOfConstraints_= 0;
    problemName_        = "FM5600r";
	
    featureModel=pfm;
    featureModel.setRequiresArray(requires_pairs);
    
    this.pfm = pfm;
        
    lowerLimit_ = new double[numberOfVariables_];
    upperLimit_ = new double[numberOfVariables_];        
    //for (int var = 0; var < numberOfBits.intValue(); var++){
      lowerLimit_[0] = 0.0;
      upperLimit_[0] = 1.0;
   // } //for
        
    if (solutionType.compareTo("Binary") == 0)
    	solutionType_ = new BinarySolutionType(this) ;
    else {
    	System.out.println("Error: solution type " + solutionType + " invalid") ;
    	System.exit(-1) ;
    }
        length_    = new int[numberOfVariables_];
        length_[0] = numberOfBits.intValue();
    REQUIRE_RULES = featureModel.require_rules + requires_pairs.length;
    TOTAL_RULES = featureModel.total_rules;
}            
    //public static final ParsedFM featureModel = new ParsedFM("src\\jmetal\\problems\\FMr\\FM-56.xml");
    public static final int FEATURES = 56;
    public static final int [] [] requires_pairs = {{16,7} , {9,27} , {19,34} , {37,24} , {20,23}};
    
    public static int REQUIRE_RULES;// = featureModel.require_rules + requires_pairs.length;
    public static int TOTAL_RULES;// = featureModel.total_rules;
	
	public static ObjectiveParser op = new ObjectiveParser("..\\trunk2\\jmetal\\problems\\FMr\\qualities56.csv");

    public static final boolean[] USED_BEFORE = op.getBools(0);

    public static final int[] DEFECTS = op.getInts(1);
    
    //public static final int TOTAL_DEFECTS = 101;
    
    public static final double[] COST = op.getDoubles(2);
    
    //public static final double TOTAL_COST = 396.8404924;

  public ParsedFM getParsedFM() {
	  return featureModel;
  }
    
  /** 
  * Evaluates a solution 
  * @param solution The solution to evaluate
   * @throws JMException 
  */    
  public void evaluate(Solution solution) throws JMException {

        Binary variable = ((Binary)solution.getDecisionVariables()[0]) ;
        int requires_viol = featureModel.requiresViolations(variable);
        int group_viol = featureModel.groupViolations(variable);
        int excludes_viol = 0;
        int num_features = 0;
        int num_used_before = 0;
        int num_defects = 0;
        double actual_cost = 0;
        
        // check for "excludes" rule violations
        // only one rule in this FM
        if (variable.bits_.get(24) && variable.bits_.get(40)) excludes_viol++;
        
        // Find the total number of features in this individual
        for(int x=0; x<variable.getNumberOfBits(); x++)
            num_features += (variable.bits_.get(x) ? 1 : 0);
        
        // Find the total number of features that were used before
        for(int x=0; x<variable.getNumberOfBits(); x++)
            num_used_before += ((variable.bits_.get(x) && USED_BEFORE[x]) ? 1 : 0);

        // Find the total number of known defects in the chosen features
        for(int x=0; x<variable.getNumberOfBits(); x++)
            num_defects += (variable.bits_.get(x) ? DEFECTS[x] : 0);
        
        // Find the total cost of the chosen features
        for(int x=0; x<variable.getNumberOfBits(); x++)
            actual_cost += (variable.bits_.get(x) ? COST[x] : 0);
        
        
        // Assign objectives
        if (numberOfObjectives_ == 5){
            // First: The correctness objective, minimize violations to
            // maximize correctness
        	
            solution.setObjective(0, (requires_viol + group_viol + excludes_viol));

            // Second: Maximize the total number of features
            // Here: we minimize the missing features
            solution.setObjective(1, FEATURES - num_features); 
  
            // Third: Maximize the number of features that were used before
            // Here: we minimize the features that WERE'NT used before
            solution.setObjective(2, num_features - num_used_before); 
  
            // Fourth: Minimize the number of known defects in the chosen features
            solution.setObjective(3, num_defects); 
  
            // Fifth: Minimize the total cost
            solution.setObjective(4, actual_cost); 
        }
        else if (numberOfObjectives_ == 4){
            solution.setObjective(0, (requires_viol + group_viol + excludes_viol == 0)
                ? (FEATURES - num_features)
                : FEATURES + requires_viol + group_viol + excludes_viol);

            solution.setObjective(1, num_features - num_used_before); 
  
            solution.setObjective(2, num_defects); 
  
            solution.setObjective(3, actual_cost); 
        }
 
        else if (numberOfObjectives_ == 3){
            solution.setObjective(0, (requires_viol + group_viol + excludes_viol)); 

            // Second: Maximize the total number of features
            // Here: we minimize the missing features
            solution.setObjective(1, FEATURES - num_features); 
  
            solution.setObjective(2, actual_cost); 
        }
        else if (numberOfObjectives_ == 2){
            solution.setObjective(0, (requires_viol + group_viol + excludes_viol)); 

            // Second: Maximize the total number of features
            // Here: we minimize the missing features
            solution.setObjective(1, FEATURES - num_features); 
        }
  
  } // evaluate   
  
}

