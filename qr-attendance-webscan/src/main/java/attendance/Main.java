package attendance;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Main {

    private static final String WEB_APP_URL =
            "https://script.google.com/macros/s/AKfycbzWLZoe1IA0llaJkFXWyQ_izircZ7klSQMK54EaNtaoywlhqAsC-g2QUA-3VwzSK4u0-g/exec";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::showQrWindow);
    }

    private static void showQrWindow() {

        if (WEB_APP_URL.equals("PASTE_YOUR_WEB_APP_URL_HERE")) {
            JOptionPane.showMessageDialog(
                    null,
                    "Please put your Google Apps Script Web App URL in Main.java.",
                    "Setup Needed",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        JFrame frame = new JFrame("Attendance QR Code");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(480, 600);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JLabel heading = new JLabel(
                "Scan to Record Attendance",
                SwingConstants.CENTER
        );
        heading.setFont(new Font("Arial", Font.BOLD, 22));
        heading.setBorder(
                BorderFactory.createEmptyBorder(24, 10, 16, 10)
        );

        frame.add(heading, BorderLayout.NORTH);

        JLabel qrLabel = new JLabel(
                buildQrIcon(WEB_APP_URL),
                SwingConstants.CENTER
        );

        frame.add(qrLabel, BorderLayout.CENTER);

        JLabel footer = new JLabel(
                "Scan the QR code using your phone camera",
                SwingConstants.CENTER
        );
        footer.setFont(new Font("Arial", Font.PLAIN, 13));
        footer.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 20, 10)
        );

        frame.add(footer, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static ImageIcon buildQrIcon(String url) {
        try {
            QRCodeWriter writer = new QRCodeWriter();

            BitMatrix matrix = writer.encode(
                    url,
                    BarcodeFormat.QR_CODE,
                    400,
                    400
            );

            BufferedImage image =
                    MatrixToImageWriter.toBufferedImage(matrix);

            return new ImageIcon(image);

        } catch (WriterException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Could not generate QR code."
            );

            return new ImageIcon();
        }
    }
}