window.ParkingTestCommon = (() => {
    const state = {
        resultEl: null,
        statusEl: null,
        lastResponse: null
    };

    function bindOutput(resultId, statusId) {
        state.resultEl = document.getElementById(resultId);
        state.statusEl = document.getElementById(statusId);
    }

    function setOutput(value, ok = true) {
        if (state.statusEl) {
            state.statusEl.textContent = ok ? 'OK' : 'ERROR';
            state.statusEl.style.color = ok ? '#047857' : '#b91c1c';
        }
        if (state.resultEl) {
            state.resultEl.textContent = typeof value === 'string'
                ? value
                : JSON.stringify(value, null, 2);
        }
    }

    function unwrap(payload) {
        return payload && typeof payload === 'object' && 'success' in payload && 'data' in payload
            ? payload.data
            : payload;
    }

    async function readResponse(response) {
        const text = await response.text();
        if (!response.ok) {
            throw new Error(`${response.status} ${response.statusText}\n${text}`);
        }
        try {
            return JSON.parse(text);
        } catch {
            return text;
        }
    }

    async function request(path, method = 'GET', body) {
        const response = await fetch(path, {
            method,
            credentials: 'include',
            headers: body instanceof FormData ? undefined : (body ? {'Content-Type': 'application/json'} : undefined),
            body: body instanceof FormData ? body : (body ? JSON.stringify(body) : undefined)
        });
        return readResponse(response);
    }

    function toLocalInputValue(date) {
        const pad = (value) => String(value).padStart(2, '0');
        return [
            date.getFullYear(),
            pad(date.getMonth() + 1),
            pad(date.getDate())
        ].join('-') + 'T' + [pad(date.getHours()), pad(date.getMinutes())].join(':');
    }

    function setRelativeDateTime(inputId, hoursOffset = 0, minutesOffset = 0) {
        const date = new Date();
        date.setHours(date.getHours() + hoursOffset);
        date.setMinutes(date.getMinutes() + minutesOffset);
        const el = document.getElementById(inputId);
        if (el) {
            el.value = toLocalInputValue(date);
        }
    }

    function randomPlate(prefix = '51A') {
        const suffix = String(Math.floor(Math.random() * 9000) + 1000);
        return `${prefix}-${suffix}`;
    }

    async function login(usernameId, passwordId) {
        const data = await request('/api/auth/login', 'POST', {
            username: document.getElementById(usernameId).value,
            password: document.getElementById(passwordId).value
        });
        setOutput(data);
        return data;
    }

    async function me() {
        const data = await request('/api/auth/me');
        setOutput(data);
        return data;
    }

    async function logout() {
        await request('/api/auth/logout', 'POST');
        setOutput('Logged out');
    }

    return {
        state,
        bindOutput,
        setOutput,
        unwrap,
        readResponse,
        request,
        toLocalInputValue,
        setRelativeDateTime,
        randomPlate,
        login,
        me,
        logout
    };
})();
