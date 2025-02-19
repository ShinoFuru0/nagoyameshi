package com.example.nagoyameshi.Service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.User;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Subscription;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.SubscriptionListParams;

@Service
public class StripeService {
    
    @Value("${stripe.api-key}")
    private String stripeApiKey;
    
    private void setApiKey() {
        Stripe.apiKey = stripeApiKey;
    }

    public Customer createCustomer(User user) throws StripeException {
        setApiKey();
        CustomerCreateParams params = CustomerCreateParams.builder()
            .setName(user.getName())
            .setEmail(user.getEmail())
            .build();
        return Customer.create(params);
    }

    public Subscription createSubscription(String customerId, String priceId) {
        setApiKey();
        SubscriptionCreateParams subscriptionCreateParams =
            SubscriptionCreateParams.builder()
                .setCustomer(customerId)
                .addItem(
                    SubscriptionCreateParams.Item.builder()
                        .setPrice(priceId)
                        .build()
                )
                .build();
        try {
            return Subscription.create(subscriptionCreateParams);
        } catch (StripeException e) {
            throw new RuntimeException("Failed to create subscription", e);
        }
    }
    
    public void setDefaultPaymentMethod(String paymentMethodId, String customerId) throws StripeException {
        setApiKey();
        CustomerUpdateParams customerUpdateParams =
            CustomerUpdateParams.builder()
                .setInvoiceSettings(
                    CustomerUpdateParams.InvoiceSettings.builder()
                        .setDefaultPaymentMethod(paymentMethodId)
                        .build()
                )
                .build();
        
        Customer customer = Customer.retrieve(customerId);
        customer.update(customerUpdateParams);
    }
    
    public void updateDefaultPaymentMethod(String customerId, String paymentMethodId) throws StripeException {
        setApiKey();
        Customer customer = Customer.retrieve(customerId);
        CustomerUpdateParams updateParams = CustomerUpdateParams.builder()
            .setInvoiceSettings(
                CustomerUpdateParams.InvoiceSettings.builder()
                    .setDefaultPaymentMethod(paymentMethodId)
                    .build()
            )
            .build();
        customer.update(updateParams);
    }

    public PaymentMethod getDefaultPaymentMethod(String customerId) throws StripeException {
        setApiKey();
        Customer customer = Customer.retrieve(customerId);
        String defaultPaymentMethodId = customer.getInvoiceSettings().getDefaultPaymentMethod();
        return defaultPaymentMethodId != null ? PaymentMethod.retrieve(defaultPaymentMethodId) : null;
    }

    public String getDefaultPaymentMethodId(String customerId) throws StripeException {
        setApiKey();
        Customer customer = Customer.retrieve(customerId);
        return customer.getInvoiceSettings().getDefaultPaymentMethod();
    }

    public void attachPaymentMethodToCustomer(String customerId, String paymentMethodId) throws StripeException {
        setApiKey();
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        paymentMethod.attach(Map.of("customer", customerId));
    }

    public void detachPaymentMethodFromCustomer(String paymentMethodId) throws StripeException {
        setApiKey();
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        paymentMethod.detach();
    }

    public List<Subscription> getSubscriptions(String customerId) throws StripeException {
        setApiKey();
        SubscriptionListParams subscriptionListParams =
            SubscriptionListParams.builder()
                .setCustomer(customerId)
                .build();
        return Subscription.list(subscriptionListParams).getData();
    }

    public void cancelSubscriptions(List<Subscription> subscriptions) throws StripeException {
        setApiKey();
        for (Subscription subscription : subscriptions) {
            subscription.cancel();
        }
    }

    public void upgradeUserRoleToPaidMember(Integer userId) {
        // ロールアップグレードの処理（実装はUserRepositoryとRoleRepositoryに依存）
    }

    public void handleSuccessfulPayment(Event event) {
        try {
            Invoice invoice = (Invoice) event.getDataObjectDeserializer().getObject().orElse(null);
            if (invoice != null) {
                String customerId = invoice.getCustomer();
                System.out.println("Payment succeeded for customer: " + customerId);
            }
        } catch (Exception e) {
            System.out.println("Error handling payment success: " + e.getMessage());
        }
    }

    public void handleSubscriptionCanceled(Event event) {
        try {
            Subscription subscription = (Subscription) event.getDataObjectDeserializer().getObject().orElse(null);
            if (subscription != null) {
                String customerId = subscription.getCustomer();
                System.out.println("Subscription canceled for customer: " + customerId);
            }
        } catch (Exception e) {
            System.out.println("Error handling subscription cancellation: " + e.getMessage());
        }
    }
}
