public class Variable {

    String name;
    Domain d;

    public Variable(String name, Domain d) {
        this.name = name;
        this.d = new Domain(d);
    }

    public String toString() {
        return this.name + " = " + d;
    }
    
    public void setDomain(Domain dom) {
    	d = dom;
    }
    
    public Domain getDomain() {
    	return d;
    }
    
    public String getName() {
    	return name;
    }

}