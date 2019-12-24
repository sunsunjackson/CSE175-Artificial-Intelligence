//
// BackwardChain
//
// This class implements a backward chaining inference procedure. The
// implementation is very skeletal, and the resulting reasoning process is
// not particularly efficient. Knowledge is restricted to the form of
// definite clauses, grouped into a list of positive literals (facts) and
// a list of Horn clause implications (rules). The inference procedure
// maintains a list of goals. On each step, a proof is sought for the
// first goal in this list, starting by an attempt to unify the goal with
// any known fact in the knowledge base. If this fails, the rules are
// examined in the order in which they appear in the knowledge base, searching
// for a consequent that unifies with the goal. Upon successful unification,
// a proof is sought for the conjunction of the rule antecedents. If this
// fails, further rules are considered. Note that this is a strictly
// depth-first approach, so it is incomplete. Note, also, that there is
// no backtracking with regard to matches to facts -- the first match is
// always taken and other potential matches are ignored. This can make
// the algorithm incomplete in another way. In short, the order in which
// facts and rules appear in the knowledge base can have a large influence
// on the behavior of this inference procedure.
//
// In order to use this inference engine, the knowledge base must be
// initialized by a call to "initKB". Queries are then submitted using the
// "ask" method. The "ask" function returns a binding list which includes
// bindings for intermediate variables.
//
// David Noelle -- Tue Oct  9 18:48:57 PDT 2018
//
//
import java.util.*;

//sources: textbook page 328 pseudocode, help from TA Tiantian during office hour
//collaborator: Christopher Payumo

public class BackwardChain {

    public KnowledgeBase kb;

	// Default constructor ...
	public BackwardChain() {
		this.kb = new KnowledgeBase();
	}

	// initKB -- Initialize the knowledge base by interactively requesting
	// file names and reading those files. Return false on error.
	public boolean initKB() {
		return (kb.readKB());
	}

	// unify -- Return the most general unifier for the two provided literals,
	// or null if no unification is possible. The returned binding list
	// should be freshly allocated.
	public BindingList unify(Literal lit1, Literal lit2, BindingList bl) {

		// PROVIDE YOUR CODE HERE!
		
		//if literal 1 predicate does not equal to literal 2 predicate
		//return failure
		if(!(lit1.pred.equals(lit2.pred))) {
			
			return null;
		}
		
		return (unify(lit1.args, lit2.args, bl));
		//return null;
	}

	// unify -- Return the most general unifier for the two provided terms,
	// or null if no unification is possible. The returned binding list
	// should be freshly allocated.
	public BindingList unify(Term t1, Term t2, BindingList bl) {

		// PROVIDE YOUR CODE HERE!
		
		//new binding list to store new values
		BindingList newList;
		newList = new BindingList(bl);
		//1.check if term 1 constant exists
		//2.if 1 checks out, then check if term 2 constant exists
		//3.if 2 checks out, then check if term 1 equals to term 2
		//4.if 3 checks out, return failure
		//5.if 3 doesn't check out, return the new list
		//6.check if term 2 variable exists and if term 2 function is null
		//7.if 6 checks out, unify (term 2, term 1, new binding list)
		//8.if 6 fails, return failure
		if(t1.c != null) {
			if(t2.c != null) {
				if(!(t1.equals (t2))) {
					
					return null;
				}
				else {
					return newList;
				}
			}
			if (t2.v != null && t2.f == null) {
				return (unify(t2, t1, newList));
			}
			else {
				
				return null;
			}
			
		}
		//1.check if term 1 variable exists
		//if 1 checks out, creates new term to store term 1 variable in new list
		//2.check if the new term is null or not, if not, unify (term 2, the new term, new list)
		if (t1.v != null) {
			Term varValue = newList.groundValue(t1.v);
			if(varValue != null) {
				return (unify(t2, varValue, newList));
			}
			//check if term 2 constant exists
			//if it checks out, add term 1 variable and term 2 to the new list
			//return the new list
			else if (t2.c != null) {
				newList.addVariableBinding(t1.v, t2);
				return newList;
			}
		//check if term 2 variable exists
			else if (t2.v != null) {
				//if it checks out, check if term 1 variable equals to term 2 variable
				//if that checks out too, return the new list
				if(t1.v.equals(t2.v)) {
					
					return newList;
				}
				//new term t2val
				//equals to the ground value of term 2 variable
				Term t2Val = newList.groundValue(t2.v);
				//check if t2val exists
				//if t2val exist, unify term 1, t2val, and the new list
				//if it is null, add term 1 variable and term 2 to the new list
				//return the new list
				if(t2Val != null) {
					return (unify(t1, t2Val, newList));
				}
				else {
					newList.addVariableBinding(t1.v, t2);
					return newList;
				}
			}
			//check if term 2 function exists
			if (t2.f != null) {
				//check if term 2 function substitution of the new binding list contains variables from term 1 variable
				//if it doesn't contain the variable of term 1
				//add term 1 variable and term 2 to the new list
				//then return the new list
				if(!((t2.f.subst(newList)).allVariables()).contains(t1.v)) {
					
					newList.addVariableBinding(t1.v, t2);
					
					return newList;
				}
				//otherwise
				//return failure
				else {
					return null;
				}
			}
		}
			//check if term 1 function exists
			//if checks out, perform the sub-if statements
			if(t1.f != null) {
				//check if term 2 variable exists and if term 2 constant is null
				//if it does, unify (term 2, term 1, the new binding list)
				if(t2.v != null && t2.c == null) {
					return (unify(t2, t1, newList));
				}
				//check if term 2 function exists
				//if it does, unify (term 1 function, term 2 function, the new binding list)
				else if(t2.f != null) {
					return (unify(t1.f, t2.f, newList));
				}
				//otherwise, return failure
				else {
					return null;
				}
				
			}
			
		

		return null;
	}

	// unify -- Return the most general unifier for the two provided functions,
	// or null if no unification is possible. The returned binding list
	// should be freshly allocated.
	public BindingList unify(Function f1, Function f2, BindingList bl) {

		// PROVIDE YOUR CODE HERE!
		
		//if function 1 function does not equal to function 2 function
		//return failure
		if(!(f1.func.equals(f2.func))) {
			
			return null;
		}
		
		return (unify(f1.args, f2.args, bl));
		
		//return null;
	}

	// unify -- Return the most general unifier for the two provided lists of
	// terms, or null if no unification is possible. The returned binding
	// list should be freshly allocated.
	public BindingList unify(List<Term> ts1, List<Term> ts2, BindingList bl) {
		if (bl == null)
			return (null);
		if ((ts1.size() == 0) && (ts2.size() == 0))
			// Empty lists match other empty lists ...
			return (new BindingList(bl));
		if ((ts1.size() == 0) || (ts2.size() == 0))
			// Ran out of arguments in one list before reaching the
			// end of the other list, which means the two argument lists
			// can't match ...
			return (null);
		List<Term> terms1 = new LinkedList<Term>();
		List<Term> terms2 = new LinkedList<Term>();
		terms1.addAll(ts1);
		terms2.addAll(ts2);
		Term t1 = terms1.get(0);
		Term t2 = terms2.get(0);
		terms1.remove(0);
		terms2.remove(0);
		return (unify(terms1, terms2, unify(t1, t2, bl)));
	}

	// askFacts -- Examine all of the facts in the knowledge base to
	// determine if any of them unify with the given literal, under the
	// given binding list. If a unification is found, return the
	// corresponding most general unifier. If none is found, return null
	// to indicate failure.
	BindingList askFacts(Literal lit, BindingList bl) {
		BindingList mgu = null; // Most General Unifier
		for (Literal fact : kb.facts) {
			mgu = unify(lit, fact, bl);
			if (mgu != null)
				return (mgu);
		}
		return (null);
	}

	// askFacts -- Examine all of the facts in the knowledge base to
	// determine if any of them unify with the given literal. If a
	// unification is found, return the corresponding most general unifier.
	// If none is found, return null to indicate failure.
	BindingList askFacts(Literal lit) {
		return (askFacts(lit, new BindingList()));
	}

	// ask -- Try to prove the given goal literal, under the constraints of
	// the given binding list, using both the list of known facts and the
	// collection of known rules. Terminate as soon as a proof is found,
	// returning the resulting binding list for that proof. Return null if
	// no proof can be found. The returned binding list should be freshly
	// allocated.
	BindingList ask(Literal goal, BindingList bl) {
		BindingList result = askFacts(goal, bl);
		if (result != null) {
			// The literal can be unified with a known fact ...
			return (result);
		}
		// Need to look at rules ...
		for (Rule candidateRule : kb.rules) {
			if (candidateRule.consequent.pred.equals(goal.pred)) {
				// The rule head uses the same predicate as the goal ...
				// Standardize apart ...
				Rule r = candidateRule.standardizeApart();
				// Check to see if the consequent unifies with the goal ...
				result = unify(goal, r.consequent, bl);
				if (result != null) {
					// This rule might be part of a proof, if we can prove
					// the rule's antecedents ...
					result = ask(r.antecedents, result);
					if (result != null) {
						// The antecedents have been proven, so the goal
						// is proven ...
						return (result);
					}
				}
			}
		}
		// No rule that matches has antecedents that can be proven. Thus,
		// the search fails ...
		return (null);
	}

	// ask -- Try to prove the given goal literal using both the list of
	// known facts and the collection of known rules. Terminate as soon as
	// a proof is found, returning the resulting binding list for that proof.
	// Return null if no proof can be found. The returned binding list
	// should be freshly allocated.
	BindingList ask(Literal goal) {
		return (ask(goal, new BindingList()));
	}

	// ask -- Try to prove the given list of goal literals, under the
	// constraints of the given binding list, using both the list of known
	// facts and the collection of known rules. Terminate as soon as a proof
	// is found, returning the resulting binding list for that proof. Return
	// null if no proof can be found. The returned binding list should be
	// freshly allocated.
	BindingList ask(List<Literal> goals, BindingList bl) {
		if (goals.size() == 0) {
			// All goals have been satisfied ...
			return (bl);
		} else {
			List<Literal> newGoals = new LinkedList<Literal>();
			newGoals.addAll(goals);
			Literal goal = newGoals.get(0);
			newGoals.remove(0);
			BindingList firstBL = ask(goal, bl);
			if (firstBL == null) {
				// Failure to prove one of the goals ...
				return (null);
			} else {
				// Try to prove the remaining goals ...
				return (ask(newGoals, firstBL));
			}
		}
	}

}
