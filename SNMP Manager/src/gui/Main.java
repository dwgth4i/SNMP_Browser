package gui;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public class Main extends Application {
    private Label fpsLabel;
    private Label memoryLabel;
    private Label cpuLabel;

    private long lastUpdateTime = 0;
    private long lastCpuTime = 0;

    @Override
    public void start(Stage stage) throws Exception {
        // Load the FXML
        Parent root = FXMLLoader.load(getClass().getResource("Screen.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("application.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("SNMP Manager");
        stage.setResizable(true);
        stage.setMinHeight(610);
        stage.setMinWidth(620);
        stage.show();

        // Access the labels from the FXML file
        fpsLabel = (Label) scene.lookup("#fpsLabel");
        memoryLabel = (Label) scene.lookup("#memoryLabel");
        cpuLabel = (Label) scene.lookup("#cpuLabel");

        // Create an AnimationTimer to update performance stats every frame
        AnimationTimer performanceTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateStats();
            }
        };
        performanceTimer.start();
    }

    private void updateStats() {
        // Update FPS (Frames per second)
        long currentTime = System.nanoTime();
        long elapsedTime = currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;

        int fps = (int) (1000000000.0 / elapsedTime);
        fpsLabel.setText("FPS: " + fps);

        // Update memory usage
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        memoryLabel.setText("Memory: " + formatMemorySize(usedMemory));

        // Update CPU usage
        double cpuUsage = getCpuUsage();
        cpuLabel.setText("CPU: " + String.format("%.2f", cpuUsage) + "%");
    }

    private double getCpuUsage() {
        // Get the OS MXBean
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        double cpuLoad = -1.0;

        // Check if the system supports load average
        if (osBean.getSystemLoadAverage() >= 0) {
            cpuLoad = osBean.getSystemLoadAverage();
        } else {
            // If not, we calculate it manually using system time (for Windows)
            long currentCpuTime = (long) ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
            long deltaCpuTime = currentCpuTime - lastCpuTime;

            if (deltaCpuTime > 0) {
                cpuLoad = (double) deltaCpuTime / (System.nanoTime() - lastUpdateTime);
            }

            lastCpuTime = currentCpuTime;
        }

        // Return the percentage value
        return (cpuLoad >= 0 ? cpuLoad * 100 : 0);
    }

    private String formatMemorySize(long size) {
        long kb = 1024;
        long mb = kb * kb;
        long gb = mb * kb;
        if (size >= gb) {
            return (size / gb) + " GB";
        } else if (size >= mb) {
            return (size / mb) + " MB";
        } else if (size >= kb) {
            return (size / kb) + " KB";
        } else {
            return size + " Bytes";
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
