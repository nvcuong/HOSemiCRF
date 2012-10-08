/*
Copyright (C) 2012 Nguyen Viet Cuong, Ye Nan, Sumit Bhagwani

This file is part of HOSemiCRF.

HOSemiCRF is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

HOSemiCRF is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with HOSemiCRF. If not, see <http://www.gnu.org/licenses/>.
*/

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
