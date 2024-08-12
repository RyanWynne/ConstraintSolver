public class ConstraintEqualityVarCons extends Constraint {

    public ConstraintEqualityVarCons(Variable v1, String val) {
        this.v1 = v1;
        this.val = val;
    }
    
    public ConstraintEqualityVarCons(Constraint original) {
    	this.v1 = new Variable(original.v1.name, original.v1.d);
    	this.val = original.val;
    }

    @Override
    public String toString() {
    	String s = v1.name + " = " + val;
        return s;
    }

    @Override
    public String constraintClassCheck() {
    	return "ConstraintEqualityVarCons";
    }
    
    @Override
    protected boolean isSatisfied() {
    	boolean satisfaction = false;
    	val = val.replaceAll("[^0-9]", "");
    	for(int i = 0; i < v1.d.vals.length; i++) {
    		if(v1.d.vals[i] == Integer.parseInt(val));
    			satisfaction = true;
    	}
        return satisfaction;
    }

    @Override
    protected void reduce() {
    	val = val.replaceAll("[^0-9]", "");
    	v1.d.setToOneValue(v1, Integer.parseInt(val));
    }

}