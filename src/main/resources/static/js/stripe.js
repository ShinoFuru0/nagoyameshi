const stripe = Stripe('pk_test_51QNaYMC7xSmMs4vIRz6zTrtD90QMSypdTF60W9leYHBrPgCTKbvfqk0GUbR6fLVN3JgA86eYrVNayc0Z5kKXYiUr003Jedzt51');
const elements = stripe.elements();
const cardElement = elements.create('card');
cardElement.mount('#cardElement');

const cardButton = document.getElementById('cardButton');

// CSRFトークンの取得
const csrfTokenMeta = document.querySelector('meta[name="csrf-token"]');
const csrfToken = csrfTokenMeta ? csrfTokenMeta.getAttribute('content') : null;

if (!csrfToken) {
    console.error('CSRF token is missing');
}

// カード情報の送信ハンドリング
cardButton.addEventListener('click', function(e) {
    e.preventDefault();

    stripe.createPaymentMethod({
        type: 'card',
        card: cardElement,
    }).then(function(result) {
        if (result.error) {
            console.error(result.error.message);
        } else {
            fetch('/user/create-checkout-session', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': csrfToken
                },
                body: JSON.stringify({ 
                    paymentMethodId: result.paymentMethod.id
                })
            }).then(response => response.json())  
            .then(data => {
                console.log('Response Data:', data);
                if (data.error) {
                    console.error('Error:', data.error);
                    return;
                }

                // sessionIdを取得し、リダイレクト
                const sessionId = data.id; 
                if (sessionId) {
                    window.location.href = `/user/success?session_id=${sessionId}`;
                } else {
                    console.error("Session ID not received.");
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
        }
    });
});
