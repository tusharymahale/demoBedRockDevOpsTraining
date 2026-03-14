(function(){
  const chatWindow = document.getElementById('chatWindow');
  const input = document.getElementById('messageInput');
  const sendBtn = document.getElementById('sendBtn');
  const status = document.getElementById('status');

  function appendMessage(text, who){
    const div = document.createElement('div');
    div.className = 'message ' + who;
    const b = document.createElement('div');
    b.className = 'bubble';
    b.textContent = text;
    div.appendChild(b);
    chatWindow.appendChild(div);
    chatWindow.scrollTop = chatWindow.scrollHeight;
  }

  async function send(){
    const text = input.value.trim();
    if(!text) return;
    appendMessage(text, 'user');
    input.value = '';
    status.textContent = 'Sending...';
    try{
      const res = await fetch('/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sessionId: 'front', message: text })
      });
      if(!res.ok){
        const txt = await res.text();
        throw new Error('HTTP ' + res.status + ' ' + txt);
      }
      const data = await res.json();
      appendMessage(data.reply + ' (' + data.latency + 'ms)', 'bot');
      status.textContent = '';
    } catch(err){
      console.error(err);
      appendMessage('Error: ' + err.message, 'bot');
      status.textContent = '';
    }
  }

  sendBtn.addEventListener('click', send);
  input.addEventListener('keydown', (e)=>{ if(e.key === 'Enter'){ send(); } });
})();
