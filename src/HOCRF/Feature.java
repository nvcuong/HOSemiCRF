package HOCRF;

/**
 * Feature class
 * @author Nguyen Viet Cuong
 */
public class Feature {

    String obs; // The observation part of the feature
    String pat; // The pattern of the feature
    double value; // Value of the feature

    /**
     * Construct a new feature from observation, pattern, and value.
     * @param obs Observation of the feature
     * @param pat Label pattern of the feature
     * @param value Value of the feature
     */
    public Feature(String obs, String pat, double value) {
        this.obs = obs;
        this.pat = pat;
        this.value = value;
    }
}
