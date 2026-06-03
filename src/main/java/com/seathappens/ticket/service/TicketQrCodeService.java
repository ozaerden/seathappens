package com.seathappens.ticket.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.seathappens.common.exception.ErrorCode;
import com.seathappens.common.exception.InfrastructureException;
import com.seathappens.ticket.entity.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class TicketQrCodeService {

    private static final int QR_CODE_SIZE = 320;

    public byte[] generate(Ticket ticket) {
        String qrPayload = """
                {
                  "ticketId": "%s",
                  "ticketCode": "%s",
                  "status": "%s"
                }
                """.formatted(
                ticket.getId(),
                ticket.getTicketCode(),
                ticket.getStatus()
        );

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                    qrPayload,
                    BarcodeFormat.QR_CODE,
                    QR_CODE_SIZE,
                    QR_CODE_SIZE
            );

            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            ImageIO.write(image, "png", outputStream);

            return outputStream.toByteArray();
        } catch (WriterException | IOException exception) {
            throw new InfrastructureException(ErrorCode.TICKET_QR_GENERATION_ERROR, exception);
        }
    }
}
