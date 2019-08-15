import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Stack;
import java.awt.Color;
import java.util.Arrays;

public class SeamCarver {
    private Picture pic;
    private double[][] energy;
    private int width;
    private int height;
    private boolean transposed;

    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException();
        this.pic = new Picture(picture);
        this.width = this.pic.width();
        this.height = this.pic.height();
        this.energy = new double[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                energy[row][col] = energy(col, row);
            }
        }
    }

    private void transpose() {
        int temp = height;
        this.height = this.width;
        this.width = temp;
        double[][] engergyT = new double[height][width];
        this.transposed = !transposed;
        Picture transPic = new Picture(width, height);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                engergyT[row][col] = this.energy[col][row];
                transPic.setRGB(col, row, pic.getRGB(row, col));
            }
        }
        this.pic = transPic;
        this.energy = engergyT;
    }

    // current picture
    public Picture picture() {
        Picture ans = new Picture(width, height);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                ans.setRGB(col, row, pic.getRGB(col, row));
            }
        }
        return ans;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int col, int row) {
        if (col < 0 || col > width - 1 || row < 0 || row > height - 1)
            throw new IllegalArgumentException();
        if (col == 0 || col == width - 1 || row == 0 || row == height - 1)
            return 1000.0;
        return Math.sqrt(getX(col, row) + getY(col, row));
    }

    private double getX(int col, int row) {
        Color left = pic.get(col + 1, row);
        Color right = pic.get(col - 1, row);
        double red = Math.pow(left.getRed() - right.getRed(), 2);
        double blue = Math.pow(left.getBlue() - right.getBlue(), 2);
        double green = Math.pow(left.getGreen() - right.getGreen(), 2);
        return red + blue + green;
    }

    private double getY(int col, int row) {
        Color left = pic.get(col, row + 1);
        Color right = pic.get(col, row - 1);
        double red = Math.pow(left.getRed() - right.getRed(), 2);
        double blue = Math.pow(left.getBlue() - right.getBlue(), 2);
        double green = Math.pow(left.getGreen() - right.getGreen(), 2);
        return red + blue + green;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        transpose();
        int[] ans = findVerticalSeam();
        transpose();
        return ans;
    }

    private void relax(int col, int row, double[][] dist, int[][] prev) {
        if (col > 0 && dist[row][col] + energy[row + 1][col - 1] < dist[row + 1][col - 1]) {
            dist[row + 1][col - 1] = dist[row][col] + energy[row + 1][col - 1];
            prev[row + 1][col - 1] = col;
        }
        if  (dist[row][col] + energy[row + 1][col] < dist[row + 1][col]) {
            dist[row + 1][col] = dist[row][col] + energy[row + 1][col];
            prev[row + 1][col] = col;
        }
        if (col < width -1 && dist[row][col] + energy[row + 1][col + 1] < dist[row + 1][col + 1]) {
            dist[row + 1][col + 1] = dist[row][col] + energy[row + 1][col + 1];
            prev[row + 1][col + 1] = col;
        }
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        Stack<Integer> ans = new Stack<>();
        double[][] distTo = new double[height][width];
        int[][] colTo = new int[height][width];
        Arrays.fill(distTo[0], 0);
        double min = Double.POSITIVE_INFINITY;
        int minI = 0;
        for (int i = 1; i < height; i++) {
            Arrays.fill(distTo[i], Double.POSITIVE_INFINITY);
        }
        for (int row = 0; row < height - 1; row++) {
            // not relaxing the last row where there are no more edge
            for (int col = 0; col < width; col++) { // topological ordered relaxation
                relax(col, row, distTo, colTo);
            }
        }

        for (int i = 0; i < width; i++) {
            if (distTo[height - 1][i] < min) {
                min = distTo[height - 1][i];
                minI = i;
            }
        }
        // we take the 2 bottom seam pixels at the same column
        // so the column of bottom seam pixel is pushed twice
        ans.push(minI);
        for (int row = height - 1; row > 0; row--) {
            minI = colTo[row][minI];
            ans.push(minI);
        }
        int[] path = new int[height];
        int i = 0;
        while (!ans.isEmpty()) {
            path[i] = ans.pop();
            i++;
        }
        return path;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        transpose();
        removeVerticalSeam(seam);
        transpose();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null || seam.length != height)
            throw new IllegalArgumentException();
        if (width <= 1)
            throw new IllegalArgumentException();
        Picture removePic = new Picture(width - 1, height);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int cut = seam[row];
                if (cut < 0 || cut >= width)
                    throw new IllegalArgumentException();
                if (row > 0 && Math.abs(seam[row] - seam[row - 1]) > 1)
                    throw new IllegalArgumentException();
                if (col < cut)
                    removePic.setRGB(col, row, pic.get(col, row).getRGB());
                else if (col > cut)
                    removePic.setRGB(col - 1, row, pic.get(col, row).getRGB());
            }
        }
        pic = removePic;
        width--;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                energy[row][col] = energy(col, row);
            }
        }
    }

    // unit testing (optional)
    // public static void main(String[] args) {
    // }

}
