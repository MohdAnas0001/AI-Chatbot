// =============================================
//  State
// =============================================
let currentTopic = 'college';
let isLoading = false;

const topicConfig = {
	college: { emoji: '🎓', name: 'CollegeBot', welcome: "Hey there! I'm CollegeBot", desc: 'Ask me about admissions, courses, fees, hostel, placements, or campus life!', suggestions: ['What courses do you offer?', 'What is the fee structure?', 'Tell me about placements', 'How to apply for admission?'] },
	restaurant: { emoji: '🍽️', name: 'MenuBot', welcome: "Welcome! I'm MenuBot", desc: 'Ask me about our menu, reservations, timings, delivery, or special offers!', suggestions: ["What's on the menu?", 'Do you take reservations?', 'What are your timings?', 'Do you offer delivery?'] },
	product: { emoji: '🛍️', name: 'ShopBot', welcome: "Hi! I'm ShopBot", desc: 'Ask me about products, pricing, shipping, returns, or warranty information!', suggestions: ['What products do you have?', 'What is your return policy?', 'How long is shipping?', 'Do you offer EMI?'] },
	hospital: { emoji: '🏥', name: 'MediBot', welcome: "Hello! I'm MediBot", desc: 'Ask me about departments, doctors, appointments, timings, or health packages!', suggestions: ['How to book an appointment?', 'What departments do you have?', 'What are OPD timings?', 'Do you accept insurance?'] },
};

// =============================================
//  Load chatbot info on page load
// =============================================
window.onload = async () => {
	try {
		const res = await fetch('/api/chat/info');
		const info = await res.json();
		currentTopic = info.topic || 'college';
		document.getElementById('topicSelect').value = currentTopic;
		updateTopicUI(currentTopic);
	} catch (e) {
		console.log('Could not fetch info, using defaults');
	}
};

// =============================================
//  Change topic
// =============================================
function changeTopic(topic) {
	currentTopic = topic;
	updateTopicUI(topic);
}

function updateTopicUI(topic) {
	const config = topicConfig[topic] || topicConfig.college;
	document.getElementById('botAvatar').textContent = config.emoji;
	document.getElementById('botName').textContent = config.name;
	document.getElementById('welcomeTitle').textContent = `Hey there! I'm ${config.name}`;
	document.getElementById('welcomeText').textContent = config.desc;

	// Update suggestion chips
	const sugDiv = document.getElementById('suggestions');
	sugDiv.innerHTML = config.suggestions
		.map(s => `<div class="chip" onclick="sendSuggestion('${s}')">${s}</div>`)
		.join('');

	// Update empty state icon
	document.querySelector('#emptyState .icon').textContent = config.emoji;
}

// =============================================
//  Send message
// =============================================
async function sendMessage() {
	const input = document.getElementById('messageInput');
	const message = input.value.trim();

	if (!message || isLoading) return;

	// Hide empty state on first message
	document.getElementById('emptyState').style.display = 'none';

	// Show user message
	appendMessage('user', message);
	input.value = '';
	autoResize(input);

	// Show typing indicator
	isLoading = true;
	setSendBtnDisabled(true);
	const typingId = showTyping();

	try {
		const res = await fetch('/api/chat', {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ message, topic: currentTopic })
		});

		const data = await res.json();
		removeTyping(typingId);

		if (data.success) {
			appendMessage('bot', data.reply);
		} else {
			appendMessage('bot', '⚠️ ' + (data.error || 'Something went wrong. Please try again.'));
		}

	} catch (err) {
		removeTyping(typingId);
		showToast('Network error. Is the server running?');
		appendMessage('bot', '⚠️ Could not reach the server. Please check your connection.');
	}

	isLoading = false;
	setSendBtnDisabled(false);
}

function sendSuggestion(text) {
	document.getElementById('messageInput').value = text;
	sendMessage();
}

// =============================================
//  Append message to chat
// =============================================
function appendMessage(role, text) {
	const area = document.getElementById('messagesArea');
	const config = topicConfig[currentTopic] || topicConfig.college;

	const msgDiv = document.createElement('div');
	msgDiv.className = `message ${role}`;

	const time = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
	const avatar = role === 'bot' ? config.emoji : '👤';

	msgDiv.innerHTML = `
        <div class="msg-avatar">${avatar}</div>
        <div>
            <div class="bubble">${escapeHtml(text)}</div>
            <div class="msg-time">${time}</div>
        </div>
    `;

	area.appendChild(msgDiv);
	area.scrollTop = area.scrollHeight;
}

// =============================================
//  Typing indicator
// =============================================
function showTyping() {
	const area = document.getElementById('messagesArea');
	const id = 'typing-' + Date.now();
	const div = document.createElement('div');
	div.className = 'typing-indicator';
	div.id = id;
	div.innerHTML = `
        <div class="msg-avatar" style="background:linear-gradient(135deg,#4f8ef7,#7c5cfc);width:32px;height:32px;border-radius:10px;display:flex;align-items:center;justify-content:center;font-size:16px;">${topicConfig[currentTopic]?.emoji || '🤖'}</div>
        <div class="typing-dots"><div class="dot"></div><div class="dot"></div><div class="dot"></div></div>
    `;
	area.appendChild(div);
	area.scrollTop = area.scrollHeight;
	return id;
}

function removeTyping(id) {
	const el = document.getElementById(id);
	if (el) el.remove();
}

// =============================================
//  Helpers
// =============================================
function handleKeyDown(e) {
	if (e.key === 'Enter' && !e.shiftKey) {
		e.preventDefault();
		sendMessage();
	}
}

function autoResize(el) {
	el.style.height = 'auto';
	el.style.height = Math.min(el.scrollHeight, 100) + 'px';
}

function setSendBtnDisabled(val) {
	document.getElementById('sendBtn').disabled = val;
}

function clearChat() {
	const area = document.getElementById('messagesArea');
	area.innerHTML = '';
	const emptyState = document.createElement('div');
	emptyState.id = 'emptyState';
	emptyState.className = 'empty-state';
	const config = topicConfig[currentTopic] || topicConfig.college;
	emptyState.innerHTML = `
        <div class="icon">${config.emoji}</div>
        <h2 id="welcomeTitle">Hey there! I'm ${config.name}</h2>
        <p id="welcomeText">${config.desc}</p>
        <div class="suggestions" id="suggestions">
            ${config.suggestions.map(s => `<div class="chip" onclick="sendSuggestion('${s}')">${s}</div>`).join('')}
        </div>
    `;
	area.appendChild(emptyState);
}

function showToast(msg) {
	const toast = document.getElementById('toast');
	toast.textContent = msg;
	toast.classList.add('show');
	setTimeout(() => toast.classList.remove('show'), 3000);
}

function escapeHtml(str) {
	return str
		.replace(/&/g, '&amp;')
		.replace(/</g, '&lt;')
		.replace(/>/g, '&gt;')
		.replace(/\n/g, '<br>');
}