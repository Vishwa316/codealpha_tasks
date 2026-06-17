// ============================
// Docker Web Server - app.js
// Interactive Container Simulation
// ============================

// ---- Command Cheatsheet Data ----
const commands = [
    {
        category: 'lifecycle',
        title: 'Build Image',
        description: 'Build a Docker image from a Dockerfile in current directory.',
        command: 'docker build -t myapp:latest .'
    },
    {
        category: 'lifecycle',
        title: 'Run Container',
        description: 'Start a new container from an image with port mapping.',
        command: 'docker run -d -p 8080:80 --name myapp myapp:latest'
    },
    {
        category: 'lifecycle',
        title: 'Stop Container',
        description: 'Gracefully stop a running container using SIGTERM.',
        command: 'docker stop myapp'
    },
    {
        category: 'lifecycle',
        title: 'Start Container',
        description: 'Restart a previously stopped container.',
        command: 'docker start myapp'
    },
    {
        category: 'lifecycle',
        title: 'Pause Container',
        description: 'Freeze all processes inside a container temporarily.',
        command: 'docker pause myapp'
    },
    {
        category: 'lifecycle',
        title: 'Remove Container',
        description: 'Delete a stopped container from the host system.',
        command: 'docker rm myapp'
    },
    {
        category: 'management',
        title: 'List Containers',
        description: 'Show all running containers and their status.',
        command: 'docker ps -a'
    },
    {
        category: 'management',
        title: 'List Images',
        description: 'Display all locally stored Docker images.',
        command: 'docker images'
    },
    {
        category: 'management',
        title: 'Inspect Container',
        description: 'Display detailed JSON info about a container.',
        command: 'docker inspect myapp'
    },
    {
        category: 'management',
        title: 'Docker Stats',
        description: 'Real-time CPU, memory, and network usage for containers.',
        command: 'docker stats --no-stream'
    },
    {
        category: 'management',
        title: 'Remove Image',
        description: 'Delete a Docker image from local registry.',
        command: 'docker rmi myapp:latest'
    },
    {
        category: 'management',
        title: 'Compose Up',
        description: 'Start all services from docker-compose.yml in detached mode.',
        command: 'docker-compose up --build -d'
    },
    {
        category: 'trouble',
        title: 'Container Logs',
        description: 'Stream live output logs from a running container.',
        command: 'docker logs -f myapp'
    },
    {
        category: 'trouble',
        title: 'Exec Shell',
        description: 'Open an interactive bash shell inside a running container.',
        command: 'docker exec -it myapp /bin/sh'
    },
    {
        category: 'trouble',
        title: 'Health Check',
        description: 'Inspect the health status defined in Dockerfile/Compose.',
        command: 'docker inspect --format="{{json .State.Health}}" myapp'
    },
    {
        category: 'trouble',
        title: 'Prune System',
        description: 'Clean up all stopped containers, unused images, and volumes.',
        command: 'docker system prune -af --volumes'
    },
    {
        category: 'trouble',
        title: 'Copy From Container',
        description: 'Copy a file from inside a container to the host machine.',
        command: 'docker cp myapp:/etc/nginx/nginx.conf ./nginx.conf'
    },
    {
        category: 'trouble',
        title: 'Docker Events',
        description: 'Monitor real-time lifecycle events from the Docker daemon.',
        command: 'docker events --filter container=myapp'
    },
];

// ---- Container State Machine ----
let containerState = 'dead'; // dead -> built -> running -> paused -> stopped -> removed
let metricsInterval = null;

const states = {
    dead:    { badge: 'status: dead',    badgeClass: '',              termClass: 'output' },
    built:   { badge: 'status: built',   badgeClass: 'status-building', termClass: 'info' },
    running: { badge: 'status: running', badgeClass: 'status-running', termClass: 'success' },
    paused:  { badge: 'status: paused',  badgeClass: 'status-paused',   termClass: 'output' },
    stopped: { badge: 'status: stopped', badgeClass: 'status-stopped',  termClass: 'error' },
};

const btnBuild  = document.getElementById('btn-build');
const btnRun    = document.getElementById('btn-run');
const btnPause  = document.getElementById('btn-pause');
const btnStop   = document.getElementById('btn-stop');
const btnHealth = document.getElementById('btn-health');
const badgeEl   = document.getElementById('container-status-badge');
const terminal  = document.getElementById('terminal-logs');

function log(text, cls = 'output') {
    const line = document.createElement('div');
    line.className = `terminal-line ${cls}`;
    const ts = new Date().toLocaleTimeString('en-US', { hour12: false });
    line.textContent = `[${ts}] ${text}`;
    terminal.appendChild(line);
    terminal.scrollTop = terminal.scrollHeight;
}

function setBadge(state) {
    badgeEl.textContent = states[state].badge;
    badgeEl.className   = `badge ${states[state].badgeClass}`;
}

function updateButtons() {
    const s = containerState;
    btnBuild.disabled  = (s !== 'dead' && s !== 'stopped');
    btnRun.disabled    = (s !== 'built');
    btnPause.disabled  = (s !== 'running' && s !== 'paused');
    btnStop.disabled   = (s !== 'running' && s !== 'paused');
    btnHealth.disabled = (s !== 'running');
    if (s === 'paused') {
        btnPause.innerHTML = '<i class="fa-solid fa-play"></i> docker unpause';
    } else {
        btnPause.innerHTML = '<i class="fa-solid fa-pause"></i> docker pause';
    }
}

function animateBuild(callback) {
    const steps = [
        { text: '$ docker build -t devops-webserver:latest .', cls: 'command' },
        { text: 'Step 1/5 : FROM nginx:alpine', cls: 'output' },
        { text: 'Step 2/5 : COPY nginx.conf /etc/nginx/nginx.conf', cls: 'output' },
        { text: 'Step 3/5 : COPY src/ /usr/share/nginx/html/', cls: 'output' },
        { text: 'Step 4/5 : EXPOSE 80', cls: 'output' },
        { text: 'Step 5/5 : HEALTHCHECK CMD curl --fail http://localhost/health || exit 1', cls: 'output' },
        { text: 'Successfully built a4f92c1b3d7e', cls: 'success' },
        { text: 'Successfully tagged devops-webserver:latest', cls: 'success' },
    ];
    let i = 0;
    const interval = setInterval(() => {
        if (i < steps.length) {
            log(steps[i].text, steps[i].cls);
            i++;
        } else {
            clearInterval(interval);
            callback();
        }
    }, 250);
}

function startMetrics() {
    let cpu = 0, mem = 80;
    metricsInterval = setInterval(() => {
        if (containerState !== 'running') return;
        cpu = Math.min(80, Math.max(3, cpu + (Math.random() * 10 - 4)));
        mem = Math.min(140, Math.max(70, mem + (Math.random() * 8 - 4)));
        const health = 90 + Math.floor(Math.random() * 10);
        document.getElementById('metric-cpu').textContent  = `${cpu.toFixed(1)}%`;
        document.getElementById('metric-mem').textContent  = `${mem.toFixed(0)} MB`;
        document.getElementById('metric-health').textContent = `${health}%`;
        document.getElementById('cpu-progress').style.width    = `${cpu}%`;
        document.getElementById('mem-progress').style.width    = `${(mem / 200) * 100}%`;
        document.getElementById('health-progress').style.width = `${health}%`;
        document.getElementById('cpu-progress').style.background = cpu > 60 ? '#ef4444' : '#3b82f6';
        document.getElementById('mem-progress').style.background = mem > 120 ? '#f59e0b' : '#3b82f6';
        document.getElementById('health-progress').style.background = '#10b981';
    }, 900);
}

function stopMetrics() {
    clearInterval(metricsInterval);
    ['metric-cpu', 'metric-mem', 'metric-health'].forEach(id => {
        document.getElementById(id).textContent = '0';
    });
    ['cpu-progress', 'mem-progress', 'health-progress'].forEach(id => {
        document.getElementById(id).style.width = '0%';
    });
}

// ---- Button Event Listeners ----
btnBuild.addEventListener('click', () => {
    containerState = 'building';
    setBadge('built');
    updateButtons();
    log('Initiating Docker build process...', 'info');
    animateBuild(() => {
        containerState = 'built';
        setBadge('built');
        updateButtons();
        log('Image ready. Use "docker run" to start the container.', 'success');
    });
});

btnRun.addEventListener('click', () => {
    log('$ docker run -d -p 8080:80 --name devops-webserver devops-webserver:latest', 'command');
    log('Container ID: 7f3a1c09d2b8e4f1a5c60d87e942f3...', 'output');
    log('Port binding: 0.0.0.0:8080 -> 80/tcp', 'output');
    log('Nginx master process started. Server is LIVE.', 'success');
    containerState = 'running';
    setBadge('running');
    updateButtons();
    startMetrics();
});

btnPause.addEventListener('click', () => {
    if (containerState === 'running') {
        log('$ docker pause devops-webserver', 'command');
        log('All cgroup processes frozen. Container is PAUSED.', 'output');
        containerState = 'paused';
        setBadge('paused');
    } else if (containerState === 'paused') {
        log('$ docker unpause devops-webserver', 'command');
        log('Processes resumed. Container is RUNNING again.', 'success');
        containerState = 'running';
        setBadge('running');
    }
    updateButtons();
});

btnStop.addEventListener('click', () => {
    log('$ docker stop devops-webserver', 'command');
    log('SIGTERM sent to container PID 1...', 'output');
    setTimeout(() => {
        log('Container devops-webserver stopped gracefully.', 'error');
        containerState = 'stopped';
        setBadge('stopped');
        updateButtons();
        stopMetrics();
    }, 800);
});

btnHealth.addEventListener('click', () => {
    log('$ docker inspect --format="{{json .State.Health}}" devops-webserver', 'command');
    log('{"Status":"healthy","FailingStreak":0,"Log":[{"Start":"2026-...","End":"...","ExitCode":0,"Output":"healthy"}]}', 'output');
    log('Healthcheck result: HEALTHY ✓', 'success');
});

document.getElementById('btn-clear-logs').addEventListener('click', () => {
    terminal.innerHTML = '<div class="terminal-line text-muted">// Terminal cleared. Ready.</div>';
});

// ---- Cheatsheet Rendering ----
function renderCheatsheet(filter = 'all') {
    const container = document.getElementById('cheatsheet-container');
    container.innerHTML = '';
    const filtered = filter === 'all' ? commands : commands.filter(c => c.category === filter);
    filtered.forEach(cmd => {
        const card = document.createElement('div');
        card.className = 'cmd-card';
        card.innerHTML = `
            <span class="cmd-category">${cmd.category}</span>
            <div class="cmd-title">${cmd.title}</div>
            <div class="cmd-desc">${cmd.description}</div>
            <div class="cmd-box">
                <span class="cmd-text">${cmd.command}</span>
                <button class="btn-copy" title="Copy command" onclick="copyCmd(this, '${cmd.command.replace(/'/g, "\\'")}')">
                    <i class="fa-solid fa-copy"></i>
                </button>
            </div>
        `;
        container.appendChild(card);
    });
}

function copyCmd(btn, text) {
    navigator.clipboard.writeText(text).then(() => {
        btn.innerHTML = '<i class="fa-solid fa-check" style="color: #10b981"></i>';
        setTimeout(() => { btn.innerHTML = '<i class="fa-solid fa-copy"></i>'; }, 1500);
    });
}

function switchTab(filter) {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');
    renderCheatsheet(filter);
}

// Initialize
renderCheatsheet('all');
updateButtons();
