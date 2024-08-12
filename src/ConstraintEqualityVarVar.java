public class ConstraintEqualityVarVar extends Constraint {

    public ConstraintEqualityVarVar(Variable v1, Variable v2) {
        this.v1 = v1;
        this.v2 = v2;
    }
    
    public ConstraintEqualityVarVar(Constraint original) {
    	this.v1 = new Variable(original.v1.name, original.v1.d);
    	this.v2 = new Variable(original.v2.name, original.v2.d);
    }

    @Override
    public String toString() {
    	String s = v1.name + " = " + v2.name;
        return s;
    }

    @Override
    public String constraintClassCheck() {
    	return "ConstraintEqualityVarVar";
    }
    
    @Override
    protected boolean isSatisfied() {
    	boolean satisfaction = false;
    	for(int i = 0; i<v1.d.vals.length; i++) {
    		for(int j = 0; j<v2.d.vals.length; j++) {
    			if(v1.d.vals[i] == v2.d.vals[j])
    				satisfaction = true;
    		}
    	}
        return satisfaction;
    }

    @Override
    protected void reduce() {
    	Domain d1 = v1.d;
    	Domain d2 = v2.d;
    	Domain newD1 = d1.getShared(d2);
    	Domain newD2 = d2.getShared(d1);   
    	v1.setDomain(newD1);
    	v2.setDomain(newD2);
    }

}