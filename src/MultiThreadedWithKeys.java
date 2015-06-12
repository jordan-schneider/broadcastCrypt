import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultiThreadedWithKeys {
	private static final int	THREADS	= 4;

	public static void main(String[] args) {
		ExecutorService ex;
		BufferedWriter summaryOut;

		try {
			summaryOut = new BufferedWriter(new FileWriter("data" + File.separator + "summary.txt", true));
			ex = Executors.newFixedThreadPool(THREADS);

			for (int n = 3; n < 8; n++) {
				for (int s = n - 1; s <= Math.round(Math.pow(2, n - 1)); s++) {
					for (int w = choose(n,2) + (s-(n-1)); w <= Math.round(Math.pow(2, n)) - n - 1; w++) {
						File f = new File("data" + File.separator + "data[" + n + "," + s + "," + w + "].txt");
						if(!f.exists()){
							ex.execute(new FindBandwidthThread(n, s, w, summaryOut, f));
						}
					}
				}
			}

			try {
				ex.awaitTermination(10, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			summaryOut.close();
		} catch (IOException e) {

		}
	}

	private static int choose(int n, int i) {
		return factorial(n)/factorial(i)/factorial(n-i);
	}

	private static int factorial(int n) {
		if(n<=1){
			return 1;
		}
		return n * factorial(n-1);
	}
}
