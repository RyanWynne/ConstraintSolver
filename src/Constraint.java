public abstract class Constraint {
    //**HAVE CHANGED MINIMALLY - POTENTIAL ISSUE LYING HERE**
        Variable v1, v2;
        String val;
        
        public String toString() {
            return "";
        }
    
        public String constraintClassCheck() {
            return "";
        }
        
        protected boolean isSatisfied() {
            return true;
        }
    
        protected void reduce() {
        }
    
    }