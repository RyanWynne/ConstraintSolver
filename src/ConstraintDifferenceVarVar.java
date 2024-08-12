public class ConstraintDifferenceVarVar extends Constraint {

    public ConstraintDifferenceVarVar(Variable v1, Variable v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public ConstraintDifferenceVarVar(Constraint original) {
    	this.v1 = new Variable(original.v1.name, original.v1.d);
    	this.v2 = new Variable(original.v2.name, original.v2.d);
    }
    
    @Override
    public String toString() {
    	String s = v1.name + " does not equal " + v2.name;
        return s;
    }    
    
    @Override
    public String constraintClassCheck() {
    	return "ConstraintDifferenceVarVar";
    }
    
    @Override
    protected boolean isSatisfied() {
    	if(v1.d.vals.length==1 && v2.d.vals.length==1 && v1.d.vals==v2.d.vals)
    		return false;
    	else
    		return true;
    }

    @Override
    protected void reduce() {
    	v1.d.xLessy(v1, v2);
    	v2.d.xLessy(v2, v1);
    }

}