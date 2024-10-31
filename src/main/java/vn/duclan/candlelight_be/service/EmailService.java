package vn.duclan.candlelight_be.service;

public interface EmailService {
    public void sendEmail(String src, String dst, String subject, String text);
}
