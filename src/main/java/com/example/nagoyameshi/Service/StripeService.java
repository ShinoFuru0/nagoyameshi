package com.example.nagoyameshi.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.Role;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.RoleRepository;
import com.example.nagoyameshi.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.model.Subscription;

@Service
public class StripeService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Value("${stripe.api-key}")
    private String stripeApiKey;

    public StripeService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    private void setApiKey() {
        Stripe.apiKey = stripeApiKey;
    }

    public Customer createCustomer(User user) {
        setApiKey();
        Map<String, Object> customerParams = new HashMap<>();
        customerParams.put("name", user.getName());
        customerParams.put("email", user.getEmail());

        try {
            Customer customer = Customer.create(customerParams);
            user.setCustomerId(customer.getId());
            userRepository.save(user);
            return customer;
        } catch (StripeException e) {
            throw new RuntimeException("Failed to create Stripe customer", e);
        }
    }

    public Subscription createSubscription(String customerId, String priceId) {
        setApiKey();
        Map<String, Object> params = new HashMap<>();
        params.put("customer", customerId);
        List<Map<String, Object>> items = List.of(Map.of("price", priceId));
        params.put("items", items);

        try {
            return Subscription.create(params);
        } catch (StripeException e) {
            throw new RuntimeException("Failed to create subscription", e);
        }
    }

    public PaymentMethod getDefaultPaymentMethod(String customerId) {
        setApiKey();
        try {
            Customer customer = Customer.retrieve(customerId);
            String defaultPaymentMethodId = customer.getInvoiceSettings().getDefaultPaymentMethod();

            if (defaultPaymentMethodId != null) {
                return PaymentMethod.retrieve(defaultPaymentMethodId);
            }

            Map<String, Object> params = new HashMap<>();
            params.put("customer", customerId);
            params.put("type", "card");
            PaymentMethodCollection paymentMethods = PaymentMethod.list(params);
            return paymentMethods.getData().isEmpty() ? null : paymentMethods.getData().get(0);
        } catch (StripeException e) {
            throw new RuntimeException("Failed to get default payment method", e);
        }
    }
    
    
    public void attachPaymentMethodToCustomer(String customerId, String paymentMethodId) throws StripeException {
        setApiKey();
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        paymentMethod.attach(Map.of("customer", customerId));
    }

    public void updateDefaultPaymentMethod(String customerId, String paymentMethodId) throws StripeException {
        setApiKey();
        Customer customer = Customer.retrieve(customerId);
        Map<String, Object> invoiceSettings = new HashMap<>();
        invoiceSettings.put("default_payment_method", paymentMethodId);
        Map<String, Object> updateParams = new HashMap<>();
        updateParams.put("invoice_settings", invoiceSettings);
        customer.update(updateParams);
    }

  //  public void attachPaymentMethodToCustomer(String customerId, String paymentMethodId) {
  //      setApiKey();
  //      try {
  //          PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
  //          paymentMethod.attach(Map.of("customer", customerId));
  //      } catch (StripeException e) {
  //          throw new RuntimeException("Failed to attach payment method to customer", e);
  //      }
  //  }

  //  public void updateDefaultPaymentMethod(String customerId, String paymentMethodId) {
  //      setApiKey();
   //     try {
    //        Customer customer = Customer.retrieve(customerId);
    //       Map<String, Object> invoiceSettings = new HashMap<>();
    //        invoiceSettings.put("default_payment_method", paymentMethodId);
    //        Map<String, Object> updateParams = new HashMap<>();
    //        updateParams.put("invoice_settings", invoiceSettings);
    //        customer.update(updateParams);
    //    } catch (StripeException e) {
    //        throw new RuntimeException("Failed to update default payment method", e);
    //    }
  //  }
    
    
    public void upgradeUserRoleToPaidMember(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Role premiumRole = roleRepository.findByName("ROLE_PREMIUM")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.setRole(premiumRole);
        userRepository.save(user);
    }
   // public void upgradeUserRoleToPaidMember(Integer userId) {
   //     User user = userRepository.findById(userId)
   //             .orElseThrow(() -> new RuntimeException("User not found"));
        
     //   roleRepository.findByName("ROLE_PREMIUM").ifPresentOrElse(
         //       user::setRole,
           //     () -> { throw new RuntimeException("Role not found"); }
       // );
        
      //  userRepository.save(user);
  //  }

    public void handleSuccessfulPayment(Event event) {
        try {
            Invoice invoice = (Invoice) event.getDataObjectDeserializer().getObject().orElse(null);
            if (invoice == null) {
                System.out.println("Invoice object not found in event.");
                return;
            }

            String customerId = invoice.getCustomer();
            System.out.println("Payment succeeded for customer: " + customerId);
        } catch (Exception e) {
            System.out.println("Error handling payment success: " + e.getMessage());
        }
    }

    public void handleSubscriptionCanceled(Event event) {
        try {
            Subscription subscription = (Subscription) event.getDataObjectDeserializer().getObject().orElse(null);
            if (subscription == null) {
                System.out.println("Subscription object not found in event.");
                return;
            }

            String customerId = subscription.getCustomer();
            System.out.println("Subscription canceled for customer: " + customerId);
        } catch (Exception e) {
            System.out.println("Error handling subscription cancellation: " + e.getMessage());
        }
    }
}
