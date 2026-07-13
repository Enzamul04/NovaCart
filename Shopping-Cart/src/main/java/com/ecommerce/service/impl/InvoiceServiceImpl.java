package com.ecommerce.service.impl;

import com.ecommerce.model.ProductOrder;
import com.ecommerce.repository.ProductOrderRepository;
import com.ecommerce.service.InvoiceService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private ProductOrderRepository orderRepository;

    @Override
    public void generateInvoice(Integer orderId, HttpServletResponse response) throws Exception {
        ProductOrder order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new RuntimeException("Order Not Found");
        }

        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=Invoice_" + order.getOrderId() + ".pdf");
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        try {
            ClassPathResource resource = new ClassPathResource("static/img/logo/novacart-logo.png");
            Image logo = Image.getInstance(resource.getFile().getAbsolutePath());
            logo.scaleToFit(80, 80);
            logo.setAlignment(Image.ALIGN_CENTER);
            document.add(logo);
        } catch (Exception e) {
            System.out.println("Logo Not Found");
        }
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22);
        Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
        Font footerFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10);
        // ===========================
        // Company Details
        // ===========================
        document.add(new Paragraph("NovaCart", titleFont));
        document.add(new Paragraph("E-COMMERCE PRIVATE LIMITED", headingFont));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(
                "Address : Barasat, Kolkata, West Bengal - 700124",
                normalFont));
        document.add(new Paragraph(
                "Phone : +91 9832407065",
                normalFont));
        document.add(new Paragraph(
                "Email : support@novacart.com",
                normalFont));
        document.add(new Paragraph(
                "Website : www.novacart.com", normalFont));
        document.add(new Paragraph(
                "------------------------------------------------"));
        // ===========================
        // Invoice Details
        // ===========================
        document.add(new Paragraph(
                "Invoice No : " + order.getOrderId(),
                headingFont));
        document.add(new Paragraph(
                "Order Date : " + order.getOrderDate(),
                normalFont));
        document.add(new Paragraph(
                "------------------------------------------------"));
        // ===========================
        // Customer Details
        // ===========================

        document.add(new Paragraph(" "));
        document.add(new Paragraph(
                "Customer Details",
                headingFont));
        document.add(new Paragraph("Name : " + order.getUser().getName(),
                normalFont));
        document.add(new Paragraph(
                "Email : " + order.getUser().getEmail(),
                normalFont));
        document.add(new Paragraph(
                "Mobile : " + order.getUser().getMobileNumber(),
                normalFont));
        document.add(new Paragraph(
                "Address : " + order.getUser().getAddress(),
                normalFont));
        document.add(new Paragraph(
                "------------------------------------------------"));

        // ===========================
        // Product Details
        // ===========================

        document.add(new Paragraph(" "));
        document.add(new Paragraph(
                "Product Details",
                headingFont));
        document.add(new Paragraph(
                "Product : " + order.getProduct().getTitle(),
                normalFont));
        document.add(new Paragraph(
                "Quantity : " + order.getQuantity(),
                normalFont));
        document.add(new Paragraph(
                "Price : ₹" + order.getPrice(),
                normalFont));
        document.add(new Paragraph(
                "Total Price : ₹" + order.getTotalPrice(),
                normalFont));
        // ===========================
        // Coupon Details
        // ===========================

        if (order.getCouponCode() != null) {
            document.add(new Paragraph(
                    "------------------------------------------------"));
            document.add(new Paragraph(
                    "Coupon : " + order.getCouponCode(),
                    normalFont));
            document.add(new Paragraph(
                    "Discount : ₹" + order.getDiscountAmount(),
                    normalFont));
            document.add(new Paragraph(
                    "Final Amount : ₹" + order.getFinalAmount(),
                    headingFont));
        }

        // ===========================
        // Order Details
        // ===========================

        document.add(new Paragraph(" "));
        document.add(new Paragraph(
                "Order Status",
                headingFont));
        document.add(new Paragraph(
                "Status : " + order.getStatus(),
                normalFont));
        document.add(new Paragraph(
                "Payment Type : " + order.getPaymentType(),
                normalFont));
        document.add(new Paragraph(
                "Payment Status : " + order.getPaymentStatus(),
                normalFont));
        document.add(new Paragraph(
                "================================================"));

        // ===========================
        // Footer
        // ===========================

        document.add(new Paragraph(" "));
        document.add(new Paragraph(
                "Thank You For Shopping With NovaCart ❤️",
                headingFont));
        document.add(new Paragraph(
                "Visit Again!",
                normalFont));
        document.add(new Paragraph(" "));
        document.add(new Paragraph(
                "This is a computer generated invoice.",
                footerFont));
        document.add(new Paragraph(
                "No Signature Required.",
                footerFont));
        document.close();
    }
}