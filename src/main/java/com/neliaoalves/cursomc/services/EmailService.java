package com.neliaoalves.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import com.neliaoalves.cursomc.domain.Pedido;

public interface EmailService {
	void sendOrderConfirmationEmail(Pedido obj);

	void sendEmail(SimpleMailMessage msg);
}
