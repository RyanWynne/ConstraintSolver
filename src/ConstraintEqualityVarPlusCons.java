public class ConstraintEqualityVarPlusCons extends Constraint {

    public ConstraintEqualityVarPlusCons(Variable v1, Variable v2, String val) {
        this.v1 = v1;
        this.v2 = v2;
        this.val = val;
    }

    public ConstraintEqualityVarPlusCons(Constraint original) {
    	this.v1 = new Variable(original.v1.name, original.v1.d);
    	this.v2 = new Variable(original.v2.name, original.v2.d);
    	this.val = original.val;
    }
    
    @Override
    public String toString() {
    	String s = v1.name + " = " + v2.name + val;
        return s;
    }

    @Override
    public String constraintClassCheck() {
    	return "ConstraintEqualityVarPlusCons";
    }

    @Override
    protected boolean isSatisfied() {
    	boolean satisfaction = false;
    	val = val.replaceAll("[^0-9]", "");
    	for(int i = 0; i<v1.d.vals.length; i++) {
    		for(int j = 0; j<v2.d.vals.length; j++) {
    			if(v1.d.vals[i] == v2.d.vals[j]+Integer.parseInt(val))
    				satisfaction = true;
    		}
    	}
    	return satisfaction;
    }

    @Override
    protected void reduce() {
    	val = val.replaceAll("[^0-9]", "");
		v1.d.VPCReduceA(v1, v2, Integer.parseInt(val));
		v2.d.VPCReduceB(v1, v2, Integer.parseInt(val));
    }
}