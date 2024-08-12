import java.util.ArrayList;
import java.util.List;

public class Domain {

    int[] vals;


    public Domain(int[] vals) {
        this.vals = vals;
    }


    public Domain(Domain d2) {
        vals = new int[d2.vals.length];
        for(int i = 0; i < vals.length; i++)
            this.vals[i] = d2.vals[i];
    }

    /**
     * @return
     */
    public String toString() {
        String result  = "{";
        for (int i = 0; i < vals.length; i++)
            result += vals[i];
        result += "}";
        return result;
    }

    /**
     * @return
     */
    public Domain[] split() {
    	int half = vals.length/2;
    	Domain[] bothHalves = new Domain[2];
    	int left[] = new int[half];
        int right[] = new int[vals.length - half];
    	if (half > 0 && half < vals.length) {
            for (int i = 0; i < vals.length; i++) {
                if (i < half)
                    left[i] = vals[i];
                else
                    right[i - half] = vals[i];
            }
    	}
    	Domain leftDomain = new Domain(left);
    	Domain rightDomain = new Domain(right);
    	bothHalves[0] = leftDomain;
    	bothHalves[1] = rightDomain;
    	return bothHalves;
    }

    /**
     * @return
     */
    private boolean isEmpty() {
    	if(vals.length == 0)
    		return true;
    	else
    		return false;
    }

    /**
     * @return
     */
    private boolean equals(Domain d2) {
    	if(d2.vals == vals)
    		return true;
    	else
    		return false;
    }

    /**
     * @return
     */
    private boolean  isReducedToOnlyOneValue() {
    	if(vals.length == 1)
    		return true;
    	else
    		return false;
    }

    public void setToOneValue(Variable var, int value) {
    	Domain dom = new Domain(new int[1]);
    	dom.vals[0] = value;
    	var.setDomain(dom);
    }
    
    public Domain getShared(Domain passedDomain) {
    	int valueFoundCounter = 0;
    	boolean valueFound = false;
    	int[] sharedDomainArray = new int[0];
    	for(int i = 0; i<vals.length; i++) {
    		valueFound = false;
    		for(int j = 0; j<passedDomain.vals.length; j++) {
    			if(vals[i] == passedDomain.vals[j]) {
    				valueFoundCounter++;
    				valueFound = true;
    			}
    		}
    		if(valueFound == true) {
    			int[] copyingDomainArray = new int[valueFoundCounter];
    			for(int y = 0; y < sharedDomainArray.length; y++){
    				copyingDomainArray[y] = sharedDomainArray[y];
    			}
    			copyingDomainArray[valueFoundCounter-1] = vals[i];
    			sharedDomainArray = copyingDomainArray;
    		}
    	}
    	Domain sharedDomain = new Domain(sharedDomainArray);
    	return sharedDomain;
    }
    
    public Domain VPCReduceA(Variable A, Variable B, int val) {
    	List<Integer> found = new ArrayList<Integer>();
    	for (int i =0; i<A.d.vals.length; i++) {
    		for (int j = 0; j<B.d.vals.length; j++) {
    			if(A.d.vals[i] == B.d.vals[j]+val) {
    				found.add(A.d.vals[i]);
    			}
    		}
    	}
    	//found contains the correct domain of A
    	int[] foundAsArray = new int[found.size()];
		for (int i = 0; i < foundAsArray.length; i++)
			foundAsArray[i] = found.get(i);
		Domain foundDomain = new Domain(foundAsArray);
		A.setDomain(foundDomain);
    	return A.d;
    }
    
    public Domain VPCReduceB(Variable A, Variable B, int val) {
    	List<Integer> found = new ArrayList<Integer>();
    	for (int i =0; i<B.d.vals.length; i++) {
    		for (int j = 0; j<A.d.vals.length; j++) {
    			if(B.d.vals[i] == A.d.vals[j]-val) {
    				found.add(B.d.vals[i]);
    			}
    		}
    	}
    	//found contains the correct domain of B
    	int[] foundAsArray = new int[found.size()];
		for (int i = 0; i < foundAsArray.length; i++)
			foundAsArray[i] = found.get(i);
		Domain foundDomain = new Domain(foundAsArray);
		B.setDomain(foundDomain);
    	return B.d;
    }
    
    public Domain ABSReduceA(Variable A, Variable B, int val) {
    	List<Integer> found = new ArrayList<Integer>();
    	for (int i =0; i<A.d.vals.length; i++) {
    		for (int j = 0; j<B.d.vals.length; j++) {
    			if((A.d.vals[i] == B.d.vals[j]+val || A.d.vals[i] == B.d.vals[j]-val ) && !found.contains(A.d.vals[i])) {
    				found.add(A.d.vals[i]);
    			}
    		}
    	}
    	//found contains the correct domain of A
		int[] foundAsArray = new int[found.size()];
		for (int i = 0; i < foundAsArray.length; i++)
			foundAsArray[i] = found.get(i);
		Domain foundDomain = new Domain(foundAsArray);
		A.setDomain(foundDomain);
    	return A.d;
    }
    
    public Domain ABSReduceB(Variable A, Variable B, int val) {
    	List<Integer> found = new ArrayList<Integer>();
    	for (int i =0; i<B.d.vals.length; i++) {
    		for (int j = 0; j<A.d.vals.length; j++) {
    			if((B.d.vals[i] == A.d.vals[j]-val || B.d.vals[i] == A.d.vals[j]+val) && !found.contains(B.d.vals[i])) {
    				found.add(B.d.vals[i]);
    			}
    		}
    	}
    	//found contains the correct domain of B
    	int[] foundAsArray = new int[found.size()];
		for (int i = 0; i < foundAsArray.length; i++)
			foundAsArray[i] = found.get(i);
		Domain foundDomain = new Domain(foundAsArray);
		B.setDomain(foundDomain);
    	return B.d;
    }
    
    public Domain xLessy(Variable x, Variable y) {
		if (y.d.vals.length == 1) {
			for (int i = 0; i < x.d.vals.length; i++) {
				if (x.d.vals[i] == y.d.vals[0]) {
					int[] copyArray = new int[x.d.vals.length-1];
					Domain copy = new Domain(copyArray);
					for (int h = 0, z = 0; h < x.d.vals.length; h++) {
						if (h != i) {
							copy.vals[z] = x.d.vals[h];
							z++;
						}
					}
					x.setDomain(copy); //this is where the constraintSet error shows itself
				}
			}
		}
		return x.d;
	}
    
}