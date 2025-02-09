// Stripe.jsの初期化（公開可能なキーを設定）
const stripe = Stripe('pk_test_51Q5unLBZ4UD9z1bMve2FOhVXnfb3Nzb51ghCQFK8KzKI0je1qfgayfTBcja04miG1xNMBZKQqXH9hfJmgRJ36rFC00Qte1CdJE');
const elements = stripe.elements();

document.addEventListener('DOMContentLoaded', () => {
  // DOM要素の取得と存在確認
  const cardForm = document.getElementById('cardForm');
  const cardElementContainer = document.getElementById('cardElement');
  const cardElementError = document.getElementById('cardElementError');
  const cardButton = document.getElementById('cardButton');

  if (!cardForm || !cardElementContainer || !cardButton) {
    console.error('必要なDOM要素が見つかりません。');
    return;
  }

  // カード要素を作成
  const cardElement = elements.create('card', {
    hidePostalCode: true, // 郵便番号入力を非表示にする
  });
  cardElement.mount('#cardElement'); // カード要素を指定IDにマウント

  // フォーム送信時のイベントリスナー
  cardForm.addEventListener('submit', async (event) => {
    event.preventDefault(); // フォームのデフォルト動作をキャンセル

    cardButton.disabled = true; // ボタンを無効化して多重送信を防止

    // 支払い方法を作成
    const { error, paymentMethod } = await stripe.createPaymentMethod({
      type: 'card',
      card: cardElement,
      billing_details: {
        name: document.getElementById('cardHolderName').value,
      },
    });

    if (error) {
      cardElementError.textContent = error.message; // エラーを表示
      cardButton.disabled = false; // ボタンを再度有効化
      return;
    }

    // サーバーに支払い方法を送信
    const response = await fetch('/user/update-payment-method', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-CSRF-Token': document.querySelector('meta[name="csrf-token"]').getAttribute('content'),
      },
      body: JSON.stringify({
        paymentMethodId: paymentMethod.id,
      }),
    });

    if (response.ok) {
      alert('支払い方法が正常に更新されました！');
    } else {
      cardElementError.textContent = '支払い方法の更新に失敗しました。';
      console.error('支払い方法の更新に失敗しました。');
    }

    cardButton.disabled = false; // ボタンを再度有効化
  });
});