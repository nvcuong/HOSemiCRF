package HOCRF;

/**
 * Index of a feature
 * @author Nguyen Viet Cuong
 */
public class FeatureIndex {

    int obsID; // ID of the observation part
    int patID; // ID of the pattern part

    /**
     * Construct a feature index from observation and pattern IDs.
     * @param obsID Observation ID
     * @param patID Pattern ID
     */
    public FeatureIndex(int obsID, int patID) {
        this.obsID = obsID;
        this.patID = patID;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FeatureIndex that = (FeatureIndex) o;
        if (obsID != that.obsID) return false;
        if (patID != that.patID) return false;

        return true;
    }
	
    @Override
    public int hashCode() {
        int result = 23;
        result = result*31 + obsID;
        result = result*31 + patID;
        return result;
    }
}
