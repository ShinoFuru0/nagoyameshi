const stripe = Stripe('pk_test_51Q5unLBZ4UD9z1bMve2FOhVXnfb3Nzb51ghCQFK8KzKI0je1qfgayfTBcja04miG1xNMBZKQqXH9hfJmgRJ36rFC00Qte1CdJE');
// const paymentButton = document.querySelector('#checkout-button');
 const csrfToken = document.querySelector('input[name="csrf-token"]').value;
 const elements = stripe.elements();
const cardElement = elements.create('card');
cardElement.mount('#cardElement');

const cardButton = document.getElementById('cardButton');


// カード情報の送信ハンドリング
document.getElementById('cardButton').addEventListener('click', function(e) {
    e.preventDefault();

    stripe.createPaymentMethod({
        type: 'card',
        card: cardElement,
    }).then(function(result) {
        if (result.error) {
            // エラーハンドリング
            console.error(result.error.message);
        } else {
			 const csrfToken = document.querySelector('meta[name="csrf-token"]').getAttribute('content');
			 if (!csrfToken) {
    console.error('CSRF token is missing');
}
            // バックエンドに支払い情報を送信
            fetch('/user/create-checkout-session', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]').getAttribute('content')
                    

//                     'X-CSRF-TOKEN': csrfToken // CSRFトークンをヘッダーにセット
                },
                
                body: JSON.stringify({ 
					paymentMethodId: result.paymentMethod.id
//    				paymentMethodId: 'pm_exampleId',  
                
                })
            }).then(function(response) {
//                return response.json();
 return response.text();
            })
            .then(data => {
				console.log('Data:', data);
//				 const sessionId = data.session_id;
//      window.location.href = '/user/success'; // 成功時にリダイレクト
 const sessionId = data.session_id;
                window.location.href = `/user/success?session_id=${sessionId}`;
  })
  .catch(error => {
    console.error('Error:', error);
  })
//            .then(response => response.json())
//    .then(data => console.log(data))
//    .catch(error => console.error(error))
//            .then(function(subscriptionResponse) {
//                console.log(subscriptionResponse);
//            })
//            .then(response => {
//    if (!response.ok) {
//        return response.json().then(error => {
//            throw new Error(error.message);
//        });
//    }
    
//    return response.json();
//})
//.catch(error => {
//    alert(`エラー: ${error.message}`);
//});;
//            if (result.error) {
//    document.getElementById('cardElementError').textContent = result.error.message;
//}

        }
    });
});

//const cardButton = document.getElementById('cardButton');

//cardButton.addEventListener('click', function(e) {
//  e.preventDefault();
  
  // エラーメッセージを初期化する
//  document.getElementById('cardHolderNameError').innerHTML = '';
//  document.getElementById('cardElementError').innerHTML = '';  
  
  // カード名義人が未入力の場合はエラーメッセージを表示する
//  const cardHolderName = document.getElementById('cardHolderName');
//  let cardHolderNameError = false;
//  if (!cardHolderName.value) {
//      document.getElementById('cardHolderNameError').innerHTML = '<div class="text-danger small mb-2">カード名義人の入力は必須です。</div>';   
//      cardHolderNameError = true;   
//  }  
  
//  stripe.createPaymentMethod({
//    type: 'card',
//    card: cardElement,
//    billing_details: {
//        name: cardHolderName.value,
//    },    
//  }).then(function(result) {
//    if (result.error) {
      // カード番号に不備がある場合はエラーメッセージを表示する
//      document.getElementById('cardElementError').innerHTML = '<div class="text-danger small mb-2">カード番号に不備があります。</div>';
//    } else if (!cardHolderNameError) {
      // Payment MethodのIDをサーバーに送信する
//      stripePaymentIdHandler(result.paymentMethod.id);
//    }
//  });
//});

//function stripePaymentIdHandler(paymentMethodId) {
//  const form = document.getElementById('cardForm');
  
//  const hiddenInput = document.createElement('input');
//  hiddenInput.setAttribute('type', 'hidden');
//  hiddenInput.setAttribute('name', 'paymentMethodId');
//  hiddenInput.setAttribute('value', paymentMethodId);
//  form.appendChild(hiddenInput);
  
//  form.submit();
//}






// paymentButton.addEventListener('click', () => {
//	  console.log(sessionId);
//   stripe.redirectToCheckout({
//     sessionId: sessionId
     
//   }).then(function (result) {
//        if (result.error) {
            // エラーメッセージを表示する
//            alert(result.error.message);
//        }
//     });
//     console.log(sessionId);
// });