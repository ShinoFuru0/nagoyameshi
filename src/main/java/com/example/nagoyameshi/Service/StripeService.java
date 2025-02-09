package com.example.nagoyameshi.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
import com.stripe.model.Invoice;
import com.stripe.model.InvoiceCollection;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentMethodCollection;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.SubscriptionListParams;
@Service
public class StripeService {
	 private final UserRepository userRepository;
	 private final RoleRepository roleRepository;
	 @Value("${stripe.api-key}")
	    private String stripeApiKey;

	 
	 public StripeService(UserRepository userRepository,RoleRepository roleRepository) {
		 this.userRepository = userRepository;
		 this.roleRepository = roleRepository;
	 }

	    
	    public Customer createCustomer(User user) {
	        Stripe.apiKey = stripeApiKey;
	        Customer customer = null;

	        Map<String, Object> customerParams = new HashMap<>();


	        customerParams.put("name", user.getName());
	        customerParams.put("email", user.getEmail());        

	        try {
	            customer = Customer.create(customerParams);
	            user.setCustomerId(customer.getId());
	            
	            Map<String, Object> params = new HashMap<>();
	            params.put("customer", customer.getId());
	            userRepository.save(user);

	            
	            Map<String, Object> updateParams = new HashMap<>();
	            customer.update(updateParams);
	        } catch (StripeException e) {
	            e.printStackTrace();
	            throw new RuntimeException("Stripeに顧客を作成した際にエラーが発生しました。", e);
	        }
	        
	        return customer;
	    }
	    
	    public Subscription createSubscription(String customerId, String planId) {
	        Stripe.apiKey = stripeApiKey;

	        Map<String, Object> item = new HashMap<>();
	        item.put("plan", planId);

	        Map<String, Object> items = new HashMap<>();
	        items.put("0", item);

	        Map<String, Object> params = new HashMap<>();        
	        
	        params.put("customer", customerId);
	        params.put("items", items);

	        try {
	            return Subscription.create(params);
	        } catch (StripeException e) {
	            throw new RuntimeException(e);
	        }
	    }
	    
	    public PaymentMethod getDefaultPaymentMethod(String customerId) {
	    	Stripe.apiKey = stripeApiKey;
	        try {
	            Map<String, Object> params = new HashMap<>();
	            params.put("customer", customerId);
	            params.put("type", "card");
	            PaymentMethodCollection paymentMethods = PaymentMethod.list(params);
	            
	            // 顧客が支払い方法を追加しているか確認する
	            if (!paymentMethods.getData().isEmpty()) {
	                return paymentMethods.getData().get(0);
	            }
	        } catch (StripeException e) {
	            e.printStackTrace();
	        }        
	        
	        // 顧客が支払い方法を追加していない場合、nullを返す
	        return null;       
	    }
	    
	    public void attachPaymentMethodToCustomer(String customerId, String paymentMethodId) throws StripeException {
	    	Stripe.apiKey = stripeApiKey;
	        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
	        Map<String, Object> params = new HashMap<>();
	        params.put("customer", customerId);
	        paymentMethod.attach(params);
	    }
	    
	    public void updateSubscription(String customerId, String paymentMethodId) throws StripeException {
	        attachPaymentMethodToCustomer(customerId, paymentMethodId);
	        Map<String, Object> params = new HashMap<>();
	        params.put("invoice_settings", Map.of("default_payment_method", paymentMethodId));
	        Customer customer = Customer.retrieve(customerId);
	        customer.update(params);

	    }
	    
	    
	    public String getDefaultPaymentMethodId(String customerId) throws StripeException {
	        Customer customer = Customer.retrieve(customerId);
	        return customer.getInvoiceSettings().getDefaultPaymentMethod();
	    }
	    
	    public void detachPaymentMethod(String paymentMethodId) throws StripeException {
	        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
	        paymentMethod.detach();

	    }

	    
	    public void upgradeUserRoleToPaidMember(Integer userId) {
	        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

	        Role paidMemberRole = roleRepository.findByName("ROLE_PAID_MEMBER");
	        if (paidMemberRole == null) {
	            throw new RuntimeException("Role 'ROLE_PAID_MEMBER' not found");
	        }

	        // UserにRoleを設定
	        user.setRole(paidMemberRole);
	        userRepository.save(user);
	    }
	    
	    public Subscription getSubscription(String customerId) {
	        try {
	            SubscriptionListParams params = SubscriptionListParams.builder()
	                .setCustomer(customerId)
	                .build();

	            List<Subscription> subscriptions = Subscription.list(params).getData();

	            for (Subscription subscription : subscriptions) {
	                if ("active".equals(subscription.getStatus())) {
	                    return subscription;
	                }
	            }
	            
	            return null;            
	        } catch (StripeException e) { 
	            e.printStackTrace();
	            return null;
	        }
	    }

	    public void cancelSubscription(Subscription subscription) {
	        try {
	            subscription.cancel();
	        } catch (StripeException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    // 月間売上を取得する
	    public long getMonthlyTotalRevenue(LocalDate month) throws StripeException {
	        Stripe.apiKey = stripeApiKey;

	        Instant firstDayOfMonthInstant = month.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
	        Instant lastDayOfMonthInstant = month.plusMonths(1).withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

	        long startTime = firstDayOfMonthInstant.getEpochSecond();
	        long endTime = lastDayOfMonthInstant.getEpochSecond();

	        Map<String, Object> params = new HashMap<>();
	        params.put("created", Map.of(
	            "gte", startTime,
	            "lt", endTime
	        ));        

	        InvoiceCollection invoices = Invoice.list(params);

	        long totalRevenueCents = 0;

	        for (Invoice invoice : invoices.getData()) {
	            totalRevenueCents += invoice.getAmountPaid();
	        }

	        return totalRevenueCents;
	    }
	    
	    public PaymentIntent createPaymentIntent(String customerId, int amount, String currency, String paymentMethodId) throws StripeException {
	        Stripe.apiKey = stripeApiKey;

	        Map<String, Object> params = new HashMap<>();
	        params.put("customer", customerId);

	        params.put("payment_method", paymentMethodId);
	        params.put("confirm", true);
	        params.put("off_session", true);

	        return PaymentIntent.create(params);
	    }
	    
	    public void updateDefaultPaymentMethod(String customerId, String paymentMethodId) throws StripeException {
	    	Stripe.apiKey = stripeApiKey;
	        CustomerUpdateParams params = CustomerUpdateParams.builder()
	            .setInvoiceSettings(CustomerUpdateParams.InvoiceSettings.builder()
	                .setDefaultPaymentMethod(paymentMethodId)
	                .build())
	            .build();
	        Customer customer = Customer.retrieve(customerId);
	        customer.update(params);
	    }
	    
	 
	    
	    

	    public String getSubscriptionIdForUser(String customerId) throws StripeException {
	        Stripe.apiKey =  stripeApiKey; // 秘密鍵を設定
	        SubscriptionCollection subscriptions = Subscription.list(
	                Map.of("customer", customerId)
	        );

	        // 仮に最初のサブスクリプションを取得
	        Subscription subscription = subscriptions.getData().isEmpty() ? null : subscriptions.getData().get(0);

	        return subscription != null ? subscription.getId() : null;
	    }
	    

	    public void upgradeUserRoleToGeneralMember(Integer userId) {
	        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

	        Role generalMemberRole = roleRepository.findByName("ROLE_GENERAL");
	        if (generalMemberRole == null) {
	            throw new RuntimeException("Role 'ROLE_GENERAL' not found");
	        }

	        // UserにRoleを設定
	        user.setRole(generalMemberRole);
	        userRepository.save(user);
	    }
	    
}