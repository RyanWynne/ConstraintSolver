import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Arrays;

public class ConstraintSolver {

	private Domain dom;
	private List<Variable> variableSet;
	private List<Constraint> constraintSet;
	public static int treeNumber = 0;
	public boolean answer = false;

	public ConstraintSolver() {
		this.variableSet = new ArrayList<Variable>();
		this.constraintSet = new ArrayList<Constraint>();
	}

	public String toString() {
		// print variable
		for (int i = 0; i < variableSet.size(); i++)
			System.out.println(variableSet.get(i));
		// needs to print constraints as well
		for (int i = 0; i < constraintSet.size(); i++)
			System.out.println(constraintSet.get(i));
		return "";
	}

	private void parse(ConstraintSolver problem, String fileName) {
		try {
			File inputFile = new File(fileName);
			Scanner scanner = new Scanner(inputFile);
			int variableCount = 0;
			int consCount = 0;
			while (scanner.hasNextLine()) {
				String currentLine = scanner.nextLine();
				if (currentLine.startsWith("Var-"))
					variableCount++;
				if (currentLine.startsWith("Cons-"))
					consCount++;

			}
			scanner.close();
			scanner = new Scanner(inputFile);
			while (scanner.hasNextLine()) {
				String currentLine = scanner.nextLine();
				if (currentLine.startsWith("Domain-")) {
					// this is our domain - i.e. a data structure that contains values and can be
					// updated, played with etc.
					String s = currentLine.replace("Domain-", "");
					String[] array = s.split(",");
					int[] vals = new int[array.length];
					for (int i = 0; i < array.length; i++) {
						vals[i] = Integer.parseInt(array[i]);
					}
					problem.dom = new Domain(vals);
				} else if (currentLine.startsWith("Var-") && problem.variableSet.size() < variableCount) {
					// this is the code for every variable (a name and a domain)
					String s = currentLine.replace("Var-", "");
					Variable var = new Variable(s, problem.dom);
					problem.variableSet.add(var);
				} else if (currentLine.startsWith("Cons-") && problem.constraintSet.size() < consCount) {
					// this is the code for the constraints
					if (currentLine.contains("diff")) {
						// code for first variable
						String s = currentLine.replace("Cons-diff(", "");
						List<Variable> utility = new ArrayList<Variable>();
						int index = s.indexOf(",");
						s = (String) s.subSequence(0, index);
						for (int i = 0; i < problem.variableSet.size(); i++) {
							if (problem.variableSet.get(i).name.equalsIgnoreCase(s)) {
								utility.add(problem.variableSet.get(i));
							}

						}
						// code for second variable
						s = currentLine.replace("Cons-diff(" + utility.get(0).name + ",", "");
						s = s.replace(")", "");
						for (int i = 0; i < problem.variableSet.size(); i++) {
							if (problem.variableSet.get(i).name.equalsIgnoreCase(s)) {
								utility.add(problem.variableSet.get(i));
							}
						}
						// final code for constraint type #1
						ConstraintDifferenceVarVar cons = new ConstraintDifferenceVarVar(utility.get(0),
								utility.get(1));
						problem.constraintSet.add(cons);
					} else {
						boolean intPresent = false;
						if (currentLine.contains("0") || currentLine.contains("1") || currentLine.contains("2")
								|| currentLine.contains("3") || currentLine.contains("4") || currentLine.contains("5")
								|| currentLine.contains("6") || currentLine.contains("7") || currentLine.contains("8")
								|| currentLine.contains("9")) {
							intPresent = true;
						}
						if (intPresent == true) {
							boolean signPresent = false;
							if (currentLine.contains("+") || currentLine.contains(" - "))
								signPresent = true;
							if (signPresent == true) {
								// code for first variable
								String s = currentLine.replace("Cons-", "");
								List<Variable> utility = new ArrayList<Variable>();
								int index = s.indexOf("=");
								s = (String) s.subSequence(0, index - 1);
								for (int i = 0; i < problem.variableSet.size(); i++) {
									if (problem.variableSet.get(i).name.equalsIgnoreCase(s)) {
										utility.add(problem.variableSet.get(i));
									}

								}
								// code for determining absolute value present
								if (currentLine.contains("abs")) {
									// code for second variable abs
									s = currentLine.replace("Cons-" + utility.get(0).name + " = abs(", "");
									index = s.indexOf("+");
									s = (String) s.subSequence(0, index - 1);
									for (int i = 0; i < problem.variableSet.size(); i++) {
										if (problem.variableSet.get(i).name.equalsIgnoreCase(s)) {
											utility.add(problem.variableSet.get(i));
										}

									}
									// code for integer abs
									s = currentLine.replace(
											"Cons-" + utility.get(0).name + " = abs(" + utility.get(1).name + " + ",
											"");
									s = s.replaceAll("[^0-9]", "");
									String integer = " +/- " + s;
									// final code for constraint #2 abs
									ConstraintEqualityVarPlusConsABS cons = new ConstraintEqualityVarPlusConsABS(
											utility.get(0), utility.get(1), integer);
									problem.constraintSet.add(cons);
								} else {
									// code for second variable non-abs
									s = currentLine.replace("Cons-" + utility.get(0).name + " = ", "");
									index = s.indexOf("+");
									s = (String) s.subSequence(0, index - 1);
									for (int i = 0; i < problem.variableSet.size(); i++) {
										if (problem.variableSet.get(i).name.equalsIgnoreCase(s)) {
											utility.add(problem.variableSet.get(i));
										}

									}
									// code for integer non-abs
									s = currentLine.replace(
											"Cons-" + utility.get(0).name + " = " + utility.get(1).name + " + ", "");
									s = s.replaceAll("[^0-9]", "");
									String integer = " + " + s;
									// final code for constraint #2 non-abs
									ConstraintEqualityVarPlusCons cons = new ConstraintEqualityVarPlusCons(
											utility.get(0), utility.get(1), integer);
									problem.constraintSet.add(cons);
								}

							} else {
								// code for variable
								List<Variable> utility = new ArrayList<Variable>();
								String s = currentLine.replace("Cons-", "");
								int index = s.indexOf("=");
								s = (String) s.subSequence(0, index - 1);
								for (int i = 0; i < problem.variableSet.size(); i++) {
									if (problem.variableSet.get(i).name.equalsIgnoreCase(s)) {
										utility.add(problem.variableSet.get(i));
									}
								}
								// code for integer
								s = currentLine;
								s = s.replaceAll("[^0-9]", "");
								String integer = s;
								// final code for constraint type #3
								ConstraintEqualityVarCons cons = new ConstraintEqualityVarCons(utility.get(0), integer);
								problem.constraintSet.add(cons);
							}
						} else {
							// code for first variable
							List<Variable> utility = new ArrayList<Variable>();
							String s = currentLine.replace("Cons-", "");
							int index = s.indexOf("=");
							s = (String) s.subSequence(0, index - 1);
							for (int i = 0; i < problem.variableSet.size(); i++) {
								if (problem.variableSet.get(i).name.equalsIgnoreCase(s)) {
									utility.add(problem.variableSet.get(i));
								}
							}
							// code for second variable
							s = currentLine.replace("Cons-" + utility.get(0).name + " = ", "");
							for (int i = 0; i < problem.variableSet.size(); i++) {
								if (problem.variableSet.get(i).name.equalsIgnoreCase(s)) {
									utility.add(problem.variableSet.get(i));
								}
							}
							// final code for constraint type #4
							ConstraintEqualityVarVar cons = new ConstraintEqualityVarVar(utility.get(0),
									utility.get(1));
							problem.constraintSet.add(cons);
						}
					}
				}

			}

			scanner.close();

		} catch (FileNotFoundException e) {
			System.out.println("Error.");
			e.printStackTrace();
		}
	}

	private ConstraintSolver execute(ConstraintSolver problem) {
		// create new subproblem
		ConstraintSolver subproblem = new ConstraintSolver();
		// copy the domain size across
		subproblem.dom = problem.dom;
		// copy the variables across - no pointers, new variables
		for(int i = 0; i<problem.variableSet.size(); i++) {
			String s;
			Domain d;
			s = problem.variableSet.get(i).name;
			d = problem.variableSet.get(i).d;
			Variable vToAdd = new Variable(s, d);
			subproblem.variableSet.add(vToAdd);
		}
		// copy the constraints across - no pointers, new constraints
		for(int i = 0; i<problem.constraintSet.size(); i++) {
			Constraint cons;
			if(problem.constraintSet.get(i).constraintClassCheck().equals("ConstraintDifferenceVarVar")) {
				cons = new ConstraintDifferenceVarVar(problem.constraintSet.get(i));
				subproblem.constraintSet.add(cons);
			}
			else if(problem.constraintSet.get(i).constraintClassCheck().equals("ConstraintEqualityVarPlusCons")) {
				cons = new ConstraintEqualityVarPlusCons(problem.constraintSet.get(i));
				subproblem.constraintSet.add(cons);
			}
			else if(problem.constraintSet.get(i).constraintClassCheck().equals("ConstraintEqualityVarPlusConsABS")) {
				cons = new ConstraintEqualityVarPlusConsABS(problem.constraintSet.get(i));
				subproblem.constraintSet.add(cons);
			}
			else if(problem.constraintSet.get(i).constraintClassCheck().equals("ConstraintEqualityVarCons")) {
				cons = new ConstraintEqualityVarCons(problem.constraintSet.get(i));
				subproblem.constraintSet.add(cons);
			}
			else {
				cons = new ConstraintEqualityVarVar(problem.constraintSet.get(i));
				subproblem.constraintSet.add(cons);
			}
		}
		// initialize a counter for checking whether any variables get updated
		int counter = 0;
		// so long as there are changes being made
		do {
			counter = 0;
			// take each constraint
			for(int i = 0; i < subproblem.constraintSet.size(); i++) {
				boolean changed = false;
				Constraint cons;
				if(subproblem.constraintSet.get(i).constraintClassCheck().equals("ConstraintDifferenceVarVar"))
					cons = new ConstraintDifferenceVarVar(subproblem.constraintSet.get(i));
				else if(subproblem.constraintSet.get(i).constraintClassCheck().equals("ConstraintEqualityVarPlusCons"))
					cons = new ConstraintEqualityVarPlusCons(subproblem.constraintSet.get(i));
				else if(subproblem.constraintSet.get(i).constraintClassCheck().equals("ConstraintEqualityVarPlusConsABS")) 
					cons = new ConstraintEqualityVarPlusConsABS(subproblem.constraintSet.get(i));
				else if(subproblem.constraintSet.get(i).constraintClassCheck().equals("ConstraintEqualityVarCons")) 
					cons = new ConstraintEqualityVarCons(subproblem.constraintSet.get(i));
				else 
					cons = new ConstraintEqualityVarVar(subproblem.constraintSet.get(i));
				// make copies to compare later
				Domain copy1 = cons.v1.d;
				Domain copy2 = null;
				if (cons.v2 != null) {
					copy2 = cons.v2.d;
				}
				// reduce
				cons.reduce();
				// if there has been a change, up the counter and flag changed to be true
				if(!copy1.toString().equals(cons.v1.d.toString()) || 
						(copy2 != null && !copy2.toString().equals(cons.v2.d.toString()))) {
					changed = true;
					counter++;
				}
				// if there has been a change, update the change in the variableSet/constraintSet
				if (changed == true) {
					for (int j = 0; j < subproblem.variableSet.size(); j++) {
						if (subproblem.variableSet.get(j).getName().equals(cons.v1.name))
							subproblem.variableSet.get(j).setDomain(cons.v1.d);
						if (cons.v2 != null && subproblem.variableSet.get(j).getName().equals(cons.v2.name))
							subproblem.variableSet.get(j).setDomain(cons.v2.d);
					}
					for (int j = 0; j < subproblem.constraintSet.size(); j++) {
						if (subproblem.constraintSet.get(j).v1.getName().equals(cons.v1.name))
							subproblem.constraintSet.get(j).v1.setDomain(cons.v1.d);
						if (subproblem.constraintSet.get(j).v2 != null 
								&& subproblem.constraintSet.get(j).v2.getName().equals(cons.v1.name))
							subproblem.constraintSet.get(j).v2.setDomain(cons.v1.d);
						if (cons.v2 != null
								&& subproblem.constraintSet.get(j).v1.getName().equals(cons.v2.name))
							subproblem.constraintSet.get(j).v1.setDomain(cons.v2.d);
						if (cons.v2 != null && subproblem.constraintSet.get(j).v2 != null 
								&& subproblem.constraintSet.get(j).v2.getName().equals(cons.v2.name))
							subproblem.constraintSet.get(j).v2.setDomain(cons.v2.d);
					}
				}
			}
		} while(counter != 0);
		return subproblem;
	}

	public ConstraintSolver findSubproblem(TreeMap<Integer, ConstraintSolver> map) {
		Object key = map.keySet().toArray(new Object[map.size()])[treeNumber - 1];
		ConstraintSolver subproblem = new ConstraintSolver();
		subproblem.dom = map.get(0).dom;
		for (int i = 0; i < map.get(key).variableSet.size(); i++) {
			Variable var = new Variable(map.get(key).variableSet.get(i).name, map.get(key).variableSet.get(i).d);
			subproblem.variableSet.add(var);
		}
		//gives constraintSets different variables across nodes
		for (int i = 0; i < map.get(key).constraintSet.size(); i++) {
			if (constraintSet.get(i).constraintClassCheck().equals("ConstraintDifferenceVarVar")) {
				ConstraintDifferenceVarVar cons = new ConstraintDifferenceVarVar(map.get(key).constraintSet.get(i).v1,
						map.get(key).constraintSet.get(i).v2);
				subproblem.constraintSet.add(cons);
			}
			else if (constraintSet.get(i).constraintClassCheck().equals("ConstraintEqualityVarPlusCons")) {
				ConstraintEqualityVarPlusCons cons = new ConstraintEqualityVarPlusCons(map.get(key).constraintSet.get(i).v1,
						map.get(key).constraintSet.get(i).v2, map.get(key).constraintSet.get(i).val);
				subproblem.constraintSet.add(cons);
			}
			else if (constraintSet.get(i).constraintClassCheck().equals("ConstraintEqualityVarPlusConsABS")) {
				ConstraintEqualityVarPlusConsABS cons = new ConstraintEqualityVarPlusConsABS(map.get(key).constraintSet.get(i).v1,
						map.get(key).constraintSet.get(i).v2, map.get(key).constraintSet.get(i).val);
				subproblem.constraintSet.add(cons);
			}
			else if (constraintSet.get(i).constraintClassCheck().equals("ConstraintEqualityVarCons")) {
				ConstraintEqualityVarCons cons = new ConstraintEqualityVarCons(map.get(key).constraintSet.get(i).v1,
						map.get(key).constraintSet.get(i).val);
				subproblem.constraintSet.add(cons);
			}
			else {
				ConstraintEqualityVarVar cons = new ConstraintEqualityVarVar(map.get(key).constraintSet.get(i).v1,
						map.get(key).constraintSet.get(i).v2);
				subproblem.constraintSet.add(cons);
			}
		}
		return subproblem;
	}

	public Variable findHighestDomain(ConstraintSolver subproblem) {
		int[] ia = new int[0];
		Domain d = new Domain(ia);
		String s = "";
		Variable highestDomain = new Variable(s, d);
		for (int i = 0; i < subproblem.variableSet.size(); i++) {
			if (subproblem.variableSet.get(i).d.vals.length > highestDomain.d.vals.length)
				highestDomain = subproblem.variableSet.get(i);
		}
		return highestDomain;
	}
	
	public boolean isSplitable(Variable highestDomain, ConstraintSolver subproblem) {
		boolean splitable = true;
		for (int i = 0; i < subproblem.variableSet.size(); i++) {
			if (subproblem.variableSet.get(i).name.equalsIgnoreCase(highestDomain.name)
					&& subproblem.variableSet.get(i).d.vals.length < 2) {
				splitable = false;
				break;
			}
		}
		return splitable;
	}
	
	// creates a tree and explores it in DFS
	private ReturnSet split(TreeMap<Integer, ConstraintSolver> map, ArrayList<String> answerList) {
		ConstraintSolver subproblem = findSubproblem(map);
		// execute
		subproblem = execute(subproblem);
		// check if a base case has been met
		boolean tooSmall = false;
		boolean finished = true;
		for (int b = 0; b < subproblem.variableSet.size(); b++) {
			if (subproblem.variableSet.get(b).d.vals.length == 0)
				tooSmall = true;
			else if (subproblem.variableSet.get(b).d.vals.length != 1)
				finished = false;
		}
		if (tooSmall) {
			ReturnSet returning = new ReturnSet(answer, answerList);
			return returning;
		} 
		else if (finished) {
			for (int i = 0; i < subproblem.variableSet.size(); i++) {
				String item = "Sol-" + subproblem.variableSet.get(i).name + "-" + subproblem.variableSet.get(i).d.vals[0];
				answerList.add(item);
			}
			answer = true;
			ReturnSet returning = new ReturnSet(answer, answerList);
			return returning;
		}
		if (answer == false) {
			// find the new highest domain
			Variable highestDomain = findHighestDomain(subproblem);
			// check if that domain is splitable
			boolean splitable = isSplitable(highestDomain, subproblem);
			// if it is, then proceed
			if (splitable == true) {
				// split highestDomain
				Domain[] bothHalves = highestDomain.d.split();				
				// search through subproblem.variableSet to find highestDomain.name
				for (int i = 0; i < subproblem.variableSet.size(); i++) {
					if (subproblem.variableSet.get(i).name.equalsIgnoreCase(highestDomain.name)) {
						// update that variable to have the leftHalf domain
						subproblem.variableSet.get(i).setDomain(bothHalves[0]);
						break;
					}
				}
				//update the same variable of appropriate constraints 
				for (int i = 0; i < subproblem.constraintSet.size(); i++) {
					if (subproblem.constraintSet.get(i).v1.name.equalsIgnoreCase(highestDomain.name)) {
						subproblem.constraintSet.get(i).v1.setDomain(bothHalves[0]);
					}
					if(subproblem.constraintSet.get(i).v2 != null && 
							subproblem.constraintSet.get(i).v2.name.equalsIgnoreCase(highestDomain.name)) {
						subproblem.constraintSet.get(i).v2.setDomain(bothHalves[0]);
					}
				}
				
				// place this new subproblem into the tree as left of current node
				treeNumber++;
				map.put(treeNumber-1, subproblem);
				// pursue the left node of current node
				answer = subproblem.split(map, answerList).answer;	
				if(answer == false) {
					// search through subproblem.variableSet to find highestDomain.name
					for (int i = 0; i < subproblem.variableSet.size(); i++) {
						if (subproblem.variableSet.get(i).name.equalsIgnoreCase(highestDomain.name)) {
							// update that variable to have the rightHalf domain
							subproblem.variableSet.get(i).setDomain(bothHalves[1]);
							break;
						}
					}
					//update the same variable of appropriate constraints 
					for (int i = 0; i < subproblem.constraintSet.size(); i++) {
						if (subproblem.constraintSet.get(i).v1.name.equalsIgnoreCase(highestDomain.name)) {
							subproblem.constraintSet.get(i).v1.setDomain(bothHalves[1]);
						}
						if(subproblem.constraintSet.get(i).v2 != null && subproblem.constraintSet.get(i).v2.name.equalsIgnoreCase(highestDomain.name)) {
							subproblem.constraintSet.get(i).v2.setDomain(bothHalves[1]);
						}
					}
					// place this new subproblem into the tree as right of current node
					treeNumber++;
					map.put(treeNumber-1, subproblem);
					// pursue the right node of current node
					answer = subproblem.split(map, answerList).answer;
				}
				ReturnSet returning = new ReturnSet(answer, answerList);
				return returning;
			}
		}
		ReturnSet returning = new ReturnSet(answer, answerList);
		return returning;
	}

	public ArrayList<String> printAnswer(String filename){
		ArrayList<String> answerList = new ArrayList<String>();
		ConstraintSolver problem = new ConstraintSolver();
		TreeMap<Integer, ConstraintSolver> map = new TreeMap<>();
		problem.parse(problem, filename);
		map.put(treeNumber, problem);
		treeNumber++;
		problem.split(map, answerList);
		if(answerList.size() != 0) { 
			for(int i = 0; i<answerList.size(); i++) {
				System.out.println(answerList.get(i));
			}
		}
		else
			System.out.println("No solution found.");
		return answerList;
	}
	
	public static void main(String[] args) {
		ConstraintSolver problem = new ConstraintSolver();
		problem.printAnswer("C:\\Users\\Ryan Wynne\\OneDrive\\Documents\\MyVSCode\\Constraint_Solver\\src\\data.txt\\");
	}

}