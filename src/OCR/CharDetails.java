package OCR;

/**
 * Details of a character
 * @author Nguyen Viet Cuong
 */
public class CharDetails {
    public static final int ROWS = 16;
    public static final int COLS = 8;
    
    int[][] pixels = new int[ROWS][COLS];

    public CharDetails(int[][] p) {
        pixels = p;
    }

    public int getPixels(int r, int c) {
        return pixels[r][c];
    }
    
    @Override
    public String toString() {
        return "";
    }
}
