async function analyzeCode() {
  const code = document.getElementById('codeInput').value.trim();
  const btn = document.getElementById('analyzeBtn');
  const results = document.getElementById('results');

  if (!code) {
    results.innerHTML = '<p class="placeholder">Please enter some C code first.</p>';
    return;
  }

  btn.disabled = true;
  btn.textContent = '⏳ Analyzing...';
  results.innerHTML = '<p class="placeholder">Running analysis...</p>';

  try {
    const response = await fetch('http://localhost:8080/api/analyze', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ code })
    });

    const data = await response.json();
    renderResults(data);
  } catch (err) {
    results.innerHTML = `
      <div class="error-card SYNTAX">
        <span class="badge">CONNECTION ERROR</span>
        <p class="error-msg">Cannot connect to backend. Make sure Spring Boot is running on port 8080.</p>
      </div>`;
  } finally {
    btn.disabled = false;
    btn.textContent = '🔍 Analyze Code';
  }
}

function renderResults(data) {
  const results = document.getElementById('results');

  if (data.success) {
    results.innerHTML = '<div class="success-msg">✅ No errors found! Your code looks good.</div>';
    return;
  }

  const counts = { LEXICAL: 0, SYNTAX: 0, SEMANTIC: 0 };
  data.errors.forEach(e => counts[e.type] = (counts[e.type] || 0) + 1);

  let html = `<div class="summary">
    Found <strong>${data.errors.length}</strong> error(s) —
    🟠 Lexical: ${counts.LEXICAL} &nbsp;
    🔴 Syntax: ${counts.SYNTAX} &nbsp;
    🟣 Semantic: ${counts.SEMANTIC}
  </div>`;

  data.errors.forEach(err => {
    html += `
      <div class="error-card ${err.type}">
        <span class="badge">${err.type}</span>
        <div class="line-info">📍 Line ${err.line}</div>
        <div class="error-msg">${err.message}</div>
      </div>`;
  });

  results.innerHTML = html;
}