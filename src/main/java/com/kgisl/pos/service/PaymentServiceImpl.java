package com.kgisl.pos.service;

import com.kgisl.pos.entity.Payment;
import com.kgisl.pos.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    @Override
    public Payment updatePayment(Long id, Payment payment) {
        Payment existing = paymentRepository.findById(id).orElse(null);

        if (existing != null) {
            existing.setPaymentReference(payment.getPaymentReference());
            existing.setPolicy(payment.getPolicy());
            existing.setCustomer(payment.getCustomer());
            existing.setClaim(payment.getClaim());
            existing.setPaymentType(payment.getPaymentType());
            existing.setAmount(payment.getAmount());
            existing.setPaymentMethod(payment.getPaymentMethod());
            existing.setPaymentStatus(payment.getPaymentStatus());
            existing.setPaymentDate(payment.getPaymentDate());
            existing.setTransactionDate(payment.getTransactionDate());

            return paymentRepository.save(existing);
        }

        return null;
    }

    @Override
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
}